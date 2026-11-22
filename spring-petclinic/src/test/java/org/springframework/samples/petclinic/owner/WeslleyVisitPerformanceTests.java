/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.owner;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

/**
 * Teste não funcional (eficiência de desempenho, ISO/IEC 25010) do cadastro de visitas.
 * RNF-01: abrir o formulário e concluir o cadastro em até 1 s no percentil 95, em 30
 * requisições sequenciais após 5 de aquecimento. Servidor, MVC e banco H2 são reais.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,
		properties = { "spring.docker.compose.lifecycle-management=NONE",
				"spring.datasource.url=jdbc:h2:mem:weslley-visits-performance;DB_CLOSE_DELAY=-1",
				"spring.datasource.username=sa", "spring.datasource.password=" })
class WeslleyVisitPerformanceTests {

	private static final int WARMUP = 5;

	private static final int MEASURED = 30;

	private static final long LIMIT_P95_MS = 1000;

	@LocalServerPort
	private int port;

	@Value("${user.dir}")
	private String projectDirectory;

	@Autowired
	private JdbcTemplate jdbc;

	private HttpClient client;

	private Integer ownerId;

	private Integer petId;

	@BeforeEach
	void prepararDados() {
		ownerId = new SimpleJdbcInsert(jdbc).withTableName("owners")
			.usingColumns("first_name", "last_name", "address", "city", "telephone")
			.usingGeneratedKeyColumns("id")
			.executeAndReturnKey(Map.of("first_name", "Weslley", "last_name", "Desempenho", "address", "Rua de teste",
					"city", "Niteroi", "telephone", "2198765432"))
			.intValue();
		Integer typeId = jdbc.queryForObject("SELECT id FROM types WHERE name = ?", Integer.class, "cat");
		petId = new SimpleJdbcInsert(jdbc).withTableName("pets")
			.usingColumns("name", "birth_date", "type_id", "owner_id")
			.usingGeneratedKeyColumns("id")
			.executeAndReturnKey(Map.of("name", "Animal Desempenho", "birth_date",
					java.sql.Date.valueOf(LocalDate.now().minusYears(1)), "type_id", typeId, "owner_id", ownerId))
			.intValue();
		// O cookie de sessão é necessário para a mensagem de sucesso após o
		// redirecionamento.
		client = HttpClient.newBuilder()
			.cookieHandler(new CookieManager())
			.followRedirects(HttpClient.Redirect.NORMAL)
			.connectTimeout(Duration.ofSeconds(5))
			.build();
	}

	@AfterEach
	void limparDados() {
		if (petId != null) {
			jdbc.update("DELETE FROM visits WHERE pet_id = ?", petId);
			jdbc.update("DELETE FROM pets WHERE id = ?", petId);
		}
		if (ownerId != null) {
			jdbc.update("DELETE FROM owners WHERE id = ?", ownerId);
		}
	}

	@Test
	void cadastroDeVisitaAtendeTempoDeRespostaNoPercentil95() throws Exception {
		List<Long> formulario = new ArrayList<>();
		List<Long> cadastro = new ArrayList<>();

		for (int i = 0; i < WARMUP + MEASURED; i++) {
			long tempoFormulario = abrirFormulario();
			long tempoCadastro = cadastrarVisita("Consulta de desempenho " + i);
			if (i >= WARMUP) {
				formulario.add(tempoFormulario);
				cadastro.add(tempoCadastro);
			}
		}

		// O desempenho só vale se a funcionalidade continuou correta durante a medição.
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM visits WHERE pet_id = ?", Integer.class, petId))
			.isEqualTo(WARMUP + MEASURED);

		String relatorio = resumo("Abrir formulário de visita (GET)", formulario) + System.lineSeparator()
				+ resumo("Cadastrar visita (POST + redirecionamento)", cadastro);
		salvarEvidencia(relatorio);
		System.out.println(relatorio);

		assertThat(percentil95(formulario)).as("p95 do formulário em ms").isLessThanOrEqualTo(LIMIT_P95_MS);
		assertThat(percentil95(cadastro)).as("p95 do cadastro em ms").isLessThanOrEqualTo(LIMIT_P95_MS);
	}

	private long abrirFormulario() throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder(URI.create(visitUrl())).GET().build();
		long inicio = System.nanoTime();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		long duracao = (System.nanoTime() - inicio) / 1_000_000;
		assertThat(response.statusCode()).isEqualTo(200);
		assertThat(response.body()).contains("id=\"description\"");
		return duracao;
	}

	private long cadastrarVisita(String descricao) throws IOException, InterruptedException {
		String form = "date=" + LocalDate.now().plusDays(1) + "&description="
				+ URLEncoder.encode(descricao, StandardCharsets.UTF_8);
		HttpRequest request = HttpRequest.newBuilder(URI.create(visitUrl()))
			.header("Content-Type", "application/x-www-form-urlencoded")
			.POST(HttpRequest.BodyPublishers.ofString(form))
			.build();
		long inicio = System.nanoTime();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		long duracao = (System.nanoTime() - inicio) / 1_000_000;
		assertThat(response.statusCode()).isEqualTo(200);
		// O primeiro redirecionamento pode trazer ;jsessionid=..., como na suíte
		// Selenium.
		assertThat(response.uri().getPath()).matches(Pattern.quote("/owners/" + ownerId) + "(?:;jsessionid=[^/?#]+)?");
		assertThat(response.body()).contains("Your visit has been booked");
		return duracao;
	}

	private static long percentil95(List<Long> amostras) {
		List<Long> ordenadas = new ArrayList<>(amostras);
		Collections.sort(ordenadas);
		// Método do posto mais próximo: menor valor que cobre 95% das amostras.
		int posto = (int) Math.ceil(0.95 * ordenadas.size());
		return ordenadas.get(posto - 1);
	}

	private static String resumo(String operacao, List<Long> amostras) {
		double media = amostras.stream().mapToLong(Long::longValue).average().orElse(0);
		long minimo = Collections.min(amostras);
		long maximo = Collections.max(amostras);
		return String.format(Locale.ROOT, "%s: n=%d, min=%d ms, media=%.1f ms, p95=%d ms, max=%d ms, limite p95=%d ms",
				operacao, amostras.size(), minimo, media, percentil95(amostras), maximo, LIMIT_P95_MS);
	}

	private void salvarEvidencia(String relatorio) throws IOException {
		Path pasta = Path.of(projectDirectory, "target", "performance-evidence");
		Files.createDirectories(pasta);
		Files.writeString(pasta.resolve("visitas-desempenho.txt"), relatorio + System.lineSeparator());
	}

	private String visitUrl() {
		return "http://localhost:" + port + "/owners/" + ownerId + "/pets/" + petId + "/visits/new";
	}

}

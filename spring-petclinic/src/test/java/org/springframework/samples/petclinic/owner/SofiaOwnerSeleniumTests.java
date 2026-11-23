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
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Testes de sistema do cadastro de proprietário, com navegador, MVC, validação e banco H2
 * reais. O fluxo começa na página inicial e usa somente a interface. Cada teste usa um
 * sobrenome exclusivo e remove os registros que criar.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,
		properties = { "spring.docker.compose.lifecycle-management=NONE",
				"spring.datasource.url=jdbc:h2:mem:sofia-owner-selenium;DB_CLOSE_DELAY=-1",
				"spring.datasource.username=sa", "spring.datasource.password=" })
class SofiaOwnerSeleniumTests {

	private static final String TELEFONE_ERRO = "Telephone must be a 10-digit number";

	@LocalServerPort
	private int port;

	@Value("${user.dir}")
	private String projectDirectory;

	@Autowired
	private JdbcTemplate jdbc;

	private WebDriver driver;

	private WebDriverWait wait;

	private String lastName;

	private int totalInicial;

	@BeforeEach
	void abrirNavegador() {
		lastName = "Recreio" + UUID.randomUUID().toString().substring(0, 8);
		totalInicial = totalDeProprietarios();
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless=new", "--window-size=1440,900", "--no-sandbox", "--disable-dev-shm-usage",
				"--lang=en-US");
		driver = new ChromeDriver(options);
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	@AfterEach
	void fecharNavegadorELimparDados() {
		try {
			if (driver != null) {
				driver.quit();
			}
		}
		finally {
			jdbc.update("DELETE FROM owners WHERE last_name = ?", lastName);
		}
	}

	@Test
	void cadastrarProprietarioValidoExibePersisteEPodeSerBuscado() throws IOException {
		Map<String, String> dados = Map.of("firstName", "Sofia", "lastName", lastName, "address", "Rua das Flores, 10",
				"city", "Rio de Janeiro", "telephone", "2198765432");
		abrirFormularioPelaInterface();
		preencher(dados);
		int ownerId = enviarEAguardarDetalhes();

		assertThat(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("success-message"))).getText())
			.isEqualTo("New Owner Created");
		verificarDetalhes(dados);
		verificarBanco(ownerId, dados);
		assertThat(totalDeProprietarios()).isEqualTo(totalInicial + 1);
		salvarEvidencia("01-cadastro-valido.png");

		driver.navigate().refresh();
		verificarDetalhes(dados);
		verificarBuscaPorSobrenome(ownerId);
		salvarEvidencia("02-busca-proprietario-cadastrado.png");
	}

	@Test
	void aceitarNomeComTrintaCaracteresNoLimiteDaColuna() throws IOException {
		Map<String, String> dados = Map.of("firstName", "N".repeat(30), "lastName", lastName, "address", "Rua A",
				"city", "Niteroi", "telephone", "0000000000");
		abrirFormularioPelaInterface();
		preencher(dados);
		int ownerId = enviarEAguardarDetalhes();

		verificarDetalhes(dados);
		verificarBanco(ownerId, dados);
		salvarEvidencia("03-nome-30-caracteres.png");
	}

	@ParameterizedTest(name = "Telefone inválido ''{0}'' ({1})")
	@CsvSource({ "219876543, nove-digitos", "21987654321, onze-digitos", "21987a5432, letra", "(21)987654, simbolos" })
	void rejeitarTelefoneForaDoFormatoSemCadastrar(String telefone, String caso) throws IOException {
		Map<String, String> dados = Map.of("firstName", "Sofia", "lastName", lastName, "address", "Rua B", "city",
				"Rio de Janeiro", "telephone", telefone);
		abrirFormularioPelaInterface();
		preencher(dados);
		enviarEAguardarErros();

		assertThat(camposComErro()).containsExactly("telephone");
		assertThat(erroDoCampo("telephone")).isEqualTo(TELEFONE_ERRO);
		verificarValoresPreservados(dados);
		verificarNadaGravado();
		salvarEvidencia("04-telefone-" + caso + ".png");
	}

	@Test
	void rejeitarFormularioVazioIndicandoTodosOsCamposObrigatorios() throws IOException {
		abrirFormularioPelaInterface();
		enviarEAguardarErros();

		assertThat(camposComErro()).containsExactlyInAnyOrder("firstName", "lastName", "address", "city", "telephone");
		for (String campo : List.of("firstName", "lastName", "address", "city")) {
			assertThat(erroDoCampo(campo)).isNotBlank();
		}
		assertThat(erroDoCampo("telephone")).isNotBlank();
		verificarNadaGravado();
		salvarEvidencia("05-formulario-vazio.png");
	}

	@Test
	void rejeitarCamposSomenteComEspacos() throws IOException {
		Map<String, String> dados = Map.of("firstName", "Sofia", "lastName", lastName, "address", "   ", "city", "   ",
				"telephone", "2198765432");
		abrirFormularioPelaInterface();
		preencher(dados);
		enviarEAguardarErros();

		assertThat(camposComErro()).containsExactlyInAnyOrder("address", "city");
		verificarNadaGravado();
		salvarEvidencia("06-campos-com-espacos.png");
	}

	private void abrirFormularioPelaInterface() {
		driver.get(baseUrl() + "/");
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/owners/find']"))).click();
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/owners/new']"))).click();
		wait.until(ExpectedConditions.urlToBe(baseUrl() + "/owners/new"));
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("add-owner-form")));
		assertThat(botaoEnviar().getText()).isEqualTo("Add Owner");
	}

	private void preencher(Map<String, String> dados) {
		dados.forEach((campo, valor) -> {
			WebElement input = driver.findElement(By.id(campo));
			input.clear();
			input.sendKeys(valor);
		});
	}

	private int enviarEAguardarDetalhes() {
		botaoEnviar().click();
		wait.until(
				ExpectedConditions.urlMatches(Pattern.quote(baseUrl() + "/owners/") + "\\d+(?:;jsessionid=[^/?#]+)?$"));
		String path = driver.getCurrentUrl().substring((baseUrl() + "/owners/").length());
		return Integer.parseInt(path.replaceAll(";.*$", ""));
	}

	private void enviarEAguardarErros() {
		WebElement button = botaoEnviar();
		button.click();
		wait.until(ExpectedConditions.stalenessOf(button));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div.has-error")));
		// Erro de validação: o servidor devolve o formulário no mesmo endereço, sem
		// redirecionar.
		assertThat(driver.getCurrentUrl()).isEqualTo(baseUrl() + "/owners/new");
	}

	private WebElement botaoEnviar() {
		return driver.findElement(By.cssSelector("#add-owner-form button[type='submit']"));
	}

	private List<String> camposComErro() {
		return driver.findElements(By.cssSelector("div.has-error input"))
			.stream()
			.map(input -> input.getDomAttribute("id"))
			.toList();
	}

	private String erroDoCampo(String campo) {
		return driver.findElement(By.xpath("//div[contains(@class,'has-error')][.//input[@id='" + campo
				+ "']]//span[contains(@class,'help-inline')]"))
			.getText();
	}

	private void verificarValoresPreservados(Map<String, String> dados) {
		dados.forEach((campo, valor) -> assertThat(driver.findElement(By.id(campo)).getDomProperty("value"))
			.as("valor preservado em %s", campo)
			.isEqualTo(valor));
	}

	private void verificarDetalhes(Map<String, String> dados) {
		// Somente a tabela de dados do proprietário, logo após o título; a de animais vem
		// depois.
		List<WebElement> rows = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
				By.xpath("//h2[normalize-space()='Owner Information']/following-sibling::table[1]//tr")));
		assertThat(valorDaLinha(rows, "Name")).isEqualTo(dados.get("firstName") + " " + dados.get("lastName"));
		assertThat(valorDaLinha(rows, "Address")).isEqualTo(dados.get("address"));
		assertThat(valorDaLinha(rows, "City")).isEqualTo(dados.get("city"));
		assertThat(valorDaLinha(rows, "Telephone")).isEqualTo(dados.get("telephone"));
	}

	private String valorDaLinha(List<WebElement> rows, String titulo) {
		return rows.stream()
			.filter(row -> row.findElement(By.tagName("th")).getText().equals(titulo))
			.map(row -> row.findElement(By.tagName("td")).getText())
			.findFirst()
			.orElseThrow();
	}

	private void verificarBanco(int ownerId, Map<String, String> dados) {
		Map<String, Object> row = jdbc
			.queryForMap("SELECT first_name, last_name, address, city, telephone FROM owners WHERE id = ?", ownerId);
		assertThat(row).containsEntry("FIRST_NAME", dados.get("firstName"))
			.containsEntry("LAST_NAME", dados.get("lastName"))
			.containsEntry("ADDRESS", dados.get("address"))
			.containsEntry("CITY", dados.get("city"))
			.containsEntry("TELEPHONE", dados.get("telephone"));
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM owners WHERE last_name = ?", Integer.class, lastName))
			.isEqualTo(1);
	}

	private void verificarBuscaPorSobrenome(int ownerId) {
		driver.get(baseUrl() + "/owners/find");
		WebElement campo = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("lastName")));
		campo.sendKeys(lastName);
		driver.findElement(By.cssSelector("#search-owner-form button[type='submit']")).click();
		// Um único resultado leva diretamente aos detalhes do proprietário.
		wait.until(ExpectedConditions
			.urlMatches(Pattern.quote(baseUrl() + "/owners/" + ownerId) + "(?:;jsessionid=[^/?#]+)?$"));
	}

	private void verificarNadaGravado() {
		assertThat(totalDeProprietarios()).isEqualTo(totalInicial);
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM owners WHERE last_name = ?", Integer.class, lastName))
			.isZero();
	}

	private int totalDeProprietarios() {
		return jdbc.queryForObject("SELECT COUNT(*) FROM owners", Integer.class);
	}

	private String baseUrl() {
		return "http://localhost:" + port;
	}

	private void salvarEvidencia(String filename) throws IOException {
		Path directory = Path.of(projectDirectory, "target", "selenium-evidence", "cadastrar-proprietario");
		Files.createDirectories(directory);
		Files.write(directory.resolve(filename), ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
	}

}

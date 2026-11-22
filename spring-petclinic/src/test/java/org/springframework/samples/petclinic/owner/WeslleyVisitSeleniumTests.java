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
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

/**
 * Testes de sistema do cadastro de visitas, com navegador, MVC e banco H2 reais. Cada
 * execução cria e remove seu próprio proprietário e animal.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,
		properties = { "spring.docker.compose.lifecycle-management=NONE",
				"spring.datasource.url=jdbc:h2:mem:weslley-visits-selenium;DB_CLOSE_DELAY=-1",
				"spring.datasource.username=sa", "spring.datasource.password=" })
class WeslleyVisitSeleniumTests {

	@LocalServerPort
	private int port;

	@Value("${user.dir}")
	private String projectDirectory;

	@Autowired
	private JdbcTemplate jdbc;

	private WebDriver driver;

	private WebDriverWait wait;

	private Integer ownerId;

	private Integer petId;

	private LocalDate today;

	@BeforeEach
	void prepararDadosEAbrirNavegador() {
		today = LocalDate.now();
		ownerId = new SimpleJdbcInsert(jdbc).withTableName("owners")
			.usingColumns("first_name", "last_name", "address", "city", "telephone")
			.usingGeneratedKeyColumns("id")
			.executeAndReturnKey(Map.of("first_name", "Weslley", "last_name", "Selenium", "address", "Rua de teste",
					"city", "Niteroi", "telephone", "2198765432"))
			.intValue();
		Integer typeId = jdbc.queryForObject("SELECT id FROM types WHERE name = ?", Integer.class, "cat");
		petId = new SimpleJdbcInsert(jdbc).withTableName("pets")
			.usingColumns("name", "birth_date", "type_id", "owner_id")
			.usingGeneratedKeyColumns("id")
			.executeAndReturnKey(Map.of("name", "Animal Selenium", "birth_date",
					java.sql.Date.valueOf(today.minusYears(1)), "type_id", typeId, "owner_id", ownerId))
			.intValue();
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
			if (petId != null) {
				jdbc.update("DELETE FROM visits WHERE pet_id = ?", petId);
				jdbc.update("DELETE FROM pets WHERE id = ?", petId);
			}
			if (ownerId != null) {
				jdbc.update("DELETE FROM owners WHERE id = ?", ownerId);
			}
		}
	}

	@Test
	void cadastrarVisitaValidaExibeEPersisteDataEDescricao() throws IOException {
		verificarCadastro(today.plusDays(1), "Consulta de revisao", "01-visita-valida.png");
	}

	@Test
	void aceitarDescricaoCom255CaracteresSemTruncar() throws IOException {
		verificarCadastro(today.plusDays(3), "V".repeat(255), "02-descricao-255.png");
	}

	@Test
	void rejeitarDescricaoVaziaSemCadastrarVisita() throws IOException {
		verificarDescricaoInvalida("", "03-descricao-vazia.png");
	}

	@Test
	void rejeitarDescricaoSomenteComEspacosSemCadastrarVisita() throws IOException {
		verificarDescricaoInvalida("   ", "04-descricao-espacos.png");
	}

	@Test
	void rejeitarDescricaoCom256CaracteresPreservandoTexto() throws IOException {
		verificarDescricaoInvalida("X".repeat(256), "05-descricao-256.png");
	}

	@ParameterizedTest(name = "Navegador bloqueia data com deslocamento {0}")
	@ValueSource(ints = { -1, 0 })
	void navegadorBloqueiaDataAnteriorAoMinimo(int offset) throws IOException {
		LocalDate date = today.plusDays(offset);
		abrirFormulario(date, "Consulta bloqueada pelo navegador");
		WebElement dateInput = driver.findElement(By.id("date"));
		assertThat(dateInput.getDomAttribute("min")).isEqualTo(today.plusDays(1).toString());
		driver.findElement(By.cssSelector("form button[type='submit']")).click();

		assertThat((Boolean) script().executeScript("return arguments[0].validity.rangeUnderflow;", dateInput))
			.isTrue();
		assertThat(dateInput.getDomProperty("validationMessage")).isNotBlank();
		assertThat(driver.getCurrentUrl()).isEqualTo(visitUrl());
		assertThat(quantidadeDeVisitas()).isZero();
		salvarEvidencia("06-data-navegador-" + offset + ".png");
		verificarAusenciaNaTelaDoProprietario();
	}

	@ParameterizedTest(name = "Servidor rejeita data com deslocamento {0}")
	@ValueSource(ints = { -1, 0 })
	void servidorRejeitaDataInvalidaMesmoSemRestricaoDoNavegador(int offset) throws IOException {
		LocalDate date = today.plusDays(offset);
		String description = "Consulta bloqueada pelo servidor";
		abrirFormulario(date, description);
		// Retira somente o limite do HTML para exercitar a validação do servidor.
		script().executeScript("arguments[0].removeAttribute('min');", driver.findElement(By.id("date")));
		enviarEAguardarErro("date");

		assertThat(erroDoCampo("date").getText()).isEqualTo("Visit date must be in the future");
		assertThat(driver.findElement(By.id("date")).getDomProperty("value")).isEqualTo(date.toString());
		assertThat(driver.findElement(By.id("description")).getDomProperty("value")).isEqualTo(description);
		assertThat(quantidadeDeVisitas()).isZero();
		salvarEvidencia("07-data-servidor-" + offset + ".png");
		verificarAusenciaNaTelaDoProprietario();
	}

	private void verificarCadastro(LocalDate date, String description, String screenshot) throws IOException {
		abrirFormulario(date, description);
		driver.findElement(By.cssSelector("form button[type='submit']")).click();
		wait.until(ExpectedConditions.urlMatches(Pattern.quote(ownerUrl()) + "(?:;jsessionid=[^/?#]+)?$"));
		WebElement success = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("success-message")));
		assertThat(success.getText()).isEqualTo("Your visit has been booked");
		assertThat(quantidadeDeVisitas()).isEqualTo(1);
		assertThat(jdbc.queryForObject("SELECT description FROM visits WHERE pet_id = ?", String.class, petId))
			.isEqualTo(description);
		assertThat(jdbc.queryForObject("SELECT visit_date FROM visits WHERE pet_id = ?", LocalDate.class, petId))
			.isEqualTo(date);
		verificarVisitaNaTela(date, description);
		salvarEvidencia(screenshot);
		driver.navigate().refresh();
		verificarVisitaNaTela(date, description);
	}

	private void verificarDescricaoInvalida(String description, String screenshot) throws IOException {
		LocalDate date = today.plusDays(1);
		abrirFormulario(date, description);
		enviarEAguardarErro("description");
		assertThat(erroDoCampo("description").getText()).isNotBlank();
		assertThat(driver.getCurrentUrl()).isEqualTo(visitUrl());
		assertThat(driver.findElement(By.id("description")).getDomProperty("value")).isEqualTo(description);
		assertThat(driver.findElement(By.id("date")).getDomProperty("value")).isEqualTo(date.toString());
		assertThat(quantidadeDeVisitas()).isZero();
		salvarEvidencia(screenshot);
		verificarAusenciaNaTelaDoProprietario();
	}

	private void abrirFormulario(LocalDate date, String description) {
		driver.get(ownerUrl());
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href$='/visits/new']"))).click();
		wait.until(ExpectedConditions.urlToBe(visitUrl()));
		WebElement dateInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("date")));
		// O valor ISO evita depender do formato regional do controle de data do Chrome.
		script().executeScript(
				"arguments[0].value = arguments[1];"
						+ "arguments[0].dispatchEvent(new Event('change', {bubbles: true}));",
				dateInput, date.toString());
		driver.findElement(By.id("description")).sendKeys(description);
	}

	private void enviarEAguardarErro(String field) {
		WebElement button = driver.findElement(By.cssSelector("form button[type='submit']"));
		button.click();
		wait.until(ExpectedConditions.stalenessOf(button));
		wait.until(ExpectedConditions.visibilityOf(erroDoCampo(field)));
	}

	private WebElement erroDoCampo(String field) {
		return driver.findElement(By.xpath("//div[contains(@class,'has-error')][.//input[@id='" + field
				+ "']]//span[contains(@class,'help-inline')]"));
	}

	private void verificarVisitaNaTela(LocalDate date, String description) {
		List<WebElement> rows = wait
			.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("table.table-condensed tr")));
		assertThat(rows.stream()
			.map(row -> row.findElements(By.tagName("td")))
			.filter(cells -> cells.size() == 2)
			.filter(cells -> cells.get(0).getText().equals(date.toString()))
			.map(cells -> cells.get(1).getText())
			.toList()).containsExactly(description);
	}

	private void verificarAusenciaNaTelaDoProprietario() {
		driver.get(ownerUrl());
		List<WebElement> rows = wait
			.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("table.table-condensed tr")));
		assertThat(rows.stream()
			.map(row -> row.findElements(By.tagName("td")))
			.filter(cells -> cells.size() == 2)
			.filter(cells -> cells.get(0).getText().matches("\\d{4}-\\d{2}-\\d{2}"))
			.toList()).isEmpty();
	}

	private int quantidadeDeVisitas() {
		return jdbc.queryForObject("SELECT COUNT(*) FROM visits WHERE pet_id = ?", Integer.class, petId);
	}

	private String ownerUrl() {
		return "http://localhost:" + port + "/owners/" + ownerId;
	}

	private String visitUrl() {
		return ownerUrl() + "/pets/" + petId + "/visits/new";
	}

	private JavascriptExecutor script() {
		return (JavascriptExecutor) driver;
	}

	private void salvarEvidencia(String filename) throws IOException {
		Path directory = Path.of(projectDirectory, "target", "selenium-evidence", "visitas");
		Files.createDirectories(directory);
		Files.write(directory.resolve(filename), ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
	}

}

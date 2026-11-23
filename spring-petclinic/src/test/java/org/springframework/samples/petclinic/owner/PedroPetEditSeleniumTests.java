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
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,
		properties = { "spring.docker.compose.lifecycle-management=NONE",
				"spring.datasource.url=jdbc:h2:mem:pedro-pet-edit;DB_CLOSE_DELAY=-1", "spring.datasource.username=sa",
				"spring.datasource.password=" })
class PedroPetEditSeleniumTests {

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

	private Integer otherPetId;

	private Integer catTypeId;

	private LocalDate originalDate;

	@BeforeEach
	void prepararDadosEAbrirNavegador() {
		originalDate = LocalDate.now().minusYears(1);
		ownerId = new SimpleJdbcInsert(jdbc).withTableName("owners")
			.usingColumns("first_name", "last_name", "address", "city", "telephone")
			.usingGeneratedKeyColumns("id")
			.executeAndReturnKey(Map.of("first_name", "Pedro", "last_name", "Edicao", "address", "Rua de teste", "city",
					"Niteroi", "telephone", "2198765432"))
			.intValue();
		catTypeId = jdbc.queryForObject("SELECT id FROM types WHERE name = ?", Integer.class, "cat");
		petId = criarAnimal("Original Pedro");
		otherPetId = criarAnimal("Outro Pedro");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless=new", "--window-size=1440,1000", "--lang=en-US");
		driver = new ChromeDriver(options);
		wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	}

	private int criarAnimal(String name) {
		return new SimpleJdbcInsert(jdbc).withTableName("pets")
			.usingColumns("name", "birth_date", "type_id", "owner_id")
			.usingGeneratedKeyColumns("id")
			.executeAndReturnKey(Map.of("name", name, "birth_date", java.sql.Date.valueOf(originalDate), "type_id",
					catTypeId, "owner_id", ownerId))
			.intValue();
	}

	@AfterEach
	void fecharNavegadorELimparDados() {
		try {
			if (driver != null) {
				driver.quit();
			}
		}
		finally {
			if (ownerId != null) {
				jdbc.update("DELETE FROM visits WHERE pet_id IN (SELECT id FROM pets WHERE owner_id = ?)", ownerId);
				jdbc.update("DELETE FROM pets WHERE owner_id = ?", ownerId);
				jdbc.update("DELETE FROM owners WHERE id = ?", ownerId);
			}
		}
	}

	@Test
	void alterarNomeDataETipoPersisteAposRecarregarPagina() throws IOException {
		verificarEdicaoValida("Atualizado Pedro", originalDate.minusDays(1), "dog", "01-edicao-valida.png");
	}

	@Test
	void manterNomeDoProprioAnimalEAceitarNascimentoHoje() throws IOException {
		verificarEdicaoValida("Original Pedro", LocalDate.now(), "cat", "02-nascimento-hoje.png");
	}

	@Test
	void aceitarNomeComTrintaCaracteres() throws IOException {
		verificarEdicaoValida("P".repeat(30), originalDate, "cat", "03-nome-30.png");
	}

	@Test
	void rejeitarNomeVazioSemAlterarAnimal() throws IOException {
		verificarEdicaoInvalida("", originalDate.toString(), "name", "required", "04-nome-vazio.png");
	}

	@Test
	void rejeitarNomeSomenteComEspacos() throws IOException {
		verificarEdicaoInvalida("   ", originalDate.toString(), "name", "required", "05-nome-espacos.png");
	}

	@Test
	void rejeitarNomeDeOutroAnimalDoMesmoProprietario() throws IOException {
		verificarEdicaoInvalida("Outro Pedro", originalDate.toString(), "name", "is already in use",
				"06-nome-duplicado.png");
	}

	@Test
	void rejeitarNascimentoAmanha() throws IOException {
		verificarEdicaoInvalida("Nome valido", LocalDate.now().plusDays(1).toString(), "birthDate", "invalid date",
				"07-data-futura.png");
	}

	@Test
	void rejeitarNascimentoVazio() throws IOException {
		verificarEdicaoInvalida("Nome valido", "", "birthDate", "required", "08-data-vazia.png");
	}

	@Test
	void rejeitarNomeComTrintaEUmCaracteres() throws IOException {
		verificarEdicaoInvalida("P".repeat(31), originalDate.toString(), "name",
				"Name must be no more than 30 characters", "09-nome-31.png");
	}

	private void abrirEPreencher(String name, String birthDate, String type) {
		driver.get(ownerUrl());
		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href$='/pets/" + petId + "/edit']")))
			.click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("name")));
		assertThat(driver.findElement(By.id("name")).getDomProperty("value")).isEqualTo("Original Pedro");
		WebElement nameInput = driver.findElement(By.id("name"));
		nameInput.clear();
		nameInput.sendKeys(name);
		WebElement dateInput = driver.findElement(By.id("birthDate"));
		((JavascriptExecutor) driver).executeScript(
				"arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
				dateInput, birthDate);
		new Select(driver.findElement(By.id("type"))).selectByVisibleText(type);
	}

	private void verificarEdicaoValida(String name, LocalDate birthDate, String type, String screenshot)
			throws IOException {
		abrirEPreencher(name, birthDate.toString(), type);
		driver.findElement(By.cssSelector("form button[type='submit']")).click();
		wait.until(ExpectedConditions.urlMatches(Pattern.quote(ownerUrl()) + "(?:;jsessionid=[^/?#]+)?$"));
		assertThat(wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("success-message"))).getText())
			.isEqualTo("Pet details has been edited");
		verificarAnimalNoBanco(name, birthDate, type);
		verificarAnimalNaTela(name, birthDate, type);
		salvarEvidencia(screenshot);
		driver.navigate().refresh();
		verificarAnimalNaTela(name, birthDate, type);
		verificarOutroAnimalIntacto();
	}

	private void verificarEdicaoInvalida(String name, String birthDate, String field, String message, String screenshot)
			throws IOException {
		abrirEPreencher(name, birthDate, "dog");
		WebElement button = driver.findElement(By.cssSelector("form button[type='submit']"));
		button.click();
		By error = By.xpath("//div[contains(@class,'has-error')][.//input[@id='" + field
				+ "']]//span[contains(@class,'help-inline')]");
		assertThat(wait.until(ExpectedConditions.visibilityOfElementLocated(error)).getText()).contains(message);
		assertThat(driver.getCurrentUrl()).contains("/pets/" + petId + "/edit");
		verificarAnimalNoBanco("Original Pedro", originalDate, "cat");
		verificarOutroAnimalIntacto();
		salvarEvidencia(screenshot);
	}

	private void verificarAnimalNoBanco(String name, LocalDate date, String type) {
		assertThat(jdbc.queryForObject("SELECT name FROM pets WHERE id = ?", String.class, petId)).isEqualTo(name);
		assertThat(jdbc.queryForObject("SELECT birth_date FROM pets WHERE id = ?", LocalDate.class, petId))
			.isEqualTo(date);
		assertThat(jdbc.queryForObject("SELECT t.name FROM types t JOIN pets p ON p.type_id = t.id WHERE p.id = ?",
				String.class, petId))
			.isEqualTo(type);
		assertThat(jdbc.queryForObject("SELECT owner_id FROM pets WHERE id = ?", Integer.class, petId))
			.isEqualTo(ownerId);
		assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM pets WHERE owner_id = ?", Integer.class, ownerId))
			.isEqualTo(2);
	}

	private void verificarAnimalNaTela(String name, LocalDate date, String type) {
		WebElement link = wait
			.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href$='/pets/" + petId + "/edit']")));
		List<String> values = link.findElement(By.xpath("ancestor::tr[td/dl]"))
			.findElements(By.cssSelector("dl dd"))
			.stream()
			.map(WebElement::getText)
			.toList();
		assertThat(values).containsExactly(name, date.toString(), type);
	}

	private void verificarOutroAnimalIntacto() {
		assertThat(jdbc.queryForObject("SELECT name FROM pets WHERE id = ?", String.class, otherPetId))
			.isEqualTo("Outro Pedro");
		assertThat(jdbc.queryForObject("SELECT birth_date FROM pets WHERE id = ?", LocalDate.class, otherPetId))
			.isEqualTo(originalDate);
		assertThat(jdbc.queryForObject("SELECT type_id FROM pets WHERE id = ?", Integer.class, otherPetId))
			.isEqualTo(catTypeId);
	}

	private String ownerUrl() {
		return "http://localhost:" + port + "/owners/" + ownerId;
	}

	private void salvarEvidencia(String filename) throws IOException {
		Path directory = Path.of(projectDirectory, "target", "selenium-evidence", "alterar-animal");
		Files.createDirectories(directory);
		Files.write(directory.resolve(filename), ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
	}

}

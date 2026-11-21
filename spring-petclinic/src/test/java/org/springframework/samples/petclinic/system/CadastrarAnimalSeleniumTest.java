package org.springframework.samples.petclinic.system;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.samples.petclinic.PetClinicApplication;
import org.springframework.samples.petclinic.owner.Owner;
import org.springframework.samples.petclinic.owner.OwnerRepository;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(classes = PetClinicApplication.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CadastrarAnimalSeleniumTest {

	@LocalServerPort
	private int port;

	@Autowired
	private OwnerRepository ownerRepository;

	private WebDriver driver;

	private WebDriverWait wait;

	@BeforeEach
	void prepararCenario() {
		Owner proprietario = new Owner();
		proprietario.setFirstName("lucio");
		proprietario.setLastName("teste");
		proprietario.setAddress("rua neves, 100");
		proprietario.setCity("niteroi");
		proprietario.setTelephone("2100000000");

		ownerRepository.saveAndFlush(proprietario);

		driver = new ChromeDriver();
		wait = new WebDriverWait(driver, Duration.ofSeconds(15));

		driver.manage().window().maximize();
	}

	@AfterEach
	void fecharNavegador() {
		if (driver != null) {
			driver.quit();
		}
	}

	@Test
	void deveCadastrarAnimalParaProprietarioExistente() {

		driver.get("http://localhost:" + port);
		pausar(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a[href='/owners/find']"))).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("lastName")));

		pausar(1500);

		WebElement campoSobrenome = driver.findElement(By.id("lastName"));
		campoSobrenome.sendKeys("teste");

		pausar(1000);

		driver.findElement(By.cssSelector("#search-owner-form button[type='submit']")).click();

		wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "lucio teste"));

		pausar(2000);

		wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Add New Pet"))).click();

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));

		pausar(2000);

		driver.findElement(By.id("name")).sendKeys("mel");
		pausar(1000);

		WebElement campoData = driver.findElement(By.id("birthDate"));

		((JavascriptExecutor) driver).executeScript("arguments[0].value = '2023-06-14';", campoData);

		pausar(1000);

		Select campoTipo = new Select(driver.findElement(By.id("type")));
		campoTipo.selectByVisibleText("dog");

		pausar(2500);

		driver.findElement(By.cssSelector("button[type='submit']")).click();

		wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "mel"));

		String resultado = driver.findElement(By.tagName("body")).getText();

		assertAll(() -> assertTrue(resultado.contains("lucio teste"), "O nome do proprietário não foi apresentado."),

				() -> assertTrue(resultado.contains("mel"), "O nome do animal não foi apresentado."),

				() -> assertTrue(resultado.contains("2023-06-14"), "A data de nascimento não foi apresentada."),

				() -> assertTrue(resultado.toLowerCase().contains("dog"), "O tipo do animal não foi apresentado."));

		pausar(5000);
	}

	private void pausar(long milissegundos) {
		try {
			Thread.sleep(milissegundos);
		}
		catch (InterruptedException e) {
			Thread.currentThread().interrupt();

			throw new IllegalStateException("A execução do teste foi interrompida.", e);
		}
	}

}
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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

/**
 * Teste de sistema do fluxo de busca de proprietário pelo navegador.
 *
 * <p>
 * A aplicação é iniciada de verdade e o Selenium interage com a página renderizada,
 * incluindo o formulário, o controlador MVC, o banco de teste e o HTML de resposta.
 * </p>
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,
		properties = "spring.docker.compose.lifecycle-management=NONE")
class LucasOwnerSeleniumTests {

	@LocalServerPort
	private int port;

	@Value("${user.dir}")
	private String projectDirectory;

	private WebDriver driver;

	@BeforeEach
	void abrirNavegador() {
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless=new", "--window-size=1440,900", "--no-sandbox", "--disable-dev-shm-usage");
		driver = new ChromeDriver(options);
	}

	@AfterEach
	void fecharNavegador() {
		if (driver != null) {
			driver.quit();
		}
	}

	@Test
	void buscarDavisPelaTelaExibeOsProprietariosEncontrados() throws IOException {
		driver.get("http://localhost:" + port + "/owners/find");

		driver.findElement(By.name("lastName")).sendKeys("Davis");
		driver.findElement(By.cssSelector("#search-owner-form button[type='submit']")).click();

		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		wait.until(ExpectedConditions.presenceOfElementLocated(By.id("owners")));

		String page = driver.findElement(By.id("owners")).getText();
		assertThat(page).contains("Betty Davis", "Harold Davis");
		salvarEvidencia();
	}

	private void salvarEvidencia() throws IOException {
		Path evidenceDirectory = Path.of(projectDirectory, "target", "selenium-evidence");
		Files.createDirectories(evidenceDirectory);
		Path screenshot = evidenceDirectory.resolve("01-busca-davis-resultados.png");
		Files.write(screenshot, ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
	}

}

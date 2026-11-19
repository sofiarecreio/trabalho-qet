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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.samples.petclinic.PetClinicApplication;
import org.springframework.web.client.RestTemplate;

/**
 * Testes de integração do fluxo de busca e consulta de proprietários.
 *
 * <p>
 * Os testes executam a aplicação completa com banco H2, repositório JPA, controlador MVC
 * e renderização Thymeleaf, sem substituir essas dependências por mocks.
 * </p>
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,
		properties = "spring.docker.compose.lifecycle-management=NONE")
class LucasOwnerIntegrationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private RestTemplateBuilder restTemplateBuilder;

	@Test
	void buscarDavisRetornaOsDoisProprietariosDaBaseDeTeste() {
		ResponseEntity<String> response = client().getForEntity("/owners?lastName=Davis", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("Betty", "Harold", "Davis");
	}

	@Test
	void consultarBettyDavisRetornaDetalhesEAnimalAssociado() {
		ResponseEntity<String> response = client().getForEntity("/owners/2", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).contains("Betty", "Davis", "Basil");
	}

	private RestTemplate client() {
		return restTemplateBuilder.baseUri("http://localhost:" + port).build();
	}

	public static void main(String[] args) {
		SpringApplication.run(PetClinicApplication.class, "--spring.docker.compose.lifecycle-management=NONE");
	}

}

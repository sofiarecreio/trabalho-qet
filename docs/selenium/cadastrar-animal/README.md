# Teste de sistema — cadastro de animal

Responsável: Lucio Diniz Araujo Martelo Junior. Execução: 20/11/2026.

Implementação: [CadastrarAnimalSeleniumTest.java](../../../spring-petclinic/src/test/java/org/springframework/samples/petclinic/system/CadastrarAnimalSeleniumTest.java).

O teste inicia o Spring PetClinic em uma porta aleatória, cria um proprietário para o cenário e utiliza um navegador Chrome real para executar o fluxo de cadastro. Pela interface, o teste pesquisa o proprietário, acessa **Add New Pet**, preenche os dados do animal, envia o formulário e verifica os dados apresentados na página final.

## Caso executado

| Caso | Pré-condição | Entrada | Resultado verificado | Evidência |
| --- | --- | --- | --- | --- |
| CTM-03 — cadastrar animal | Proprietário `lucio teste` previamente criado | Nome `mel`, nascimento `2023-06-14` e tipo `dog` | Página final com mensagem de cadastro; asserções confirmam proprietário, nome, nascimento e tipo do animal | [Animal cadastrado](01-animal-cadastrado.png) |

O cenário representa a partição válida da funcionalidade: proprietário existente e todos os campos obrigatórios preenchidos com valores aceitos. As asserções conferem o nome do proprietário, o nome do animal, a data de nascimento e o tipo selecionado.

## Fluxo automatizado

1. Inicia a aplicação Spring PetClinic em uma porta aleatória.
2. Cria o proprietário `lucio teste` por meio do repositório da aplicação.
3. Abre a página inicial no Chrome.
4. Acessa **Find Owners** e pesquisa o sobrenome `teste`.
5. Abre **Add New Pet**.
6. Preenche o nome `mel`, a data `2023-06-14` e o tipo `dog`.
7. Envia o formulário.
8. Confere os dados exibidos na página do proprietário.

## Ambiente e independência

- Spring Boot 4.1.0 e servidor real em porta aleatória.
- Java 17.0.12.
- Selenium WebDriver com Chrome 158.
- Banco H2 em memória criado durante a execução.
- Proprietário próprio para o cenário, criado antes da interação pelo navegador.
- Um `ChromeDriver` criado para o teste e encerrado após a execução.
- Esperas explícitas de até 15 segundos para elementos e navegação, com pausas auxiliares para acompanhar visualmente o fluxo.

O teste não depende de um proprietário previamente cadastrado manualmente, de uma porta fixa ou de dados persistentes de outra execução.

## Execução e resultado

Na pasta `spring-petclinic`, usando o Prompt de Comando do Windows:

```text
mvnw.cmd -Dtest=CadastrarAnimalSeleniumTest test
```

Resultado obtido:

| Testes executados | Falhas | Erros | Ignorados | Build |
| ---: | ---: | ---: | ---: | --- |
| 1 | 0 | 0 | 0 | **BUILD SUCCESS** |

Evidência da execução: [resultado no terminal](02-build-success.png).

Durante a execução foi apresentado um aviso de compatibilidade do Chrome DevTools Protocol, mas ele não impediu a inicialização do navegador nem a aprovação do teste.

## Evidências

- [Página final com o animal cadastrado](01-animal-cadastrado.png)
- [Execução aprovada no Maven](02-build-success.png)

As capturas documentam a execução final do cenário CTM-03 e devem permanecer na mesma pasta deste `README.md`.

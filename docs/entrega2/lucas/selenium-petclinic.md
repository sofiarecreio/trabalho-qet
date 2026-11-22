# Entrega 2 — Lucas: teste de sistema com Selenium

## Escopo

Foi criado um teste de sistema para o fluxo de busca de proprietário do PetClinic.
O teste inicia a aplicação Spring Boot em uma porta aleatória e usa o Chrome em modo
headless para executar as mesmas ações de um usuário:

1. acessar a tela de busca de proprietários;
2. preencher o sobrenome `Davis`;
3. enviar o formulário;
4. verificar na tabela da página que `Betty Davis` e `Harold Davis` foram encontrados.

Esse cenário atravessa a interface HTML, o controlador MVC, o serviço, o repositório,
o banco H2 de teste e a renderização Thymeleaf. Portanto, ele complementa o teste de
integração HTTP, que não interage com os elementos visuais da página.

## Implementação

O código está em:

```text
spring-petclinic/src/test/java/org/springframework/samples/petclinic/owner/LucasOwnerSeleniumTests.java
```

Foi adicionada a dependência de teste `selenium-java` 4.43.0. O Selenium Manager
localiza o ChromeDriver automaticamente. O navegador é executado com `--headless=new`
para que o teste possa rodar sem abrir uma janela, e o `WebDriverWait` aguarda a tabela
de resultados antes das verificações.

Ao final do cenário, o teste salva uma evidência em:

```text
spring-petclinic/target/selenium-evidence/01-busca-davis-resultados.png
```

A pasta `target` é gerada pelo Maven e não deve ser versionada.

## Execução

No diretório `spring-petclinic`:

```bash
mvn -Dtest=LucasOwnerSeleniumTests test
```

O resultado esperado é `BUILD SUCCESS` com o teste aprovado.

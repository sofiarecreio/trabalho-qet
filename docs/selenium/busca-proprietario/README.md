# Teste de sistema — buscar proprietário

Responsável: Lucas Ardelino.

O cenário automatizado utiliza Chrome headless e a aplicação PetClinic iniciada de
verdade com Spring Boot e banco H2. O teste acessa a tela de busca, informa o
sobrenome `Davis`, envia o formulário e confirma que `Betty Davis` e `Harold Davis`
aparecem na tabela de resultados.

## Execução

```bash
mvn -Dtest=LucasOwnerSeleniumTests test
```

Resultado: **1 teste aprovado**, sem falhas ou erros.

## Evidência

- [Captura do resultado da busca](01-busca-davis-resultados.png)

O código do teste está em
[`LucasOwnerSeleniumTests.java`](../../../spring-petclinic/src/test/java/org/springframework/samples/petclinic/owner/LucasOwnerSeleniumTests.java).

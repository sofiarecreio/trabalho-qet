# Entrega 2 — Lucas: testes de integração

## Escopo

Foi criado o teste `LucasOwnerIntegrationTests` para validar o fluxo de proprietários do PetClinic com a aplicação completa em execução.

O cenário usa o banco H2 carregado pelos dados de teste da aplicação e percorre as camadas reais de:

- servidor Spring Boot;
- controlador MVC de proprietários;
- repositório JPA;
- banco H2;
- renderização das páginas Thymeleaf.

Não são usados mocks nesse teste. A finalidade é verificar se as partes funcionam juntas.

## Cenários

1. Buscar `Davis` em `/owners?lastName=Davis` e confirmar que Betty e Harold Davis aparecem na resposta.
2. Acessar `/owners/2` e confirmar que os detalhes de Betty Davis e o animal Basil aparecem na página.

## Execução

```bash
mvn -Dtest=LucasOwnerIntegrationTests test
```

O teste usa uma porta aleatória e desativa somente o ciclo automático do Docker Compose, mantendo o banco H2 local da aplicação.

## Resultado verificado

Em 22/11/2026, os dois cenários foram executados com Maven e terminaram com
`BUILD SUCCESS`: 2 testes aprovados, sem falhas, erros ou testes ignorados.

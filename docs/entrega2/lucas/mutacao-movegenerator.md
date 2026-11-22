# Entrega 2 — Lucas: teste de mutação do MoveGenerator

## Ferramenta e configuração

Foi configurado o PIT Mutation Testing 1.17.4 no `chesslib/pom.xml`. A análise fica restrita à classe `MoveGenerator` e aos testes `LucasMoveGeneratorTest` e `MoveGeneratorTest`.

O Maven foi configurado com limite mínimo de 80% por meio de `mutationThreshold`. Assim, uma execução abaixo desse valor termina com falha.

## Execução

```bash
mvn -Djacoco.skip=true -DtimestampedReports=false -DwithHistory=false \
  org.pitest:pitest-maven:1.17.4:mutationCoverage
```

## Resultado

| Indicador | Resultado |
|---|---:|
| Mutações geradas | 78 |
| Mutações mortas pelos testes | 70 |
| Escore de mutação apresentado pelo PIT | **90%** |
| Limite exigido/configurado | **80%** |

Resultado da execução: **BUILD SUCCESS**.

Além da contagem de mutações, o PIT indicou 5 mutações sem cobertura, 3 sobreviventes e 6 encerradas por erro de memória do processo mutante. Mesmo considerando essas ocorrências no cálculo do PIT, o escore final ficou acima do mínimo de 80%. O relatório detalhado é gerado em `chesslib/target/pit-reports/index.html` e não precisa ser versionado porque fica dentro de `target`.

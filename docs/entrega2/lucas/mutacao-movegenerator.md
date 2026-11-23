# Entrega 2 — Lucas: teste de mutação do MoveGenerator

## Ferramenta e configuração

Foi configurado o PIT Mutation Testing 1.17.4 no `chesslib/pom.xml`. A análise fica restrita à classe `MoveGenerator` e aos testes `LucasMoveGeneratorTest` e `MoveGeneratorTest`.

O Maven foi configurado com limite mínimo de 80% por meio de `mutationThreshold`. Assim, uma execução abaixo desse valor termina com falha.

## Execução

```bash
mvn -Djacoco.skip=true -DtimestampedReports=false -DwithHistory=false \
  org.pitest:pitest-maven:1.17.4:mutationCoverage
```

## Resultado da execução focada

| Indicador | Resultado |
|---|---:|
| Mutações geradas | 78 |
| Mutações mortas pelos testes | 70 |
| Escore de mutação apresentado pelo PIT | **90%** |
| Limite exigido/configurado | **80%** |

Resultado da execução: **BUILD SUCCESS**.

Além da contagem de mutações, o PIT indicou 5 mutações sem cobertura, 3 sobreviventes e 6 encerradas por erro de memória do processo mutante. Mesmo considerando essas ocorrências no cálculo do PIT, o escore final ficou acima do mínimo de 80%. O relatório detalhado é gerado em `chesslib/target/pit-reports/index.html` e não precisa ser versionado porque fica dentro de `target`.

## Resultado estrito usado na consolidação do grupo

Na medição consolidada, o grupo separou mutantes realmente mortos dos encerramentos
por erro de execução. O resultado estrito do `MoveGenerator` foi:

| Indicador | Resultado |
|---|---:|
| Mutantes mortos (`KILLED`) | 64 de 78 |
| Sobreviventes | 3 |
| Sem cobertura | 5 |
| Erros de memória | 4 |
| Timeouts | 2 |
| Escore estrito | **82,05%** |

O valor de 82,05% é o que deve aparecer na tabela final, pois considera somente
`KILLED / total`. Ele continua acima do mínimo de 80%. O PIT apresentou 90% em uma
execução anterior porque classificou ocorrências de execução como detecções; os
estados foram mantidos separados para evitar misturar as métricas.

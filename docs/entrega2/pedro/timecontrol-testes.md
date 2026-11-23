# TimeControl — testes, cobertura e mutação

Responsável pela atividade: Pedro Piaes. Medição local: 22/11/2026.

## Escopo e ambiente

A suíte `PedroTimeControlTest` foi ampliada de cinco para **23 testes**. O código de produção de `TimeControl` foi preservado. A base de trabalho é a main no commit `4750daa`, já com a integração da entrega de Lucio.

Ambiente: macOS ARM64, OpenJDK 21.0.11, Maven 3.9.15, JUnit 4.13.1, JaCoCo 0.8.12 e PIT 1.17.4. O Maven foi executado explicitamente com JDK 21, pois sua configuração padrão nesta máquina selecionava JDK 25.

## Casos e técnicas

| Grupo | Cenários e verificações |
| --- | --- |
| Formatos válidos | Segundos simples, incremento com `+` e `\|`, movimentos por tempo, dois e três estágios; conferência de tipo, unidades, componentes e ordem |
| Limites | Tempo e incremento zero, menor valor inteiro positivo, um movimento, frações de segundo via setters e truncamento de minutos |
| Controle desconhecido | `?` e `-`, inclusive com componentes previamente preenchidos |
| Entradas inválidas | Texto não numérico, entrada vazia e número acima do intervalo de `int`; exceção esperada |
| Estado | Instâncias independentes, lista de estágios própria, profundidade e quantidade de nós |
| Representação | PGN e texto legível com valores esperados completos, componentes negativos configurados diretamente e composição de estágios |
| Isolamento | Um stub de `MovePerTime` retorna notações controladas para verificar a delegação e composição de `TimeControl`; demais casos do parser não dependem de arquivos, banco ou rede |

Foram utilizadas partições de equivalência e análise de valores-limite. Valores negativos e fracionários inseridos por setters caracterizam o comportamento atual da representação; não são apresentados como formatos PGN válidos. Os casos com `MovePerTime` real complementam o teste isolado de composição.

## Resultados

| Indicador | Contagem | Percentual |
| --- | ---: | ---: |
| Testes da suíte | 23 aprovados, zero falhas/erros/ignorados | 100% |
| Branches de TimeControl | 40/42 | 95,24% |
| Linhas de TimeControl | 97/99 | 97,98% |
| Instruções | 412/424 | 97,17% |
| Mutantes mortos | 60/69 | 86,96% |
| Mutantes sobreviventes | 7 | — |
| Mutantes sem cobertura | 2 | — |

A primeira regressão com a suíte ampliada executou **333 testes**, sem falhas, erros ou ignorados; a base anterior tinha 315. Após os oito testes da inspeção de PgnHolder, a regressão integrada de Pedro totalizou **341 testes aprovados**. A cobertura permaneceu em 40/42 branches na execução completa.

O PIT não registrou erros, timeouts ou mutantes não viáveis nesta execução. Todos os 69 mutantes permanecem no denominador. A força dos testes é uma métrica distinta e não substitui o escore de mutação.

O contador de complexidade ciclomática agregado do JaCoCo é **41** (39 cobertos e 2 não cobertos), incluindo a contribuição dos métodos. Isso identifica a métrica utilizada e não afirma que um método isolado possui complexidade 41. O maior método é `toPGNString`, com complexidade 7 pela mesma ferramenta. Se a disciplina exigir complexidade mínima por método, em vez de por classe, essa diferença precisa ser considerada.

O contador BRANCH do JaCoCo é usado como evidência estrutural, seguindo a convenção adotada pelo grupo. Ele mede decisões condicionais e não enumera todas as arestas de um grafo de fluxo, especialmente os caminhos de exceção.

## Caminhos e mutantes restantes

Os dois branches não cobertos estão em `parseTM` e `parseMT`. O parser público prioriza `/` antes de `+`, e `parseMT` converte o campo de segundos com `Integer.parseInt` antes de verificar se ele contém incremento. Isso limita o acesso aos caminhos de controles combinados. A ampliação não recorre a reflexão para exercitar métodos privados nem altera a produção apenas para aumentar a cobertura.

Os sete sobreviventes são mudanças de fronteira nas condições de detecção de separadores e nas verificações de listas de estágios. Eles não foram descartados como equivalentes. Permanecem como limites conhecidos, embora a meta mínima de 80% esteja superada.

## Execução reproduzível

Na raiz do repositório, com JDK 21 selecionado:

```sh
mvn -B -f chesslib/pom.xml -Ptimecontrol-quality -Dtest=PedroTimeControlTest -Dmaven.javadoc.skip=true verify
mvn -B -f chesslib/pom.xml -Ptimecontrol-quality -Dmaven.javadoc.skip=true verify
mvn -B -f chesslib/pom.xml -Ptimecontrol-quality -Djacoco.skip=true test-compile org.pitest:pitest-maven:1.17.4:mutationCoverage
```

O primeiro comando mede a suíte individual. O segundo executa a regressão completa e verifica a meta de branches. O terceiro restringe o PIT à `TimeControl` e à suíte `PedroTimeControlTest`.

Relatórios locais: `chesslib/target/site/jacoco-timecontrol/index.html` e `chesslib/target/pit-reports-timecontrol/index.html`. O perfil grava os relatórios em diretórios próprios e preserva os perfis dos demais integrantes.

Evidências versionáveis desta medição: [JaCoCo XML](evidencias/timecontrol-jacoco.xml) e [PIT XML](evidencias/timecontrol-mutacoes.xml). O XML do PIT permite conferir cada estado de mutante, teste que o detectou e localização no código.

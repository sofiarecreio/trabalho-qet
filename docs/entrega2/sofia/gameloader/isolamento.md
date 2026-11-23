# GameLoader — testes unitários isolados

Responsável: Sofia Recreio. Execução local: 23/11/2026, sobre a main `441aa73` com as alterações desta contribuição. Ferramentas: Java 21.0.12, JUnit 4, JaCoCo 0.8.12, PIT 1.17.4.

## Problema

Os 14 casos de [GameLoaderTest](../../../../chesslib/src/test/java/com/github/bhlangonijr/chesslib/GameLoaderTest.java) usam componentes reais: cada PGN passa pela interpretação de lances de `Game` (Board, MoveList e SAN). Uma falha nesses componentes derrubaria os testes de GameLoader, e decisões do próprio carregador ficavam difíceis de observar — por exemplo, quantas linhas ele consome ou o que entrega para a interpretação de lances.

## Isolamento

GameLoader criava a partida internamente com `new Game(...)`. Foi adicionado um ponto de injeção mínimo:

- `loadNextGame(Iterator<String>)` (público) mantém a assinatura e o comportamento; apenas delega.
- `loadNextGame(Iterator<String>, Function<Round, Game>)` (visível só no pacote) recebe a fábrica da partida.

[GameLoaderIsolatedTest](../../../../chesslib/src/test/java/com/github/bhlangonijr/chesslib/pgn/GameLoaderIsolatedTest.java) usa dois stubs escritos à mão, no mesmo estilo do stub de Board em `GameIsolatedTest`:

| Dependência substituída | Stub | O que permite verificar |
| --- | --- | --- |
| `Game` (e, por ela, Board, MoveList e SAN) | `GameStub`: registra o texto recebido em `loadMoveText`, devolve uma quantidade fixa de lances e pode lançar uma falha configurada | Texto entregue para interpretação, cálculo de PlyCount, empacotamento de falhas, interpretação antecipada antes de uma nova tag Event |
| Fonte de linhas (arquivo PGN) | `RecordingIterator`: conta as linhas consumidas e pode falhar numa linha escolhida | Que o carregador não consome a próxima partida e que falhas de leitura não são convertidas em `PgnException` |

## Casos (12)

| # | Caso | Técnica / partição |
| --- | --- | --- |
| 1 | Texto dos lances entregue sem o marcador de resultado | Funcional — caso válido |
| 2 | Os quatro marcadores (`1-0`, `0-1`, `1/2-1/2`, `*`) são removidos | Partição dos resultados |
| 3 | PlyCount vem da quantidade de lances interpretados, não do cabeçalho | Regra de sobrescrita |
| 4 | Falha da interpretação vira `PgnException` com rodada, evento e causa | Caso de erro |
| 5 | A partida devolvida é a da fábrica, ligada à rodada e ao evento | Integração interna do carregador |
| 6 | Não consome linhas da partida seguinte | Contrato de consumo do iterador |
| 7 | Resultado dentro de comentário não encerra a partida | Valor de fronteira do parser de comentários |
| 8 | Tag Event depois de lances pendentes interpreta os lances antes | Decisão `moveTextParsing && halfMoves vazio` — verdadeiro |
| 9 | Tag Event não reinterpreta quando já há lances | Mesma decisão — falso |
| 10 | Falha do iterador não é convertida em `PgnException` | Caso de erro fora do bloco protegido |
| 11 | Iterador vazio não cria partida | Valor limite (zero linhas) |
| 12 | Só linhas em branco não produzem partida | Partição de entrada sem conteúdo |

Os casos 8 e 9 cobrem a decisão que deixava três mutantes vivos ou sem cobertura na medição anterior.

## Resultados

| Medida | Antes do isolamento (22–23/11) | Depois |
| --- | ---: | ---: |
| Testes de GameLoader | 14 | 26 (14 + 12 isolados) |
| Regressão completa do Chesslib | 349 | **361**, zero falhas, erros ou ignorados |
| Branches de GameLoader (regressão completa) | 60/68 = 88,24% | **63/68 = 92,65%** |
| Mutação (PIT, escore estrito) | 71/75 = 94,67% | **76/77 = 98,70%** |
| Apontamentos Sonar (comprehensive, 608 regras) | 0 | **0** |

O total de mutantes passou de 75 para 77 por causa do novo método de delegação. O único sobrevivente troca `round < 1` por `round <= 1` em `setPlayerDetails`: com rodada 1 os dois lados atribuem 1, então o mutante é equivalente e permanece no denominador.

O perfil `gameloader-quality` passou a incluir `GameLoaderIsolatedTest` nos testes do PIT. Os demais perfis não foram alterados.

Evidências: [PIT](evidencias/mutations-isolamento.xml), [JaCoCo](evidencias/jacoco-isolamento.xml), [regressão](evidencias/regressao-isolamento.txt), [Sonar](evidencias/sonar-isolamento.json).

## Reprodução

```text
mvn -B -f chesslib/pom.xml -Pgameloader-quality "-Dmaven.javadoc.skip=true" verify
mvn -B -f chesslib/pom.xml -Pgameloader-quality test-compile org.pitest:pitest-maven:mutationCoverage
```

## Compatibilidade

A assinatura pública não mudou. A nova sobrecarga é visível só no pacote `pgn` e existe para os testes; `PgnIterator` e os demais consumidores continuam usando o método público.

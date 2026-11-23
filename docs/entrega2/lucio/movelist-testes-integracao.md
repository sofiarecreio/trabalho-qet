# MoveList — testes, integração e resultados da Entrega 2

Responsável: Lucio Diniz Araujo Martelo Junior. Atualização: 22/11/2026.

Este relatório consolida os testes, as medições de cobertura estrutural e os resultados do teste de mutação da classe `MoveList`. A configuração reproduzível foi adicionada ao perfil Maven `movelist-quality`, preservando a configuração padrão já existente para `MoveGenerator`.

## Escopo e suítes

| Suíte | Casos | Objetivo |
| --- | ---: | --- |
| [MoveListTest](../../../chesslib/src/test/java/com/github/bhlangonijr/chesslib/move/MoveListTest.java) | 12 | Testes originais de conversão, carregamento, notações SAN/FAN, PGN, ambiguidades e roque com xeque-mate |
| [MoveListQetTest](../../../chesslib/src/test/java/com/github/bhlangonijr/chesslib/move/MoveListQetTest.java) | 17 | Cenários adicionais para regras especiais, cópia, operações da lista, limites e caminhos excepcionais |
| **Total das duas suítes** | **29** | Casos diretamente associados à `MoveList` |

Na medição individual da `MoveList`, a regressão completa do Chesslib executou **248 testes**, sem falhas, erros ou testes ignorados. Essa execução completa foi utilizada para a medição de cobertura, pois outras suítes do projeto também exercitam caminhos da `MoveList`.

Após a integração da contribuição à versão atual da `main`, a regressão completa foi executada novamente e apresentou **342 testes aprovados**, sem falhas, erros ou testes ignorados. Os perfis `movelist-quality` e `game-quality` também foram executados separadamente e terminaram com `BUILD SUCCESS`.

## Técnicas e casos funcionais

Os testes verificam os resultados por meio de asserções sobre tamanho da lista, movimentos convertidos, notações SAN/FAN, posições FEN, valores retornados e exceções esperadas.

| Grupo | Entradas e cenários | Resultado esperado |
| --- | --- | --- |
| Movimentos básicos | Movimento simples de peão, captura e sequências SAN | Movimentos adicionados na ordem correta e conversão coerente |
| Ambiguidade | Cavalos e torres que podem alcançar a mesma casa | Identificação correta da peça pela coluna ou fileira |
| Regras especiais | Promoção, roque pequeno, roque grande e captura en passant | Movimento interno, SAN e FEN compatíveis com a regra aplicada |
| Notações | Conversões SAN, FAN e texto coordenado, com e sem numeração | Representação gerada sem perda ou alteração da sequência |
| Partidas extensas | Três sequências PGN longas | FEN final correspondente à posição esperada |
| Cópia | Lista com e sem notações previamente calculadas | Estado, movimentos e notações preservados conforme o contrato |
| Operações da coleção | `add`, `addAll`, remoções, limpeza, índice e lista vazia | Valores de retorno, tamanho e conteúdo corretos |
| Limites | Criação de lista a partir de índice interno ou acima do tamanho | Nova lista iniciada na posição FEN adequada |
| Entradas inválidas | SAN ilegal e movimento incompatível com a posição | Lançamento de `MoveConversionException` |
| Xeque e xeque-mate | Sequência encerrada por roque grande com mate | Último movimento representado como `O-O-O#` |

Embora as suítes sejam centradas na `MoveList`, os cenários de SAN, FAN, FEN e regras especiais utilizam componentes reais da Chesslib para interpretar e validar os movimentos.

## Cobertura estrutural com JaCoCo

O perfil `movelist-quality` restringe o relatório e a regra de qualidade à classe `MoveList`.

| Indicador | Resultado |
| --- | ---: |
| Instruções | 1.340/1.515 = **88%** |
| Branches | 159/196 = **81%** |
| Linhas | 343/388 = **88%** |
| Limite configurado | **80% de branches** |
| Resultado da verificação | **Aprovado — BUILD SUCCESS** |

A cobertura de branches superou o mínimo exigido. O relatório HTML é gerado localmente em `chesslib/target/site/jacoco-movelist/index.html`.

## Teste de mutação com PIT

O PIT foi configurado para alterar somente a classe `MoveList` e utilizar `MoveListTest` e `MoveListQetTest` como suítes-alvo.

| Indicador | Resultado |
| --- | ---: |
| Cobertura de linhas das classes mutadas | 342/388 = **81%** |
| Mutantes mortos | 169/211 = **80,09%** |
| Força dos testes | 169/185 = **91%** |
| Mutantes sem cobertura | 26 |
| Mutantes sobreviventes | 17 |
| Limite configurado | **80% de mutação** |
| Resultado da verificação | **Aprovado — BUILD SUCCESS** |

O resultado demonstra que as suítes detectaram a maior parte das alterações artificiais introduzidas pelo PIT. Os mutantes sobreviventes e sem cobertura permanecem contabilizados no resultado. O relatório HTML é gerado localmente em `chesslib/target/pit-reports-movelist/index.html`.

## Configuração Maven

O perfil opcional `movelist-quality` está definido no [POM do Chesslib](../../../chesslib/pom.xml) e reaproveita o JaCoCo 0.8.12 e o PIT 1.17.4 existentes no projeto. O perfil:

- instrumenta somente `com.github.bhlangonijr.chesslib.move.MoveList`;
- exige no mínimo 80% de cobertura de branches;
- executa a análise de mutação somente em `MoveList`;
- seleciona `MoveListTest` e `MoveListQetTest` para o PIT;
- exige no mínimo 80% de mutação;
- grava os relatórios em diretórios próprios, sem substituir os relatórios de outros integrantes.

A configuração foi registrada no commit [`b207919`](https://github.com/sofiarecreio/trabalho-qet/commit/b207919).

## Execução reproduzível

Na raiz do repositório, usando o Prompt de Comando do Windows:

```text
set "JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8"
mvn -f chesslib\pom.xml -Pmovelist-quality clean verify -Dmaven.javadoc.skip=true
mvn -f chesslib\pom.xml -Pmovelist-quality org.pitest:pitest-maven:mutationCoverage
```

A definição de UTF-8 evita falhas dependentes da codificação padrão do Windows em testes preexistentes do Chesslib. O primeiro comando executa a regressão, verifica o limite de branches e gera o relatório JaCoCo. O segundo executa o PIT e gera o relatório de mutação.

## Inspeção e referências

A inspeção estática da `MoveList`, as correções realizadas e as evidências do Sonar estão documentadas separadamente no [relatório de inspeção](../../inspecao/movelist/relatorio.md). Evidências adicionais:

- [Sonar antes das correções](../../inspecao/movelist/sonar_antes.png)
- [Sonar depois das correções](../../inspecao/movelist/sonar_depois.png)
- [Build após as correções](../../inspecao/movelist/build_sucess.png)
- [Registro de uso de IA](../../ai/AI-LOG.md)

Os percentuais de cobertura e mutação apresentados neste documento correspondem à medição individual realizada com o perfil `movelist-quality` na branch `lucio/entrega2`. Após a integração na `main`, a regressão com 342 testes e as verificações dos perfis `movelist-quality` e `game-quality` foram aprovadas, confirmando que a integração não introduziu regressões conhecidas.
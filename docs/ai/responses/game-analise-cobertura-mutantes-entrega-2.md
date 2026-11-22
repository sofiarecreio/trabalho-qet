# Saída da revisão de cobertura e mutantes de Game

Data: 22/11/2026. Classe de produção: `com.github.bhlangonijr.chesslib.game.Game`.

## Resultado e decisão

A revisão ampliou `GameQualityTest` de cinco para 13 casos. A versão inicial está no commit `0d5dbf9`; a versão revisada está no commit que acrescenta este documento. A produção e o POM permaneceram intactos nesta etapa. A contribuição e os filtros padrão de Lucas foram preservados.

| Medida | Antes (`7376d34`) | Depois (versão revisada) |
| --- | --- | --- |
| Testes da regressão Chesslib | 292 | 305 |
| Casos nas suítes `Game*Test` | 74, quatro suítes | 87, cinco suítes |
| Branches JaCoCo de Game | 189/218 = 86,70% | 195/218 = 89,45% |
| Linhas JaCoCo de Game | 359/377 | 371/377 = 98,41% |
| Mutantes PIT mortos/gerados | 180/208 = 86,54% | 201/208 = 96,63% |
| Sobreviventes / sem cobertura | 23 / 5 | 7 / 0 |
| Estados de erro de mutantes | 0 | 0 |

Os 208 mutantes continuam no denominador. Os sete sobreviventes não foram retirados por hipótese de equivalência. PIT arredonda o escore final para 97%. A complexidade agregada de Game permanece 176, com 57 em `loadMoveText(StringBuilder)`; ela é propriedade da mesma produção, que não mudou.

## Casos e oráculos da versão revisada

| Caso em `GameQualityTest` | Entrada/partição ou limite | Resultado esperado e justificativa |
| --- | --- | --- |
| `shouldDistinguishPositionsBeforeAndAtEnd` | Lista com três movimentos; posições 0, 1, 2 e 3 | Falso antes do último índice e verdadeiro no último/acima. Detecta retorno constante e inversão da condição de fim; a posição 3 foi acrescentada na revisão. |
| `shouldTreatMissingAndEmptyListsAsEnd` | Lista ativa nula e depois vazia | Fim verdadeiro em ambas, exercitando os dois caminhos de curto-circuito. |
| `shouldKeepAssignedMetadata` | ID não vazio; horário e nome da variante não vazios; posição inicial 3 | Valores exatos atribuídos, incluindo posição diferente de zero, detectando substituições por vazio/zero. |
| `shouldReadCompactBlackMoveNumbers` | `1.e4 1...e5 2.Nf3 2...Nc6` | SAN exata `e4,e5,Nf3,Nc6`, sem perder movimentos após os três pontos. |
| `shouldSerializeAllMovesOfLongVariation` | Alternativa branca com seis meios-lances | Texto exato com os números 1, 2 e 3, fechamento e retomada `1... e5`; detecta erros de incremento, paridade e limite. |
| `shouldNumberBlackVariationAndResumeNextWhiteMove` | Alternativa preta `1... c5 2. Nf3 d6` | Texto exato com início preto e retomada da linha principal `2. Nf3`, sem número preto extra. |
| `shouldCloseSiblingVariationBeforeOpeningNext` | Alternativas `d4 d5` e `c4 e5` para o primeiro movimento | Dois pares separados de parênteses, mantendo a ordem e a retomada da linha principal. |
| `shouldKeepNestedVariationInsideParent` | Alternativa `d4 d5`, subvariante `Nf6 c4 e6`, continuação `c4` | String completa com a subvariante dentro do pai e continuação fora dela; verifica estrutura e numeração, além da presença dos lances. |
| `shouldUseAssignedInitialPositionInMoveNumber` | Posição inicial atribuída 2; movimento `e4` | Texto `3. e4 *`, conforme a relação atual `initialPosition + 1`, sem confundir o teste com validação geral de numeração para FEN preto. |
| `shouldRenderGameAsPgnString` | Partida válida com `e4 e5` | `toString()` delega ao PGN completo. Este caso verifica o contrato de delegação; os casos anteriores fornecem os oráculos independentes da serialização. |
| `shouldReturnNullWhenStringConversionFails` | `MoveList` controlada informa tamanho 1 e lança `MoveConversionException` na conversão SAN | `toString()` retorna nulo conforme seu tratamento explícito da exceção. O stub força somente a falha do colaborador e não usa reflexão para alterar Game. |
| `shouldReuseCommentMapAcrossLineCommentsAndReloads` | Dois comentários de linha; recarga com um comentário diferente | Comentários associados aos índices corretos e mapa limpo na recarga; SAN da nova partida preservada. |
| `shouldIgnoreMoveMarkersInsideBlockComment` | Comentário de bloco contém `1...`, `$2` e parênteses | Texto preservado como comentário, SAN principal intacta e nenhum NAG/variação criado. |

Os cinco primeiros casos compõem a versão inicial; os oito seguintes e o limite adicional de navegação compõem a melhoria. O primeiro commit contém somente código de teste. Este documento registra a revisão realizada com IA, sem atribuir autoria manual à versão inicial.

## Análise individual dos 28 mutantes que escapavam

Linhas se referem ao `Game.java` inalterado. `idx` é o índice da instrução registrado no XML PIT, desambiguando mutantes na mesma linha. Os nomes abreviados identificam o operador do relatório.

| Linha / idx / operador | Estado inicial → final | Análise e decisão |
| --- | --- | --- |
| 77 / 36 / VoidMethodCall | Sobreviveu → sobreviveu | Remove `setPosition(0)` do construtor. Para instância concreta de Game, o campo int já começa em zero e o setter só atribui esse valor. Equivalência restrita a esse uso; uma subclasse que sobrescreva o setter pode observar a chamada. Não classificado como equivalente universal. |
| 577 / 5 / EmptyObjectReturn | Sem cobertura → morto | ID não vazio verificado por `shouldKeepAssignedMetadata`. |
| 613 / 5 / PrimitiveReturns | Sobreviveu → morto | Posição inicial não zero verificada diretamente e na numeração de saída. |
| 123 / 5 / EmptyObjectReturn | Sem cobertura → morto | Horário não vazio verificado por `shouldKeepAssignedMetadata`. |
| 685 / 5 / EmptyObjectReturn | Sem cobertura → morto | Nome de variante não vazio verificado pelo mesmo caso. |
| 1079 / 5 / NegateConditionals | Sobreviveu → morto | Lista não nula e posição anterior ao fim agora exigem falso. |
| 1079 / 23 / BooleanTrueReturn | Sobreviveu → morto | O mesmo oráculo rejeita retorno constante verdadeiro. |
| 844 / 464 / ConditionalsBoundary | Sobreviveu → sobreviveu | Troca `lastClosedIndex >= 0` por `> 0`. Exige alternativa fechada no índice zero para diferenciar. Variações normais da suíte começam após um lance, com índice positivo; variante antes do primeiro lance não foi assumida como entrada válida. Lacuna de domínio, sem prova de equivalência. |
| 914 / 790 / Increments | Sobreviveu → morto | Inverte incremento de `halfMove`; as variações completas detectam perda da linha precedente. |
| 800 / 268 / NegateConditionals | Sobreviveu → morto | A forma compacta `1...e5` exige preservar `e5` após remover o número. |
| 451 / 390 / ConditionalsBoundary | Sobreviveu → sobreviveu | Troca quebra em comprimento `> 80` por `>= 80`. Os testes existentes ultrapassam 80; faltou uma saída com exatamente 80 caracteres antes da quebra. Lacuna de limite de apresentação, mantida explícita. |
| 437 / 326 / Math | Sobreviveu → sobreviveu | Troca paridade inicial por multiplicação. A suíte usa contador inicial ímpar; falta contador par na saída. O ramo original produz `.. `, cuja conformidade PGN precisa ser investigada antes de fixar esse formato como comportamento correto em um teste. |
| 478 / 523 / Math | Sobreviveu → morto | Alternativa preta verifica a ausência da retomada preta indevida. |
| 478 / 524 / NegateConditionals | Sobreviveu → morto | Oráculos exatos das alternativas branca e preta distinguem a retomada. |
| 567 / 16 / EmptyObjectReturn | Sem cobertura → morto | Falha controlada de conversão exige nulo, rejeitando string vazia. |
| 536 / 174 / ConditionalsBoundary | Sobreviveu → morto | Alternativa longa atravessa o índice par 2 e exige o próximo número correto. |
| 536 / 180 / ConditionalsBoundary | Sobreviveu → morto | Último lance da alternativa não pode produzir numeração de movimento inexistente. |
| 538 / 183 / Increments | Sem cobertura → morto | Alternativa longa exige numeração crescente 1, 2 e 3. |
| 543 / 199 / Increments | Sobreviveu → morto | Sequência de cores e números da alternativa exige incremento do índice. |
| 513 / 66 / Math | Sobreviveu → morto | Alternativa iniciada pelas pretas exige `1...`, distinguindo a paridade. |
| 527 / 131 / Math | Sobreviveu → sobreviveu | Altera cálculo do último índice da variante. Os casos não tornam observável a combinação de filho no último lance com pai diferente do índice da variante corrente. Não há demonstração de equivalência para todas as estruturas aceitas pelo setter público. |
| 536 / 170 / Math | Sobreviveu → morto | Numeração completa da alternativa diferencia os índices pares. |
| 536 / 179 / Math | Sobreviveu → morto | Não acrescentar número depois do último lance diferencia o limite alterado. |
| 527 / 132 / NegateConditionals | Sobreviveu → sobreviveu | Mesmo ponto de fechamento antecipado de 527/131; falta estrutura em que o teste de parentesco faça essa mudança aparecer na saída. |
| 528 / 138 / NegateConditionals | Sobreviveu → sobreviveu | Compara pai do filho com índice antigo da variante. Os casos ainda não distinguem a inversão no fechamento antecipado; requer investigação da estrutura produzida pelo parser versus estruturas fornecidas por `setVariations`. |
| 536 / 171 / NegateConditionals | Sobreviveu → morto | Paridade da numeração verificada pela saída exata. |
| 536 / 174 / NegateConditionals | Sobreviveu → morto | Alternativa longa verifica a passagem do segundo meio-lance. |
| 536 / 180 / NegateConditionals | Sobreviveu → morto | Saída exata rejeita números ausentes ou adicionados após o término. |

Não se alterou a produção para facilitar a eliminação de mutantes. A meta de 80% já foi superada sem descontos. Os sobreviventes continuam registrados como limitações e oportunidades futuras; não é necessário chegar a 100% para avançar à inspeção.

## Análise das branches inicialmente ausentes

As 29 saídas ausentes estavam distribuídas pelas 26 linhas abaixo. Uma linha pode conter várias decisões por curto-circuito. A redução foi de 29 para 23 saídas ausentes. A tabela preserva também as hipóteses para ramos que continuam ausentes; não os remove do total.

| Linha | Ausentes antes → depois | Análise |
| --- | --- | --- |
| 437 | 1 → 1 | Paridade do contador inicial; saída para contador par continua pendente de análise da notação. |
| 478 | 1 → 0 | Alternativa preta cobre a saída sem retomada preta. |
| 484 | 1 → 1 | `index >= 2` só é avaliado após paridade par; `index` começa em 1 no laço, portanto índice par menor que 2 não ocorre no fluxo normal. |
| 502 | 1 → 1 | Variação nula não produzida pelo parser; setter público pode fornecer listas com elemento nulo. Defesa ainda não exercitada. |
| 527 | 1 → 0 | Variação com filho antes do último lance cobre o outro resultado do primeiro teste. |
| 528 | 1 → 1 | Comparação de parentesco no último lance continua com uma saída ausente, conforme mutantes 527/528. |
| 536 | 2 → 1 | Variação longa cobre numeração intermediária; índice par menor que 2 não ocorre, pois o índice parte de pelo menos 1. |
| 546 | 1 → 1 | Falta caminho com fechamento antecipado (`terminated=true`), ligado à comparação em 528. |
| 797, 804, 811 | 1 cada → 1 cada | `onLineCommentBlock=true` já executa `continue` antes dessas verificações. Seu lado verdadeiro nos testes posteriores é redundante nesse fluxo. |
| 819 | 2 → 2 | Comentário de linha já desviado; dentro de bloco, um novo `{` é texto. Conferir as saídas de curto-circuito antes de atribuir cada uma a uma condição fonte. |
| 824 | 1 → 1 | Mesmo desvio antecipado dos comentários de linha torna o lado negado redundante no ponto posterior. |
| 826 | 1 → 1 | Fechamento `}` sem abertura não exercitado; entrada malformada, sem definir aceitação como requisito. |
| 842 | 1 → 1 | Estado de comentário de linha não chega aqui. Marcadores em comentário de bloco agora são verificados como texto. |
| 844 | 1 → 1 | Índice zero após fechamento de variante não exercitado, conforme mutante de limite. |
| 850 | 1 → 1 | Comentário de linha já foi desviado; há combinação redundante na condição composta. |
| 853 | 1 → 1 | Lista local `variation` é criada e nunca recebe nulo. |
| 870 | 1 → 1 | Com variação externa ainda aberta, a pilha possui pai; combinação `onVariationBlock=true` com pai nulo não é produzida pelo fluxo normal. |
| 883 | 1 → 1 | `last` nulo exige inconsistência da pilha ao fechar variação; o estado normal só chega a esse fechamento com entrada correspondente na pilha. |
| 895 | 1 → 1 | Estado de comentário de linha já foi desviado no início da iteração. |
| 896 | 1 → 1 | Comentário aberto cria o builder; ausência do builder com bloco aberto não ocorre normalmente. |
| 904 | 1 → 1 | Lista local `variation` nunca é nula. |
| 920 | 1 → 0 | Dois comentários sucessivos cobrem reutilização do mapa no comentário final sem newline. |
| 947 | 1 → 1 | Índice válido interrompe o laço com `break` na posição pedida; esgotar o iterador sem atingir esse índice exigiria lista/iterador inconsistente. |
| 1079 | 2 → 0 | Lista nula, vazia, posição anterior, exata e posterior ao último lance agora verificadas. |

## Relação com todas-arestas

No grafo de fluxo de controle, todas-arestas exige percorrer suas arestas. As duas saídas de um `if`, saídas de `switch`, retornos do laço e caminhos de curto-circuito correspondem a decisões relevantes. Por exemplo, `isEndOfMoveList` distingue lista nula, lista não nula antes do fim e lista não nula no fim; os casos de navegação verificam cada resultado observável.

O contador C1/BRANCH do JaCoCo mede saídas de decisões no bytecode. O resultado **195/218 = 89,45%** comprova mais de 80% nesse critério operacional, para a classe inteira, mantendo inclusive saídas redundantes no denominador. Isso sustenta a análise das arestas de decisão. Ele não fornece diretamente a razão entre todas as arestas de um grafo-fonte independente: arestas sequenciais e tratamento de exceções têm convenções próprias. Não se apresenta 89,45% como contagem independente de todas as arestas desse outro grafo.

A documentação final deve explicitar que usa C1/JaCoCo como operacionalização das saídas de decisão e descrever seu escopo. Se a disciplina exigir percentual sobre grafo com todas as arestas sequenciais e excepcionais, será necessário construir esse grafo e medi-lo separadamente; esta etapa não afirma que esse cálculo foi realizado. A cobertura de linhas de 98,41% é informação complementar, não substitui a de decisões. A justificativa e seus limites estão documentados; a aceitação dessa operacionalização é uma questão do critério acadêmico.

Referências técnicas: [contadores JaCoCo](https://www.jacoco.org/jacoco/trunk/doc/counters.html) e [análise do fluxo de controle](https://www.jacoco.org/jacoco/trunk/doc/flow.html). As fontes descrevem o contador de decisões, curto-circuito no bytecode e as limitações de tratamento de exceções; as classificações específicas de Game acima são análise deste código.

## Execução e validação da resposta

```text
mvn -B -f chesslib/pom.xml "-Dtest=GameQualityTest" test
mvn -B -f chesslib/pom.xml -Pgame-quality "-Dmaven.javadoc.skip=true" clean verify
mvn -B -f chesslib/pom.xml -Pgame-quality "-Djacoco.skip=true" test-compile org.pitest:pitest-maven:1.17.4:mutationCoverage
```

A versão inicial passou nos cinco testes. A revisada passou nos 13 casos e na regressão completa de 305 testes, com verificação de cobertura aprovada. Os XMLs foram comparados por classe, método, operador e índice de instrução: 21 dos 28 mutantes antes não mortos passaram para KILLED. As cinco localizações antes sem cobertura agora têm teste que observa seu retorno ou efeito. Nenhum mutante antes morto deixou de ser morto.

Duas tentativas restritas do PIT falharam na geração de cobertura com `MINION_DIED`, sem produzir escore válido. A mesma execução fora da restrição dos processos auxiliares concluiu com sucesso. O XML final contém somente 201 KILLED e sete SURVIVED, sem erros ou NO_COVERAGE; os resultados das tentativas interrompidas não foram somados ao resultado final.

**Validação técnica e decisão:** aceitar as melhorias porque os oráculos exatos distinguem a estrutura, a ordem e a numeração das variações, enquanto as entradas de limite distinguem retorno constante de navegação e os valores não padrão detectam apagamento de metadados. A conversão com falha controlada exercita o contrato de exceção sem adulterar o estado de Game. A evidência de sensibilidade é a mudança identificável dos mesmos 21 mutantes, além da regressão aprovada. Não aceitar inferências de equivalência universal dos sobreviventes nem a identidade numérica entre C1 e todo grafo de arestas: ambas extrapolariam a evidência disponível.

Relatórios completos gerados localmente em `chesslib/target/site/jacoco-game/index.html` e `chesslib/target/pit-reports-game/index.html`. Não foram acrescentados scripts, logs brutos ou cópias de código ao commit. As versões inicial e revisada são recuperáveis pelo histórico Git.

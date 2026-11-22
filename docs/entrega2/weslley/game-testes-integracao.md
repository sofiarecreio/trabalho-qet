# Game — testes, integração e resultados da Entrega 2

Responsável: Weslley Ribeiro. Atualização: 22/11/2026.

Código e evidências integrados à `main` pelo [PR #16](https://github.com/sofiarecreio/trabalho-qet/pull/16), merge `f2f875e0394e28801fe832c6c5ffbda3644cffdc`. Este relatório consolida a contribuição de Game e a integração Chesslib de Weslley. Os resultados das classes dos demais integrantes são consolidados pelos respectivos responsáveis.

## Escopo e suítes

| Suíte | Casos | Objetivo |
| --- | ---: | --- |
| [GameTest](../../../chesslib/src/test/java/com/github/bhlangonijr/chesslib/game/GameTest.java) | 22 | Comportamentos básicos e regressões dos defeitos de navegação |
| [GameIsolatedTest](../../../chesslib/src/test/java/com/github/bhlangonijr/chesslib/game/GameIsolatedTest.java) | 18 | Coordenação da navegação, com Board controlado por stub |
| [GameProcessingTest](../../../chesslib/src/test/java/com/github/bhlangonijr/chesslib/game/GameProcessingTest.java) | 22 | Processamento de movimentos, comentários, variações e geração de PGN |
| [GameIntegrationTest](../../../chesslib/src/test/java/com/github/bhlangonijr/chesslib/game/GameIntegrationTest.java) | 12 | Leitura e gravação de arquivos PGN com componentes reais |
| [GameQualityTest](../../../chesslib/src/test/java/com/github/bhlangonijr/chesslib/game/GameQualityTest.java) | 15 | Lacunas identificadas por cobertura e mutação, e robustez após Sonar |
| **Total das cinco suítes** | **89** | Casos executados na versão final de Game |

A regressão completa do Chesslib executou **307 testes aprovados**, incluindo testes originais e dos outros integrantes. Os totais históricos de 247, 269, 281, 292 e 305 correspondem a etapas anteriores de implementação e não substituem a medição final.

## Técnicas e casos funcionais

As partições de equivalência e os valores-limite orientam as mesmas suítes usadas nas medições estruturais e de mutação. Cada caso contém asserções concretas de resultado; executar um caminho sem observar seu efeito não é suficiente.

| Grupo | Entradas e partições | Resultado esperado |
| --- | --- | --- |
| Estado e carregamento básico | Instância nova; PGN válido, vazio e inválido | Estado inicial coerente, sequência SAN exata ou exceção específica |
| Limites da navegação | Índices -1, 0, n-1, n e acima de n; lista vazia; Board ausente | Executar somente a faixa válida; preservar a posição quando não há navegação; ativar a lista fornecida conforme contrato |
| Atalhos e listas alternativas | Primeiro/último/próximo/anterior; lista menor e maior que a principal | Usar FEN e tamanho da lista recebida e atualizar o índice correspondente |
| Falha do colaborador | Board aceita todos os lances ou rejeita a primeira/segunda tentativa | Passar `fullValidation=true`; lançar MoveException e interromper sem tentar lances posteriores |
| Comentários de linha | Vazio/com texto, com/sem newline final, LF/CRLF, texto imediatamente após `;` | Preservar o comentário no índice correto e retomar os movimentos após newline |
| Conteúdo literal de comentários | `$`, parênteses, chaves e números dentro de comentários | Preservar o texto, sem criar NAG ou variação a partir do conteúdo |
| Comentário de bloco | Texto normal ou com ponto e vírgula | Armazenar o comentário e manter a linha principal |
| Recarga | Uma partida anotada seguida de outra diferente | Limpar comentários, NAG e variações anteriores, sem misturar os movimentos |
| FEN e lista ausente | FEN padrão/customizado; halfMoves nulo | Recriar a lista com o FEN devido e processar os movimentos na posição esperada |
| Texto previamente atribuído | loadMoveText() com e sem texto definido | Carregar o texto atribuído ou preservar os movimentos existentes quando não há texto |
| Variações | Simples, sucessivas, aninhadas e inválidas | Preservar ordem, vínculo ao pai e linha principal; erro contextualizado quando houver lance ilegal |
| Metadados de PGN | Tags opcionais presentes/ausentes, ELO, controle de tempo e propriedades | Incluir somente valores definidos e manter as tags obrigatórias e o resultado |
| Serialização | Texto bruto, partida longa, alternativas brancas/pretas e numeração compacta | PGN com números, espaços, retomada e parênteses verificados; quebra de linha sem perda de movimentos |
| Fim da lista | Lista nula/vazia; posição antes/no/depois do último índice | Falso antes do fim e verdadeiro no fim ou acima; listas ausentes/vazias tratadas conforme contrato |
| Robustez de toString | Partida válida, incompleta, vazia sem texto bruto ou falha controlada de conversão | PGN válido quando possível; string vazia para metadados ausentes ou falha de conversão, conforme contrato final |

Nos testes isolados, o stub de Board registra o FEN recebido, os movimentos, sua ordem e o argumento de validação. Ele controla aceitação/rejeição sem executar as regras reais de xadrez. MoveList continua real como coleção. Nos testes de processamento, os componentes reais interpretam a notação; nos testes de integração, também são exercitados arquivos e carregadores.

## Integração do Chesslib

O fluxo real é arquivo temporário → PgnHolder → LargeFile/PgnIterator → GameLoader → Game/MoveList → Board. JUnit TemporaryFolder cria dados por caso; os cenários não dependem de arquivos pessoais, rede ou ordem de execução.

| Método de GameIntegrationTest | Entrada | Resultado esperado e técnica |
| --- | --- | --- |
| shouldLoadMetadataAndMovesFromRealPgnFile | PGN com evento, local, data, rodada, jogadores e `e4 e5 Nf3 Nc6` | Uma partida com metadados exatos, quatro meios-lances e evento associado; partição válida |
| shouldNavigateLoadedGameUsingRealBoard | Partida carregada e navegação último/anterior/primeiro/próximo | Índices e FENs literais esperados após cada sequência; limites e transições |
| shouldKeepGamesIndependentWithinSameFile | Duas partidas de eventos distintos; anotações somente na primeira | Dois objetos e listas independentes, sem propagar comentário/NAG à segunda; partição de múltiplas partidas |
| shouldNavigateGameLoadedFromFenTag | Posição de dois reis por tag FEN e `Ke2 Kd7` | FEN inicial da lista preservado e posição final conhecida; partição de início customizado |
| shouldNavigateLoadedVariationAndReturnToMainLine | Alternativa de dois meios-lances e linha principal de quatro | Navegar ao fim da lista correta e retornar à linha principal com FEN exato; limite de tamanhos diferentes |
| shouldPreserveAnnotationsReadFromFile | Comentário entre chaves, comentário de linha e `$1` | Textos e índices corretos, sequência SAN e posição final preservadas; partições de anotações |
| shouldReportIllegalMainLineWithGameContext | Lance ilegal na linha principal | PgnException com evento/rodada e causa MoveConversionException; nenhuma partida incorporada; partição inválida |
| shouldReportIllegalVariationWithCause | `1. e4 (1. Qh5) e5` | Erro com evento, texto `Qh5` e cadeia de causas pertinente; nenhuma partida incorporada; partição inválida |
| shouldPreserveGameAfterSavingAndReloadingPgn | Salvar partida anotada e reler o arquivo em outro holder | Arquivo não vazio; novo objeto preserva metadados, SAN, comentário, NAG, alternativa e FEN final; ida e volta |
| shouldLoadEmptyFileWithoutCreatingGame | Arquivo vazio | Coleções de partidas, eventos e jogadores vazias; limite vazio |
| shouldReportMissingPgnFile | Caminho inexistente | FileNotFoundException e lista de partidas vazia; partição de recurso ausente |
| shouldReadCrLfPgnAndBuildExpectedPosition | PGN válido com quebras CRLF | Mesma SAN e FEN final esperado; partição de terminação de linha |

## Cobertura e mutação

| Indicador final de Game | Resultado |
| --- | --- |
| Branches JaCoCo | **166 cobertas / 179 totais = 92,74%** |
| Linhas JaCoCo | 371/378 = 98,15% |
| Mutantes PIT | **203 mortos / 213 gerados = 95,31%** |
| Mutantes restantes | 7 sobreviventes e 3 sem cobertura; zero erros de execução |
| Limites configurados | 80% de branches e 80% de mutação; verificações aprovadas |

A classe é não CRUD e possui lógica de parsing, serialização e navegação. Na inspeção final, o Sonar registrou complexidade ciclomática agregada 177 e cognitiva agregada 109. A complexidade agregada aumentou com extrações/novos tratamentos, enquanto os métodos deixaram de exceder o limite cognitivo 15. Não se usam as duas medidas como sinônimos.

O perfil opcional `game-quality` do [POM](../../../chesslib/pom.xml) reaproveita JaCoCo 0.8.12 e PIT 1.17.4 disponibilizados por Lucas, com relatórios separados e seleção dos métodos de Game. Os novos tipos internos apenas guardam dados; a lógica permanece na classe instrumentada. O comportamento padrão para MoveGenerator foi preservado.

Os mutantes restantes permanecem no denominador. Eles envolvem inicialização redundante, quebra exata em 80 caracteres, numeração, fechamento de variantes, delimitadores sem abertura e alternativa nula. As limitações e as mudanças do denominador após a refatoração estão detalhadas no [relatório de inspeção](../../inspecao/game/relatorio.md#validação-após-as-correções).

### Relação com todas-arestas

O valor de 92,74% é cobertura de branches C1/JaCoCo, que observa saídas de decisões no bytecode, incluindo caminhos de curto-circuito. O grafo não foi desenhado manualmente: a ferramenta faz a contagem das arestas a partir do bytecode.

**Conclusão:** na disciplina, o critério todas-arestas é medido pelo contador de branches do EclEmma/JaCoCo, como apresentado na aula de cobertura com essa ferramenta. Com 166 de 179 branches cobertos (92,74%), o critério está atendido acima da meta de 80%. A cobertura de linhas é apenas complementar.

## Execução reproduzível

Na raiz do repositório, com Maven e JDK compatível:

```text
mvn -B -f chesslib/pom.xml "-Dtest=GameTest,GameIsolatedTest,GameProcessingTest,GameIntegrationTest,GameQualityTest" test
mvn -B -f chesslib/pom.xml -Pgame-quality "-Dmaven.javadoc.skip=true" clean verify
mvn -B -f chesslib/pom.xml -Pgame-quality "-Djacoco.skip=true" test-compile org.pitest:pitest-maven:1.17.4:mutationCoverage
```

O primeiro comando seleciona os 89 casos de Game. O segundo executa a regressão e gera `chesslib/target/site/jacoco-game/index.html`; o terceiro gera `chesslib/target/pit-reports-game/index.html`. Os resultados registrados foram medidos no código incorporado pelo PR #16; esta consolidação altera somente documentação e não representa uma nova execução dos testes.

## Inspeção, Selenium e dados para a avaliação de qualidade

| Evidência disponível de Weslley | Medida/resultado | Limite para a consolidação da equipe |
| --- | --- | --- |
| Inspeção de Game | 16 apontamentos antes e zero depois; mesmo perfil comprehensive de 608 regras | Escopo somente Game.java; não informa o resultado das demais classes |
| Testes de Game e integração Chesslib | 89 casos nas cinco suítes; 12 de integração; regressão completa com 307 aprovados | Verifica os cenários codificados, sem provar todas as regras, encodings ou combinações de eventos |
| Cobertura e mutação | Branches 92,74%; PIT 95,31% | Não são notas ISO prontas; servem de evidência para a consolidação da equipe |
| Selenium de visitas | 9 casos e 9 capturas; seleção conjunta de 18 testes aprovada | Uma funcionalidade; não comprova as cinco funcionalidades do grupo |

Referências: [inspeção e PNGs](../../inspecao/game/relatorio.md), [Selenium de visitas](../../selenium/visitas/README.md) e [relatório compartilhado de inspeção](https://docs.google.com/document/d/1LBd-p5gI73bchdHi-wWXTSUu7rfhmelV1IxTPfpdvk8/edit).

Os dados acima estão disponíveis para a tabela de atributos, medidas, escala, resultados e justificativas coordenada por Sofia. A escolha dos atributos e da escala e a avaliação consolidada da ISO/IEC 25010 continuam pendentes da equipe. Os resultados finais dos outros integrantes, a atualização coletiva do Plano de Teste e os slides não são declarados concluídos por este relatório.

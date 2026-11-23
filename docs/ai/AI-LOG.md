# Registro de Uso de Inteligência Artificial

Este documento registra as interações com ferramentas de Inteligência Artificial Generativa que contribuíram de forma relevante para os artefatos do trabalho de Qualidade e Teste de Software.

As respostas da IA não foram aceitas automaticamente. Todo conteúdo utilizado foi revisado pelo integrante responsável e validado por inspeção do código, execução dos testes e comparação dos resultados esperados e obtidos. Interações usadas apenas para correções ortográficas ou configurações simples de ambiente não precisam ser registradas.

## Registro 001 — Geração da suíte inicial de testes unitários de `Game`

| Informação | Registro |
| --- | --- |
| Data | 20/09/2026 |
| Responsável | Weslley |
| Entrega | Entrega 1 |
| Atividade | Projeto da suíte inicial de testes unitários para a classe não CRUD `Game`, do Chesslib |
| Ferramenta | ChatGPT — GPT-5.6 Sol, raciocínio leve |
| Prompt/instrução | Prompt completo armazenado em `docs/ai/prompts/game-suite-entrega-1.md`, acompanhado do código-fonte completo de `Game.java` |
| Resultado | A IA propôs 15 casos de teste e uma implementação inicial de `GameTest.java`. Foram contemplados estado inicial, MoveText/PGN válido, vazio e inválido, comentários, NAGs, variações, navegação, limites, atualização do `Board`, geração de PGN e fim da lista. A resposta também analisou dependências e apontou possíveis defeitos em `isStartOfMoveList()` e `gotoLast(MoveList)` |
| Decisão | A saída inicial foi preservada e incorporada sem correções para manter a evidência original produzida pela IA. Não foi adicionado Mockito nesta etapa. A revisão e as correções foram realizadas posteriormente no código de teste |
| Validação | A execução inicial de `mvn -Dtest=GameTest test` executou 16 testes: 15 passaram e 1 falhou. A falha ocorreu porque a IA esperou o FEN com campo de en passant `-`, enquanto o `Board` produziu `e6`. A expectativa foi revisada posteriormente. A suíte revisada também reproduziu defeitos de navegação da implementação de `Game`. Ao final corrigi e revisei os testes. |

### Evidências relacionadas

- [Prompt utilizado](prompts/game-suite-entrega-1.md)
- [Saída inicial completa da IA](responses/game-suite-entrega-1-gpt-5.6-sol.md)
- [Classe analisada](../../chesslib/src/main/java/com/github/bhlangonijr/chesslib/game/Game.java)
- [Solução revisada](../../chesslib/src/test/java/com/github/bhlangonijr/chesslib/game/GameTest.java)

## Registro 002 — Comparação dos testes de `MoveList`

| Informação | Registro |
| --- | --- |
| Data | 19/09/2026 |
| Responsável | Lucio |
| Entrega | Entrega 1 |
| Atividade | Revisão dos testes unitários da classe `MoveList` do Chesslib |
| Ferramenta | ChatGPT — GPT-5.6 Sol, raciocínio médio |
| Artefato relacionado | `MoveListQetTest.java` |
| Código utilizado como referência | `MoveListTest.java`, presente no repositório original do Chesslib |

### Prompt/instrução utilizada

> Compare os testes unitários que desenvolvi para a classe `MoveList` com os testes já existentes no arquivo `MoveListTest.java` do repositório original do Chesslib. Verifique se os novos testes são suficientemente diferentes, se apenas repetem os testes existentes e se os cenários possuem complexidade adequada para o trabalho. Considere os testes de movimento simples de peão, captura, movimento ambíguo de cavalo, promoção, roque pequeno, roque grande, captura en passant e movimento inválido. Explique quais testes são complementares, quais possuem possível sobreposição e quais ajustes seriam necessários.

### Resultado apresentado pela IA

A IA indicou que existe alguma sobreposição natural, pois os dois arquivos exercitam a mesma classe e as mesmas regras de xadrez. Entretanto, concluiu que os testes desenvolvidos pelo grupo não eram cópias diretas dos testes originais.

Os testes originais de `MoveList` utilizam, entre outros elementos, sequências maiores de movimentos, conversões completas entre SAN e FAN, posições FEN, numeração de jogadas e partidas completas. Os novos testes foram organizados como cenários menores e isolados, com uma regra específica por método de teste.

A comparação apontou como diferenciais dos testes desenvolvidos:

- utilização de posições FEN controladas para promoção e roques;
- validação direta do movimento interno, como `e1g1` e `e1c1`;
- validação da notação SAN gerada;
- conferência do estado FEN final após a captura en passant;
- teste específico de desambiguação do movimento `Nbd2`;
- separação de cada regra especial em um método de teste independente.

A IA também recomendou que um caso de movimento inválido somente fosse mantido se a entrada escolhida realmente provocasse a exceção esperada pela implementação.

### Comparação realizada

| Aspecto | Testes originais de `MoveList` | Testes desenvolvidos |
| --- | --- | --- |
| Organização | Alguns testes utilizam sequências maiores ou partidas completas | Cada método concentra-se em um comportamento específico |
| Movimento simples | Avaliado dentro de sequências e conversões | Cenário isolado para `e4` |
| Captura | Presente em sequências de movimentos | Cenário curto para `exd5` |
| Ambiguidade | Há verificações de notação e desambiguação | Validação específica de `Nbd2` e do movimento `b1d2` |
| Promoção | Cobertura relacionada à conversão de movimentos | Posição FEN controlada e promoção `e8=Q` |
| Roques | Avaliados em contextos da suíte original | Casos independentes para `O-O` e `O-O-O` |
| En passant | Pode aparecer em sequências de movimentos | Validação do movimento, da SAN e do FEN final |
| Resultado verificado | SAN, FAN, FEN e sequências completas | Movimento interno, tamanho da lista, SAN e FEN conforme o caso |

### Decisão

Foram mantidos na versão final os seguintes cenários:

1. movimento simples de peão;
2. captura de peão;
3. movimento ambíguo de cavalo;
4. promoção de peão;
5. roque pequeno;
6. roque grande;
7. captura en passant;
8. rejeição do movimento ilegal `Qh5`, com lançamento de `MoveConversionException`.

O cenário de movimento inválido foi revisado durante o desenvolvimento. Após confirmar que `Qh5` não pode ser executado a partir da posição inicial, pois o caminho da dama está bloqueado, o teste foi mantido com `@Test(expected = MoveConversionException.class)`. A execução confirmou que a biblioteca lança a exceção esperada.

### Alterações realizadas após a revisão

- organização dos testes em métodos pequenos e independentes;
- conferência de mais de um resultado nos cenários relevantes, como movimento, SAN e FEN;
- manutenção somente de resultados esperados confirmados pela execução;
- inclusão do teste negativo somente após confirmar o lançamento de `MoveConversionException`;
- documentação da diferença entre a suíte criada e a suíte original do projeto.

### Validação

O resultado da IA foi validado por leitura do arquivo original `MoveListTest.java`, conferência dos dados de entrada e posições FEN, execução dos testes pelo Eclipse com JUnit 4, execução pelo Maven e revisão das mensagens de falha durante o desenvolvimento.

### Evidências relacionadas

- [Teste desenvolvido](../../chesslib/src/test/java/com/github/bhlangonijr/chesslib/move/MoveListQetTest.java)
- [Repositório original do Chesslib](https://github.com/bhlangonijr/chesslib)

## Registro 003 — Definição do escopo dos sistemas

| Informação | Registro |
| --- | --- |
| Data | 20/09/2026 |
| Responsável | Sofia Recreio |
| Entrega | Entrega 1 |
| Atividade | Definição do escopo do Chesslib e do Spring PetClinic |
| Ferramenta | ChatGPT |
| Prompt/instrução | “Descrição do escopo do(s) sistema(s). Definir quais módulos/componentes serão testados. Descrever o escopo no Plano de Teste. Faça isso para o Chesslib e o Spring PetClinic do jeito que você julgar mais completo, considerando o que foi pedido pela professora.” |
| Resultado | A resposta descreveu o escopo de testes dos dois sistemas escolhidos. No Chesslib, propôs foco em classes com lógica mais complexa, voltadas para testes unitários, estruturais, cobertura e mutação. No Spring PetClinic, propôs foco nas funcionalidades da aplicação web, como cadastro de proprietários, animais e visitas, priorizando testes funcionais, de integração e futura automação com Selenium. |
| Decisão | O grupo aceitou a separação entre testes unitários de classes não CRUD do Chesslib e testes funcionais manuais das principais funcionalidades do Spring PetClinic. Para a Entrega 1, foram mantidos o projeto, a implementação e a execução inicial dos testes unitários e manuais. Cobertura estrutural completa, testes de mutação, inspeção com SonarQube e automação de interface com Selenium foram adiados para a Entrega 2, conforme o enunciado e a capacidade do grupo. |
| Validação | A resposta foi lida por Sofia e discutida com os demais integrantes. O conteúdo foi comparado com o enunciado da disciplina, com a divisão das classes e funcionalidades e com o Plano de Teste. Foram retiradas ou adiadas as atividades que não pertenciam à Entrega 1. |

### Evidências relacionadas

- Plano de Teste do grupo.
- Distribuição das classes do Chesslib e dos casos manuais do Spring PetClinic.
- Casos implementados e relatórios armazenados neste repositório.

## Registro E2-WES-GAME-01 — Testes isolados de navegação de Game

| Informação | Registro |
| --- | --- |
| Data | 22/11/2026 |
| Responsável | Weslley Ribeiro |
| Entrega | Entrega 2 |
| Atividade | Revisão e melhoria dos testes unitários de navegação de `Game`, com isolamento do processamento de `Board` |
| Versão inicial | Elaborada por Weslley sem auxílio de IA, com quatro testes, no commit `f1f091a`. A IA foi utilizada apenas para melhorar essa versão |
| Ferramenta | Codex/ChatGPT |
| Prompt/instrução | [Prompt de melhoria dos testes](prompts/game-isolados-entrega-2.md) |
| Resultado | Resposta com a suíte revisada de 18 testes isolados de navegação de `Game`, usando stub de `Board` para controlar respostas e registrar interações. [Saída produzida](responses/game-isolados-entrega-2.md) |
| Decisão | Aceitas as verificações de FEN, ordem de chamadas, validação completa, rejeição de jogadas e limites. Mantido o stub sem novas dependências. Não foi exigida a preservação da lista anterior em chamadas inválidas porque o contrato prevê ativar a lista fornecida. Weslley informou ter validado a melhoria |
| Validação | Os casos foram confrontados com o contrato de navegação e com as fronteiras -1, 0, n-1, n e n+1. O stub separa a coordenação de Game das regras reais de xadrez e permite conferir FEN, ordem, fullValidation e interrupção sem calcular o esperado pelo Board real. A revisão detectou lacunas na versão anterior e a suíte ampliada detectou três alterações defeituosas controladas: tamanho da lista errado, validação desativada e limite inclusivo incorreto. Os 40 testes de Game e os 247 da regressão passaram. Esses resultados sustentam as asserções escolhidas, mas não substituem cobertura estrutural, PIT ou integração com componentes reais. |

### Evidências relacionadas

- [Entrada: prompt utilizado](prompts/game-isolados-entrega-2.md).
- [Saída: código da resposta](responses/game-isolados-entrega-2.md).
- [Suíte executável](../../chesslib/src/test/java/com/github/bhlangonijr/chesslib/game/GameIsolatedTest.java).

## Registro E2-WES-GAME-02 — Processamento de movimentos, comentários e PGN

| Informação | Registro |
| --- | --- |
| Data | 22/11/2026 |
| Responsável | Weslley Ribeiro |
| Entrega | Entrega 2 |
| Atividade | Ampliação dos testes funcionais de processamento de `Game` e correção de comentários de linha |
| Ferramenta | Codex/ChatGPT |
| Prompt/instrução | [Prompt de melhoria](prompts/game-processamento-entrega-2.md) |
| Resultado | [Resposta com correção e 22 testes propostos](responses/game-processamento-entrega-2.md). A versão inicial de quatro testes está no commit `c9aaa88`: três passaram e um reproduziu a perda de movimento após comentário de linha. A proposta ampliada foi preservada no commit `a9d9ac2`, com uma falha de oráculo entre os 62 testes de Game executados. Depois da correção separada desse oráculo, os 62 testes de Game e os 269 da regressão completa passaram |
| Decisão | Aceita a correção de encerrar comentários antes de descartar a quebra de linha e de impedir que marcadores de notação no comentário sejam interpretados. Aceitos os cenários de recarregamento, FEN, variações e metadados. Rejeitada a expectativa de 40 movimentos no teste de PGN longo: a entrada literal possui 10 pares, portanto 20 meios-lances. A resposta é preservada sem reescrever esse erro. Em commit posterior à proposta, a expectativa foi corrigida para 20 e complementada pela sequência SAN completa esperada; esse ajuste também foi realizado com auxílio de IA. Não foram alterados os parâmetros documentados como ignorados de toPgn |
| Validação | O teste inicial forneceu uma sequência concreta esperada e falhou na ausência de e5, identificando um erro real em vez de ajustar o esperado à implementação. A correção foi confrontada com três causas no código: descarte antecipado de newline, interpretação do conteúdo como variação e ausência de armazenamento no fim do texto. Os novos casos verificam o texto do comentário, a sequência SAN, índices de associação e a ausência de NAGs/variações indevidas; casos separados mantêm comentários entre chaves e aceitam LF/CRLF. Para FEN e recarregamento, os resultados são definidos por posições e sequências explícitas. A expectativa 40 foi rejeitada por contagem independente da entrada, não porque o programa retornou 20; a asserção final também confere todos os 20 meios-lances, evitando esconder perda ou reordenação de movimentos. Comandos executados: mvn -B -f chesslib/pom.xml -Dtest=GameTest,GameIsolatedTest,GameProcessingTest test (62 aprovados) e mvn -B -f chesslib/pom.xml test (269 aprovados). A proposta usa componentes reais e não substitui teste de arquivos PGN, cobertura estrutural, PIT ou Sonar. |

### Evidências relacionadas

- [Entrada: prompt utilizado](prompts/game-processamento-entrega-2.md).
- [Saída original da revisão, incluindo a expectativa rejeitada](responses/game-processamento-entrega-2.md).
- [Testes executáveis](../../chesslib/src/test/java/com/github/bhlangonijr/chesslib/game/GameProcessingTest.java).
- [Código de produção](../../chesslib/src/main/java/com/github/bhlangonijr/chesslib/game/Game.java).

## Registro E2-WES-GAME-03 — Integração do Chesslib com arquivos PGN

| Informação | Registro |
| --- | --- |
| Data | 22/11/2026 |
| Responsável | Weslley Ribeiro |
| Entrega | Entrega 2 |
| Atividade | Leitura de arquivos PGN, criação de partidas e processamento/navegação com componentes reais |
| Ferramenta | Codex/ChatGPT |
| Prompt/instrução | [Prompt de melhoria da integração](prompts/game-integracao-entrega-2.md) |
| Resultado | [Resposta com 12 testes de integração](responses/game-integracao-entrega-2.md). A versão inicial de dois casos aprovados está no commit `0990830`. A suíte revisada atravessa arquivos temporários, LargeFile, PgnIterator, GameLoader, Game, MoveList e Board sem dublês. Os 12 casos passaram; as quatro suítes ligadas a Game tiveram 74 testes aprovados e a regressão completa do Chesslib teve 281 |
| Decisão | Aceitos arquivos criados por cada teste, FENs explícitos, verificação de metadados e anotações, navegação da variação e retorno à linha principal, erros com contexto e exportação com releitura. Mantidas as classes de produção e as dependências existentes. Nenhuma expectativa da resposta precisou ser rejeitada nesta etapa; não foi necessário um commit corretivo adicional |
| Validação | A distinção em relação aos testes anteriores foi verificada no caminho real de execução: PgnHolder lê bytes do arquivo e usa PgnIterator/GameLoader para criar Game; MoveList interpreta SAN e Board executa movimentos legais. FENs completos foram definidos pelas posições esperadas após cada sequência, evitando validar só o índice ou comparar dois resultados da mesma implementação. A variação com dois meios-lances e linha principal com quatro detecta o uso do tamanho da lista errada. Os erros verificam evento/rodada e causas específicas; o teste de exportação exige conteúdo gravado, relê outro objeto e confere metadados, SAN, comentários, NAG, variação e FEN conhecido. Arquivos vazios, ausentes, LF/CRLF e duas partidas de eventos distintos completam as partições escolhidas. JUnit TemporaryFolder separa os dados de cada teste. Comandos: mvn -B -f chesslib/pom.xml -Dtest=GameTest,GameIsolatedTest,GameProcessingTest,GameIntegrationTest test (74 aprovados) e mvn -B -f chesslib/pom.xml test (281 aprovados). Isso valida os fluxos cobertos, sem provar todas as regras de xadrez, todos os encodings, agrupamento de partidas no mesmo evento, fechamento de recursos em todo erro ou metas de cobertura/mutação. |

### Evidências relacionadas

- [Entrada: prompt utilizado](prompts/game-integracao-entrega-2.md).
- [Saída: suíte revisada](responses/game-integracao-entrega-2.md).
- [Testes executáveis](../../chesslib/src/test/java/com/github/bhlangonijr/chesslib/game/GameIntegrationTest.java).

## Registro E2-WES-GAME-04 — Reaproveitamento de JaCoCo e PIT para Game

| Informação | Registro |
| --- | --- |
| Data | 22/11/2026 |
| Responsável | Weslley Ribeiro |
| Entrega | Entrega 2 |
| Atividade | Adaptar para Game as ferramentas da contribuição de Lucas, integrada à main pelo PR #14 |
| Ferramenta | Codex/ChatGPT |
| Prompt/instrução | [Prompt de adaptação](prompts/game-configuracao-qualidade-entrega-2.md) |
| Resultado | [Perfil Maven game-quality e comandos](responses/game-configuracao-qualidade-entrega-2.md). JaCoCo: 189/218 branches de Game, 86,70%; complexidade ciclomática agregada 176, incluindo loadMoveText com 57, toPgn com 26 e translateVariation com 13. PIT: 180/208 mutações mortas, 86,54% (87% exibido), 23 sobreviventes e 5 sem cobertura, sem erro de execução. Regressão completa integrada: 292 testes aprovados |
| Decisão | Reutilizadas as versões e metas de Lucas: JaCoCo 0.8.12, PIT 1.17.4 e mínimos de 80%. Aceito um perfil optativo para Game, mantendo os filtros padrão de MoveGenerator. Usados filtros diferentes para o agente (nome da classe) e relatório (arquivo Game.class), com substituição explícita das listas herdadas. Relatórios separados para evitar misturar métricas. Não foram alterados testes, produção ou limites para elevar os resultados |
| Validação | O relatório XML foi inspecionado para confirmar que a análise contém exatamente Game e que a cobertura não está vazia. A regra de branches de Game foi aprovada no verify com os 292 testes do Chesslib; o PIT selecionou Game e suas quatro suítes pelo filtro Game*Test. O XML de mutações confirmou os estados KILLED=180, SURVIVED=23 e NO_COVERAGE=5, sem excluir sobreviventes do denominador. A execução padrão de Lucas também foi revalidada com seus 20 testes e a regra JaCoCo aprovada, mostrando que o perfil não substituiu o padrão. A agregação de complexidade e as contagens são da versão analisada e deverão ser reavaliadas após refatorações. Cobertura de branches ainda precisa ser relacionada ao critério todas-arestas no relatório acadêmico; as mutações sobreviventes e sem cobertura precisam de análise individual. |

### Evidências relacionadas

- [Entrada: prompt utilizado](prompts/game-configuracao-qualidade-entrega-2.md).
- [Saída: perfil e comandos](responses/game-configuracao-qualidade-entrega-2.md).
- [Configuração executável](../../chesslib/pom.xml).
- Relatório JaCoCo gerado localmente: `chesslib/target/site/jacoco-game/index.html`.
- Relatório PIT gerado localmente: `chesslib/target/pit-reports-game/index.html`.
- Os relatórios completos são gerados pelos comandos preservados na resposta; scripts e logs não foram adicionados ao repositório nesta etapa.

## E2-WES-GAME-05 — Análise de cobertura e mutantes de Game

| Informação | Registro |
| --- | --- |
| Data | 22/11/2026 |
| Responsável | Weslley Ribeiro |
| Entrega | Entrega 2 |
| Atividade | Analisar as lacunas JaCoCo/PIT de Game e melhorar os testes dirigidos pelos relatórios |
| Ferramenta | Codex/ChatGPT |
| Prompt/instrução | [Instrução de revisão](prompts/game-analise-cobertura-mutantes-entrega-2.md) |
| Resultado | [Saída, casos e análise individual](responses/game-analise-cobertura-mutantes-entrega-2.md). GameQualityTest passou de cinco para 13 casos; regressão completa de 305 testes. Branches: 195/218 = 89,45%. PIT: 201/208 = 96,63%, sete sobreviventes e nenhum sem cobertura ou erro na execução final. Analisados os 28 mutantes inicialmente não mortos e as 29 saídas de decisão inicialmente ausentes |
| Decisão | Aceitos os oráculos exatos de variações, limites de navegação, metadados não padrão e falha controlada de conversão. Preservados produção, POM e contribuição de Lucas. Nenhum sobrevivente excluído do denominador. Não aceita a conclusão de equivalência universal dos sobreviventes nem a identidade automática entre percentual C1 e um grafo completo de arestas. A saída documenta a operacionalização e os limites do critério acadêmico |
| Validação | A comparação dos XMLs por método, descritor, operador e índice identifica 21 mutantes antes não mortos agora KILLED, sem regressão dos 180 antes mortos. Os oráculos de PGN verificam ordem, numeração, retomada e fechamento completo das variantes; não apenas presença de palavras. Limites de navegação distinguem falso antes do fim e verdadeiro no último índice/acima. Valores não vazios e posição inicial não zero detectam apagamento de estado. O stub de MoveList permite exercitar o tratamento explícito da falha de conversão sem modificar Game. A regressão completa e a regra de cobertura passaram. Duas execuções restritas do PIT falharam com MINION_DIED; a execução final fora da restrição concluiu, e somente seu XML foi usado no escore. As sete limitações restantes foram analisadas e mantidas explícitas. |

### Evidências relacionadas

- Versão inicial: commit `0d5dbf9`, somente código com cinco casos.
- [Entrada da revisão](prompts/game-analise-cobertura-mutantes-entrega-2.md).
- [Saída da revisão e decisões](responses/game-analise-cobertura-mutantes-entrega-2.md).
- [Suíte final executável](../../chesslib/src/test/java/com/github/bhlangonijr/chesslib/game/GameQualityTest.java).
- Relatórios completos gerados localmente em `chesslib/target/site/jacoco-game/` e `chesslib/target/pit-reports-game/`; sem scripts ou logs brutos novos no repositório.

## E2-WES-GAME-06 — Inspeção Sonar e refatoração de Game

| Informação | Registro |
| --- | --- |
| Data | 22/11/2026 |
| Responsável | Weslley Ribeiro |
| Entrega | Entrega 2 |
| Atividade | Inspeção inicial/final e correção dos apontamentos Sonar de Game |
| Ferramenta | Codex/ChatGPT; SonarQube Community Build 26.11.0.134175 |
| Prompt/instrução | [Revisão das correções](prompts/game-inspecao-sonar-entrega-2.md) |
| Resultado | [Saída da revisão](responses/game-inspecao-sonar-entrega-2.md). Perfil comprehensive com 608 regras: 16 → 0 apontamentos. Regressão: 307 testes; branches: 166/179 = 92,74%; PIT: 203/213 = 95,31%, sete sobreviventes e três sem cobertura, sem erro de execução |
| Decisão | Aceitas extrações por responsabilidade, nomes claros, exceções específicas e retorno não nulo de toString. A mudança de contrato e os dois casos novos de robustez estão explícitos no relatório. Rejeitada a substituição indiscriminada de trim por isBlank por diferença semântica. Produção dos colegas e POM preservados. Não usar zero do Sonar como prova de ausência de defeitos, nem esconder mutantes restantes |
| Validação | Capturas reais e APIs conferem o mesmo perfil e escopo em dois projetos que preservam as versões inicial/final; nenhuma regra foi desativada. As saídas completas de PGN continuam a verificar numeração, parentesco e retomada das variantes; comentários, navegação e integração com arquivos reais passaram na regressão. A falha controlada de conversão e as entradas vazia/incompleta verificam os motivos concretos da correção de toString. Os auxiliares de lógica permanecem em Game e foram incluídos nas medições; os tipos internos novos guardam dados. O XML PIT confirma todos os estados e o denominador completo de 213; a regra JaCoCo mínima de 80% foi aprovada. Complexidade cognitiva diminuiu de 218 para 109, mas a ciclomática agregada Sonar cresceu de 174 para 177, portanto não se afirma redução desta última. |

### Evidências relacionadas

- Correções iniciais: commit `71df887`, somente código de Game.
- [Entrada](prompts/game-inspecao-sonar-entrega-2.md) e [saída](responses/game-inspecao-sonar-entrega-2.md) da revisão.
- [Relatório e capturas antes/depois](../inspecao/game/relatorio.md).
- [Classe corrigida](../../chesslib/src/main/java/com/github/bhlangonijr/chesslib/game/Game.java) e [casos de robustez](../../chesslib/src/test/java/com/github/bhlangonijr/chesslib/game/GameQualityTest.java).

## Modelo para novos registros

### Registro NNN — Título da interação

| Informação | Registro |
| --- | --- |
| Data | DD/MM/AAAA |
| Responsável | Nome do integrante |
| Entrega | Entrega 1 ou Entrega 2 |
| Atividade | Atividade do trabalho associada |
| Ferramenta | ChatGPT, Claude, Copilot etc. |
| Prompt/instrução | Prompt completo ou referência para um arquivo armazenado em `docs/ai/` |
| Resultado | Resumo objetivo da resposta produzida pela IA |
| Decisão | O que foi aceito, alterado ou rejeitado pelo grupo |
| Validação | Como o resultado foi revisado e verificado |

### Evidências relacionadas

- Solução inicial gerada pela IA: caminho ou link.
- Solução final revisada: caminho ou link.
- Alterações realizadas após a revisão: descrição resumida.


## Registro 004 — Redução da complexidade dos métodos `encode` e `decodeSan`

| **Informação** | **Registro** |
| --- | --- |
| Data | 20/11/2026 |
| Responsável | Lucio |
| Entrega | Entrega 2 |
| Atividade | Inspeção e correção da classe `MoveList` com base nos problemas identificados pelo Sonar |
| Ferramenta | ChatGPT |
| Artefato relacionado | `MoveList.java` |
| Problemas analisados | Complexidade cognitiva dos métodos `encode` e `decodeSan` |

### Prompt/instrução utilizada

> Analise os problemas de complexidade cognitiva apontados pelo Sonar na classe `MoveList.java` do Chesslib. O método `encode` possui complexidade cognitiva acima do limite permitido e o método `decodeSan` possui complexidade cognitiva 88, enquanto o limite configurado é 15. Ajude a refatorar os métodos de forma incremental, extraindo responsabilidades para métodos auxiliares, sem alterar o comportamento da biblioteca.

### Resultado apresentado pela IA

A IA recomendou que a refatoração fosse realizada de forma incremental, evitando substituir toda a implementação de uma vez. A estratégia utilizada foi identificar responsabilidades internas independentes e movê-las para métodos auxiliares menores.

No método `encode`, foram identificadas três responsabilidades principais:

1. codificação de movimentos de roque;
2. resolução de movimentos ambíguos;
3. montagem da notação SAN ou FAN do movimento.

A lógica de roque foi extraída para o método:

```java
encodeCastleMove(...)
```

A lógica de identificação e desambiguação da origem do movimento foi extraída para:

```java
appendAmbiguityResolution(...)
```

Com essas extrações, o método `encode` passou a apenas coordenar a construção da notação, a execução do movimento, o tratamento de capturas, a promoção e a indicação de xeque ou xeque-mate.

No método `decodeSan`, a maior parte da complexidade estava concentrada na identificação da casa de origem do movimento. A IA sugeriu separar essa lógica nos seguintes métodos:

```java
findPawnSourceSquare(...)
findNonPawnSourceSquare(...)
determineSourcePieceType(...)
extractSourceLocation(...)
findAmbiguousSourceSquare(...)
filterCandidatesByLocation(...)
```

Também foram realizadas as seguintes simplificações:

- separação do tratamento de roque pequeno e roque grande em retornos antecipados;
- remoção da atribuição inicial desnecessária `Square from = Square.NONE`;
- manutenção das validações de posição, promoção e movimento inválido;
- preservação das exceções `MoveConversionException`;
- uso de retornos antecipados para reduzir níveis de aninhamento.

### Evolução observada no Sonar

| **Método** | **Complexidade inicial** | **Valores intermediários** | **Resultado final** |
| --- | --- | --- | --- |
| `encode` | 33 | 26 após a extração do roque | Aviso de complexidade eliminado |
| `decodeSan` | 88 | 81 após a extração do movimento de peão e 17 após a extração da busca da origem | Aviso de complexidade eliminado |

Após as refatorações, o Sonar deixou de apresentar problemas de complexidade cognitiva nos métodos `encode` e `decodeSan`. A análise final ainda apresentou um aviso independente relacionado ao uso de `ThreadLocal` por `boardHolder`, que não fazia parte desta interação específica.

### Decisão

As sugestões de extração de métodos foram aceitas porque reduziram a complexidade sem alterar as regras de conversão de movimentos.

Foram mantidos os seguintes comportamentos:

- conversão para SAN e FAN;
- identificação de roque pequeno e grande;
- identificação de capturas;
- resolução de movimentos ambíguos;
- promoção de peões;
- identificação da casa de origem;
- tratamento de movimentos ilegais;
- indicação de xeque e xeque-mate.

As alterações foram aplicadas uma por vez. Depois de cada etapa, os testes foram executados antes de continuar a refatoração. Essa decisão reduziu o risco de introduzir uma regressão difícil de localizar.

### Alterações realizadas após a revisão

- extração da lógica de roque de `encode`;
- extração da lógica de desambiguação de movimentos de `encode`;
- extração da busca da origem de movimentos de peão;
- separação da busca da origem dos demais movimentos;
- criação de métodos específicos para tipo da peça, localização, filtragem de candidatos e movimento ambíguo;
- substituição de estruturas aninhadas por retornos antecipados;
- remoção de atribuição local desnecessária;
- reorganização do código para que cada método possua uma responsabilidade mais específica.

### Validação

A resposta da IA foi validada de forma incremental. Inicialmente, foi realizada uma leitura manual do código sugerido, comparando cada método auxiliar com o trecho correspondente da implementação original. Essa comparação foi utilizada para verificar se nenhuma condição, exceção ou atualização do estado do tabuleiro havia sido removida.

Depois de cada alteração, o projeto foi compilado e as suítes MoveListTest e MoveListQetTest foram executadas. A refatoração somente foi mantida quando todos os testes continuaram passando. Dessa forma, caso uma alteração provocasse uma regressão, seria possível identificar exatamente em qual etapa ela havia sido introduzida.

Também foi executada novamente a análise do Sonar após cada conjunto de alterações. Os valores intermediários demonstraram que algumas sugestões eram úteis, mas ainda insuficientes:
- encode: complexidade reduzida de 33 para 26, seguida de novas extrações até a eliminação do aviso;
- decodeSan: complexidade reduzida de 88 para 81 e posteriormente para 17, exigindo outra revisão até ficar dentro do limite permitido.

Na etapa em que a refatoração foi validada, estavam disponíveis 12 testes de `MoveListTest` e 8 testes de `MoveListQetTest`. Assim, a validação daquela etapa foi composta pelas seguintes evidências:

- execução bem-sucedida dos 12 testes de `MoveListTest`;
- execução bem-sucedida dos 8 testes de `MoveListQetTest` disponíveis naquele estágio;
- total de 20 testes executados, sem falhas, erros ou testes ignorados;
- resultado BUILD SUCCESS no Maven;
- desaparecimento dos avisos de complexidade de encode e decodeSan;
- revisão manual dos comportamentos de roque, captura, promoção, ambiguidade, movimentos ilegais, xeque e xeque-mate;
- comparação entre os resultados inicial e final do Sonar.

Em uma validação posterior do valor exato do escore de mutação, a suíte `MoveListQetTest` foi ampliada para 17 casos, totalizando 29 testes diretamente associados à `MoveList`. Com o novo teste, a regressão completa passou a executar 316 testes sem falhas, erros ou testes ignorados.

Com base nesses resultados, as refatorações foram consideradas válidas porque produziram uma redução mensurável da complexidade, preservaram o comportamento verificado pelos testes e não introduziram falhas conhecidas. O resultado da IA foi, portanto, utilizado como ponto de partida, mas foi revisado, aplicado gradualmente e aceito somente após validação técnica.

### Evidências relacionadas

- Classe revisada: `chesslib/src/main/java/com/github/bhlangonijr/chesslib/move/MoveList.java`
- Evidência inicial do Sonar: `docs/inspecao/movelist/sonar_antes.png`
- Evidência após as correções de complexidade: `docs/inspecao/movelist/sonar_depois.png`

## Registro 005 — Configuração e validação de cobertura e mutação da `MoveList`

| **Informação** | **Registro** |
| --- | --- |
| Data | 22/11/2026 |
| Responsável | Lucio |
| Entrega | Entrega 2 |
| Atividade | Configuração e validação da cobertura estrutural com JaCoCo e do teste de mutação com PIT |
| Ferramenta | ChatGPT |
| Artefatos relacionados | `chesslib/pom.xml`, `MoveListTest.java` e `MoveListQetTest.java` |
| Classe analisada | `com.github.bhlangonijr.chesslib.move.MoveList` |

### Prompt/instrução utilizada

> Ajude a configurar o JaCoCo e o PIT no `pom.xml` do Chesslib para analisar somente a classe `MoveList`. A configuração deve exigir pelo menos 80% de cobertura de branches e 80% de mutação, utilizar `MoveListTest` e `MoveListQetTest` e não substituir a configuração já existente para `MoveGenerator`. Também indique os comandos necessários para executar e validar os relatórios.

### Resultado apresentado pela IA

A IA recomendou criar um perfil Maven separado chamado `movelist-quality`, preservando a configuração padrão já existente no projeto.

Para o JaCoCo, foram sugeridas as seguintes configurações:

- instrumentação somente da classe `MoveList`;
- relatório separado em `target/site/jacoco-movelist`;
- arquivo de execução separado em `target/jacoco-movelist.exec`;
- verificação mínima de 80% de cobertura de branches;
- uso de `combine.self="override"` para substituir os filtros herdados no perfil.

Para o PIT, foram sugeridas:

- seleção de `MoveList` como classe-alvo;
- seleção de `MoveListTest` e `MoveListQetTest` como testes-alvo;
- relatório separado em `target/pit-reports-movelist`;
- manutenção do limite mínimo de 80% de mutação já configurado no projeto.

A primeira execução do JaCoCo utilizando somente as duas suítes diretamente relacionadas à `MoveList` apresentou 73% de branches. A IA explicou que outras suítes do Chesslib também exercitam caminhos da classe e recomendou executar a regressão completa para reproduzir a medição final.

Também foram analisadas duas dificuldades do ambiente:

- falha na geração do Javadoc devido à configuração local do `JAVA_HOME`, contornada com `-Dmaven.javadoc.skip=true`;
- falhas de testes dependentes da codificação do Windows, corrigidas na sessão com `JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8`.

### Decisão

A sugestão do perfil separado foi aceita porque permite executar as verificações específicas da `MoveList` sem alterar o comportamento padrão configurado para `MoveGenerator` e sem substituir os relatórios dos outros integrantes.

Foram utilizados os seguintes comandos:

```text
set "JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF-8"
mvn -f chesslib\pom.xml -Pmovelist-quality clean verify -Dmaven.javadoc.skip=true
mvn -f chesslib\pom.xml -Pmovelist-quality org.pitest:pitest-maven:mutationCoverage
```

### Validação

A resposta da IA foi validada inicialmente pela revisão da configuração adicionada ao `pom.xml`. O perfil `movelist-quality` foi comparado com a configuração já existente para verificar se o JaCoCo e o PIT estavam direcionados somente à classe `MoveList` e se a análise de `MoveGenerator` permanecia preservada.

Após a configuração do perfil, foi realizada uma primeira execução do JaCoCo utilizando apenas as suítes `MoveListTest` e `MoveListQetTest`. Essa execução apresentou 73% de cobertura de branches, valor inferior ao mínimo de 80%. Como outros testes do Chesslib também exercitam métodos da `MoveList`, foi executada posteriormente a regressão completa do projeto.

Na medição individual utilizada para gerar os relatórios da `MoveList`, 248 testes foram aprovados, sem falhas, erros ou testes ignorados. O relatório do JaCoCo registrou:

- 159 de 196 branches cobertos, correspondendo a 81%;
- 343 de 388 linhas cobertas, correspondendo a 88%;
- limite mínimo de 80% de branches atendido;
- resultado `BUILD SUCCESS`.

Em seguida, o PIT foi executado utilizando `MoveListTest` e `MoveListQetTest` como suítes-alvo. A primeira execução registrou 168 de 211 mutantes mortos. Embora o PIT apresentasse o valor arredondado de 80%, o cálculo exato correspondia a 79,62%, ainda abaixo do mínimo exigido.

O relatório detalhado `mutations.xml` foi analisado para identificar os mutantes sobreviventes. Entre eles, foi identificado um mutante no método `toFan()`, que substituía o retorno correto por uma string vazia. A sugestão de criar um teste específico para esse comportamento foi aceita porque a documentação do próprio método define que uma lista não vazia deve produzir a notação FAN correspondente.

Foi então adicionado o teste `testConversaoFanSemNumeracao`, que utiliza a sequência `e4 e5 Nf3` e verifica o retorno completo de `toFan()`. Após essa alteração, o relatório de mutação registrou:

- 169 de 211 mutantes mortos, correspondendo exatamente a 80,09%;
- cobertura de linhas de 316/388, correspondendo a 81%;
- força dos testes de 169/185, correspondendo a 91%;
- 26 mutantes sem cobertura e 16 sobreviventes;
- resultado `BUILD SUCCESS`.

Após a inclusão do novo teste, a regressão completa foi executada novamente e apresentou 316 testes aprovados, sem falhas, erros ou testes ignorados. O perfil `movelist-quality` também terminou com `BUILD SUCCESS`, confirmando que o limite de cobertura estrutural continuava atendido.

A validação final foi composta pelas seguintes evidências:

- execução bem-sucedida dos 29 testes diretamente relacionados à `MoveList`;
- execução bem-sucedida dos 316 testes da regressão completa do Chesslib;
- cobertura de branches superior ao mínimo exigido;
- escore exato de mutação de 80,09%, superior ao mínimo de 80%;
- eliminação do mutante que substituía o retorno de `toFan()` por uma string vazia;
- correspondência entre os valores apresentados no terminal e no relatório do PIT;
- preservação da configuração existente para `MoveGenerator`.

Com base nesses resultados, o novo teste foi considerado válido porque verifica um comportamento previsto no contrato de `toFan()`, eliminou um mutante sobrevivente sem modificar o código de produção, elevou o escore para acima do limite exigido e não introduziu regressões nos demais testes.

### Evidências relacionadas

- Configuração Maven: `chesslib/pom.xml`
- Testes desenvolvidos: `chesslib/src/test/java/com/github/bhlangonijr/chesslib/move/MoveListQetTest.java`
- Testes originais utilizados: `chesslib/src/test/java/com/github/bhlangonijr/chesslib/move/MoveListTest.java`
- Relatório consolidado: `docs/entrega2/lucio/movelist-testes-integracao.md`
- Relatório JaCoCo gerado localmente: `chesslib/target/site/jacoco-movelist/index.html`
- Relatório PIT gerado localmente: `chesslib/target/pit-reports-movelist/index.html`

## Registro 006 — Testes unitários e cobertura do `MoveGenerator`

| Informação | Registro |
|---|---|
| Data | 19/11/2026 |
| Responsável | Lucas Ardelino |
| Entrega | Entrega 2 |
| Atividade | Ampliação dos testes unitários da classe `MoveGenerator` e configuração da cobertura estrutural |
| Ferramentas | ChatGPT/Codex, Maven, JUnit e JaCoCo |
| Artefatos relacionados | `LucasMoveGeneratorTest.java`, `MoveGenerator.java` e `movegenerator-cobertura.md` |
| Prompt/instrução | Solicitei apoio para identificar cenários importantes da classe `MoveGenerator` e ampliar a cobertura dos métodos relacionados à geração de movimentos |
| Resultado | Foram adicionados testes para peões, capturas, promoções, movimentos de peças, máscaras, movimentos pseudo-legais e roques |
| Decisão | As sugestões foram revisadas e adaptadas ao funcionamento real da biblioteca. Foram mantidos os casos que apresentavam resultados esperados claros e verificáveis |
| Validação | Lucas executou os testes com Maven e conferiu os relatórios do JaCoCo. A execução selecionada teve 20 testes aprovados e cobertura de branches acima de 80% |
| Evidência no GitHub | [Commit `017a24d`](https://github.com/sofiarecreio/trabalho-qet/commit/017a24d), de 19/11/2026 |

## Registro 007 — Integração do PetClinic e teste de mutação

| Informação | Registro |
|---|---|
| Data | 19/11/2026 |
| Responsável | Lucas Ardelino |
| Entrega | Entrega 2 |
| Atividade | Criação do teste de integração da busca de proprietário e configuração do teste de mutação do `MoveGenerator` |
| Ferramentas | ChatGPT/Codex, Maven, JUnit, PIT e Spring Boot |
| Artefatos relacionados | `LucasOwnerIntegrationTests.java`, `mutacao-movegenerator.md` e configuração do `pom.xml` |
| Prompt/instrução | Solicitei apoio para definir um teste de integração utilizando as camadas reais do PetClinic e analisar quais mutações do `MoveGenerator` eram detectadas pelos testes |
| Resultado | O teste de integração verificou a busca de proprietários e o acesso aos detalhes de Betty Davis. O PIT foi executado sobre o `MoveGenerator` para avaliar a força dos testes |
| Decisão | Foi mantido o uso dos componentes reais da aplicação, sem substituir o fluxo por mocks. Na mutação, os resultados foram analisados sem remover mutantes apenas para aumentar o percentual |
| Validação | Lucas executou os testes com Maven, conferiu as respostas da aplicação e analisou o relatório do PIT, incluindo mutantes mortos, sobreviventes e sem cobertura |
| Evidência no GitHub | [Commit `df1b211`](https://github.com/sofiarecreio/trabalho-qet/commit/df1b211), de 19/11/2026 |

## Registro 008 — Teste de sistema com Selenium

| Informação | Registro |
|---|---|
| Data | 22/11/2026 |
| Responsável | Lucas Ardelino |
| Entrega | Entrega 2 |
| Atividade | Execução do teste de sistema para buscar um proprietário pela interface web do PetClinic |
| Ferramentas | ChatGPT/Codex, Selenium WebDriver, Chrome, JUnit e Maven |
| Artefatos relacionados | `LucasOwnerSeleniumTests.java`, `selenium-petclinic.md` e a captura versionada do cenário |
| Prompt/instrução | Solicitei apoio para organizar o roteiro do teste, revisar as verificações do código e registrar a evidência do cenário realizado no PetClinic |
| Resultado | Lucas realizou o fluxo no sistema: acessou a busca de proprietários, informou o sobrenome `Davis`, enviou o formulário e conferiu a presença de Betty Davis e Harold Davis na tabela |
| Decisão | A IA foi utilizada somente como apoio técnico na organização do roteiro e na revisão das asserções. A execução do cenário, a conferência do resultado e a seleção da evidência ficaram sob responsabilidade de Lucas |
| Validação | O teste foi executado com Maven usando Chrome em modo headless. O resultado foi aprovado e a captura da tela foi conferida e copiada para a documentação versionada |
| Evidência no GitHub | [Commit `77881c1`](https://github.com/sofiarecreio/trabalho-qet/commit/77881c1), de 22/11/2026 |

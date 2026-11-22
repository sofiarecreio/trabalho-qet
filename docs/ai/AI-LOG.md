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
A validação final foi composta pelas seguintes evidências:
- execução bem-sucedida dos 12 testes de MoveListTest;
- execução bem-sucedida dos 8 testes de MoveListQetTest;
- total de 20 testes executados, sem falhas, erros ou testes ignorados;
- resultado BUILD SUCCESS no Maven;
- desaparecimento dos avisos de complexidade de encode e decodeSan;
- revisão manual dos comportamentos de roque, captura, promoção, ambiguidade, movimentos ilegais, xeque e xeque-mate;
- comparação entre os resultados inicial e final do Sonar.
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

Na execução completa, 248 testes foram aprovados, sem falhas, erros ou testes ignorados. O relatório do JaCoCo registrou:

- 159 de 196 branches cobertos, correspondendo a 81%;
- 343 de 388 linhas cobertas, correspondendo a 88%;
- limite mínimo de 80% de branches atendido;
- resultado `BUILD SUCCESS`.

Em seguida, o PIT foi executado utilizando `MoveListTest` e `MoveListQetTest` como suítes-alvo. O relatório de mutação registrou:

- 168 de 211 mutantes mortos, correspondendo a 80%;
- cobertura de linhas de 316/388, correspondendo a 81%;
- força dos testes de 168/185, correspondendo a 91%;
- 26 mutantes sem cobertura e 17 sobreviventes;
- resultado `BUILD SUCCESS`.

A validação final foi composta pelas seguintes evidências:

- execução bem-sucedida dos 248 testes da regressão do Chesslib;
- cobertura de branches superior ao mínimo exigido;
- escore de mutação igual ao mínimo exigido;
- relatórios HTML gerados separadamente para a `MoveList`;
- correspondência entre os valores apresentados no terminal e nos relatórios;
- preservação da configuração existente para `MoveGenerator`.

Com base nesses resultados, a configuração sugerida foi considerada válida porque permitiu reproduzir as análises de cobertura e mutação, atingiu os limites definidos, preservou o funcionamento dos testes existentes e manteve os resultados da `MoveList` separados das análises dos outros integrantes.

### Evidências relacionadas

- Configuração Maven: `chesslib/pom.xml`
- Testes desenvolvidos: `chesslib/src/test/java/com/github/bhlangonijr/chesslib/move/MoveListQetTest.java`
- Testes originais utilizados: `chesslib/src/test/java/com/github/bhlangonijr/chesslib/move/MoveListTest.java`
- Relatório consolidado: `docs/entrega2/lucio/movelist-testes-integracao.md`
- Relatório JaCoCo gerado localmente: `chesslib/target/site/jacoco-movelist/index.html`
- Relatório PIT gerado localmente: `chesslib/target/pit-reports-movelist/index.html`

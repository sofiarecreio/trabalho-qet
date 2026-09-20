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
| Validação | A execução inicial de `mvn -Dtest=GameTest test` executou 16 testes: 15 passaram e 1 falhou. A falha ocorreu porque a IA esperou o FEN com campo de en passant `-`, enquanto o `Board` produziu `e6`. A expectativa foi revisada posteriormente. A suíte revisada também reproduziu defeitos de navegação da implementação de `Game` |

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

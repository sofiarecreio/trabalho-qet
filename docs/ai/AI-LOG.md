# Registro de Uso de Inteligência Artificial

Este documento registra as interações com ferramentas de Inteligência Artificial que contribuíram substancialmente para os artefatos do trabalho de Qualidade e Teste.

Interações usadas apenas para correções ortográficas ou configurações simples de ambiente não precisam ser registradas.

## Registro 001 — Geração da suíte inicial de testes unitários de `Game`

| Informação | Registro |
| --- | --- |
| Data | 20/09/2026 |
| Responsável | Weslley |
| Entrega | Entrega 1 |
| Atividade | Projeto da suíte inicial de testes unitários para a classe não CRUD `Game`, do Chesslib |
| Ferramenta | ChatGPT — GPT-5.6 Sol, raciocínio leve |
| Prompt/instrução | Prompt completo armazenado em `docs/ai/prompts/game-suite-entrega-1.md`, acompanhado do código-fonte completo de `Game.java`. |
| Resultado | A IA propôs 15 casos de teste e uma implementação inicial de `GameTest.java`. Foram contemplados estado inicial, MoveText/PGN válido, vazio e inválido, comentários, NAGs, variações, navegação, limites, atualização do `Board`, geração de PGN e fim da lista. A resposta também analisou dependências e apontou possíveis defeitos em `isStartOfMoveList()` e `gotoLast(MoveList)`. |
| Decisão | A saída inicial foi preservada e incorporada sem correções para manter a evidência original produzida pela IA. Não foi adicionado Mockito nesta etapa. A revisão e as correções serão realizadas posteriormente. |
| Validação | A execução de `mvn -Dtest=GameTest test` executou 16 testes: 15 passaram e 1 falhou. A falha ocorreu em `shouldUpdatePositionAndBoardWhenGotoMoveIsCalled`, pois a IA esperou o FEN com campo de en passant `-`, enquanto o `Board` produziu `e6`. A inconsistência foi registrada, mas não corrigida nesta versão inicial. |

### Evidências relacionadas

- Prompt utilizado: `docs/ai/prompts/game-suite-entrega-1.md`
- Classe analisada: `chesslib/src/main/java/com/github/bhlangonijr/chesslib/game/Game.java`
- Destino da solução revisada: `chesslib/src/test/java/com/github/bhlangonijr/chesslib/game/GameTest.java`
- Saída inicial completa da IA: `docs/ai/responses/game-suite-entrega-1-gpt-5.6-sol.md`

## Modelo para novos registros

Copie esta seção para cada interação relevante.

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

- Solução inicial gerada pela IA: caminho ou link
- Solução final revisada: caminho ou link
- Alterações realizadas após a revisão: descrição resumida

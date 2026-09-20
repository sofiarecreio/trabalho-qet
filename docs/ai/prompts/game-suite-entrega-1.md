# Prompt — Suíte inicial de testes unitários da classe `Game`

Atue como um testador de software experiente. Estou realizando a Entrega 1 de um trabalho acadêmico da disciplina Qualidade e Teste. O sistema livre escolhido é o Chesslib, escrito em Java, e a unidade sob minha responsabilidade é a classe não CRUD `Game`.

O projeto utiliza Maven, Java 11 e JUnit 4.13.1. O código-fonte completo de `Game.java` será fornecido ao final deste prompt. Analise o código antes de responder.

## Objetivo

Projete uma suíte inicial de testes unitários para verificar, de forma controlada e tão isolada quanto for viável, os comportamentos da classe `Game`. Cada caso deve deixar claros:

- o estado inicial e as entradas fornecidas pelo driver de teste;
- a ação executada na unidade sob teste;
- o resultado esperado, que funcionará como oráculo;
- as asserções JUnit 4 que compararão o resultado observado com o esperado.

## Escopo da Entrega 1

1. Teste regras e comportamentos relevantes, evitando limitar a suíte a getters e setters triviais.
2. Considere, conforme existirem na implementação:
   - estado inicial de uma partida;
   - carregamento de jogadas em PGN por `loadMoveText`;
   - comentários, variações e NAGs;
   - navegação por `gotoMove`, `gotoFirst`, `gotoLast`, `gotoNext` e `gotoPrior`;
   - atualização do tabuleiro e da posição corrente;
   - geração de PGN por `toPgn`;
   - entradas válidas, vazias, inválidas e valores de fronteira relevantes;
   - exceções previstas pela implementação.
3. Não inclua testes de integração, sistema, Selenium, mutação ou metas de cobertura estrutural, pois pertencem à Entrega 2.
4. Não altere `Game.java` para facilitar os testes.

## Isolamento e testabilidade

Identifique as dependências utilizadas por `Game`, como `Board`, `Round`, `MoveList`, jogadores e outras classes relacionadas.

- Explique quais dependências podem ser usadas como objetos reais simples e quais dificultam o isolamento.
- Quando um dublê for realmente necessário, classifique-o como dummy, fake, stub, spy ou mock e justifique a escolha.
- O projeto não possui Mockito configurado. Não use Mockito nem acrescente dependências automaticamente. Caso considere Mockito necessário, apresente essa sugestão separadamente, sem utilizá-lo no código da Entrega 1.
- Se faltar o código ou a assinatura de alguma dependência indispensável, solicite exatamente o elemento necessário, sem inventar APIs.

## Regras de implementação

1. Gere testes compatíveis exclusivamente com JUnit 4.13.1.
2. Use `@Before` quando houver estado comum que precise ser recriado antes de cada teste.
3. Use asserções adequadas, como `assertEquals`, `assertTrue`, `assertFalse`, `assertNull`, `assertNotNull`, `assertSame`, `assertNotSame` e `fail`.
4. Cada teste deve ser independente, determinístico, rápido e fácil de controlar.
5. Use nomes descritivos no padrão `should...When...`.
6. Organize cada teste explicitamente em Arrange, Act e Assert.
7. Não invente métodos, construtores ou dependências.
8. O arquivo final deverá ser:
   `src/test/java/com/github/bhlangonijr/chesslib/game/GameTest.java`

## Formato obrigatório da resposta

Primeiro, apresente uma tabela com:

- ID do caso de teste;
- comportamento ou método testado;
- objetivo;
- pré-condições e estado inicial;
- dados de entrada;
- ações;
- resultado esperado/oráculo;
- asserções utilizadas;
- justificativa da relevância;
- dependências envolvidas e estratégia de isolamento.

Depois da tabela:

1. Produza o conteúdo completo e compilável de `GameTest.java`.
2. Explique brevemente qual regra cada teste verifica.
3. Informe o comando Maven para executar apenas `GameTest`.
4. Liste suposições, limitações e pontos que exigem validação no repositório.
5. Não afirme que os testes passaram, pois você não executou o projeto.

## Código-fonte de `Game.java`

```java
COLE AQUI O CONTEÚDO COMPLETO DE Game.java
```

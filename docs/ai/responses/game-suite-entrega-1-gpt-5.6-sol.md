ANÁLISE E PROPOSTA DE TESTES UNITÁRIOS – CHESSLIB / GAME
Disciplina: Qualidade e Teste
Entrega 1

Analisei o Game.java fornecido. A classe concentra três comportamentos que merecem prioridade na Entrega 1: interpretação de MoveText/PGN, navegação/reconstrução do Board e serialização para PGN.

============================================================
1. CASOS DE TESTE PROPOSTOS
============================================================

CT01 – Construtor / estado inicial
Objetivo: validar o estado inicial relevante de uma partida.
Pré-condições: Game recém-criado com Round válido.
Entrada: gameId "game-1" e Round.
Ação: criar Game.
Resultado esperado: resultado ONGOING, posição 0 e lista principal vazia.
Asserções: assertEquals, assertNotNull, assertTrue, assertSame.
Relevância: confirma invariantes iniciais.
Dependências: Event e Round reais e simples.

CT02 – loadMoveText com PGN válido
Objetivo: carregar uma sequência válida de jogadas.
Estado inicial: partida sem jogadas.
Entrada: "1. e4 e5 2. Nf3 Nc6".
Ação: executar loadMoveText.
Resultado esperado: quatro half-moves com SAN e4, e5, Nf3, Nc6.
Asserções: assertNotNull, assertEquals, assertArrayEquals.
Dependências: MoveList real.

CT03 – loadMoveText com entrada vazia
Objetivo: verificar comportamento com MoveText vazio.
Entrada: "".
Ação: executar loadMoveText.
Resultado esperado: lista de jogadas existente e vazia.
Asserções: assertNotNull, assertEquals, assertTrue.
Relevância: classe de equivalência de entrada vazia.

CT04 – loadMoveText com entrada inválida
Objetivo: verificar rejeição de uma jogada SAN inválida.
Entrada: "1. e4 INVALID_MOVE".
Ação: executar loadMoveText.
Resultado esperado: ocorrência de exceção.
Asserções: fail e assertNotNull.
Relevância: robustez contra PGN inválido.

CT05 – Comentários
Objetivo: verificar interpretação de comentário em bloco.
Entrada: "1. e4 {good move} e5".
Ação: executar loadMoveText.
Resultado esperado: comentário "good move " associado ao índice 1.
Asserções: assertNotNull, assertTrue, assertEquals.
Relevância: exercita recurso específico do formato PGN.

CT06 – NAG
Objetivo: verificar interpretação de Numeric Annotation Glyph.
Entrada: "1. e4 $1 e5".
Ação: executar loadMoveText.
Resultado esperado: NAG "$1" associado ao índice 1.
Asserções: assertNotNull, assertTrue, assertEquals.

CT07 – Variação
Objetivo: verificar interpretação de uma variação PGN.
Entrada: "1. e4 (1. d4 d5) e5".
Ação: executar loadMoveText.
Resultado esperado: mapa de variações criado e não vazio; linha principal com duas jogadas.
Asserções: assertNotNull, assertFalse, assertEquals.

CT08 – gotoMove
Objetivo: verificar reconstrução do tabuleiro até uma posição.
Pré-condições: quatro jogadas carregadas e Board configurado.
Entrada: índice 1.
Ação: gotoMove(moves, 1).
Resultado esperado: posição 1, lista ativa correta e Board correspondente a e4/e5.
Asserções: assertEquals e assertSame.
Dependência: Board real.

CT09 – gotoFirst
Objetivo: navegar para a primeira jogada.
Pré-condições: partida carregada e posicionada no final.
Ação: gotoFirst().
Resultado esperado: posição 0.
Asserções: assertEquals e assertSame.

CT10 – gotoLast
Objetivo: navegar para a última jogada.
Entrada: lista de quatro jogadas.
Ação: gotoLast().
Resultado esperado: posição 3 e fim da lista.
Asserções: assertEquals e assertTrue.

CT11 – gotoNext / gotoPrior
Objetivo: verificar navegação incremental.
Pré-condição: posição 0.
Ação: gotoNext() e depois gotoPrior().
Resultado esperado: posições 1 e 0, respectivamente.
Asserções: assertEquals.

CT12 – gotoMove fora do intervalo
Objetivo: verificar valores de fronteira inválidos.
Entradas: -1 e moves.size().
Ação: executar gotoMove.
Resultado esperado: posição anterior preservada.
Asserções: assertEquals.

CT13 – toPgn
Objetivo: verificar geração de PGN com metadados e jogadas.
Pré-condições: Event, Round, jogadores, resultado e jogadas configurados.
Ação: toPgn(true, true).
Resultado esperado: tags essenciais, jogadores, PlyCount, jogadas e resultado presentes.
Asserções: assertNotNull e assertTrue.

CT14 – toPgn com comentário e NAG
Objetivo: verificar preservação de anotações na serialização.
Entrada: "1. e4 $1 {good move} e5".
Ação: carregar e serializar.
Resultado esperado: PGN contém "$1", "{good move }" e "e5".
Asserções: assertNotNull e assertTrue.

CT15 – isEndOfMoveList
Objetivo: verificar detecção do fim da lista.
Pré-condição: partida posicionada na última jogada.
Ação: isEndOfMoveList().
Resultado esperado: true.
Asserção: assertTrue.

Observação sobre isStartOfMoveList():
A implementação analisada é:

return getCurrentMoveList() == null && getPosition() == 0;

Isso parece inconsistente com o Javadoc. Uma lista não nula posicionada na primeira jogada produz false. Esse comportamento pode ser documentado como possível defeito em vez de ser normalizado por um teste que assuma que ele é correto.

============================================================
2. CONTEÚDO DE GameTest.java
============================================================

Caminho:
src/test/java/com/github/bhlangonijr/chesslib/game/GameTest.java

------------------------------------------------------------

package com.github.bhlangonijr.chesslib.game;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.MoveList;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameTest {

    private Event event;
    private Round round;
    private Game game;

    @Before
    public void setUp() {
        // Arrange comum
        event = new Event();
        event.setName("Test Event");
        event.setSite("Test Site");
        event.setStartDate("2026.09.20");

        round = new Round(event);
        round.setNumber(1);

        game = new Game("game-1", round);
    }

    @Test
    public void shouldInitializeGameWithExpectedStateWhenCreated() {
        // Arrange realizado em setUp()

        // Act
        GameResult result = game.getResult();
        int position = game.getPosition();
        MoveList moves = game.getHalfMoves();

        // Assert
        assertEquals(GameResult.ONGOING, result);
        assertEquals(0, position);
        assertNotNull(moves);
        assertTrue(moves.isEmpty());
        assertSame(round, game.getRound());
    }

    @Test
    public void shouldLoadMainMovesWhenValidPgnIsProvided() throws Exception {
        // Arrange
        StringBuilder moveText =
                new StringBuilder("1. e4 e5 2. Nf3 Nc6");

        // Act
        game.loadMoveText(moveText);

        // Assert
        assertNotNull(game.getHalfMoves());
        assertEquals(4, game.getHalfMoves().size());

        assertArrayEquals(
                new String[]{"e4", "e5", "Nf3", "Nc6"},
                game.getHalfMoves().toSanArray()
        );
    }

    @Test
    public void shouldKeepMoveListEmptyWhenEmptyMoveTextIsLoaded()
            throws Exception {

        // Arrange
        StringBuilder moveText = new StringBuilder("");

        // Act
        game.loadMoveText(moveText);

        // Assert
        assertNotNull(game.getHalfMoves());
        assertEquals(0, game.getHalfMoves().size());
        assertTrue(game.getHalfMoves().isEmpty());
    }

    @Test
    public void shouldThrowExceptionWhenInvalidMoveTextIsLoaded() {
        // Arrange
        StringBuilder moveText =
                new StringBuilder("1. e4 INVALID_MOVE");

        // Act
        try {
            game.loadMoveText(moveText);
            fail("Expected an exception for invalid PGN move text");
        } catch (Exception e) {
            // Assert
            assertNotNull(e);
        }
    }

    @Test
    public void shouldStoreCommentWhenPgnContainsBlockComment()
            throws Exception {

        // Arrange
        StringBuilder moveText =
                new StringBuilder("1. e4 {good move} e5");

        // Act
        game.loadMoveText(moveText);

        // Assert
        assertNotNull(game.getComments());
        assertTrue(game.getComments().containsKey(1));
        assertEquals("good move ", game.getComments().get(1));
        assertEquals(2, game.getHalfMoves().size());
    }

    @Test
    public void shouldStoreNagWhenPgnContainsNumericAnnotationGlyph()
            throws Exception {

        // Arrange
        StringBuilder moveText =
                new StringBuilder("1. e4 $1 e5");

        // Act
        game.loadMoveText(moveText);

        // Assert
        assertNotNull(game.getNag());
        assertTrue(game.getNag().containsKey(1));
        assertEquals("$1", game.getNag().get(1));
        assertEquals(2, game.getHalfMoves().size());
    }

    @Test
    public void shouldStoreVariationWhenPgnContainsVariation()
            throws Exception {

        // Arrange
        StringBuilder moveText =
                new StringBuilder("1. e4 (1. d4 d5) e5");

        // Act
        game.loadMoveText(moveText);

        // Assert
        assertNotNull(game.getVariations());
        assertFalse(game.getVariations().isEmpty());
        assertEquals(2, game.getHalfMoves().size());
    }

    @Test
    public void shouldUpdatePositionAndBoardWhenGotoMoveIsCalled()
            throws Exception {

        // Arrange
        game.loadMoveText(
                new StringBuilder("1. e4 e5 2. Nf3 Nc6")
        );

        Board board = new Board();
        game.setBoard(board);

        MoveList moves = game.getHalfMoves();

        // Act
        game.gotoMove(moves, 1);

        // Assert
        assertEquals(1, game.getPosition());
        assertSame(moves, game.getCurrentMoveList());

        assertEquals(
                "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq - 0 2",
                board.getFen()
        );
    }

    @Test
    public void shouldNavigateToFirstMoveWhenGotoFirstIsCalled()
            throws Exception {

        // Arrange
        game.loadMoveText(
                new StringBuilder("1. e4 e5 2. Nf3 Nc6")
        );

        game.setBoard(new Board());

        game.gotoLast();

        assertEquals(3, game.getPosition());

        // Act
        game.gotoFirst();

        // Assert
        assertEquals(0, game.getPosition());
        assertSame(game.getHalfMoves(), game.getCurrentMoveList());
    }

    @Test
    public void shouldNavigateToLastMoveWhenGotoLastIsCalled()
            throws Exception {

        // Arrange
        game.loadMoveText(
                new StringBuilder("1. e4 e5 2. Nf3 Nc6")
        );

        game.setBoard(new Board());

        // Act
        game.gotoLast();

        // Assert
        assertEquals(3, game.getPosition());
        assertTrue(game.isEndOfMoveList());
    }

    @Test
    public void shouldNavigateForwardAndBackwardWhenNextAndPriorAreCalled()
            throws Exception {

        // Arrange
        game.loadMoveText(
                new StringBuilder("1. e4 e5 2. Nf3 Nc6")
        );

        game.setBoard(new Board());
        game.gotoFirst();

        assertEquals(0, game.getPosition());

        // Act
        game.gotoNext();

        // Assert
        assertEquals(1, game.getPosition());

        // Act
        game.gotoPrior();

        // Assert
        assertEquals(0, game.getPosition());
    }

    @Test
    public void shouldKeepPositionWhenGotoMoveReceivesNegativeIndex()
            throws Exception {

        // Arrange
        game.loadMoveText(
                new StringBuilder("1. e4 e5 2. Nf3 Nc6")
        );

        game.setBoard(new Board());
        game.gotoMove(game.getHalfMoves(), 1);

        assertEquals(1, game.getPosition());

        // Act
        game.gotoMove(game.getHalfMoves(), -1);

        // Assert
        assertEquals(1, game.getPosition());
    }

    @Test
    public void shouldKeepPositionWhenGotoMoveReceivesIndexEqualToSize()
            throws Exception {

        // Arrange
        game.loadMoveText(
                new StringBuilder("1. e4 e5 2. Nf3 Nc6")
        );

        game.setBoard(new Board());
        game.gotoMove(game.getHalfMoves(), 1);

        int previousPosition = game.getPosition();
        int invalidIndex = game.getHalfMoves().size();

        // Act
        game.gotoMove(game.getHalfMoves(), invalidIndex);

        // Assert
        assertEquals(previousPosition, game.getPosition());
    }

    @Test
    public void shouldGeneratePgnWithRequiredMetadataAndMoves()
            throws Exception {

        // Arrange
        GenericPlayer white =
                new GenericPlayer("white-id", "White Player");

        GenericPlayer black =
                new GenericPlayer("black-id", "Black Player");

        game.setWhitePlayer(white);
        game.setBlackPlayer(black);
        game.setResult(GameResult.ONGOING);
        game.setPlyCount("2");

        game.loadMoveText(
                new StringBuilder("1. e4 e5")
        );

        // Act
        String pgn = game.toPgn(true, true);

        // Assert
        assertNotNull(pgn);

        assertTrue(pgn.contains("[Event \"Test Event\"]"));
        assertTrue(pgn.contains("[Site \"Test Site\"]"));
        assertTrue(pgn.contains("[Date \"2026.09.20\"]"));
        assertTrue(pgn.contains("[Round \"1\"]"));

        assertTrue(pgn.contains("[White \"White Player\"]"));
        assertTrue(pgn.contains("[Black \"Black Player\"]"));

        assertTrue(pgn.contains("[PlyCount \"2\"]"));
        assertTrue(pgn.contains("1. e4 e5"));
        assertTrue(pgn.contains(GameResult.ONGOING.getDescription()));
    }

    @Test
    public void shouldIncludeCommentAndNagWhenGeneratingPgn()
            throws Exception {

        // Arrange
        GenericPlayer white =
                new GenericPlayer("white-id", "White Player");

        GenericPlayer black =
                new GenericPlayer("black-id", "Black Player");

        game.setWhitePlayer(white);
        game.setBlackPlayer(black);
        game.setResult(GameResult.ONGOING);
        game.setPlyCount("2");

        game.loadMoveText(
                new StringBuilder(
                        "1. e4 $1 {good move} e5"
                )
        );

        // Act
        String pgn = game.toPgn(true, true);

        // Assert
        assertNotNull(pgn);
        assertTrue(pgn.contains("e4 $1"));
        assertTrue(pgn.contains("{good move }"));
        assertTrue(pgn.contains("e5"));
    }

    @Test
    public void shouldReportEndOfMoveListWhenPositionIsLastMove()
            throws Exception {

        // Arrange
        game.loadMoveText(
                new StringBuilder("1. e4 e5")
        );

        game.setBoard(new Board());

        // Act
        game.gotoLast();

        // Assert
        assertEquals(1, game.getPosition());
        assertTrue(game.isEndOfMoveList());
    }
}

============================================================
3. EXPLICAÇÃO DOS TESTES
============================================================

shouldInitializeGameWithExpectedStateWhenCreated:
Verifica os invariantes definidos pelo construtor: resultado ONGOING, posição zero, lista principal existente e Round correto.

shouldLoadMainMovesWhenValidPgnIsProvided:
Verifica o caminho principal de loadMoveText, confirmando quantidade e SAN das jogadas carregadas.

shouldKeepMoveListEmptyWhenEmptyMoveTextIsLoaded:
Verifica uma entrada vazia e confirma que nenhuma jogada é criada.

shouldThrowExceptionWhenInvalidMoveTextIsLoaded:
Verifica robustez contra MoveText contendo uma jogada inválida. O teste não assume uma subclasse específica de Exception porque Game declara throws Exception e o tipo concreto depende do parser utilizado por MoveList.

shouldStoreCommentWhenPgnContainsBlockComment:
Verifica que comentários PGN entre chaves são interpretados e associados à posição correta.

shouldStoreNagWhenPgnContainsNumericAnnotationGlyph:
Verifica o armazenamento de NAGs como "$1".

shouldStoreVariationWhenPgnContainsVariation:
Verifica que uma linha entre parênteses é interpretada como variação e não adicionada à linha principal.

shouldUpdatePositionAndBoardWhenGotoMoveIsCalled:
Verifica simultaneamente a posição lógica e o estado observável do Board. Após e4 e e5, o FEN esperado é comparado ao FEN produzido.

shouldNavigateToFirstMoveWhenGotoFirstIsCalled:
Verifica o limite inferior da navegação.

shouldNavigateToLastMoveWhenGotoLastIsCalled:
Verifica o limite superior e a identificação do fim da lista.

shouldNavigateForwardAndBackwardWhenNextAndPriorAreCalled:
Verifica avanço e retrocesso entre posições.

shouldKeepPositionWhenGotoMoveReceivesNegativeIndex:
Verifica a fronteira imediatamente inferior ao domínio válido.

shouldKeepPositionWhenGotoMoveReceivesIndexEqualToSize:
Verifica a fronteira imediatamente superior ao último índice válido.

shouldGeneratePgnWithRequiredMetadataAndMoves:
Verifica a saída externa produzida por toPgn, incluindo Event, Site, Date, Round, jogadores, PlyCount, jogadas e resultado.

shouldIncludeCommentAndNagWhenGeneratingPgn:
Verifica se as anotações interpretadas anteriormente reaparecem na representação PGN.

shouldReportEndOfMoveListWhenPositionIsLastMove:
Verifica isEndOfMoveList no estado em que a posição corresponde à última jogada.

============================================================
4. DEPENDÊNCIAS E ESTRATÉGIA DE ISOLAMENTO
============================================================

MoveList:
Pode ser usado como objeto real. É uma estrutura de domínio diretamente relacionada ao comportamento de Game e não envolve recursos externos.

Event:
Pode ser usado como objeto real simples para fornecer os metadados necessários a Round e toPgn.

Round:
Pode ser usado como objeto real simples. Game exige Round no construtor e toPgn consulta seus dados.

GenericPlayer / Player:
GenericPlayer pode ser utilizado como implementação concreta simples de Player para fornecer nomes dos jogadores à serialização PGN.

Board:
É a dependência que mais reduz o isolamento de Game, porque gotoMove chama diretamente loadFromFen e doMove. Entretanto, o Board real é adequado para esta suíte inicial porque é local, determinístico, rápido e não envolve rede, banco de dados, arquivos ou serviços externos.

Não é necessário usar dummy, fake, stub, spy ou mock nesta suíte inicial.

O uso de objetos reais simples permite testar o comportamento observável de Game sem adicionar Mockito.

Sugestão separada sobre Mockito:
Mockito poderia ser considerado posteriormente caso se queira testar interações muito específicas entre Game e Board, como verificar quantidade e ordem de chamadas a doMove. Para a Entrega 1, não é necessário e não deve ser adicionado automaticamente ao projeto.

============================================================
5. COMANDO MAVEN
============================================================

Para executar somente GameTest:

mvn -Dtest=GameTest test

Alternativamente:

mvn -Dtest=com.github.bhlangonijr.chesslib.game.GameTest test

============================================================
6. SUPOSIÇÕES, LIMITAÇÕES E PONTOS PARA VALIDAÇÃO
============================================================

1. As classes auxiliares Event, Round, GenericPlayer, Board e MoveList devem possuir no checkout utilizado as assinaturas esperadas pelo código de teste. O repositório local deve ser considerado a autoridade final.

2. O teste de PGN inválido utiliza Exception genericamente porque loadMoveText declara "throws Exception". O tipo concreto produzido por MoveList deve ser confirmado pela execução do projeto caso se queira tornar o oráculo mais específico.

3. isStartOfMoveList() apresenta uma possível inconsistência:

return getCurrentMoveList() == null && getPosition() == 0;

O Javadoc afirma que o método verifica se a posição atual corresponde à primeira jogada ou se a lista está vazia. Entretanto, com uma MoveList não nula e posição 0, a implementação retorna false. Isso deve ser registrado como possível defeito e validado no repositório.

4. gotoLast(final MoveList moves) contém:

gotoMove(moves, getHalfMoves().size() - 1);

Apesar de receber "moves", o índice é calculado usando getHalfMoves().size(). Isso pode produzir comportamento incorreto quando "moves" representa uma variação com tamanho diferente da linha principal. É um possível defeito a ser investigado.

5. Os parâmetros includeVariations e includeComments de toPgn são documentados pela própria implementação como atualmente ignorados. Portanto, não se deve criar um teste esperando que passar false elimine comentários ou variações.

6. O FEN utilizado como oráculo no teste de gotoMove deve ser confirmado contra a versão de Board existente no checkout do projeto. A posição de xadrez representada é a posição após 1. e4 e5.

7. Os testes são unitários no sentido de exercitarem Game de forma controlada usando objetos de domínio locais. O uso de Board e MoveList reais não transforma, por si só, os testes em testes de sistema ou de integração com infraestrutura externa.

8. Nenhuma alteração em Game.java é necessária para executar a suíte proposta.

9. Os testes foram projetados a partir do código fornecido. Não se afirma que eles passaram, pois o projeto Maven completo não foi executado neste processo.

============================================================
CONCLUSÃO
============================================================

A suíte prioriza os comportamentos não triviais da classe Game: parsing de PGN, comentários, NAGs, variações, navegação, reconstrução do Board, limites de posição e serialização para PGN.

Getters e setters triviais não constituem o foco da suíte. O desenho mantém os testes independentes, determinísticos e rápidos e utiliza Arrange, Act e Assert explicitamente.

Antes da entrega acadêmica, o passo necessário é colocar GameTest.java em:

src/test/java/com/github/bhlangonijr/chesslib/game/GameTest.java

e executar:

mvn -Dtest=GameTest test

A execução permitirá confirmar as assinaturas das dependências e identificar eventuais testes que revelem defeitos reais da implementação.

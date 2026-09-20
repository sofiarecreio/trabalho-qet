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

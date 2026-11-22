package com.github.bhlangonijr.chesslib.game;

import com.github.bhlangonijr.chesslib.move.MoveList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameProcessingTest {

    private static final String KINGS_FEN = "4k3/8/8/8/8/8/8/4K3 w - - 0 1";
    private Game game;

    @Before
    public void setUp() {
        Event event = new Event();
        event.setName("Processing Test");
        event.setSite("UFF");
        event.setStartDate("2026.11.22");
        game = new Game("processing-game", new Round(event));
        game.setWhitePlayer(new GenericPlayer("white", "White Player"));
        game.setBlackPlayer(new GenericPlayer("black", "Black Player"));
        game.setPlyCount("0");
    }

    @Test
    public void shouldResumeMovesAfterLineComment() throws Exception {
        game.loadMoveText(new StringBuilder("1. e4 ; good move\n e5"));

        assertArrayEquals(new String[]{"e4", "e5"}, game.getHalfMoves().toSanArray());
        assertEquals("good move ", game.getComments().get(1));
        assertNull(game.getVariations());
    }

    @Test
    public void shouldClearAnnotationsWhenLoadingAnotherGame() throws Exception {
        game.loadMoveText(new StringBuilder("1. e4 $1 {old comment} (1. d4 d5) e5"));
        assertFalse(game.getComments().isEmpty());
        assertFalse(game.getNag().isEmpty());
        assertFalse(game.getVariations().isEmpty());

        game.loadMoveText(new StringBuilder("1. c4 e5"));

        assertArrayEquals(new String[]{"c4", "e5"}, game.getHalfMoves().toSanArray());
        assertTrue(game.getComments().isEmpty());
        assertTrue(game.getNag().isEmpty());
        assertTrue(game.getVariations().isEmpty());
        assertSame(game.getHalfMoves(), game.getCurrentMoveList());
    }

    @Test
    public void shouldLoadMovesFromCustomFen() throws Exception {
        game.setFen(KINGS_FEN);

        game.loadMoveText(new StringBuilder("1. Ke2 Kd7"));

        assertEquals(KINGS_FEN, game.getHalfMoves().getStartFen());
        assertArrayEquals(new String[]{"Ke2", "Kd7"}, game.getHalfMoves().toSanArray());
        assertSame(game.getHalfMoves(), game.getCurrentMoveList());
    }

    @Test
    public void shouldGeneratePgnWhenOnlyRawMoveTextIsAvailable() throws Exception {
        game.setHalfMoves(new MoveList());
        game.setMoveText(new StringBuilder("1. e4 e5 "));
        game.setPlyCount("2");

        String pgn = game.toPgn(true, true);

        assertTrue(pgn.contains("[Event \"Processing Test\"]"));
        assertTrue(pgn.endsWith("\n\n1. e4 e5 *"));
        assertTrue(game.getHalfMoves().isEmpty());
    }
}

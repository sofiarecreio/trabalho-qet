package com.github.bhlangonijr.chesslib.game;

import com.github.bhlangonijr.chesslib.move.MoveList;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameQualityTest {

    private Game game;

    @Before
    public void setUp() {
        Event event = new Event();
        event.setName("Quality Test");
        event.setSite("UFF");
        event.setStartDate("2026.11.22");
        game = new Game("quality-game", new Round(event));
        game.setWhitePlayer(new GenericPlayer("white", "White"));
        game.setBlackPlayer(new GenericPlayer("black", "Black"));
        game.setPlyCount("0");
    }

    @Test
    public void shouldDistinguishPositionsBeforeAndAtEnd() throws Exception {
        game.loadMoveText(new StringBuilder("1. e4 e5 2. Nf3"));
        game.setPosition(0);
        assertFalse(game.isEndOfMoveList());
        game.setPosition(1);
        assertFalse(game.isEndOfMoveList());
        game.setPosition(2);
        assertTrue(game.isEndOfMoveList());
    }

    @Test
    public void shouldTreatMissingAndEmptyListsAsEnd() {
        game.setCurrentMoveList(null);
        assertTrue(game.isEndOfMoveList());
        game.setCurrentMoveList(new MoveList());
        assertTrue(game.isEndOfMoveList());
    }

    @Test
    public void shouldKeepAssignedMetadata() {
        assertEquals("quality-game", game.getGameId());
        game.setTime("14:35:00");
        game.setVariation("Exchange variation");
        game.setInitialPosition(3);
        assertEquals("14:35:00", game.getTime());
        assertEquals("Exchange variation", game.getVariation());
        assertEquals(3, game.getInitialPosition());
    }

    @Test
    public void shouldReadCompactBlackMoveNumbers() throws Exception {
        game.loadMoveText(new StringBuilder("1.e4 1...e5 2.Nf3 2...Nc6"));
        assertArrayEquals(new String[]{"e4", "e5", "Nf3", "Nc6"}, game.getHalfMoves().toSanArray());
    }

    @Test
    public void shouldSerializeAllMovesOfLongVariation() throws Exception {
        game.loadMoveText(new StringBuilder("1. e4 (1. d4 d5 2. c4 e6 3. Nc3 Nf6) e5"));
        assertEquals("1. e4 (1. d4 d5 2. c4 e6 3. Nc3 Nf6 ) 1... e5 *", moveSection());
    }

    private String moveSection() throws Exception {
        String pgn = game.toPgn(true, true);
        return pgn.substring(pgn.indexOf("\n\n") + 2);
    }
}

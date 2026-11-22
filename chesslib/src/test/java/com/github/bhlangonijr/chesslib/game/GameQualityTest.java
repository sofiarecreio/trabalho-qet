package com.github.bhlangonijr.chesslib.game;

import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.move.MoveConversionException;
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
        game.setPosition(3);
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

    @Test
    public void shouldNumberBlackVariationAndResumeNextWhiteMove() throws Exception {
        game.loadMoveText(new StringBuilder("1. e4 e5 (1... c5 2. Nf3 d6) 2. Nf3"));
        assertEquals("1. e4 e5 (1... c5 2. Nf3 d6 ) 2. Nf3 *", moveSection());
    }

    @Test
    public void shouldCloseSiblingVariationBeforeOpeningNext() throws Exception {
        game.loadMoveText(new StringBuilder("1. e4 (1. d4 d5) (1. c4 e5) e5"));
        assertEquals("1. e4 (1. d4 d5 ) (1. c4 e5 ) 1... e5 *", moveSection());
    }

    @Test
    public void shouldKeepNestedVariationInsideParent() throws Exception {
        game.loadMoveText(new StringBuilder("1. e4 (1. d4 d5 (1... Nf6 2. c4 e6) 2. c4) e5"));
        assertEquals("1. e4 (1. d4 d5 (1... Nf6 2. c4 e6 ) 2. c4 ) 1... e5 *", moveSection());
    }

    @Test
    public void shouldUseAssignedInitialPositionInMoveNumber() throws Exception {
        game.setInitialPosition(2);
        game.loadMoveText(new StringBuilder("1. e4"));
        assertEquals("3. e4 *", moveSection());
    }

    @Test
    public void shouldRenderGameAsPgnString() throws Exception {
        game.loadMoveText(new StringBuilder("1. e4 e5"));
        assertEquals(game.toPgn(true, true), game.toString());
    }

    @Test
    public void shouldReturnNullWhenStringConversionFails() {
        game.setHalfMoves(new MoveList() {
            @Override
            public int size() {
                return 1;
            }

            @Override
            public String[] toSanArray() throws MoveConversionException {
                throw new MoveConversionException("Controlled conversion failure");
            }
        });
        assertNull(game.toString());
    }

    @Test
    public void shouldReuseCommentMapAcrossLineCommentsAndReloads() throws Exception {
        game.loadMoveText(new StringBuilder("1. e4 ; first\n e5 ; second"));
        assertEquals("first ", game.getComments().get(1));
        assertEquals("second ", game.getComments().get(2));
        game.loadMoveText(new StringBuilder("1. d4 ; replaced\n d5"));
        assertEquals(1, game.getComments().size());
        assertEquals("replaced ", game.getComments().get(1));
        assertArrayEquals(new String[]{"d4", "d5"}, game.getHalfMoves().toSanArray());
    }

    @Test
    public void shouldIgnoreMoveMarkersInsideBlockComment() throws Exception {
        game.loadMoveText(new StringBuilder("1. e4 { 1... $2 ( literal ) } e5"));
        assertEquals("1... $2 ( literal ) ", game.getComments().get(1));
        assertArrayEquals(new String[]{"e4", "e5"}, game.getHalfMoves().toSanArray());
        assertNull(game.getVariations());
        assertNull(game.getNag());
    }

    private String moveSection() throws Exception {
        String pgn = game.toPgn(true, true);
        return pgn.substring(pgn.indexOf("\n\n") + 2);
    }
}

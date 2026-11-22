package com.github.bhlangonijr.chesslib.game;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveException;
import com.github.bhlangonijr.chesslib.move.MoveList;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class GameIsolatedTest {

    private static final String START_FEN =
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

    private Game game;
    private BoardStub board;
    private MoveList moves;
    private Move first;
    private Move second;
    private Move third;

    @Before
    public void setUp() {
        game = new Game("isolated-game", new Round(new Event()));
        board = new BoardStub();
        game.setBoard(board);
        first = new Move(Square.E2, Square.E4);
        second = new Move(Square.E7, Square.E5);
        third = new Move(Square.G1, Square.F3);
        moves = new MoveList(START_FEN);
        moves.add(first);
        moves.add(second);
        moves.add(third);
        game.setHalfMoves(moves);
    }

    @Test
    public void shouldStopAtRequestedMove() throws MoveException {
        game.gotoMove(moves, 1);

        assertEquals(1, game.getPosition());
        assertSame(moves, game.getCurrentMoveList());
        assertBoardCalls(START_FEN, first, second);
    }

    @Test
    public void shouldThrowWhenBoardRejectsFirstMove() {
        board.rejectOnAttempt = 1;
        game.setPosition(2);

        MoveException exception = assertThrows(MoveException.class, () -> game.gotoMove(moves, 2));

        assertTrue(exception.getMessage().contains("Illegal move"));
        assertEquals(2, game.getPosition());
        assertSame(moves, game.getCurrentMoveList());
        assertBoardCalls(START_FEN, first);
    }

    @Test
    public void shouldNotLoadBoardForNegativeIndexButActivateProvidedList() {
        game.setPosition(1);
        MoveList alternative = new MoveList(START_FEN);
        alternative.add(first);

        game.gotoMove(alternative, -1);

        assertEquals(1, game.getPosition());
        assertSame(alternative, game.getCurrentMoveList());
        assertNoBoardCalls();
    }

    @Test
    public void shouldKeepPositionWithoutBoard() throws MoveException {
        game.setBoard(null);
        game.setPosition(1);

        game.gotoMove(moves, 2);

        assertEquals(1, game.getPosition());
        assertSame(moves, game.getCurrentMoveList());
        assertNoBoardCalls();
    }

    @Test
    public void shouldExecuteOnlyFirstMoveAtIndexZero() {
        game.gotoMove(moves, 0);

        assertEquals(0, game.getPosition());
        assertBoardCalls(START_FEN, first);
    }

    @Test
    public void shouldExecuteAllMovesAtLastValidIndex() {
        game.gotoMove(moves, 2);

        assertEquals(2, game.getPosition());
        assertBoardCalls(START_FEN, first, second, third);
    }

    @Test
    public void shouldStopAfterBoardRejectsSecondMove() {
        board.rejectOnAttempt = 2;
        game.setPosition(2);

        assertThrows(MoveException.class, () -> game.gotoMove(moves, 2));

        assertEquals(2, game.getPosition());
        assertBoardCalls(START_FEN, first, second);
    }

    @Test
    public void shouldNotLoadBoardWhenMoveListIsEmpty() {
        MoveList empty = new MoveList(START_FEN);
        game.setPosition(1);

        game.gotoMove(empty, 0);

        assertEquals(1, game.getPosition());
        assertSame(empty, game.getCurrentMoveList());
        assertNoBoardCalls();
    }

    @Test
    public void shouldNotLoadBoardWhenIndexEqualsSize() {
        game.setPosition(1);

        game.gotoMove(moves, 3);

        assertEquals(1, game.getPosition());
        assertNoBoardCalls();
    }

    @Test
    public void shouldNotLoadBoardWhenIndexExceedsSize() {
        game.setPosition(1);

        game.gotoMove(moves, 4);

        assertEquals(1, game.getPosition());
        assertNoBoardCalls();
    }

    @Test
    public void shouldNavigateToFirstMoveOfCurrentList() {
        game.setPosition(2);

        game.gotoFirst();

        assertEquals(0, game.getPosition());
        assertBoardCalls(START_FEN, first);
    }

    @Test
    public void shouldUseProvidedShorterListWhenNavigatingToLast() {
        MoveList alternative = new MoveList(START_FEN);
        alternative.add(first);

        game.gotoLast(alternative);

        assertEquals(0, game.getPosition());
        assertSame(alternative, game.getCurrentMoveList());
        assertBoardCalls(START_FEN, first);
    }

    @Test
    public void shouldUseProvidedLongerListWhenNavigatingToLast() {
        MoveList shortMainLine = new MoveList(START_FEN);
        shortMainLine.add(first);
        game.setHalfMoves(shortMainLine);
        game.setCurrentMoveList(moves);

        game.gotoLast();

        assertEquals(2, game.getPosition());
        assertSame(moves, game.getCurrentMoveList());
        assertBoardCalls(START_FEN, first, second, third);
    }

    @Test
    public void shouldNavigateToNextMoveOfCurrentList() {
        game.setPosition(0);

        game.gotoNext();

        assertEquals(1, game.getPosition());
        assertBoardCalls(START_FEN, first, second);
    }

    @Test
    public void shouldNavigateToPriorMoveOfCurrentList() {
        game.setPosition(2);

        game.gotoPrior();

        assertEquals(1, game.getPosition());
        assertBoardCalls(START_FEN, first, second);
    }

    @Test
    public void shouldKeepFirstPositionWhenNavigatingBeforeStart() {
        game.setPosition(0);

        game.gotoPrior();

        assertEquals(0, game.getPosition());
        assertNoBoardCalls();
    }

    @Test
    public void shouldKeepLastPositionWhenNavigatingAfterEnd() {
        game.setPosition(2);

        game.gotoNext();

        assertEquals(2, game.getPosition());
        assertNoBoardCalls();
    }

    @Test
    public void shouldLoadFenFromProvidedListInsteadOfGameMetadata() {
        String alternativeFen = "4k3/8/8/8/8/8/8/4K3 b - - 0 1";
        MoveList alternative = new MoveList(alternativeFen);
        Move kingMove = new Move(Square.E8, Square.E7);
        alternative.add(kingMove);
        game.setFen(START_FEN);

        game.gotoMove(alternative, 0);

        assertEquals(0, game.getPosition());
        assertSame(alternative, game.getCurrentMoveList());
        assertBoardCalls(alternativeFen, kingMove);
    }

    private void assertNoBoardCalls() {
        assertTrue(board.interactions.isEmpty());
        assertTrue(board.attemptedMoves.isEmpty());
        assertTrue(board.fullValidationArguments.isEmpty());
    }

    private void assertBoardCalls(String fen, Move... expectedMoves) {
        List<String> expectedInteractions = new ArrayList<>();
        expectedInteractions.add("load:" + fen);
        for (Move move : expectedMoves) {
            expectedInteractions.add("move:" + move);
        }
        assertEquals(expectedInteractions, board.interactions);
        assertEquals(Arrays.asList(expectedMoves), board.attemptedMoves);
        assertEquals(Collections.nCopies(expectedMoves.length, Boolean.TRUE), board.fullValidationArguments);
    }

    private static class BoardStub extends Board {
        private final List<Move> attemptedMoves = new ArrayList<>();
        private final List<Boolean> fullValidationArguments = new ArrayList<>();
        private final List<String> interactions = new ArrayList<>();
        private boolean recording;
        private int rejectOnAttempt;

        private BoardStub() {
            recording = true;
        }

        @Override
        public void loadFromFen(String fen) {
            // Board's constructor also calls this override, before these lists exist.
            if (recording) {
                interactions.add("load:" + fen);
            }
        }

        @Override
        public boolean doMove(Move move, boolean fullValidation) {
            attemptedMoves.add(move);
            fullValidationArguments.add(fullValidation);
            interactions.add("move:" + move);
            return attemptedMoves.size() != rejectOnAttempt;
        }
    }
}

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
import java.util.List;

import static org.junit.Assert.*;

public class GameIsolatedTest {

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
        moves = new MoveList();
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
        assertEquals(Arrays.asList(first, second), board.attemptedMoves);
    }

    @Test
    public void shouldThrowWhenBoardRejectsFirstMove() {
        board.rejectOnAttempt = 1;

        assertThrows(MoveException.class, () -> game.gotoMove(moves, 2));

        assertEquals(Arrays.asList(first), board.attemptedMoves);
    }

    @Test
    public void shouldNotExecuteMovesForNegativeIndex() throws MoveException {
        game.setPosition(1);

        game.gotoMove(moves, -1);

        assertEquals(1, game.getPosition());
        assertTrue(board.attemptedMoves.isEmpty());
    }

    @Test
    public void shouldKeepPositionWithoutBoard() throws MoveException {
        game.setBoard(null);
        game.setPosition(1);

        game.gotoMove(moves, 2);

        assertEquals(1, game.getPosition());
        assertTrue(board.attemptedMoves.isEmpty());
    }

    private static class BoardStub extends Board {
        private final List<Move> attemptedMoves = new ArrayList<>();
        private int rejectOnAttempt;

        @Override
        public void loadFromFen(String fen) {
            // No chess-position processing: this double isolates Game navigation.
        }

        @Override
        public boolean doMove(Move move, boolean fullValidation) {
            attemptedMoves.add(move);
            return attemptedMoves.size() != rejectOnAttempt;
        }
    }
}

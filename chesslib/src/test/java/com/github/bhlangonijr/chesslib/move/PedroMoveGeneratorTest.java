package com.github.bhlangonijr.chesslib.move;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Testes do Pedro para a classe MoveGenerator.
 */
public class PedroMoveGeneratorTest {

    @Test
    public void deveGerarOsDoisMovimentosIniciaisDeCadaPeao() {
        Board board = new Board();
        List<Move> moves = new ArrayList<>();

        MoveGenerator.generatePawnMoves(board, moves);

        assertEquals(16, moves.size());
        assertTrue(moves.contains(new Move("e2e3", Side.WHITE)));
        assertTrue(moves.contains(new Move("e2e4", Side.WHITE)));
        assertTrue(moves.contains(new Move("d2d3", Side.WHITE)));
        assertTrue(moves.contains(new Move("d2d4", Side.WHITE)));
    }

    @Test
    public void deveGerarCapturaDePeaoSomenteParaCasaOcupadaPorAdversario() {
        Board board = new Board();
        board.loadFromFen("4k3/8/2p5/3P4/8/8/8/4K3 w - - 0 1");
        List<Move> moves = new ArrayList<>();

        MoveGenerator.generatePawnCaptures(board, moves);

        assertEquals(1, moves.size());
        assertTrue(moves.contains(new Move("d5c6", Side.WHITE)));
    }

    @Test
    public void deveGerarAsQuatroOpcoesDePromocaoDoPeaoBranco() {
        Board board = new Board();
        board.loadFromFen("4k3/P7/8/8/8/8/8/4K3 w - - 0 1");
        List<Move> moves = new ArrayList<>();

        MoveGenerator.generatePawnMoves(board, moves);

        assertEquals(4, moves.size());
        assertTrue(moves.contains(new Move(Square.A7, Square.A8, Piece.WHITE_QUEEN)));
        assertTrue(moves.contains(new Move(Square.A7, Square.A8, Piece.WHITE_ROOK)));
        assertTrue(moves.contains(new Move(Square.A7, Square.A8, Piece.WHITE_BISHOP)));
        assertTrue(moves.contains(new Move(Square.A7, Square.A8, Piece.WHITE_KNIGHT)));
    }

    @Test
    public void deveRespeitarMascaraAoGerarMovimentosDoCavalo() {
        Board board = new Board();
        board.loadFromFen("4k3/8/8/8/3N4/8/8/4K3 w - - 0 1");
        List<Move> moves = new ArrayList<>();

        MoveGenerator.generateKnightMoves(board, moves, Square.C6.getBitboard());

        assertEquals(1, moves.size());
        assertTrue(moves.contains(new Move("d4c6", Side.WHITE)));
    }

    @Test
    public void deveGerarRoquePequenoEGrandeQuandoOCaminhoEstaLivre() {
        Board board = new Board();
        board.loadFromFen("r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");
        List<Move> moves = new ArrayList<>();

        MoveGenerator.generateCastleMoves(board, moves);

        assertEquals(2, moves.size());
        assertTrue(moves.contains(new Move("e1g1", Side.WHITE)));
        assertTrue(moves.contains(new Move("e1c1", Side.WHITE)));
    }
}

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
 * Testes do Lucas para a classe MoveGenerator.
 */
public class LucasMoveGeneratorTest {

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
    public void deveGerarOsMovimentosIniciaisDosPeoesPretos() {
        Board board = new Board();
        board.loadFromFen("4k3/pppppppp/8/8/8/8/8/4K3 b - - 0 1");
        List<Move> moves = new ArrayList<>();

        MoveGenerator.generatePawnMoves(board, moves);

        assertEquals(16, moves.size());
        assertTrue(moves.contains(new Move("e7e6", Side.BLACK)));
        assertTrue(moves.contains(new Move("e7e5", Side.BLACK)));
        assertTrue(moves.contains(new Move("a7a6", Side.BLACK)));
        assertTrue(moves.contains(new Move("a7a5", Side.BLACK)));
    }

    @Test
    public void naoDeveMoverPeaoQuandoAProximaCasaEstaOcupada() {
        Board board = new Board();
        board.loadFromFen("4k3/8/8/8/8/4p3/4P3/4K3 w - - 0 1");
        List<Move> moves = new ArrayList<>();

        MoveGenerator.generatePawnMoves(board, moves);

        assertTrue(moves.isEmpty());
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
    public void deveGerarAsQuatroOpcoesDePromocaoDoPeaoPreto() {
        Board board = new Board();
        board.loadFromFen("4k3/8/8/8/8/8/p7/4K3 b - - 0 1");
        List<Move> moves = new ArrayList<>();

        MoveGenerator.generatePawnMoves(board, moves);

        assertEquals(4, moves.size());
        assertTrue(moves.contains(new Move(Square.A2, Square.A1, Piece.BLACK_QUEEN)));
        assertTrue(moves.contains(new Move(Square.A2, Square.A1, Piece.BLACK_ROOK)));
        assertTrue(moves.contains(new Move(Square.A2, Square.A1, Piece.BLACK_BISHOP)));
        assertTrue(moves.contains(new Move(Square.A2, Square.A1, Piece.BLACK_KNIGHT)));
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
    public void deveGerarMovimentosDeBispoTorreERainhaRespeitandoMascara() {
        Board board = new Board();
        board.loadFromFen("4k3/8/8/8/3B3R/8/8/4K3 w - - 0 1");

        List<Move> bishopMoves = new ArrayList<>();
        MoveGenerator.generateBishopMoves(board, bishopMoves, Square.H8.getBitboard());
        assertTrue(bishopMoves.contains(new Move("d4h8", Side.WHITE)));

        List<Move> rookMoves = new ArrayList<>();
        MoveGenerator.generateRookMoves(board, rookMoves, Square.D4.getBitboard());
        assertTrue(rookMoves.contains(new Move("h4d4", Side.WHITE)));

        List<Move> queenMoves = new ArrayList<>();
        board.loadFromFen("4k3/8/8/8/3Q4/8/8/4K3 w - - 0 1");
        MoveGenerator.generateQueenMoves(board, queenMoves, Square.H8.getBitboard());
        assertTrue(queenMoves.contains(new Move("d4h8", Side.WHITE)));
    }

    @Test
    public void deveGerarMovimentoDoReiRespeitandoMascara() {
        Board board = new Board();
        board.loadFromFen("4k3/8/8/8/4K3/8/8/8 w - - 0 1");
        List<Move> moves = new ArrayList<>();

        MoveGenerator.generateKingMoves(board, moves, Square.D4.getBitboard());

        assertEquals(1, moves.size());
        assertTrue(moves.contains(new Move("e4d4", Side.WHITE)));
    }

    @Test
    public void deveGerarTodosOsMovimentosPseudoLegaisDaPosicaoInicial() {
        List<Move> moves = MoveGenerator.generatePseudoLegalMoves(new Board());

        assertEquals(20, moves.size());
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

    @Test
    public void deveGerarSomenteRoquePequenoQuandoApenasEsseDireitoExiste() {
        Board board = new Board();
        board.loadFromFen("r3k2r/8/8/8/8/8/8/R3K2R w K - 0 1");
        List<Move> moves = new ArrayList<>();

        MoveGenerator.generateCastleMoves(board, moves);

        assertEquals(1, moves.size());
        assertTrue(moves.contains(new Move("e1g1", Side.WHITE)));
    }

    @Test
    public void naoDeveGerarRoqueQuandoOReiEstaEmXeque() {
        Board board = new Board();
        board.loadFromFen("k3r3/8/8/8/8/8/8/R3K2R w KQ - 0 1");
        List<Move> moves = new ArrayList<>();

        MoveGenerator.generateCastleMoves(board, moves);

        assertTrue(moves.isEmpty());
    }
}

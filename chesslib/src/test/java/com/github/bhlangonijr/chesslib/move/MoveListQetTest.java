package com.github.bhlangonijr.chesslib.move;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


import java.util.Collections;

import com.github.bhlangonijr.chesslib.Square;

import org.junit.Test;

public class MoveListQetTest {

    @Test
    public void testMovimentoSimplesDePeao()
            throws MoveConversionException {

        MoveList moveList = new MoveList();

        moveList.addSanMove("e4", true, true);

        assertEquals(1, moveList.size());
        assertEquals("e2e4", moveList.get(0).toString());
        assertEquals("e4 ", moveList.toSan());
    }

    @Test
    public void testCapturaDePeao()
            throws MoveConversionException {

        MoveList moveList = new MoveList();

        moveList.loadFromSan("e4 d5 exd5");

        assertEquals(3, moveList.size());
        assertEquals("e4d5", moveList.get(2).toString());
        assertEquals("e4 d5 exd5 ", moveList.toSan());
    }

    @Test
    public void testCavalosEmMovimentoAmbiguo()
            throws MoveConversionException {

        MoveList moveList = new MoveList();

        moveList.loadFromSan("d4 d5 Nf3 Nf6 Nbd2");

        assertEquals(5, moveList.size());
        assertEquals("b1d2", moveList.get(4).toString());
        assertEquals("Nbd2", moveList.toSanArray()[4]);
    }

    @Test
    public void testPromocaoDePeao()
            throws MoveConversionException {

        MoveList moveList = new MoveList(
                "k7/4P3/8/8/8/8/8/7K w - - 0 1");

        moveList.addSanMove("e8=Q", true, true);

        assertEquals(1, moveList.size());
        assertEquals("e7e8q", moveList.get(0).toString());
        assertEquals("e8=Q+", moveList.toSanArray()[0]);
    }

    @Test
    public void testRoquePequeno()
            throws MoveConversionException {

        MoveList moveList = new MoveList(
                "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");

        moveList.addSanMove("O-O", true, true);

        assertEquals(1, moveList.size());
        assertEquals("e1g1", moveList.get(0).toString());
        assertEquals("O-O", moveList.toSanArray()[0]);
    }

    @Test
    public void testRoqueGrande()
            throws MoveConversionException {

        MoveList moveList = new MoveList(
                "r3k2r/8/8/8/8/8/8/R3K2R w KQkq - 0 1");

        moveList.addSanMove("O-O-O", true, true);

        assertEquals(1, moveList.size());
        assertEquals("e1c1", moveList.get(0).toString());
        assertEquals("O-O-O", moveList.toSanArray()[0]);
    }

    @Test
    public void testCapturaEnPassant()
            throws MoveConversionException {

        MoveList moveList = new MoveList();

        moveList.loadFromSan("e4 a6 e5 d5 exd6");

        assertEquals(5, moveList.size());
        assertEquals("e5d6", moveList.get(4).toString());
        assertEquals("exd6", moveList.toSanArray()[4]);
        assertEquals(
                "rnbqkbnr/1pp1pppp/p2P4/8/8/8/PPPP1PPP/RNBQKBNR b KQkq - 0 3",
                moveList.getFen());
    }

    @Test(expected = MoveConversionException.class)
    public void testMovimentoIlegal() throws MoveConversionException {

        MoveList moveList = new MoveList();

        moveList.addSanMove("Qh5", true, true);
    }

    @Test
    public void testConstrutorCopiaSemNotacoesCalculadas() {

        MoveList original = new MoveList();

        MoveList copia = new MoveList(original);

        assertEquals(original.getStartFen(), copia.getStartFen());
        assertEquals(0, copia.size());
    }

    @Test
    public void testConstrutorCopiaComNotacoesCalculadas()
            throws MoveConversionException {

        MoveList original = new MoveList();

        original.loadFromSan("e4 e5 Nf3");
        original.toSanArray();
        original.toFanArray();

        MoveList copia = new MoveList(original);

        assertEquals(original.size(), copia.size());
        assertEquals(original.getStartFen(), copia.getStartFen());
        assertEquals(original.getFen(), copia.getFen());

        assertArrayEquals(
                original.toSanArray(),
                copia.toSanArray());

        assertArrayEquals(
                original.toFanArray(),
                copia.toFanArray());
    }

    @Test
    public void testDesambiguacaoDeTorrePelaFileira()
            throws MoveConversionException {

        MoveList moveList = new MoveList(
                "7k/8/8/8/8/R7/8/R6K w - - 0 1");

        moveList.addSanMove("R1a2", true, true);

        assertEquals(1, moveList.size());
        assertEquals("a1a2", moveList.get(0).toString());
        assertEquals("R1a2", moveList.toSanArray()[0]);
    }

    @Test
    public void testRetornoDosMetodosAddEAddAll() {

        MoveList moveList = new MoveList();

        Move e4 = new Move(Square.E2, Square.E4);
        Move e5 = new Move(Square.E7, Square.E5);
        Move nf3 = new Move(Square.G1, Square.F3);

        assertTrue(moveList.add(e4));

        assertTrue(moveList.addAll(
                Collections.singletonList(e5)));

        assertFalse(moveList.addAll(
                Collections.<Move>emptyList()));

        assertTrue(moveList.addAll(
                1,
                Collections.singletonList(nf3)));

        assertFalse(moveList.addAll(
                0,
                Collections.<Move>emptyList()));

        assertEquals(3, moveList.size());
    }

    @Test
    public void testMetodosDeRemocaoELimpeza() {

        MoveList moveList = new MoveList();

        Move e4 = new Move(Square.E2, Square.E4);
        Move e5 = new Move(Square.E7, Square.E5);
        Move nf3 = new Move(Square.G1, Square.F3);

        moveList.add(e4);
        moveList.add(e5);
        moveList.add(nf3);

        assertEquals(e4, moveList.removeFirst());
        assertEquals(nf3, moveList.removeLast());

        assertTrue(moveList.remove(e5));
        assertFalse(moveList.remove(e5));

        moveList.add(e4);
        moveList.add(e5);

        assertEquals(e4, moveList.remove(0));

        moveList.clear();

        assertTrue(moveList.isEmpty());
    }

    @Test
    public void testGetESetIndex() {

        MoveList moveList = new MoveList();

        moveList.setIndex(7);

        assertEquals(7, moveList.getIndex());
    }

    @Test
    public void testCriacaoDeListaParcialECompleta()
            throws MoveConversionException {

        MoveList original = new MoveList();
        original.loadFromSan("e4 e5 Nf3");

        MoveList primeirosDois = new MoveList();
        primeirosDois.loadFromSan("e4 e5");

        String fenDepoisDeDois = primeirosDois.getFen();
        String fenFinal = original.getFen();

        MoveList parcial =
                MoveList.createMoveListFrom(original, 2);

        MoveList completa =
                MoveList.createMoveListFrom(original, 99);

        assertEquals(
                fenDepoisDeDois,
                parcial.getStartFen());

        assertEquals(
                fenFinal,
                completa.getStartFen());

        assertEquals(0, parcial.size());
        assertEquals(0, completa.size());
    }

    @Test(expected = MoveConversionException.class)
    public void testCriacaoDeListaComMovimentoIlegal()
            throws MoveConversionException {

        MoveList moveList = new MoveList(
                "4k3/8/8/8/8/8/8/4K3 w - - 0 1");

        moveList.add(
                new Move(Square.E1, Square.E7));

        MoveList.createMoveListFrom(moveList, 1);
    }

    @Test
public void testConversaoFanSemNumeracao()
        throws MoveConversionException {

    MoveList moveList = new MoveList();
    moveList.loadFromSan("e4 e5 Nf3");

    assertEquals(
            "\u2659e4 \u265Fe5 \u2658f3 ",
            moveList.toFan());
}
}
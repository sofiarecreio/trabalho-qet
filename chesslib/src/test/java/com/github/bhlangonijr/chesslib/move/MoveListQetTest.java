package com.github.bhlangonijr.chesslib.move;

import static org.junit.Assert.assertEquals;

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
}
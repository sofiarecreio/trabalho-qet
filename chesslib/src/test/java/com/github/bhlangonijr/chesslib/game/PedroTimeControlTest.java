package com.github.bhlangonijr.chesslib.game;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Testes do Pedro para a classe TimeControl.
 */
public class PedroTimeControlTest {

    @Test
    public void deveInterpretarControleComTempoEIncremento() {
        TimeControl timeControl = TimeControl.parseFromString("3000+3");

        assertEquals(TimeControlType.TIME_BONUS, timeControl.getTimeControlType());
        assertEquals(3_000_000L, timeControl.getMilliseconds());
        assertEquals(3_000L, timeControl.getIncrement());
        assertEquals("3000+3", timeControl.toPGNString());
    }

    @Test
    public void deveAceitarBarraVerticalComoSeparadorDoIncremento() {
        TimeControl timeControl = TimeControl.parseFromString("3000|3");

        assertEquals(TimeControlType.TIME_BONUS, timeControl.getTimeControlType());
        assertEquals(3_000_000L, timeControl.getMilliseconds());
        assertEquals(3_000L, timeControl.getIncrement());
        assertEquals("3000+3", timeControl.toPGNString());
    }

    @Test
    public void deveInterpretarControlePorNumeroDeMovimentos() {
        TimeControl timeControl = TimeControl.parseFromString("40/9000");

        assertEquals(TimeControlType.MOVES_PER_TIME, timeControl.getTimeControlType());
        assertEquals(40, timeControl.getHalfMoves());
        assertEquals(9_000_000L, timeControl.getMilliseconds());
        assertEquals("40/9000", timeControl.toPGNString());
        assertEquals("40 Moves / 9000 Sec", timeControl.toString());
    }

    @Test
    public void deveInterpretarEstagiosDeTempoSeparadosPorDoisPontos() {
        TimeControl timeControl = TimeControl.parseFromString("40/9000:40/1980");

        assertEquals(TimeControlType.MOVES_PER_TIME, timeControl.getTimeControlType());
        assertEquals(40, timeControl.getHalfMoves());
        assertEquals(1, timeControl.getMovePerTime().size());
        assertEquals("40/9000:40/1980", timeControl.toPGNString());
    }

    @Test
    public void deveRepresentarControleDesconhecido() {
        TimeControl timeControl = TimeControl.parseFromString("-");

        assertEquals(TimeControlType.UNKNOW, timeControl.getTimeControlType());
        assertEquals("?", timeControl.toPGNString());
        assertEquals("Custom...", timeControl.toString());
    }
}

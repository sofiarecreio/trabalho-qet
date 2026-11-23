package com.github.bhlangonijr.chesslib.game;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;

/**
 * Testes do Pedro para a classe TimeControl.
 */
public class PedroTimeControlTest {

    @Test
    public void deveInterpretarTempoSimplesEmSegundos() {
        TimeControl tc = TimeControl.parseFromString("90");
        assertEquals(TimeControlType.TIME_BONUS, tc.getTimeControlType());
        assertEquals(90_000L, tc.getMilliseconds());
        assertEquals(0L, tc.getIncrement());
        assertEquals(0, tc.getHalfMoves());
        assertTrue(tc.getMovePerTime().isEmpty());
        assertEquals("90", tc.toPGNString());
        assertEquals("1 Min", tc.toString());
    }

    @Test
    public void devePreservarZeroSemAdicionarIncrementoOuMovimentos() {
        TimeControl tc = TimeControl.parseFromString("0+0");
        assertEquals(0L, tc.getMilliseconds());
        assertEquals(0L, tc.getIncrement());
        assertEquals("0", tc.toPGNString());
        assertEquals("0 Min", tc.toString());
    }

    @Test
    public void devePreservarMenorTempoEIncrementoPositivos() {
        TimeControl tc = TimeControl.parseFromString("1+1");
        assertEquals(1_000L, tc.getMilliseconds());
        assertEquals(1_000L, tc.getIncrement());
        assertEquals("1+1", tc.toPGNString());
        assertEquals("0 Min + 1 Sec", tc.toString());
    }

    @Test
    public void deveInterpretarInterrogacaoComoDesconhecido() {
        TimeControl tc = TimeControl.parseFromString("?");
        assertEquals(TimeControlType.UNKNOW, tc.getTimeControlType());
        assertEquals("?", tc.toPGNString());
        assertEquals("Custom...", tc.toString());
    }

    @Test
    public void deveInterpretarTresEstagiosSemPerderValores() {
        TimeControl tc = TimeControl.parseFromString("40/7200:20/3600:10/900");
        assertEquals(40, tc.getHalfMoves());
        assertEquals(7_200_000L, tc.getMilliseconds());
        assertEquals(2, tc.getMovePerTime().size());
        assertEquals(20, tc.getMovePerTime().get(0).getMoves());
        assertEquals(3_600_000L, tc.getMovePerTime().get(0).getMilliseconds());
        assertEquals(10, tc.getMovePerTime().get(1).getMoves());
        assertEquals(900_000L, tc.getMovePerTime().get(1).getMilliseconds());
        assertEquals("40/7200:20/3600:10/900", tc.toPGNString());
        assertEquals("40 Moves / 7200 Sec : 20 Moves / 3600 Sec : 10 Moves / 900 Sec", tc.toString());
    }

    @Test
    public void deveAceitarMenorQuantidadePositivaDeMovimentos() {
        TimeControl tc = TimeControl.parseFromString("1/1");
        assertEquals(1, tc.getHalfMoves());
        assertEquals(1_000L, tc.getMilliseconds());
        assertEquals("1/1", tc.toPGNString());
        assertEquals("1 Moves / 1 Sec", tc.toString());
    }

    @Test
    public void deveRepresentarTempoZeroComMovimentos() {
        TimeControl tc = TimeControl.parseFromString("40/0");
        assertEquals(40, tc.getHalfMoves());
        assertEquals(0L, tc.getMilliseconds());
        assertEquals("40/0", tc.toPGNString());
        assertEquals("40 Moves / 0 Sec", tc.toString());
    }

    @Test
    public void deveManterInstanciasIndependentes() {
        TimeControl first = TimeControl.parseFromString("40/7200:20/3600");
        TimeControl second = TimeControl.parseFromString("60+2");
        first.getMovePerTime().clear();
        first.setIncrement(9_000L);
        assertNotSame(first, second);
        assertNotSame(first.getMovePerTime(), second.getMovePerTime());
        assertEquals("60+2", second.toPGNString());
        assertTrue(second.getMovePerTime().isEmpty());
    }

    @Test
    public void devePermitirComposicaoIsoladaComEstagioControlado() {
        TimeControl tc = new TimeControl();
        tc.setTimeControlType(TimeControlType.MOVES_PER_TIME);
        tc.setHalfMoves(25);
        tc.setMilliseconds(120_000L);
        tc.setIncrement(4_000L);
        tc.getMovePerTime().add(new MovePerTime(5, 60_000L));
        assertEquals("25/120+4:5/60", tc.toPGNString());
        assertEquals("25 Moves / 120 Sec + 4 Sec : 5 Moves / 60 Sec", tc.toString());
    }

    @Test
    public void deveDelegarNotacaoAoEstagioSemDependerDaImplementacaoReal() {
        TimeControl tc = TimeControl.parseFromString("120");
        tc.getMovePerTime().add(new MovePerTime() {
            @Override public String toPGNString() { return "ESTAGIO-PGN"; }
            @Override public String toString() { return "ESTAGIO-TEXTO"; }
        });
        assertEquals("120:ESTAGIO-PGN", tc.toPGNString());
        assertEquals("2 Min : ESTAGIO-TEXTO", tc.toString());
    }

    @Test
    public void devePreservarProfundidadeENosSemAlterarRepresentacaoTemporal() {
        TimeControl tc = TimeControl.parseFromString("120");
        tc.setDepth(17);
        tc.setNodes(9_000_000_000L);
        assertEquals(17, tc.getDepth());
        assertEquals(9_000_000_000L, tc.getNodes());
        assertEquals("120", tc.toPGNString());
    }

    @Test
    public void deveOmitirComponentesNegativosConfiguradosDiretamente() {
        TimeControl tc = new TimeControl();
        tc.setTimeControlType(TimeControlType.TIME_BONUS);
        tc.setHalfMoves(-1);
        tc.setMilliseconds(-1);
        tc.setIncrement(-1);
        assertEquals("", tc.toPGNString());
        assertEquals("", tc.toString());
    }

    @Test
    public void deveTruncarFracoesDeSegundoNaSerializacao() {
        TimeControl tc = TimeControl.parseFromString("0");
        tc.setMilliseconds(999);
        tc.setIncrement(999);
        assertEquals("0+0", tc.toPGNString());
        assertEquals("0 Min + 0 Sec", tc.toString());
    }

    @Test
    public void deveTruncarMinutosSemArredondar() {
        TimeControl tc = TimeControl.parseFromString("119");
        assertEquals("1 Min", tc.toString());
        tc.setMilliseconds(120_000L);
        assertEquals("2 Min", tc.toString());
    }

    @Test
    public void deveIgnorarComponentesQuandoTipoForDesconhecido() {
        TimeControl tc = TimeControl.parseFromString("40/9000:20/3600");
        tc.setTimeControlType(TimeControlType.UNKNOW);
        tc.setIncrement(10_000L);
        assertEquals("?", tc.toPGNString());
        assertEquals("Custom...", tc.toString());
    }

    @Test
    public void deveRejeitarTextoSemNumero() {
        assertThrows(NumberFormatException.class, () -> TimeControl.parseFromString("abc"));
    }

    @Test
    public void deveRejeitarEntradaVazia() {
        assertThrows(NumberFormatException.class, () -> TimeControl.parseFromString(""));
    }

    @Test
    public void deveRejeitarNumeroAcimaDoIntervaloDeInteiro() {
        assertThrows(NumberFormatException.class, () -> TimeControl.parseFromString("2147483648"));
    }

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

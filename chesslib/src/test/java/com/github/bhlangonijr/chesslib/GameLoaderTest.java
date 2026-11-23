package com.github.bhlangonijr.chesslib;

import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.game.Termination;
import com.github.bhlangonijr.chesslib.game.TimeControl;
import com.github.bhlangonijr.chesslib.game.TimeControlType;
import com.github.bhlangonijr.chesslib.pgn.GameLoader;
import com.github.bhlangonijr.chesslib.pgn.PgnException;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class GameLoaderTest {

    @Test
    public void carregarProximoJogoComPgnValidoCarregaPropriedadesEJogadas() {
        Game jogo = carregar(
                "[Event \"Copa de Testes Unitarios\"]",
                "[Site \"Local\"]",
                "[Date \"2026.09.19\"]",
                "[Round \"7\"]",
                "[White \"Ada\"]",
                "[Black \"Grace\"]",
                "[Result \"1/2-1/2\"]",
                "[Termination \"Normal\"]",
                "[TimeControl \"300+5\"]",
                "",
                "1. e4 e5 1/2-1/2"
        );

        assertNotNull(jogo);
        assertEquals("Copa de Testes Unitarios", jogo.getRound().getEvent().getName());
        assertEquals("Local", jogo.getRound().getEvent().getSite());
        assertEquals("2026.09.19", jogo.getDate());
        assertEquals(7, jogo.getRound().getNumber());
        assertEquals("Ada", jogo.getWhitePlayer().getName());
        assertEquals("Grace", jogo.getBlackPlayer().getName());
        assertEquals("1/2-1/2", jogo.getResult().getDescription());
        assertEquals(Termination.NORMAL, jogo.getTermination());
        assertEquals(2, jogo.getHalfMoves().size());

        TimeControl controleDeTempo = jogo.getRound().getEvent().getTimeControl();
        assertNotNull(controleDeTempo);
        assertEquals(TimeControlType.TIME_BONUS, controleDeTempo.getTimeControlType());
        assertEquals(300000, controleDeTempo.getMilliseconds());
        assertEquals(5000, controleDeTempo.getIncrement());
    }

    @Test
    public void carregarProximoJogoComPgnVazioRetornaNulo() {
        assertNull(GameLoader.loadNextGame(Collections.<String>emptyList().iterator()));
    }

    @Test
    public void carregarProximoJogoComPropriedadeDesconhecidaArmazenaPropriedade() {
        Game jogo = carregar(
                "[Event \"Teste de propriedade personalizada\"]",
                "[PropriedadePersonalizada \"valor personalizado\"]",
                "[Result \"*\"]"
        );

        assertNotNull(jogo);
        assertNotNull(jogo.getProperty());
        assertEquals("valor personalizado", jogo.getProperty().get("PropriedadePersonalizada"));
    }

    @Test
    public void carregarProximoJogoComRodadaInvalidaUsaRodadaPadrao() {
        Game jogo = carregar(
                "[Event \"Teste de rodada invalida\"]",
                "[Round \"nao-e-um-numero\"]",
                "[Result \"*\"]"
        );

        assertNotNull(jogo);
        assertEquals(1, jogo.getRound().getNumber());
    }

    @Test
    public void carregarProximoJogoComTerminoInvalidoUsaNaoFinalizado() {
        Game jogo = carregar(
                "[Event \"Teste de termino invalido\"]",
                "[Termination \"Finalizado por meteoro\"]",
                "[Result \"*\"]"
        );

        assertNotNull(jogo);
        assertEquals(Termination.UNTERMINATED, jogo.getTermination());
    }

    @Test
    public void carregarProximoJogoComControleDeTempoInvalidoIgnoraPropriedade() {
        Game jogo = carregar(
                "[Event \"Teste de controle de tempo invalido\"]",
                "[TimeControl \"abc+xyz\"]",
                "[Result \"*\"]"
        );

        assertNotNull(jogo);
        assertNull(jogo.getRound().getEvent().getTimeControl());
    }

    @Test(expected = PgnException.class)
    public void carregarProximoJogoComPgnMalformadoLancaExcecao() {
        carregar(
                "[Event \"Teste de PGN malformado\"]",
                "[Round \"4\"]",
                "[Result \"1-0\"]",
                "",
                "1. jogada-invalida 1-0"
        );
    }

    private Game carregar(String... linhas) {
        return GameLoader.loadNextGame(Arrays.asList(linhas).iterator());
    }
}

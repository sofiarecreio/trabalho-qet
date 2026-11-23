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
import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

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
        assertEquals("Copa de Testes Unitarios", jogo.getRound().getEvent().getId());
        assertEquals("Local", jogo.getRound().getEvent().getSite());
        assertEquals("2026.09.19", jogo.getDate());
        assertEquals(7, jogo.getRound().getNumber());
        assertSame(jogo.getRound(), jogo.getRound().getEvent().getRound().get(7));
        assertEquals("Ada", jogo.getWhitePlayer().getName());
        assertEquals("Ada", jogo.getWhitePlayer().getId());
        assertEquals("Ada", jogo.getWhitePlayer().getDescription());
        assertEquals("Grace", jogo.getBlackPlayer().getName());
        assertEquals("Grace", jogo.getBlackPlayer().getId());
        assertEquals("Grace", jogo.getBlackPlayer().getDescription());
        assertEquals("1/2-1/2", jogo.getResult().getDescription());
        assertEquals(Termination.NORMAL, jogo.getTermination());
        assertEquals(2, jogo.getHalfMoves().size());
        assertEquals("2", jogo.getPlyCount());
        assertNotNull(jogo.getMoveText());
        assertEquals("1. e4 e5", jogo.getMoveText().toString().trim());

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

    @Test
    public void carregarCabecalhoPreservaMetadadosOpcionaisSemJogadas() {
        Game jogo = carregar(
                "[Event \"Metadados\"]",
                "[Annotator \"Sofia\"]",
                "[ECO \"C20\"]",
                "[Opening \"King Pawn Game\"]",
                "[Variation \"Main line\"]",
                "[PlyCount \"42\"]"
        );

        assertEquals("Sofia", jogo.getAnnotator());
        assertEquals("C20", jogo.getEco());
        assertEquals("King Pawn Game", jogo.getOpening());
        assertEquals("Main line", jogo.getVariation());
        assertEquals("42", jogo.getPlyCount());
    }

    @Test
    public void carregarJogoComFenUsaPosicaoInicialInformada() {
        String fen = "4k3/8/8/8/8/8/4P3/4K3 w - - 0 1";
        Game jogo = carregar(
                "[Event \"Posicao personalizada\"]",
                "[FEN \"" + fen + "\"]",
                "[Result \"*\"]",
                "1. e4 Kf7 *"
        );

        assertEquals(fen, jogo.getFen());
        assertEquals(2, jogo.getHalfMoves().size());
        assertEquals("e2e4", jogo.getHalfMoves().get(0).toString());
        assertEquals("e8f7", jogo.getHalfMoves().get(1).toString());
    }

    @Test
    public void carregarCabecalhoConverteEloDosDoisJogadores() {
        Game jogo = carregar("[WhiteElo \"2100\"]", "[BlackElo \"2200\"]");

        assertEquals(2100, jogo.getWhitePlayer().getElo());
        assertEquals(2200, jogo.getBlackPlayer().getElo());
    }

    @Test
    public void carregarEloInvalidoPreservaUltimoValorValido() {
        Game jogo = carregar(
                "[WhiteElo \"2100\"]", "[BlackElo \"2200\"]",
                "[WhiteElo \"desconhecido\"]", "[BlackElo \"?\"]"
        );

        assertEquals(2100, jogo.getWhitePlayer().getElo());
        assertEquals(2200, jogo.getBlackPlayer().getElo());
    }

    @Test
    public void carregarJogadoresSemRodadaUsaPrimeiraRodada() {
        Game jogo = carregar("[White \"Ada\"]", "[Black \"Grace\"]");

        assertEquals(1, jogo.getRound().getNumber());
        assertEquals("Ada", jogo.getWhitePlayer().getName());
        assertEquals("Grace", jogo.getBlackPlayer().getName());
    }

    @Test
    public void carregarPartidasConsecutivasRespeitaCadaMarcadorDeResultado() {
        for (String resultado : Arrays.asList("1-0", "0-1", "1/2-1/2", "*")) {
            Iterator<String> linhas = Arrays.asList(
                    "[Event \"Primeiro\"]", "[Result \"" + resultado + "\"]",
                    "1. e4 e5", "2. Nf3 Nc6 " + resultado,
                    "[Event \"Segundo\"]", "[Result \"*\"]", "1. d4 d5 *"
            ).iterator();

            Game primeiro = GameLoader.loadNextGame(linhas);
            assertEquals(resultado, primeiro.getResult().getDescription());
            assertEquals("Primeiro", primeiro.getRound().getEvent().getName());
            assertEquals(4, primeiro.getHalfMoves().size());
            assertEquals("b8c6", primeiro.getHalfMoves().get(3).toString());
            assertTrue(linhas.hasNext());

            Game segundo = GameLoader.loadNextGame(linhas);
            assertEquals("Segundo", segundo.getRound().getEvent().getName());
            assertEquals(2, segundo.getHalfMoves().size());
            assertEquals("d2d4", segundo.getHalfMoves().get(0).toString());
            assertNull(GameLoader.loadNextGame(linhas));
        }
    }

    @Test
    public void carregarComentarioMultilinhaNaoConfundeResultadoComFimDaPartida() {
        Game jogo = carregar(
                "[Event \"Comentario\"]", "[Result \"0-1\"]",
                "1. e4 { resultado hipotetico 1-0",
                "a partida continua } e5", "2. Nf3 Nc6 0-1"
        );

        assertEquals("0-1", jogo.getResult().getDescription());
        assertEquals(4, jogo.getHalfMoves().size());
        assertEquals("b8c6", jogo.getHalfMoves().get(3).toString());
        assertEquals("4", jogo.getPlyCount());
    }

    private Game carregar(String... linhas) {
        return GameLoader.loadNextGame(Arrays.asList(linhas).iterator());
    }
}

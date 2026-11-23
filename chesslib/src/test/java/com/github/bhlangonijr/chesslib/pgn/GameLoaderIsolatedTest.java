package com.github.bhlangonijr.chesslib.pgn;

import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.game.Round;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.github.bhlangonijr.chesslib.move.MoveList;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

/**
 * Testes isolados de GameLoader. A partida é um stub que não interpreta lances (sem Board, MoveList ou SAN reais)
 * e o arquivo é substituído por um iterador que registra quantas linhas foram consumidas. Assim, cada teste
 * verifica apenas as decisões do próprio GameLoader.
 */
public class GameLoaderIsolatedTest {

    private GameStub game;
    private int gamesCreated;
    private int stubHalfMoves;
    private RuntimeException stubFailure;

    @Before
    public void setUp() {
        game = null;
        gamesCreated = 0;
        stubHalfMoves = 0;
        stubFailure = null;
    }

    @Test
    public void deveEntregarOTextoDosLancesSemOMarcadorDeResultado() {
        Game loaded = load("[Event \"Final\"]", "1. e4 e5 2. Nf3 1-0");

        assertSame(game, loaded);
        assertEquals(Collections.singletonList("1. e4 e5 2. Nf3 \n"), game.loadedTexts);
        assertEquals("1. e4 e5 2. Nf3 \n", game.getMoveText().toString());
    }

    @Test
    public void deveRemoverOsQuatroMarcadoresDeResultado() {
        load("1. e4 1-0 0-1 1/2-1/2 *");

        // "1-0", "0-1", "1/2-1/2" e "*" saem do texto; os quatro separadores permanecem.
        assertEquals(Collections.singletonList("1. e4    \n"), game.loadedTexts);
    }

    @Test
    public void deveCalcularPlyCountPelaQuantidadeDeLancesInterpretados() {
        stubHalfMoves = 3;
        load("[PlyCount \"99\"]", "1. e4 e5 2. Nf3 *");

        assertEquals("3", game.getPlyCount());
    }

    @Test
    public void deveEnvolverFalhaDaInterpretacaoComRodadaEEvento() {
        MoveConversionException failure = new MoveConversionException("lance invalido");
        stubFailure = failure;

        PgnException exception = assertThrows(PgnException.class,
                () -> load("[Event \"Torneio\"]", "[Round \"2\"]", "1. Zz9 1-0"));

        assertEquals("Error parsing PGN[2, Torneio]: ", exception.getMessage());
        assertSame(failure, exception.getCause());
    }

    @Test
    public void deveUsarAPartidaDaFabricaVinculadaARodadaEAoEvento() {
        Game loaded = load("[Event \"Aberto\"]", "[Round \"4\"]", "1. d4 *");

        assertSame(game, loaded);
        assertEquals(1, gamesCreated);
        Round round = loaded.getRound();
        assertEquals(4, round.getNumber());
        assertSame(round, round.getEvent().getRound().get(4));
        assertEquals(Collections.singletonList(loaded), round.getGame());
        assertEquals("Aberto", round.getEvent().getName());
    }

    @Test
    public void naoDeveConsumirLinhasDaProximaPartida() {
        RecordingIterator lines = new RecordingIterator(
                "[Event \"A\"]", "", "1. e4 1-0", "", "[Event \"B\"]", "1. d4 0-1");

        GameLoader.loadNextGame(lines, this::newGame);

        assertEquals(3, lines.consumed);
        assertEquals("", lines.next());
        assertEquals("[Event \"B\"]", lines.next());
    }

    @Test
    public void resultadoDentroDeComentarioNaoEncerraAPartida() {
        RecordingIterator lines = new RecordingIterator("1. e4 {brancas vencem 1-0", "fim} e5 0-1", "[Event \"B\"]");

        GameLoader.loadNextGame(lines, this::newGame);

        assertEquals(2, lines.consumed);
        assertEquals(1, game.loadedTexts.size());
        assertTrue(game.loadedTexts.get(0).contains("fim} e5"));
    }

    @Test
    public void tagEventAposLancesPendentesInterpretaOsLancesAntes() {
        RecordingIterator lines = new RecordingIterator("1. e4 e5", "[Event \"Seguinte\"]");

        GameLoader.loadNextGame(lines, this::newGame);

        assertEquals(Collections.singletonList("1. e4 e5\n"), game.loadedTexts);
        assertEquals("Seguinte", game.getRound().getEvent().getName());
    }

    @Test
    public void tagEventNaoReinterpretaQuandoJaHaLances() {
        stubHalfMoves = 2;
        RecordingIterator lines = new RecordingIterator("1. e4 e5", "[Event \"Seguinte\"]");

        GameLoader.loadNextGame(lines, this::newGame);

        assertTrue(game.loadedTexts.isEmpty());
        assertEquals("Seguinte", game.getRound().getEvent().getName());
    }

    @Test
    public void falhaDoIteradorNaoEhConvertidaEmPgnException() {
        RecordingIterator lines = new RecordingIterator("[Event \"A\"]", "1. e4 1-0");
        lines.failAt = 2;

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> GameLoader.loadNextGame(lines, this::newGame));

        assertEquals("falha de leitura na linha 2", exception.getMessage());
    }

    @Test
    public void iteradorVazioNaoCriaPartida() {
        assertNull(GameLoader.loadNextGame(new RecordingIterator(), this::newGame));
        assertEquals(0, gamesCreated);
    }

    @Test
    public void linhasEmBrancoNaoProduzemPartida() {
        assertNull(load("", "   ", ""));
        assertEquals(1, gamesCreated);
        assertTrue(game.loadedTexts.isEmpty());
    }

    private Game load(String... lines) {
        return GameLoader.loadNextGame(new RecordingIterator(lines), this::newGame);
    }

    private Game newGame(Round round) {
        gamesCreated++;
        game = new GameStub(round, stubHalfMoves, stubFailure);
        return game;
    }

    /**
     * Partida que registra o texto recebido e devolve uma quantidade fixa de lances, sem interpretá-los.
     */
    private static class GameStub extends Game {

        final List<String> loadedTexts = new ArrayList<>();
        private final int halfMoves;
        private final RuntimeException failure;

        GameStub(Round round, int halfMoves, RuntimeException failure) {
            super("stub", round);
            this.halfMoves = halfMoves;
            this.failure = failure;
        }

        @Override
        public void loadMoveText(StringBuilder moveText) {
            if (failure != null) {
                throw failure;
            }
            loadedTexts.add(moveText.toString());
        }

        @Override
        public MoveList getHalfMoves() {
            MoveList moves = new MoveList();
            for (int i = 0; i < halfMoves; i++) {
                moves.add(new Move(Square.E2, Square.E4));
            }
            return moves;
        }
    }

    /**
     * Fonte de linhas que conta o consumo e pode falhar numa linha escolhida.
     */
    private static class RecordingIterator implements Iterator<String> {

        private final Iterator<String> lines;
        int consumed;
        int failAt = -1;

        RecordingIterator(String... lines) {
            this.lines = Arrays.asList(lines).iterator();
        }

        @Override
        public boolean hasNext() {
            return lines.hasNext();
        }

        @Override
        public String next() {
            if (!lines.hasNext()) {
                throw new NoSuchElementException();
            }
            consumed++;
            if (consumed == failAt) {
                throw new IllegalStateException("falha de leitura na linha " + consumed);
            }
            return lines.next();
        }
    }
}

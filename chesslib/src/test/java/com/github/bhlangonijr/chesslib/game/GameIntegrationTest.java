package com.github.bhlangonijr.chesslib.game;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class GameIntegrationTest {

    private static final String FINAL_FEN =
            "r1bqkbnr/pppp1ppp/2n5/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldLoadMetadataAndMovesFromRealPgnFile() throws Exception {
        PgnHolder holder = loadPgnFile(pgn("Integration Event", "3", "Ada", "Grace",
                "1. e4 e5 2. Nf3 Nc6 *"));

        assertEquals(1, holder.getGames().size());
        Game game = holder.getGames().get(0);
        assertEquals("Integration Event", game.getRound().getEvent().getName());
        assertEquals("UFF", game.getRound().getEvent().getSite());
        assertEquals("2026.11.22", game.getDate());
        assertEquals(3, game.getRound().getNumber());
        assertEquals("Ada", game.getWhitePlayer().getName());
        assertEquals("Grace", game.getBlackPlayer().getName());
        assertEquals(GameResult.ONGOING, game.getResult());
        assertEquals("4", game.getPlyCount());
        assertArrayEquals(new String[]{"e4", "e5", "Nf3", "Nc6"}, game.getHalfMoves().toSanArray());
        assertSame(game.getRound().getEvent(), holder.getEvent().get("Integration Event"));
    }

    @Test
    public void shouldNavigateLoadedGameUsingRealBoard() throws Exception {
        Game game = loadPgnFile(pgn("Navigation Event", "1", "Ada", "Grace",
                "1. e4 e5 2. Nf3 Nc6 *")).getGames().get(0);
        game.setBoard(new Board());

        game.gotoLast();

        assertEquals(3, game.getPosition());
        assertTrue(game.isEndOfMoveList());
        assertEquals(FINAL_FEN, game.getBoard().getFen());
    }

    private PgnHolder loadPgnFile(String contents) throws Exception {
        File file = temporaryFolder.newFile();
        Files.write(file.toPath(), contents.getBytes(StandardCharsets.UTF_8));
        PgnHolder holder = new PgnHolder(file.getAbsolutePath());
        holder.loadPgn();
        return holder;
    }

    private String pgn(String event, String round, String white, String black, String moveText) {
        return "[Event \"" + event + "\"]\n"
                + "[Site \"UFF\"]\n"
                + "[Date \"2026.11.22\"]\n"
                + "[Round \"" + round + "\"]\n"
                + "[White \"" + white + "\"]\n"
                + "[Black \"" + black + "\"]\n"
                + "[Result \"*\"]\n\n"
                + moveText + "\n\n";
    }
}

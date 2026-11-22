package com.github.bhlangonijr.chesslib.game;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.pgn.PgnException;
import com.github.bhlangonijr.chesslib.pgn.PgnHolder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.*;

public class GameIntegrationTest {

    private static final String FINAL_FEN =
            "r1bqkbnr/pppp1ppp/2n5/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R w KQkq - 2 3";
    private static final String E4_E5_FEN =
            "rnbqkbnr/pppp1ppp/8/4p3/4P3/8/PPPP1PPP/RNBQKBNR w KQkq e6 0 2";

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

        game.gotoPrior();
        assertEquals(2, game.getPosition());
        assertEquals("rnbqkbnr/pppp1ppp/8/4p3/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2",
                game.getBoard().getFen());

        game.gotoFirst();
        assertTrue(game.isStartOfMoveList());
        assertEquals("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1",
                game.getBoard().getFen());

        game.gotoNext();
        assertEquals(1, game.getPosition());
        assertEquals(E4_E5_FEN, game.getBoard().getFen());
    }

    @Test
    public void shouldKeepGamesIndependentWithinSameFile() throws Exception {
        PgnHolder holder = loadPgnFile(pgn("First Event", "1", "Ada", "Grace",
                "1. e4 $1 {first only} e5 *")
                + pgn("Second Event", "2", "Alan", "Edsger", "1. d4 d5 *"));

        assertEquals(2, holder.getGames().size());
        assertEquals(2, holder.getEvent().size());
        Game first = holder.getGames().get(0);
        Game second = holder.getGames().get(1);
        assertNotSame(first, second);
        assertEquals("First Event", first.getRound().getEvent().getName());
        assertEquals("Second Event", second.getRound().getEvent().getName());
        assertEquals(2, second.getRound().getNumber());
        assertEquals("Alan", second.getWhitePlayer().getName());
        assertEquals("Edsger", second.getBlackPlayer().getName());
        assertArrayEquals(new String[]{"e4", "e5"}, first.getHalfMoves().toSanArray());
        assertArrayEquals(new String[]{"d4", "d5"}, second.getHalfMoves().toSanArray());
        assertEquals("first only ", first.getComments().get(1));
        assertEquals("$1", first.getNag().get(1));
        assertNull(second.getComments());
        assertNull(second.getNag());
        assertNotSame(first.getHalfMoves(), second.getHalfMoves());
    }

    @Test
    public void shouldNavigateGameLoadedFromFenTag() throws Exception {
        String startFen = "4k3/8/8/8/8/8/8/4K3 w - - 0 1";
        String contents = pgn("FEN Event", "1", "Ada", "Grace", "1. Ke2 Kd7 *")
                .replace("[Result \"*\"]", "[SetUp \"1\"]\n[FEN \"" + startFen + "\"]\n[Result \"*\"]");
        Game game = loadPgnFile(contents).getGames().get(0);
        game.setBoard(new Board());

        game.gotoLast();

        assertEquals(startFen, game.getFen());
        assertEquals(startFen, game.getHalfMoves().getStartFen());
        assertArrayEquals(new String[]{"Ke2", "Kd7"}, game.getHalfMoves().toSanArray());
        assertEquals("8/3k4/8/8/8/8/4K3/8 w - - 2 2", game.getBoard().getFen());
    }

    @Test
    public void shouldNavigateLoadedVariationAndReturnToMainLine() throws Exception {
        Game game = loadPgnFile(pgn("Variation Event", "1", "Ada", "Grace",
                "1. e4 (1. d4 d5) e5 2. Nf3 Nc6 *")).getGames().get(0);
        game.setBoard(new Board());
        MoveList variation = game.getVariations().get(1).get(0);

        game.gotoLast(variation);

        assertSame(variation, game.getCurrentMoveList());
        assertArrayEquals(new String[]{"d4", "d5"}, variation.toSanArray());
        assertEquals(1, game.getPosition());
        assertTrue(game.isEndOfMoveList());
        assertEquals("rnbqkbnr/ppp1pppp/8/3p4/3P4/8/PPP1PPPP/RNBQKBNR w KQkq d6 0 2",
                game.getBoard().getFen());

        game.gotoLast(game.getHalfMoves());

        assertSame(game.getHalfMoves(), game.getCurrentMoveList());
        assertEquals(3, game.getPosition());
        assertEquals(FINAL_FEN, game.getBoard().getFen());
    }

    @Test
    public void shouldPreserveAnnotationsReadFromFile() throws Exception {
        Game game = loadPgnFile(pgn("Comments Event", "1", "Ada", "Grace",
                "1. e4 $1 {block note} e5 ; line note\n2. Nf3 Nc6 *")).getGames().get(0);
        game.setBoard(new Board());

        game.gotoLast();

        assertArrayEquals(new String[]{"e4", "e5", "Nf3", "Nc6"}, game.getHalfMoves().toSanArray());
        assertEquals("block note ", game.getComments().get(1));
        assertEquals("line note ", game.getComments().get(2));
        assertEquals("$1", game.getNag().get(1));
        assertNull(game.getVariations());
        assertEquals(FINAL_FEN, game.getBoard().getFen());
    }

    @Test
    public void shouldReportIllegalMainLineWithGameContext() throws Exception {
        File file = writePgnFile(pgn("Invalid Main", "4", "Ada", "Grace", "1. e4 Qh5 *"));
        PgnHolder holder = new PgnHolder(file.getAbsolutePath());

        PgnException exception = assertThrows(PgnException.class, holder::loadPgn);

        assertTrue(exception.getMessage().contains("Invalid Main"));
        assertTrue(exception.getMessage().contains("4"));
        assertTrue(exception.getCause() instanceof MoveConversionException);
        assertTrue(holder.getGames().isEmpty());
    }

    @Test
    public void shouldReportIllegalVariationWithCause() throws Exception {
        File file = writePgnFile(pgn("Invalid Variation", "5", "Ada", "Grace",
                "1. e4 (1. Qh5) e5 *"));
        PgnHolder holder = new PgnHolder(file.getAbsolutePath());

        PgnException exception = assertThrows(PgnException.class, holder::loadPgn);

        assertTrue(exception.getMessage().contains("Invalid Variation"));
        assertTrue(exception.getCause() instanceof PgnException);
        assertTrue(exception.getCause().getMessage().contains("Qh5"));
        assertTrue(exception.getCause().getCause() instanceof MoveConversionException);
        assertTrue(holder.getGames().isEmpty());
    }

    @Test
    public void shouldPreserveGameAfterSavingAndReloadingPgn() throws Exception {
        PgnHolder holder = loadPgnFile(pgn("Round Trip", "2", "Ada", "Grace",
                "1. e4 $1 {opening} (1. d4 d5) e5 *"));
        Game original = holder.getGames().get(0);
        File exported = temporaryFolder.newFile("exported.pgn");
        holder.setFileName(exported.getAbsolutePath());

        holder.savePgn();

        assertTrue(Files.size(exported.toPath()) > 0);
        String exportedText = new String(Files.readAllBytes(exported.toPath()), StandardCharsets.UTF_8);
        assertTrue(exportedText.contains("[Event \"Round Trip\"]"));
        PgnHolder reloaded = new PgnHolder(exported.getAbsolutePath());
        reloaded.loadPgn();

        assertEquals(1, reloaded.getGames().size());
        Game restored = reloaded.getGames().get(0);
        assertNotSame(original, restored);
        assertEquals("Round Trip", restored.getRound().getEvent().getName());
        assertEquals(2, restored.getRound().getNumber());
        assertEquals("2026.11.22", restored.getDate());
        assertEquals("Ada", restored.getWhitePlayer().getName());
        assertEquals("Grace", restored.getBlackPlayer().getName());
        assertEquals(GameResult.ONGOING, restored.getResult());
        assertEquals("2", restored.getPlyCount());
        assertArrayEquals(new String[]{"e4", "e5"}, restored.getHalfMoves().toSanArray());
        assertEquals("opening ", restored.getComments().get(1));
        assertEquals("$1", restored.getNag().get(1));
        assertArrayEquals(new String[]{"d4", "d5"}, restored.getVariations().get(1).get(0).toSanArray());
        restored.setBoard(new Board());
        restored.gotoLast();
        assertEquals(E4_E5_FEN, restored.getBoard().getFen());
    }

    @Test
    public void shouldLoadEmptyFileWithoutCreatingGame() throws Exception {
        PgnHolder holder = loadPgnFile("");

        assertTrue(holder.getGames().isEmpty());
        assertTrue(holder.getEvent().isEmpty());
        assertTrue(holder.getPlayer().isEmpty());
    }

    @Test
    public void shouldReportMissingPgnFile() {
        File missing = new File(temporaryFolder.getRoot(), "missing.pgn");
        assertFalse(missing.exists());
        PgnHolder holder = new PgnHolder(missing.getAbsolutePath());

        assertThrows(FileNotFoundException.class, holder::loadPgn);

        assertTrue(holder.getGames().isEmpty());
    }

    @Test
    public void shouldReadCrLfPgnAndBuildExpectedPosition() throws Exception {
        String contents = pgn("CRLF Event", "1", "Ada", "Grace", "1. e4 e5 2. Nf3 Nc6 *")
                .replace("\n", "\r\n");
        Game game = loadPgnFile(contents).getGames().get(0);
        game.setBoard(new Board());

        game.gotoLast();

        assertArrayEquals(new String[]{"e4", "e5", "Nf3", "Nc6"}, game.getHalfMoves().toSanArray());
        assertEquals(FINAL_FEN, game.getBoard().getFen());
    }

    private PgnHolder loadPgnFile(String contents) throws Exception {
        File file = writePgnFile(contents);
        PgnHolder holder = new PgnHolder(file.getAbsolutePath());
        holder.loadPgn();
        return holder;
    }

    private File writePgnFile(String contents) throws Exception {
        File file = temporaryFolder.newFile();
        Files.write(file.toPath(), contents.getBytes(StandardCharsets.UTF_8));
        return file;
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

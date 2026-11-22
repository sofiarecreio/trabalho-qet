package com.github.bhlangonijr.chesslib;

import com.github.bhlangonijr.chesslib.pgn.PgnHolder;
import com.github.bhlangonijr.chesslib.util.LargeFile;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import static org.junit.Assert.*;

public class PedroPgnHolderQualityTest {

    private static final String PGN = "[Event \"Inspecao\"]\n[Site \"Local\"]\n"
            + "[Date \"2026.11.22\"]\n[Round \"1\"]\n[White \"Brancas\"]\n"
            + "[Black \"Pretas\"]\n[Result \"*\"]\n[TimeControl \"60+2\"]\n\n1. e4 e5 *\n";

    @Rule
    public TemporaryFolder temporary = new TemporaryFolder();

    @Test
    @SuppressWarnings("deprecation")
    public void aliasObsoletoMantemMesmaListaMutavel() {
        PgnHolder holder = new PgnHolder();
        holder.loadPgn(PGN);
        assertSame(holder.getGames(), holder.getGame());
        holder.getGame().clear();
        assertTrue(holder.getGames().isEmpty());
    }

    @Test
    public void salvarERecarregarPreservaPartidaEMovimentos() throws Exception {
        Path output = temporary.newFile("partida.pgn").toPath();
        PgnHolder holder = new PgnHolder(output.toString());
        holder.loadPgn(PGN);
        holder.savePgn();
        assertTrue(Files.readString(output).contains("[Event \"Inspecao\"]"));
        PgnHolder restored = new PgnHolder(output.toString());
        restored.loadPgn();
        assertEquals(1, restored.getGames().size());
        assertEquals("Brancas", restored.getGames().get(0).getWhitePlayer().getName());
        assertEquals("e2e4 e7e5", restored.getGames().get(0).getHalfMoves().toString());
        assertEquals(holder.toString(), restored.toString());
    }

    @Test
    public void salvarEmDestinoInexistentePropagaCausaDeIO() {
        Path output = temporary.getRoot().toPath().resolve("inexistente/partida.pgn");
        PgnHolder holder = new PgnHolder(output.toString());
        UncheckedIOException error = assertThrows(UncheckedIOException.class, holder::savePgn);
        assertTrue(error.getMessage().contains(output.toString()));
        assertNotNull(error.getCause());
    }

    @Test
    public void salvarHolderVazioCriaArquivoVazio() throws IOException {
        Path output = temporary.getRoot().toPath().resolve("vazio.pgn");
        new PgnHolder(output.toString()).savePgn();
        assertEquals(0L, Files.size(output));
    }

    @Test
    public void contarEventosNaoCarregaPartidasEPermiteRemoverArquivo() throws IOException {
        Path input = temporary.newFile("contagem.pgn").toPath();
        Files.writeString(input, PGN + "\n" + PGN);
        PgnHolder holder = new PgnHolder(input.toString());
        assertEquals(2L, holder.countGamesInPgnFile());
        assertTrue(holder.getGames().isEmpty());
        Files.delete(input);
        assertFalse(Files.exists(input));
    }

    @Test
    public void contarArquivoAusentePropagaIOException() {
        PgnHolder holder = new PgnHolder(temporary.getRoot().toPath().resolve("ausente.pgn").toString());
        assertThrows(IOException.class, holder::countGamesInPgnFile);
    }

    @Test
    public void cargaBemSucedidaFechaRecursoENotificaProgresso() {
        TrackedFile input = new TrackedFile(false);
        PgnHolder holder = new PgnHolder();
        int[] progress = {0};
        holder.getListener().add(size -> progress[0] = size);
        holder.loadPgn(input);
        assertTrue(input.closed);
        assertEquals(1, holder.getGames().size());
        assertEquals(1, progress[0]);
    }

    @Test
    public void falhaNaCriacaoDoIteradorTambemFechaRecurso() {
        TrackedFile input = new TrackedFile(true);
        PgnHolder holder = new PgnHolder();
        IllegalStateException error = assertThrows(IllegalStateException.class, () -> holder.loadPgn(input));
        assertEquals("falha controlada", error.getMessage());
        assertTrue(input.closed);
        assertTrue(holder.getGames().isEmpty());
    }

    private static class TrackedFile extends LargeFile {
        private final boolean fail;
        private boolean closed;

        TrackedFile(boolean fail) {
            super(new ByteArrayInputStream(PGN.getBytes(StandardCharsets.UTF_8)));
            this.fail = fail;
        }

        @Override
        public Iterator<String> iterator() {
            if (fail) {
                throw new IllegalStateException("falha controlada");
            }
            return super.iterator();
        }

        @Override
        public void close() {
            closed = true;
            super.close();
        }
    }
}

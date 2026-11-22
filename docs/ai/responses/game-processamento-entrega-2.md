# Resposta — Melhoria dos testes de processamento de Game

## Correção proposta

```diff
diff --git a/chesslib/src/main/java/com/github/bhlangonijr/chesslib/game/Game.java b/chesslib/src/main/java/com/github/bhlangonijr/chesslib/game/Game.java
index 874f327..7945e42 100644
--- a/chesslib/src/main/java/com/github/bhlangonijr/chesslib/game/Game.java
+++ b/chesslib/src/main/java/com/github/bhlangonijr/chesslib/game/Game.java
@@ -750,0 +751,2 @@ public class Game {
+        StringUtil.replaceAll(moveText, "\r\n", "\n");
+        StringUtil.replaceAll(moveText, "\r", "\n");
@@ -777,0 +780,9 @@ public class Game {
+            if (token.equals("\n") && onLineCommentBlock) {
+                if (getComments() == null) {
+                    setComments(new HashMap<>());
+                }
+                getComments().put(variantIndex, comment.toString());
+                onLineCommentBlock = false;
+                comment = null;
+                continue;
+            }
@@ -780,0 +792,5 @@ public class Game {
+            if (onLineCommentBlock) {
+                comment.append(token);
+                comment.append(StringUtils.SPACE);
+                continue;
+            }
@@ -818 +834 @@ public class Game {
-            } else if (token.equals(";") && !onCommentBlock) {
+            } else if (token.startsWith(";") && !onCommentBlock) {
@@ -821,5 +837,3 @@ public class Game {
-                continue;
-            } else if (token.equals("\n") && onLineCommentBlock) {
-                onLineCommentBlock = false;
-                if (comment != null) {
-                    getComments().put(variantIndex, comment.toString());
+                if (token.length() > 1) {
+                    comment.append(token.substring(1));
+                    comment.append(StringUtils.SPACE);
@@ -827 +840,0 @@ public class Game {
-                comment = null;
@@ -830 +843 @@ public class Game {
-                    !(onCommentBlock) || onLineCommentBlock) {
+                    !(onCommentBlock || onLineCommentBlock)) {
@@ -838 +851 @@ public class Game {
-                    !(onCommentBlock) || onLineCommentBlock) {
+                    !(onCommentBlock || onLineCommentBlock)) {
@@ -905,0 +919,6 @@ public class Game {
+        if (onLineCommentBlock) {
+            if (getComments() == null) {
+                setComments(new HashMap<>());
+            }
+            getComments().put(variantIndex, comment.toString());
+        }
```

## Testes propostos

```java
package com.github.bhlangonijr.chesslib.game;

import com.github.bhlangonijr.chesslib.move.MoveList;
import com.github.bhlangonijr.chesslib.move.MoveConversionException;
import com.github.bhlangonijr.chesslib.pgn.PgnException;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class GameProcessingTest {

    private static final String KINGS_FEN = "4k3/8/8/8/8/8/8/4K3 w - - 0 1";
    private Game game;

    @Before
    public void setUp() {
        Event event = new Event();
        event.setName("Processing Test");
        event.setSite("UFF");
        event.setStartDate("2026.11.22");
        game = new Game("processing-game", new Round(event));
        game.setWhitePlayer(new GenericPlayer("white", "White Player"));
        game.setBlackPlayer(new GenericPlayer("black", "Black Player"));
        game.setPlyCount("0");
    }

    @Test
    public void shouldResumeMovesAfterLineComment() throws Exception {
        game.loadMoveText(new StringBuilder("1. e4 ; good move\n e5"));

        assertArrayEquals(new String[]{"e4", "e5"}, game.getHalfMoves().toSanArray());
        assertEquals("good move ", game.getComments().get(1));
        assertNull(game.getVariations());
    }

    @Test
    public void shouldClearAnnotationsWhenLoadingAnotherGame() throws Exception {
        game.loadMoveText(new StringBuilder("1. e4 $1 {old comment} (1. d4 d5) e5"));
        assertFalse(game.getComments().isEmpty());
        assertFalse(game.getNag().isEmpty());
        assertFalse(game.getVariations().isEmpty());

        game.loadMoveText(new StringBuilder("1. c4 e5"));

        assertArrayEquals(new String[]{"c4", "e5"}, game.getHalfMoves().toSanArray());
        assertTrue(game.getComments().isEmpty());
        assertTrue(game.getNag().isEmpty());
        assertTrue(game.getVariations().isEmpty());
        assertSame(game.getHalfMoves(), game.getCurrentMoveList());
    }

    @Test
    public void shouldLoadMovesFromCustomFen() throws Exception {
        game.setFen(KINGS_FEN);

        game.loadMoveText(new StringBuilder("1. Ke2 Kd7"));

        assertEquals(KINGS_FEN, game.getHalfMoves().getStartFen());
        assertArrayEquals(new String[]{"Ke2", "Kd7"}, game.getHalfMoves().toSanArray());
        assertSame(game.getHalfMoves(), game.getCurrentMoveList());
    }

    @Test
    public void shouldGeneratePgnWhenOnlyRawMoveTextIsAvailable() throws Exception {
        game.setHalfMoves(new MoveList());
        game.setMoveText(new StringBuilder("1. e4 e5 "));
        game.setPlyCount("2");

        String pgn = game.toPgn(true, true);

        assertTrue(pgn.contains("[Event \"Processing Test\"]"));
        assertTrue(pgn.endsWith("\n\n1. e4 e5 *"));
        assertTrue(game.getHalfMoves().isEmpty());
    }

    @Test
    public void shouldPreserveLineCommentWithoutFinalNewline() throws Exception {
        game.loadMoveText(new StringBuilder("1. e4 e5 ; final note"));

        assertArrayEquals(new String[]{"e4", "e5"}, game.getHalfMoves().toSanArray());
        assertEquals("final note ", game.getComments().get(2));
        assertNull(game.getVariations());
    }

    @Test
    public void shouldPreserveEmptyLineComment() throws Exception {
        game.loadMoveText(new StringBuilder("1. e4 ;\n e5"));

        assertArrayEquals(new String[]{"e4", "e5"}, game.getHalfMoves().toSanArray());
        assertTrue(game.getComments().containsKey(1));
        assertEquals("", game.getComments().get(1));
    }

    @Test
    public void shouldResumeMovesAfterCrLfLineComment() throws Exception {
        game.loadMoveText(new StringBuilder("1. e4 ; note\r\n e5"));

        assertArrayEquals(new String[]{"e4", "e5"}, game.getHalfMoves().toSanArray());
        assertEquals("note ", game.getComments().get(1));
    }

    @Test
    public void shouldTreatNotationMarkersInsideLineCommentAsText() throws Exception {
        game.loadMoveText(new StringBuilder("1. e4 ; $1 ( not a variation ) { note } 12...\n e5"));

        assertArrayEquals(new String[]{"e4", "e5"}, game.getHalfMoves().toSanArray());
        assertEquals("$1 ( not a variation ) { note } 12... ", game.getComments().get(1));
        assertNull(game.getVariations());
        assertNull(game.getNag());
    }

    @Test
    public void shouldAllowCommentTextImmediatelyAfterSemicolon() throws Exception {
        game.loadMoveText(new StringBuilder("1. e4 ;note\n e5"));

        assertArrayEquals(new String[]{"e4", "e5"}, game.getHalfMoves().toSanArray());
        assertEquals("note ", game.getComments().get(1));
    }

    @Test
    public void shouldKeepSemicolonInsideBlockCommentAsText() throws Exception {
        game.loadMoveText(new StringBuilder("1. e4 { note ; more } e5"));

        assertArrayEquals(new String[]{"e4", "e5"}, game.getHalfMoves().toSanArray());
        assertEquals("note ; more ", game.getComments().get(1));
    }

    @Test
    public void shouldLoadPreviouslyAssignedMoveText() throws Exception {
        game.setMoveText(new StringBuilder("1. d4 d5"));

        game.loadMoveText();

        assertArrayEquals(new String[]{"d4", "d5"}, game.getHalfMoves().toSanArray());
        assertSame(game.getHalfMoves(), game.getCurrentMoveList());
    }

    @Test
    public void shouldKeepMovesWhenNoMoveTextWasAssigned() throws Exception {
        game.loadMoveText(new StringBuilder("1. e4 e5"));
        MoveList original = game.getHalfMoves();
        assertNull(game.getMoveText());

        game.loadMoveText();

        assertSame(original, game.getHalfMoves());
        assertArrayEquals(new String[]{"e4", "e5"}, original.toSanArray());
    }

    @Test
    public void shouldRecreateMissingMoveListWithDefaultFen() {
        game.setHalfMoves(null);

        MoveList recreated = game.getHalfMoves();

        assertNotNull(recreated);
        assertTrue(recreated.isEmpty());
        assertEquals("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                recreated.getStartFen());
        assertSame(recreated, game.getHalfMoves());
    }

    @Test
    public void shouldRecreateMissingMoveListWithCustomFen() {
        game.setFen(KINGS_FEN);
        game.setHalfMoves(null);

        MoveList recreated = game.getHalfMoves();

        assertTrue(recreated.isEmpty());
        assertEquals(KINGS_FEN, recreated.getStartFen());
        assertSame(recreated, game.getHalfMoves());
    }

    @Test
    public void shouldKeepSuccessiveVariationsAtSameBranch() throws Exception {
        game.loadMoveText(new StringBuilder("1. e4 (1. d4 d5) (1. c4 e5) e5"));

        List<MoveList> alternatives = game.getVariations().get(1);
        assertEquals(2, alternatives.size());
        assertArrayEquals(new String[]{"d4", "d5"}, alternatives.get(0).toSanArray());
        assertArrayEquals(new String[]{"c4", "e5"}, alternatives.get(1).toSanArray());
        assertArrayEquals(new String[]{"e4", "e5"}, game.getHalfMoves().toSanArray());
    }

    @Test
    public void shouldKeepParentOfNestedVariation() throws Exception {
        game.loadMoveText(new StringBuilder("1. e4 (1. d4 d5 (1... Nf6)) e5"));

        MoveList outer = game.getVariations().get(1).get(0);
        MoveList inner = game.getVariations().get(3).get(0);
        assertArrayEquals(new String[]{"d4", "d5"}, outer.toSanArray());
        assertArrayEquals(new String[]{"Nf6"}, inner.toSanArray());
        assertEquals(-1, outer.getParent());
        assertEquals(1, inner.getParent());
        assertArrayEquals(new String[]{"e4", "e5"}, game.getHalfMoves().toSanArray());
    }

    @Test
    public void shouldReportInvalidMoveInsideVariation() {
        PgnException exception = assertThrows(PgnException.class,
                () -> game.loadMoveText(new StringBuilder("1. e4 (1. Qh5) e5")));

        assertTrue(exception.getCause() instanceof MoveConversionException);
        assertTrue(exception.getMessage().contains("Qh5"));
    }

    @Test
    public void shouldIncludeOptionalPgnMetadata() throws Exception {
        game.setFen(KINGS_FEN);
        game.setAnnotator("Weslley");
        game.setEco("A00");
        game.setOpening("Custom position");
        game.setTermination(Termination.NORMAL);
        game.getWhitePlayer().setElo(1600);
        game.getBlackPlayer().setElo(1700);
        game.getRound().getEvent().setTimeControl(TimeControl.parseFromString("300+2"));
        game.setProperty(Collections.singletonMap("Source", "ProcessingTest"));
        game.loadMoveText(new StringBuilder("1. Ke2 Kd7"));

        String pgn = game.toPgn(true, true);

        assertTrue(pgn.contains("[FEN \"" + KINGS_FEN + "\"]"));
        assertTrue(pgn.contains("[Annotator \"Weslley\"]"));
        assertTrue(pgn.contains("[ECO \"A00\"]"));
        assertTrue(pgn.contains("[Opening \"Custom position\"]"));
        assertTrue(pgn.contains("[Termination \"normal\"]"));
        assertTrue(pgn.contains("[WhiteElo \"1600\"]"));
        assertTrue(pgn.contains("[BlackElo \"1700\"]"));
        assertTrue(pgn.contains("[TimeControl \"300+2\"]"));
        assertTrue(pgn.contains("[Source \"ProcessingTest\"]"));
        assertTrue(pgn.endsWith("1. Ke2 Kd7 *"));
    }

    @Test
    public void shouldOmitUnsetOptionalPgnMetadata() throws Exception {
        game.loadMoveText(new StringBuilder("1. e4 e5"));

        String pgn = game.toPgn(true, true);

        for (String tag : new String[]{"FEN", "Annotator", "ECO", "Opening", "Termination", "WhiteElo", "BlackElo"}) {
            assertFalse("Unexpected optional tag: " + tag, pgn.contains("[" + tag + " "));
        }
        assertTrue(pgn.contains("[TimeControl \"-\"]"));
    }

    @Test
    public void shouldGeneratePgnWithEmptyRawMoveText() throws Exception {
        game.setMoveText(new StringBuilder());

        String pgn = game.toPgn(true, true);

        assertTrue(pgn.endsWith("\n\n*"));
        assertTrue(game.getHalfMoves().isEmpty());
    }

    @Test
    public void shouldSerializeNestedVariationsWithoutLosingMainLine() throws Exception {
        game.loadMoveText(new StringBuilder("1. e4 (1. d4 d5 (1... Nf6)) e5"));

        String pgn = game.toPgn(true, true);

        assertTrue(pgn.contains("(1. d4 d5"));
        assertTrue(pgn.contains("Nf6"));
        assertTrue(pgn.endsWith("e5 *"));
        assertArrayEquals(new String[]{"e4", "e5"}, game.getHalfMoves().toSanArray());
    }

    @Test
    public void shouldWrapLongPgnMoveTextWithoutLosingMoves() throws Exception {
        game.loadMoveText(new StringBuilder("1. Nf3 Nf6 2. Ng1 Ng8 3. Nf3 Nf6 4. Ng1 Ng8 "
                + "5. Nf3 Nf6 6. Ng1 Ng8 7. Nf3 Nf6 8. Ng1 Ng8 9. Nf3 Nf6 10. Ng1 Ng8"));

        String pgn = game.toPgn(true, true);
        String moveText = pgn.substring(pgn.indexOf("\n\n") + 2);

        assertTrue(moveText.contains("\n"));
        assertTrue(moveText.replace('\n', ' ').contains("10. Ng1 Ng8 *"));
        assertEquals(40, game.getHalfMoves().size());
    }
}
```

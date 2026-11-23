/*
 * Copyright 2017 Ben-Hur Carlos Vieira Langoni Junior
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.bhlangonijr.chesslib.pgn;

import com.github.bhlangonijr.chesslib.game.Event;
import com.github.bhlangonijr.chesslib.game.Game;
import com.github.bhlangonijr.chesslib.game.GameResult;
import com.github.bhlangonijr.chesslib.game.GenericPlayer;
import com.github.bhlangonijr.chesslib.game.Player;
import com.github.bhlangonijr.chesslib.game.PlayerType;
import com.github.bhlangonijr.chesslib.game.Round;
import com.github.bhlangonijr.chesslib.game.Termination;
import com.github.bhlangonijr.chesslib.game.TimeControl;
import com.github.bhlangonijr.chesslib.util.StringUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.function.Function;

import static com.github.bhlangonijr.chesslib.pgn.PgnProperty.UTF8_BOM;
import static com.github.bhlangonijr.chesslib.pgn.PgnProperty.isProperty;
import static com.github.bhlangonijr.chesslib.pgn.PgnProperty.parsePgnProperty;

/**
 * A convenient loader to extract a chess game and its metadata from an iterator over the lines of the PGN file.
 * <p>
 * The implementation allows loading only a single PGN game at a time.
 */
public class GameLoader {

    private GameLoader() {
        // utility class: only static methods
    }

    /**
     * Loads the next game of chess from an iterator over the lines of a Portable Game Notation (PGN) file. The
     * iteration ends when the game is fully loaded, hence the iterator is not consumed more than necessary.
     *
     * @param iterator the iterator over the lines of a PGN file
     * @return the next game read from the iterator
     */
    public static Game loadNextGame(Iterator<String> iterator) {
        return loadNextGame(iterator, round -> new Game(UUID.randomUUID().toString(), round));
    }

    /**
     * Loads the next game creating it through the given factory. The public method uses a regular {@link Game};
     * this package-private variant lets tests replace the game, isolating the loader from move parsing.
     *
     * @param iterator    the iterator over the lines of a PGN file
     * @param gameFactory creates the game that receives the loaded data, given its round
     * @return the next game read from the iterator
     */
    static Game loadNextGame(Iterator<String> iterator, Function<Round, Game> gameFactory) {

        if (!iterator.hasNext()) {
            return null;
        }

        PgnTempContainer container = new PgnTempContainer(gameFactory);

        while (iterator.hasNext()) {
            String line = removeBom(iterator.next().trim());
            try {
                if (processLine(line, container)) {
                    return container.initGame ? container.game : null;
                }
            } catch (RuntimeException e) {
                // parsing only raises unchecked exceptions; they are reported with the event context
                String name = container.event().getName();
                int r = container.round().getNumber();
                throw new PgnException(String.format("Error parsing PGN[%d, %s]: ", r, name), e);
            }
        }
        return container.initGame ? container.game : null;
    }

    private static String removeBom(String line) {
        return line.startsWith(UTF8_BOM) ? line.substring(1) : line;
    }

    /**
     * Processes one trimmed PGN line.
     *
     * @return {@code true} when the line closes the current game
     */
    private static boolean processLine(String line, PgnTempContainer container) {
        if (isProperty(line)) {
            addProperty(line, container);
            return false;
        }
        if (StringUtils.isEmpty(line)) {
            return false;
        }
        addMoveText(line, container);
        if (isEndGame(line, container)) {
            setMoveText(container.game, container.moveText);
            return true;
        }
        return false;
    }

    private static void addProperty(String line, PgnTempContainer container) {
        PgnProperty property = parsePgnProperty(line);
        if (property == null) {
            return;
        }
        container.initGame = true;
        String tag = property.name.toLowerCase().trim();
        Game game = container.game;
        //begin
        switch (tag) {
            case "event":
                setEvent(property.value, container);
                break;
            case "site":
                container.event().setSite(property.value);
                break;
            case "date":
                container.event().setStartDate(property.value);
                break;
            case "round":
                setRound(property.value, container);
                break;
            case "white":
                setPlayerDetails(property.value, game.getWhitePlayer(), container);
                break;
            case "black":
                setPlayerDetails(property.value, game.getBlackPlayer(), container);
                break;
            case "result":
                game.setResult(GameResult.fromNotation(property.value));
                break;
            case "plycount":
                game.setPlyCount(property.value);
                break;
            case "termination":
                game.setTermination(parseTermination(property.value));
                break;
            case "timecontrol":
                setTimeControl(property.value, container.event());
                break;
            case "annotator":
                game.setAnnotator(property.value);
                break;
            case "fen":
                game.setFen(property.value);
                break;
            case "eco":
                game.setEco(property.value);
                break;
            case "opening":
                game.setOpening(property.value);
                break;
            case "variation":
                game.setVariation(property.value);
                break;
            case "whiteelo":
                setElo(property.value, game.getWhitePlayer());
                break;
            case "blackelo":
                setElo(property.value, game.getBlackPlayer());
                break;
            default:
                addCustomProperty(property, game);
                break;
        }
    }

    private static void setEvent(String value, PgnTempContainer container) {
        if (container.moveTextParsing && container.game.getHalfMoves().isEmpty()) {
            setMoveText(container.game, container.moveText);
        }
        container.event().setName(value);
        container.event().setId(value);
    }

    private static void setRound(String value, PgnTempContainer container) {
        int roundNumber = Math.max(0, parseIntegerOrDefault(value, 1));
        Round round = container.round();
        round.setNumber(roundNumber);
        if (!container.event().getRound().containsKey(roundNumber)) {
            container.event().getRound().put(roundNumber, round);
        }
    }

    private static Termination parseTermination(String value) {
        try {
            return Termination.fromValue(value.toUpperCase());
        } catch (Exception e1) {
            return Termination.UNTERMINATED;
        }
    }

    private static void setTimeControl(String value, Event event) {
        if (event.getTimeControl() != null) {
            return;
        }
        try {
            event.setTimeControl(TimeControl.parseFromString(value.toUpperCase()));
        } catch (Exception e1) {
            //ignore errors in time control tag as it's not required by standards
        }
    }

    private static void setElo(String value, Player player) {
        player.setElo(parseIntegerOrDefault(value, player.getElo()));
    }

    private static void addCustomProperty(PgnProperty property, Game game) {
        if (game.getProperty() == null) {
            game.setProperty(new HashMap<>());
        }
        game.getProperty().put(property.name, property.value);
    }

    private static void addMoveText(String line, PgnTempContainer container) {
        container.initGame = true;
        container.moveText.append(line);
        container.moveText.append('\n');
        container.moveTextParsing = true;
    }

    private static boolean isEndGame(String line, PgnTempContainer container) {
        updateCommentState(line, container);
        if (container.commentOpened) {
            return false;
        }
        return line.endsWith("1-0") || line.endsWith("0-1")
                || line.endsWith("1/2-1/2") || line.endsWith("*");
    }

    private static void updateCommentState(String line, PgnTempContainer container) {
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '{') {
                container.commentOpened = true;
            } else if (c == '}' && container.commentOpened) {
                container.commentOpened = false;
            }
        }
    }

    /**
     * Parsing state of the game being loaded. Event, round and players are reached through the game itself.
     */
    private static class PgnTempContainer {

        final Game game;
        final StringBuilder moveText;
        boolean moveTextParsing;
        boolean initGame;
        boolean commentOpened;

        PgnTempContainer(Function<Round, Game> gameFactory) {
            Round round = new Round(new Event());
            this.game = gameFactory.apply(round);
            round.getGame().add(this.game);
            this.game.setWhitePlayer(newHumanPlayer());
            this.game.setBlackPlayer(newHumanPlayer());
            this.moveText = new StringBuilder();
        }

        Round round() {
            return game.getRound();
        }

        Event event() {
            return game.getRound().getEvent();
        }

        private static Player newHumanPlayer() {
            Player player = new GenericPlayer();
            player.setType(PlayerType.HUMAN);
            return player;
        }
    }

    private static void setMoveText(Game game, StringBuilder moveText) {

        //clear game result
        StringUtil.replaceAll(moveText, "1-0", StringUtils.EMPTY);
        StringUtil.replaceAll(moveText, "0-1", StringUtils.EMPTY);
        StringUtil.replaceAll(moveText, "1/2-1/2", StringUtils.EMPTY);
        StringUtil.replaceAll(moveText, "*", StringUtils.EMPTY);

        game.setMoveText(moveText);
        game.loadMoveText(moveText);

        game.setPlyCount(String.valueOf(game.getHalfMoves().size()));

    }


    private static int parseIntegerOrDefault(
            String value,
            int defaultValue) {

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }


    private static void setPlayerDetails(
            String value,
            Player player,
            PgnTempContainer container) {

        if (container.round().getNumber() < 1) {
            container.round().setNumber(1);
        }

        container.game.setDate(container.event().getStartDate());
        player.setId(value);
        player.setName(value);
        player.setDescription(value);
    }
}

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

package com.github.bhlangonijr.chesslib.move;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.github.bhlangonijr.chesslib.Bitboard;
import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Constants;
import com.github.bhlangonijr.chesslib.File;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.PieceType;
import com.github.bhlangonijr.chesslib.Rank;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.util.StringUtil;

/**
 * A convenient data structure to store an ordered sequence of moves and access to their human-readable representation
 * in one of the standard chess formats. This implementation can be used to hold the list of moves played in a chess
 * game.
 * <p>
 * The move list keeps a reference to a base initial position (by default, the standard starting chess position) used
 * to validate and disambiguate between moves.
 * <p>
 * This data structure is a {@link List}, thus the standard API of the Java collection is available for this class as
 * well.
 */
public class MoveList extends LinkedList<Move> implements List<Move> {

    private static final long serialVersionUID = -6204280556340150806L;
    private static final ThreadLocal<Board> boardHolder = ThreadLocal.withInitial(Board::new);
    private static final Move nullMove = new Move(Square.NONE, Square.NONE);
    private static final Pattern SAN_DECORATION_PATTERN =
            Pattern.compile("[+#!?]");

    private final String startFEN;
    private boolean dirty = true;

    private String[] sanArray;
    private String[] fanArray;
    private int parent;
    private int index;

    /**
     * Constructs an empty move list, using the standard starting position as a base.
     */
    public MoveList() {
        this(Constants.startStandardFENPosition);
    }

    /**
     * Constructs an empty move list, using the provided initial position as a base, retrieved in Forsyth-Edwards
     * Notation (FEN).
     *
     * @param initialFEN the FEN representation of the base position
     */
    public MoveList(String initialFEN) {
        this.startFEN = initialFEN;
    }

    /**
     * Constructs a new move list starting from an existing one. The new instance will use the initial position of the
     * existing list as a base.
     *
     * @param halfMoves the existing move list
     */
    public MoveList(MoveList halfMoves) {
        this(halfMoves.getStartFen());

        this.dirty = halfMoves.dirty;
        this.sanArray = halfMoves.sanArray == null
                ? null
                : halfMoves.sanArray.clone();
        this.fanArray = halfMoves.fanArray == null
                ? null
                : halfMoves.fanArray.clone();
        this.parent = halfMoves.parent;
        this.index = halfMoves.index;

        super.addAll(halfMoves);
    }

    /**
     * Returns a reference to the board representing the last position after the moves are played.
     *
     * @return the board representing the position after the moves are played
     */
    private static Board getBoard() {
        return boardHolder.get();
    }

    /**
     * Encodes the move to its Short Algebraic Notation (SAN), using the context of the given board.
     *
     * @param board the board used as context for encoding the move
     * @param move  the move to encode
     * @return the SAN notation of the move
     * @throws MoveConversionException if the move conversion fails
     */
    // encode the move to SAN move and update thread local board
    private static String encodeToSan(final Board board, Move move) throws MoveConversionException {
        return encode(board, move, Piece::getSanSymbol);
    }

    /**
     * Encodes the move to its Figurine Algebraic Notation (FAN), using the context of the given board.
     *
     * @param board the board used as context for encoding the move
     * @param move  the move to encode
     * @return the FAN notation of the move
     * @throws MoveConversionException if the move conversion fails
     */
    // encode the move to FAN move and update thread local board
    private static String encodeToFan(final Board board, Move move) throws MoveConversionException {
        return encode(board, move, Piece::getFanSymbol);
    }

    /**
     * Encodes the move using a conversion function and the given board as context.
     *
     * @param board    the board used as context for encoding the move
     * @param move     the move to encode
     * @param notation the conversion function to transform the move to its string representation
     * @return the notation of the move
     * @throws MoveConversionException if the move conversion fails
     */
    // encode the move to SAN/FAN move and update thread local board
    private static String encode(final Board board, Move move,
            Function<Piece, String> notation)
            throws MoveConversionException {

        Piece piece = board.getPiece(move.getFrom());

        if (piece.getPieceType().equals(PieceType.KING)
                && board.getContext().isCastleMove(move)) {
            return encodeCastleMove(board, move);
        }

        StringBuilder san = new StringBuilder();

        san.append(notation.apply(piece));

        boolean ambResolved =
                appendAmbiguityResolution(board, move, piece, san);

        if (!board.doMove(move, true)) {
            throw new MoveConversionException(
                    "Invalid move [" + move +
                    "] for current setup: " + board.getFen());
        }

        Piece captured = board.getBackup()
                .getLast()
                .getCapturedPiece();

        boolean isCapture = !captured.equals(Piece.NONE);

        if (isCapture) {
            if (!ambResolved
                    && piece.getPieceType().equals(PieceType.PAWN)) {
                san.append(move.getFrom()
                        .getFile()
                        .getNotation()
                        .toLowerCase());
            }

            san.append("x");
        }

        san.append(move.getTo().toString().toLowerCase());

        if (!move.getPromotion().equals(Piece.NONE)) {
            san.append("=");
            san.append(notation.apply(move.getPromotion()));
        }

        addCheckFlag(board, san);
        return san.toString();
    }
    
    private static boolean appendAmbiguityResolution(
            Board board,
            Move move,
            Piece piece,
            StringBuilder san) {

        boolean pawnMove =
                piece.getPieceType().equals(PieceType.PAWN)
                && move.getFrom().getFile().equals(move.getTo().getFile());

        if (pawnMove) {
            return false;
        }

        long ambiguousSquares = board.squareAttackedByPieceType(
                move.getTo(),
                board.getSideToMove(),
                piece.getPieceType());

        ambiguousSquares &= ~move.getFrom().getBitboard();

        if (ambiguousSquares != 0L) {
            List<Square> possibleSources =
                    Bitboard.bbToSquareList(ambiguousSquares);

            for (Square source : possibleSources) {
                Move possibleMove = new Move(source, move.getTo());

                if (!board.isMoveLegal(possibleMove, false)) {
                    ambiguousSquares ^= source.getBitboard();
                }
            }
        }

        if (ambiguousSquares == 0L) {
            return false;
        }

        if ((Bitboard.getFilebb(move.getFrom())
                & ambiguousSquares) == 0L) {

            san.append(move.getFrom()
                    .getFile()
                    .getNotation()
                    .toLowerCase());

        } else if ((Bitboard.getRankbb(move.getFrom())
                & ambiguousSquares) == 0L) {

            san.append(move.getFrom()
                    .getRank()
                    .getNotation()
                    .toLowerCase());

        } else {
            san.append(move.getFrom()
                    .toString()
                    .toLowerCase());
        }

        return true;
    }

    private static String encodeCastleMove(Board board, Move move)
            throws MoveConversionException {

        if (!board.doMove(move, true)) {
            throw new MoveConversionException(
                    "Invalid move [" + move +
                    "] for current setup: " + board.getFen());
        }

        StringBuilder san = new StringBuilder();

        san.append(board.getContext().isKingSideCastle(move)
                ? "O-O"
                : "O-O-O");

        addCheckFlag(board, san);
        return san.toString();
    }

    private static void addCheckFlag(Board board, StringBuilder san) {

        if (board.isKingAttacked()) {
            if (board.isMated()) {
                san.append("#");
            } else {
                san.append("+");
            }
        }
    }

    private static long findLegalSquares(Board board, Square to, Piece promotion, long pieces) {
        long result = 0L;

        if (pieces != 0L) {
            for (Square sqSource : Bitboard.bbToSquareList(pieces)) {
                Move move = new Move(sqSource, to, promotion);
                if (board.isMoveLegal(move, true)) {
                    result |= sqSource.getBitboard();
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Creates a new instance using an existing list of moves. The new instance will use the initial position of the
     * existing list as a base.
     * <p>
     * The returned list will contain only the first {@code finalIndex} moves of the original list, or all the elements
     * if {@code finalIndex} is outside the boundaries of the source list.
     *
     * @param startMoves the existing list of moves
     * @param finalIndex the last index of the source list to use
     * @return the new list of moves
     * @throws MoveConversionException if the starting list of moves is invalid
     */
    public static MoveList createMoveListFrom(MoveList startMoves, int finalIndex) throws MoveConversionException {
        String fen = null;
        final Board b = getBoard();
        if (!b.getFen().equals(startMoves.getStartFen())) {
            b.loadFromFen(startMoves.getStartFen());
        }
        int i = 0;
        for (Move move : startMoves) {
            i++;
            if (!b.doMove(move, false)) {
                throw new MoveConversionException("Couldn't parse SAN to MoveList: Illegal move: " +
                        move + " [" + move.toString() + "] on " + b.getFen());
            }
            if (i >= finalIndex) {
                fen = b.getFen();
                break;
            }
        }
        if (fen == null) {
            fen = b.getFen();
        }
        return new MoveList(fen);
    }

    /**
     * Returns the index of the moves list.
     *
     * @return the index of the list
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets the index of the moves list.
     *
     * @param index the index of the list
     */
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void add(int index, Move move) {
        dirty = true;
        super.add(index, move);
    }

    @Override
    public boolean add(Move move) {
        dirty = true;
        return super.add(move);
    }

    @Override
    public boolean addAll(Collection<? extends Move> moves) {
        dirty = true;
        return super.addAll(moves);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Move> moves) {
        dirty = true;
        return super.addAll(index, moves);
    }

    @Override
    public Move removeFirst() {
        dirty = true;
        return super.remove(0);
    }

    @Override
    public Move removeLast() {
        dirty = true;
        return super.remove(super.size() - 1);
    }

    @Override
    public boolean remove(Object o) {
        dirty = true;
        return super.remove(o);
    }

    @Override
    public Move remove(int index) {
        dirty = true;
        return super.remove(index);
    }

    @Override
    public void clear() {
        dirty = true;
        sanArray = null;
        fanArray = null;
        super.clear();
    }

    /**
     * Converts the list of moves into a Short Algebraic Notation (SAN) representation that does not include move
     * numbers, for example {@code "e4 e5 Nf3 Bc5"}.
     *
     * @return the SAN representation of the list of moves without move numbers
     * @throws MoveConversionException in case a conversion error occurs during the process
     * @see MoveList#toSanWithMoveNumbers()
     */
    public String toSan() throws MoveConversionException {
        return toStringWithoutMoveNumbers(toSanArray());
    }

    /**
     * Converts the list of moves into a Short Algebraic Notation (SAN) representation that does include move numbers,
     * for example {@code "1. e4 e5 2. Nf3 Bc5"}.
     *
     * @return the SAN representation of the list of moves with move numbers
     * @throws MoveConversionException in case a conversion error occurs during the process
     * @see MoveList#toSan()
     */
    public String toSanWithMoveNumbers() throws MoveConversionException {
        return toStringWithMoveNumbers(toSanArray());
    }

    /**
     * Converts the list of moves into a Figurine Algebraic Notation (FAN) representation that does not include move
     * numbers, for example {@code "♙e4 ♟e5 ♘f3 ♝c5"}.
     *
     * @return the FAN representation of the list of moves without move numbers
     * @throws MoveConversionException in case a conversion error occurs during the process
     * @see MoveList#toFanWithMoveNumbers()
     */
    public String toFan() throws MoveConversionException {
        return toStringWithoutMoveNumbers(toFanArray());
    }

    /**
     * Converts the list of moves into a Figurine Algebraic Notation (FAN) representation that does include move
     * numbers, for example {@code "1. ♙e4 ♟e5 2. ♘f3 ♝c5"}.
     *
     * @return the FAN representation of the list of moves with move numbers
     * @throws MoveConversionException in case a conversion error occurs during the process
     * @see MoveList#toFan()
     */
    public String toFanWithMoveNumbers() throws MoveConversionException {
        return toStringWithMoveNumbers(toFanArray());
    }

    private String toStringWithoutMoveNumbers(String[] moveArray) throws MoveConversionException {
        StringBuilder sb = new StringBuilder();
        for (String move : moveArray) {
            sb.append(move);
            sb.append(StringUtils.SPACE);
        }
        return sb.toString();
    }

    private String toStringWithMoveNumbers(String[] moveArray) throws MoveConversionException {
        StringBuilder sb = new StringBuilder();
        for (int halfMove = 0; halfMove < moveArray.length; halfMove++) {
            if (halfMove % 2 == 0) {
                sb.append((halfMove / 2) + 1).append(". ");
            }
            sb.append(moveArray[halfMove]).append(StringUtils.SPACE);
        }
        return sb.toString();
    }

    /**
     * Returns an array of strings representing the moves in Short Algebraic Notation (SAN).
     *
     * @return the SAN representations of the list of moves
     * @throws MoveConversionException in case a conversion error occurs during the process
     */
    public String[] toSanArray() throws MoveConversionException {
        if (!dirty && sanArray != null) {
            return sanArray;
        }
        updateSanArray();
        updateFanArray();
        return sanArray;
    }

    /**
     * Returns an array of strings representing the moves in Figurine Algebraic Notation (FAN).
     *
     * @return the FAN representations of the list of moves
     * @throws MoveConversionException in case a conversion error occurs during the process
     */
    public String[] toFanArray() throws MoveConversionException {
        if (!dirty && fanArray != null) {
            return fanArray;
        }
        updateSanArray();
        updateFanArray();
        return fanArray;
    }

    private void updateSanArray() throws MoveConversionException {
        sanArray = new String[this.size()];
        final Board b = getBoard();
        if (!b.getFen().equals(getStartFen())) {
            b.loadFromFen(getStartFen());
        }
        int i = 0;
        for (Move move : this) {
            sanArray[i++] = encodeToSan(b, move);
        }
        dirty = false;
    }

    private void updateFanArray() throws MoveConversionException {
        fanArray = new String[this.size()];
        final Board b = getBoard();
        if (!b.getFen().equals(getStartFen())) {
            b.loadFromFen(getStartFen());
        }
        int i = 0;
        for (Move move : this) {
            fanArray[i++] = encodeToFan(b, move);
        }
        dirty = false;
    }

    /**
     * Returns the Forsyth–Edwards Notation (FEN) string that defines the initial position of the list of moves.
     *
     * @return the FEN representation of the initial position
     */
    public String getStartFen() {
        return startFEN;
    }

    /**
     * Reloads the list with a sequence of moves separated by spaces and provided in input in their algebraic form (e.g.
     * {@code "e2e4"} or {@code "g8f6"}). The base initial position will be left untouched.
     *
     * @param text the string representing the algebraic list of moves
     * @throws MoveConversionException if it is not possible to parse and convert the moves
     */
    public synchronized void loadFromText(String text) throws MoveConversionException {
        final Board b = getBoard();
        if (!b.getFen().equals(getStartFen())) {
            b.loadFromFen(getStartFen());
        }
        try {
            text = StringUtil.normalize(text);
            String[] m = text.split(StringUtils.SPACE);
            int i = 0;
            for (String strMove : m) {
                Move move = b.fromUci(strMove);
                add(i++, move);
                b.doMove(move);
            }
        } catch (Exception e) {
            throw new MoveConversionException("Couldn't parse text to MoveList: " + e.getMessage());
        }

    }

    /**
     * Adds a move defined by its Short Algebraic Notation (SAN) to the list.
     * <p>
     * Same as invoking {@code addSanMove(san, false, true)}.
     *
     * @param san the SAN representation of the move to be added
     * @throws MoveConversionException if it is not possible to parse or validate the move
     * @see MoveList#addSanMove(String, boolean, boolean)
     */
    public void addSanMove(String san) throws MoveConversionException {
        addSanMove(san, false, true);
    }

    /**
     * Adds a move defined by its Short Algebraic Notation (SAN) to the list. It is possible to control whether to
     * replay the moves already present in the list, as well as if to perform a full validation or not. When a full
     * validation is requested, additional checks are performed to assess the validity of the move, such as if the side
     * to move is consistent with the position, if castle moves or promotions are allowed, etc.
     *
     * @param san            the SAN representation of the move to be added
     * @param replay         if {@code true}, existing moves will be played again
     * @param fullValidation if {@code true}, a full validation of the position will be performed
     * @throws MoveConversionException if it is not possible to parse or validate the move
     */
    public void addSanMove(String san, boolean replay, boolean fullValidation) throws MoveConversionException {
        final Board b = getBoard();
        if (replay) {
            if (!b.getFen().equals(getStartFen())) {
                b.loadFromFen(getStartFen());
            }
            for (Move move : this) {
                if (!b.doMove(move, false)) {
                    throw new MoveConversionException("Couldn't parse SAN to MoveList: Illegal move: " +
                            move + " [" + move.toString() + "] on " + b.getFen());
                }
            }
        }
        Move move = decodeSan(b, san, b.getSideToMove());
        if (move == nullMove) {
            return;
        }
        move.setSan(san);
        if (!b.doMove(move, fullValidation)) {
            throw new MoveConversionException("Couldn't parse SAN to MoveList: Illegal move: " +
                    move + " [" + san + "] on " + b.getFen());
        }
        add(this.size(), move);
    }

    /**
     * Reloads the list with a sequence of moves provided in input in their Short Algebraic Notation (SAN) (e.g.
     * {@code "1. e4 e5 2. Nf3 Bc5"}). The base initial position will be left untouched.
     *
     * @param text the SAN representation of the list of moves
     * @throws MoveConversionException if it is not possible to parse and convert the moves
     */
    public void loadFromSan(String text) throws MoveConversionException {
        final Board b = getBoard();
        if (!b.getFen().equals(getStartFen())) {
            b.loadFromFen(getStartFen());
        }
        try {
            text = StringUtil.normalize(text);
            String[] m = text.split(StringUtils.SPACE);
            for (String strMove : m) {
                if (strMove.startsWith("$") || strMove.contains("...")) {
                    continue;
                }

                if (strMove.contains(".")) {
                    strMove = StringUtil.afterSequence(strMove, ".");
                }

                if (!StringUtils.isBlank(strMove)) {
                    addSanMove(strMove);
                }
            }
        } catch (MoveConversionException e1) {
            throw e1;
        } catch (Exception e2) {
            throw new MoveConversionException("Couldn't parse SAN to MoveList: " + e2.getMessage());
        }
    }

    /**
     * Converts a move defined by its Short Algebraic Notation (SAN) to an instance of {@link Move}, using the given
     * board and side as context.
     *
     * @param board the board in which the move is played
     * @param san   the SAN representation of the move
     * @param side  the side executing the move
     * @return the converted move
     * @throws MoveConversionException if it is not possible to convert the move
     */
    // decode SAN to move
    protected Move decodeSan(Board board, String san, Side side) throws MoveConversionException {

        if (san.equalsIgnoreCase("Z0")) {
            return nullMove;
        }
        san = normalizeSan(san);

        String strPromotion = StringUtil.afterSequence(san, "=", 1);
        san = StringUtil.beforeSequence(san, "=");

        char lastChar = san.charAt(san.length() - 1);
        //FIX missing equal sign for pawn promotions
        if (Character.isLetter(lastChar) && Character.toUpperCase(lastChar) != 'O') {
            san = san.substring(0, san.length() - 1);
            strPromotion = String.valueOf(lastChar);
        }

        if (san.equals("O-O")) {
            return board.getContext().getoo(side);
        }

        if (san.equals("O-O-O")) {
            return board.getContext().getooo(side);
        }

        if (san.length() == 3 &&
                Character.isUpperCase(san.charAt(2))) {
            strPromotion = san.substring(2, 3);
            san = san.substring(0, 2);
        }

        Square from;
        Square to;
        try {
            to = Square.valueOf(StringUtil.lastSequence(san.toUpperCase(), 2));
        } catch (Exception e) {
            throw new MoveConversionException("Couldn't parse destination square[" + san + "]: " +
                    san.toUpperCase());
        }
        Piece promotion = Piece.NONE;

        if (!StringUtils.isEmpty(strPromotion)) {
            String promotionSymbol = side.equals(Side.WHITE)
                    ? strPromotion.toUpperCase()
                    : strPromotion.toLowerCase();

            promotion = Piece.fromFenSymbol(promotionSymbol);
        }

        if (san.length() == 2) { // is pawn move
            from = findPawnSourceSquare(board, to, side);
        } else {
            from = findNonPawnSourceSquare(
                    board, san, to, promotion);
        }
        if (from.equals(Square.NONE)) {
            throw new MoveConversionException("Couldn't parse 'from' square " + san + " to setup: " + board.getFen());
        }
        return new Move(from, to, promotion);
    }
    
    private static Square findPawnSourceSquare(
            Board board,
            Square destination,
            Side side) {

        long mask = Bitboard.getBbtable(destination) - 1L;

        long sourceCandidates =
                (side.equals(Side.WHITE) ? mask : ~mask)
                & Bitboard.getFilebb(destination)
                & board.getBitboard(Piece.make(side, PieceType.PAWN));

        int sourceIndex = side.equals(Side.BLACK)
                ? Bitboard.bitScanForward(sourceCandidates)
                : Bitboard.bitScanReverse(sourceCandidates);

        if (sourceIndex >= 0 && sourceIndex <= 63) {
            return Square.squareAt(sourceIndex);
        }

        return Square.NONE;
    }
    
    private static Square findNonPawnSourceSquare(
            Board board,
            String san,
            Square destination,
            Piece promotion)
            throws MoveConversionException {

        String sourceNotation;

        if (san.contains("x")) {
            sourceNotation = StringUtil.beforeSequence(san, "x");
        } else {
            sourceNotation = san.substring(0, san.length() - 2);
        }

        if (sourceNotation == null
                || sourceNotation.isEmpty()
                || sourceNotation.length() > 3) {
            throw new MoveConversionException(
                    "Couldn't parse 'from' square " + san
                            + ": Too many/few characters.");
        }

        PieceType sourcePieceType =
                determineSourcePieceType(sourceNotation);

        if (sourceNotation.length() == 3) {
            return Square.valueOf(
                    sourceNotation.substring(1, 3).toUpperCase());
        }

        String location = extractSourceLocation(sourceNotation);

        if (location.length() == 2) {
            return Square.valueOf(location.toUpperCase());
        }

        return findAmbiguousSourceSquare(
                board,
                destination,
                promotion,
                sourcePieceType,
                location);
    }

    private static PieceType determineSourcePieceType(
            String sourceNotation) {

        if (Character.isUpperCase(sourceNotation.charAt(0))) {
            return PieceType.fromSanSymbol(
                    String.valueOf(sourceNotation.charAt(0)));
        }

        return PieceType.PAWN;
    }

    private static String extractSourceLocation(
            String sourceNotation) {

        if (sourceNotation.length() == 2) {
            if (Character.isUpperCase(sourceNotation.charAt(0))) {
                return sourceNotation.substring(1, 2);
            }

            return sourceNotation;
        }

        if (Character.isLowerCase(sourceNotation.charAt(0))) {
            return sourceNotation;
        }

        return StringUtils.EMPTY;
    }

    private static Square findAmbiguousSourceSquare(
            Board board,
            Square destination,
            Piece promotion,
            PieceType sourcePieceType,
            String location)
            throws MoveConversionException {

        long sourceCandidates = board.squareAttackedByPieceType(
                destination,
                board.getSideToMove(),
                sourcePieceType);

        sourceCandidates = filterCandidatesByLocation(
                sourceCandidates, location);

        if (sourceCandidates == 0L) {
            return Square.NONE;
        }

        if (!Bitboard.hasOnly1Bit(sourceCandidates)) {
            sourceCandidates = findLegalSquares(
                    board,
                    destination,
                    promotion,
                    sourceCandidates);
        }

        int sourceIndex =
                Bitboard.bitScanForward(sourceCandidates);

        if (sourceIndex >= 0 && sourceIndex <= 63) {
            return Square.squareAt(sourceIndex);
        }

        return Square.NONE;
    }

    private static long filterCandidatesByLocation(
            long sourceCandidates,
            String location)
            throws MoveConversionException {

        if (location.isEmpty()) {
            return sourceCandidates;
        }

        if (Character.isDigit(location.charAt(0))) {
            int rankNumber = Integer.parseInt(location);

            if (rankNumber < 1 || rankNumber > 8) {
                throw new MoveConversionException(
                        "Couldn't parse rank: " + location);
            }

            Rank rank = Rank.allRanks[rankNumber - 1];
            return sourceCandidates & Bitboard.getRankbb(rank);
        }

        try {
            File file = File.valueOf(
                    "FILE_" + location.toUpperCase());

            return sourceCandidates & Bitboard.getFilebb(file);

        } catch (Exception exception) {
            throw new MoveConversionException(
                    "Couldn't parse file: " + location);
        }
    }

    /**
     * Returns the Forsyth-Edwards Notation (FEN) representation of the position after the moves of this list, until
     * index {@code atMoveIndex} (excluded), are executed from the base initial position.
     * <p>
     * Same as invoking {@code getFen(atMoveIndex, true)}.
     *
     * @param atMoveIndex the index until which to execute the moves
     * @return the FEN string notation of the position after the wanted number of moves are played from the initial
     * position
     * @see MoveList#getFen(int, boolean)
     */
    public String getFen(int atMoveIndex) {
        return getFen(atMoveIndex, true);
    }

    /**
     * Returns the Forsyth-Edwards Notation (FEN) representation of the position after the moves of this list, until
     * index {@code atMoveIndex} (excluded), are executed from the base initial position. Full and half moves counters
     * are included in the output if the relative flag is enabled.
     *
     * @param atMoveIndex     the index until which to execute the moves
     * @param includeCounters if {@code true}, move counters are included in the resulting string
     * @return the FEN string notation of the position after the wanted number of moves are played from the initial
     * position
     */
    public String getFen(int atMoveIndex, boolean includeCounters) {
        final Board b = getBoard();
        if (!b.getFen().equals(getStartFen())) {
            b.loadFromFen(getStartFen());
        }
        int i = 0;
        for (Move move : this) {
            i++;
            if (!b.doMove(move, false)) {
                throw new IllegalArgumentException("Couldn't parse SAN to MoveList: Illegal move: " +
                        move + " [" + move.toString() + "] on " + b.getFen(includeCounters));
            }
            if (i >= atMoveIndex) {
                return b.getFen(includeCounters);
            }
        }
        return null;
    }

    /**
     * Returns the Forsyth-Edwards Notation (FEN) representation of the position after all the moves of this list are
     * executed starting from the base initial position.
     * <p>
     * Same as invoking {@code getFen(this.size(), true)}.
     *
     * @return the FEN string notation of the position after the moves are played from the initial position
     * @see MoveList#getFen(int, boolean)
     */
    public String getFen() {
        return getFen(this.size());
    }

    /**
     * Returns the parent index of the list of moves.
     *
     * @return the parent index
     */
    public int getParent() {
        return parent;
    }

    /**
     * Sets the parent index of the list of moves.
     *
     * @param parent the parent index to set
     */
    public void setParent(int parent) {
        this.parent = parent;
    }

    /**
     * Returns a string representation of the list.
     *
     * @return a string representation of the list of moves
     */
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        for (Move move : this) {
            b.append(move.toString());
            b.append(StringUtils.SPACE);
        }
        return b.toString().trim();
    }

    /**
     * Returns a hash code value for this move list.
     *
     * @return a hash value for this move list
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + toString().hashCode();
        return result;
    }

    /**
     * Checks if this list is equivalent to another, that is, if the lists contains the same moves in the same order.
     *
     * @param obj the other object reference to compare to this list of moves
     * @return {@code true} if this list and the object reference are equivalent
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MoveList) {
            MoveList l = (MoveList) obj;
            if (l.size() != this.size()) {
                return false;
            }
            for (int i = 0; i < l.size(); i++) {
                if (!l.get(i).equals(this.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private String normalizeSan(String san) {
        return SAN_DECORATION_PATTERN.matcher(san)
                .replaceAll(StringUtils.EMPTY)
                .replace("ep", StringUtils.EMPTY)
                .replace("\\n", StringUtils.SPACE);
    }
}

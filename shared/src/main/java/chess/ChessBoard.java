package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece [][] squares = new ChessPiece[8][8];
    public ChessBoard() {

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(squares, that.squares);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares);
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //Start by adding the pawns. First loop is white, second is black.
        for(int i = 1; i <= 8; i++) {
            ChessPosition pawnLoc = new ChessPosition(2, i);
            ChessPiece wPawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            addPiece(pawnLoc,wPawn);
        }
        for(int j = 1; j <= 8; j++) {
            ChessPosition pawnLoc = new ChessPosition(7, j);
            ChessPiece bPawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            addPiece(pawnLoc,bPawn);
        }
        //I'll use something similar to the movement vectors from ChessPiece's movement system
        //to hopefully make this easier for me.
        ChessPiece.PieceType [] soldiers = {ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KING, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK};
        ChessGame.TeamColor [] color = {ChessGame.TeamColor.WHITE, ChessGame.TeamColor.BLACK};
        /*
        * Idea is as follows:
        * Implement a nested for loop. The first loop only goes twice, and is to change between
        * WHITE and BLACK. The second loop iterates 8 times, once for each piece to put down.
         */
        for(int c = 0; c < 2; c++){ //c stands for color
            int rowPosition = (c*7) + 1; //1 for while, 8 for black
            for(int s = 0; s < 8; s++){ //s stands for soldier
                ChessPosition ourPosition = new ChessPosition(rowPosition, s+1);
                ChessPiece soldier = new ChessPiece(color[c], soldiers[s]);
                addPiece(ourPosition, soldier);
            }
        }
    }

    /**
     * Below are two helper functions I implemented to make checking the boundaries of the chess
     * game easier in case we ever want to expand the board.
     */
    public int getRowBounds() {
        return squares.length;
    }

    public int getColumnBounds() {
        return squares[0].length;
    }
}

package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard currentBoard = new ChessBoard();
    private TeamColor currentTurn;
    public ChessGame() {
        this.currentBoard.resetBoard();
        this.currentTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.currentTurn = team;
        //throw new RuntimeException("Not implemented");
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> movesList = new ArrayList<>();
        ChessPiece sometimesWatching = currentBoard.getPiece(startPosition);
        if (sometimesWatching != null) {
            movesList = sometimesWatching.pieceMoves(currentBoard, startPosition);
        }
        for(int i = 0; i < movesList.size(); i++) {
            ChessBoard copyBoard = new ChessBoard(currentBoard);
            copyBoard.
        }
        return movesList;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        for(int i = 1; i < currentBoard.getRowBounds(); i++) {
            for (int j = 1; j < currentBoard.getColumnBounds(); j++) {
                ChessPosition coords = new ChessPosition(i, j);
                if(currentBoard.getPiece(coords) != null && currentBoard.getPiece(coords).getTeamColor() != teamColor) {
                    ChessPiece watching = currentBoard.getPiece(coords); //Sometimes watching, Mike Wazowski
                    watching.pieceMoves(currentBoard, coords);
                    if(watching.hasCheck) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(!isInCheck(teamColor)) {
            throw new RuntimeException("Not Implemented yet lol");
        }
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        for(int i = 1; i < currentBoard.getRowBounds(); i++) {
            for(int j = 1; j < currentBoard.getColumnBounds(); j++) {
                ChessPosition coords = new ChessPosition(i, j);
                if(board.getPiece(coords) != null) {
                    ChessPiece addPiece = new ChessPiece(board.getPiece(coords).getTeamColor(), board.getPiece(coords).getPieceType());
                    currentBoard.addPiece(coords, addPiece);
                }
            }
        }
        //throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currentBoard;
        //throw new RuntimeException("Not implemented");
    }
}

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
        Collection<ChessMove> validMovesList = new ArrayList<>();
        for(int i = 0; i < movesList.size(); i++) {
            ChessMove ourMove = ((ArrayList<ChessMove>)movesList).get(i);
            ChessBoard newBoard = testMove(currentBoard, ourMove.getStartPosition(), ourMove.getEndPosition());
            if(!testCheck(currentBoard.getPiece(startPosition).getTeamColor(), newBoard)){
                validMovesList.add(ourMove);
            }
        }
        return validMovesList;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition pastPosition = move.getStartPosition();
        ChessPosition presentPosition = move.getEndPosition();
        ChessPiece ourPiece = currentBoard.getPiece(pastPosition);
        Collection<ChessMove> validMoves = validMoves(pastPosition);
        if(validMoves.contains(move) && ourPiece.getTeamColor() == currentTurn) {
            if (move.getPromotionPiece() != null) {
                ourPiece = new ChessPiece(ourPiece.getTeamColor(), move.getPromotionPiece());
            }
            currentBoard.addPiece(presentPosition, ourPiece);
            currentBoard.addPiece(pastPosition, null);
            currentBoard.getPiece(presentPosition).hasMoved = true;
            if(ourPiece.getTeamColor() == TeamColor.WHITE) {
                currentTurn = TeamColor.BLACK;
            }
            else {
                currentTurn = TeamColor.WHITE;
            }
        }
        else {
            throw new InvalidMoveException("Move is Invalid!");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        for(int i = 1; i <= currentBoard.getRowBounds(); i++) {
            for (int j = 1; j <= currentBoard.getColumnBounds(); j++) {
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
        if(!isInCheck(teamColor)) {
            return false;
        }
        for(int i = 1; i <= currentBoard.getRowBounds(); i++){
            for (int j = 1; j <=currentBoard.getColumnBounds(); j++){
                ChessPosition spotlight = new ChessPosition(i, j);
                if(currentBoard.getPiece(spotlight) != null &&  teamColor == currentBoard.getPiece(spotlight).getTeamColor()) {
                    Collection<ChessMove> possibleMoves = validMoves(spotlight);
                    if(!possibleMoves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if(!isInCheck(teamColor)){
            for(int i = 1; i <= currentBoard.getRowBounds(); i++){
                for (int j = 1; j <=currentBoard.getColumnBounds(); j++){
                    ChessPosition spotlight = new ChessPosition(i, j);
                    if(currentBoard.getPiece(spotlight) != null && currentBoard.getPiece(spotlight).getTeamColor() == teamColor) {
                        Collection<ChessMove> possibleMoves = validMoves(spotlight);
                        if(!possibleMoves.isEmpty()) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        for(int i = 1; i <= currentBoard.getRowBounds(); i++) {
            for(int j = 1; j <= currentBoard.getColumnBounds(); j++) {
                ChessPosition coords = new ChessPosition(i, j);
                if(board.getPiece(coords) != null) {
                    ChessPiece addPiece = new ChessPiece(board.getPiece(coords).getTeamColor(), board.getPiece(coords).getPieceType());
                    currentBoard.addPiece(coords, addPiece);
                }
                else {
                    currentBoard.addPiece(coords, null);
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

    /*
    testMove is a helper function designed for validMoves.
    It takes a ChessBoard, the position on that board that the piece we want to move is,
    and the position on the board that we're moving that piece to.
    It returns a ChessBoard that has our piece moved to its new location.
     */
    private ChessBoard testMove(ChessBoard board, ChessPosition pastPos, ChessPosition newPos) {
        ChessBoard newBoard = new ChessBoard(board);
        ChessPiece ourPiece = new ChessPiece(board.getPiece(pastPos).getTeamColor(), board.getPiece(pastPos).getPieceType());
        newBoard.addPiece(newPos, ourPiece);
        newBoard.addPiece(pastPos, null);
        return newBoard;
    }

    public boolean testCheck(TeamColor teamColor, ChessBoard board) {
        for(int i = 1; i <= board.getRowBounds(); i++) {
            for (int j = 1; j <= board.getColumnBounds(); j++) {
                ChessPosition coords = new ChessPosition(i, j);
                if(board.getPiece(coords) != null && board.getPiece(coords).getTeamColor() != teamColor) {
                    ChessPiece watching = board.getPiece(coords); //Sometimes watching, Mike Wazowski
                    watching.pieceMoves(board, coords);
                    if(watching.hasCheck) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;
    private Boolean hasMoved = false;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> movesList = new ArrayList<>();
        if (type == PieceType.BISHOP) {
            return bishopMoves(board, myPosition, movesList);
        }
        if (type == PieceType.ROOK) {
            return rookMoves(board, myPosition, movesList);
        }
        if (type == PieceType.QUEEN) {
            return queenMoves(board, myPosition, movesList);
        }
        if (type == PieceType.KING) {
            return kingMoves(board, myPosition, movesList);
        }
        if (type == PieceType.KNIGHT) {
            return knightMoves(board, myPosition, movesList);
        }
        if (type == PieceType.PAWN) {
            return pawnMoves(board, myPosition, movesList);
        }
        //throw new RuntimeException("Not implemented");
        return new ArrayList<>();
    }


    /*
     * Below are the functions handling the logic of the movement of pieces. Keep in mind,
     * the first function I worked on for movement was the Bishop, so all other functions are derived
     * from Bishop's moveset. This may cause things to be less than optimized, or may be a source of
     * glitches.
     */


    private Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> movesList) {
        /*I want to implement a set of values that can be used to track all possible directions
         * That this piece can move, so I am gonna make two arrays. One (directionRow) tracks the
         * vertical direction [so the change made to the piece's Row] and the other (directionCol)
         * tracks the horizontal direction. Bishops can move diagonally, so the possible values would
         * be (1,1) for up and right, (-1,-1) for down and left, (1,-1) for up and left, and (-1,1) for
         * down and right.
         */
        int[] directionRow = {1, -1, -1, 1};
        int[] directionCol = {1, 1, -1, -1};

        for (int i = 0; i < 4; i++) { //There are 4 ordered pairs of directions to move in
            int newRow = myPosition.getRow();
            int newCol = myPosition.getColumn();

            while (true) {
                newRow += directionRow[i];
                newCol += directionCol[i];
                if (newRow <= 0 || newRow > board.getRowBounds() || newCol <= 0 || newCol > board.getColumnBounds()) {
                    break;
                }
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                if (board.getPiece(newPos) != null) {
                    if (board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        movesList.add(new ChessMove(myPosition, newPos, null));
                    }
                    break;
                }
                movesList.add(new ChessMove(myPosition, newPos, null)); //null promotion since this is not a pawn
            }
        }
        return movesList;
    }

    private Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> movesList) {
        int[] directionRow = {1, -1, 0, 0};
        int[] directionCol = {0, 0, 1, -1};

        for (int i = 0; i < 4; i++) { //There are 4 ordered pairs of directions to move in
            int newRow = myPosition.getRow();
            int newCol = myPosition.getColumn();

            while (true) {
                newRow += directionRow[i];
                newCol += directionCol[i];
                if (newRow <= 0 || newRow > board.getRowBounds() || newCol <= 0 || newCol > board.getColumnBounds()) {
                    break;
                }
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                if (board.getPiece(newPos) != null) {
                    if (board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        movesList.add(new ChessMove(myPosition, newPos, null));
                    }
                    break;
                }
                movesList.add(new ChessMove(myPosition, newPos, null)); //null promotion since this is not a pawn
            }
        }
        return movesList;
    }

    private Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> movesList) {
        int[] directionRow = {1, -1, 1, -1, 1, -1, 0, 0};
        int[] directionCol = {1, -1, -1, 1, 0, 0, 1, -1};

        for (int i = 0; i < 8; i++) { //There are 8 ordered pairs of directions to move in
            int newRow = myPosition.getRow();
            int newCol = myPosition.getColumn();

            while (true) {
                newRow += directionRow[i];
                newCol += directionCol[i];
                if (newRow <= 0 || newRow > board.getRowBounds() || newCol <= 0 || newCol > board.getColumnBounds()) {
                    break;
                }
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                if (board.getPiece(newPos) != null) {
                    if (board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        movesList.add(new ChessMove(myPosition, newPos, null));
                    }
                    break;
                }
                movesList.add(new ChessMove(myPosition, newPos, null)); //null promotion since this is not a pawn
            }
        }
        return movesList;
    }

    private Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> movesList) {
        int[] directionRow = {1, -1, 1, -1, 1, -1, 0, 0};
        int[] directionCol = {1, -1, -1, 1, 0, 0, 1, -1};

        for (int i = 0; i < 8; i++) { //There are 8 ordered pairs of directions to move in
            int newRow = myPosition.getRow();
            int newCol = myPosition.getColumn();
            boolean steppy = true; //steppy is set to become false at the end of the while loop every time to ensure we only step once in any direction
            while (steppy) {
                newRow += directionRow[i];
                newCol += directionCol[i];
                if (newRow <= 0 || newRow > board.getRowBounds() || newCol <= 0 || newCol > board.getColumnBounds()) {
                    break;
                }
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                if (board.getPiece(newPos) != null) {
                    if (board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        movesList.add(new ChessMove(myPosition, newPos, null));
                    }
                    break;
                }
                movesList.add(new ChessMove(myPosition, newPos, null)); //null promotion since this is not a pawn
                steppy = false;
            }
        }
        return movesList;
    }

    private Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> movesList) {
        int[] directionRow = {2, -2, 2, -2, 1, -1, 1, -1};
        int[] directionCol = {1, 1, -1, -1, 2, 2, -2, -2};

        for (int i = 0; i < 8; i++) { //There are 8 ordered pairs of directions to move in
            int newRow = myPosition.getRow();
            int newCol = myPosition.getColumn();
            boolean hop = true;
            while (hop) { //hop is set to become false. Much like the King, the knight can't keep moving in its directions.
                newRow += directionRow[i];
                newCol += directionCol[i];
                if (newRow <= 0 || newRow > board.getRowBounds() || newCol <= 0 || newCol > board.getColumnBounds()) {
                    break;
                }
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                if (board.getPiece(newPos) != null) {
                    if (board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                        movesList.add(new ChessMove(myPosition, newPos, null));
                    }
                    break;
                }
                hop = false;
                movesList.add(new ChessMove(myPosition, newPos, null)); //null promotion since this is not a pawn
            }
        }
        return movesList;
    }
    private Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> movesList) {
        //teamMod is negative (moves down) if the team color is black, positive (moves up) if white.
        int teamMod = 0;
        int newRow = myPosition.getRow();
        int newCol = myPosition.getColumn();
        boolean promotionIncoming = false;
        boolean hop = false;
        PieceType [] promotions = {PieceType.BISHOP, PieceType.ROOK, PieceType.KNIGHT, PieceType.QUEEN};
        if(board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
            teamMod = -1;
            if(newRow + teamMod == 1) {
                promotionIncoming = true;
            }
            if(newRow != 7) {
                hasMoved = true;
            }
        }
        else {
            teamMod = 1;
            if(newRow + teamMod == 8) {
                promotionIncoming = true;
            }
            if(newRow != 2) {
                hasMoved = true;
            }
        }
        while(!hop) {
            newRow += teamMod;
            if (newRow <= 0 || newRow > board.getRowBounds()) {
                break;
            }
            ChessPosition oneHop = new ChessPosition(newRow, newCol);
            if(board.getPiece(oneHop) == null) {
                if(promotionIncoming) {
                    for(int i = 0; i < 4; i++) {
                        movesList.add(new ChessMove(myPosition, oneHop, promotions[i]));
                    }
                }
                else {
                    movesList.add(new ChessMove(myPosition, oneHop, null));
                }
            }
            int newerCol = newCol - 1;
            if (newerCol <= 0 || newerCol > board.getRowBounds()) {
                break;
            }
            ChessPosition takeLeft = new ChessPosition(newRow, newerCol);
            if(board.getPiece(takeLeft) != null && board.getPiece(takeLeft).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                if(promotionIncoming) {
                    for(int i = 0; i < 4; i++) {
                        movesList.add(new ChessMove(myPosition, takeLeft, promotions[i]));
                    }
                }
                else {
                    movesList.add(new ChessMove(myPosition, takeLeft, null));
                }
            }
            int newestCol = newCol + 1;
            if (newestCol <= 0 || newestCol > board.getRowBounds()) {
                break;
            }
            ChessPosition takeRight = new ChessPosition(newRow, newestCol);
            if(board.getPiece(takeRight) != null && board.getPiece(takeRight).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                if(promotionIncoming) {
                    for(int i = 0; i < 4; i++) {
                        movesList.add(new ChessMove(myPosition, takeRight, promotions[i]));
                    }
                }
                else {
                    movesList.add(new ChessMove(myPosition, takeRight, null));
                }
            }
            if(hasMoved || board.getPiece(oneHop) != null) { //Checks for if we've moved OR if our front is blocked
               hop = true;
            }
            hasMoved = true;
        }
        return movesList;
//        while(!hop) {
//            ChessPosition leftDiagonal = new ChessPosition(newRow + teamMod, newCol - 1);
//            ChessPosition rightDiagonal = new ChessPosition(newRow + teamMod, newCol + 1);
//            if (board.getPiece(leftDiagonal) != null && board.getPiece(leftDiagonal).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
//                if (promotionIncoming) {
//                    for (int i = 0; i < 4; i++) {
//                        movesList.add(new ChessMove(myPosition, leftDiagonal, promotions[i]));
//                    }
//                } else {
//                    movesList.add(new ChessMove(myPosition, leftDiagonal, null));
//                }
//            }
//            if (board.getPiece(rightDiagonal) != null && board.getPiece(rightDiagonal).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
//                if (promotionIncoming) {
//                    for (int i = 0; i < 4; i++) {
//                        movesList.add(new ChessMove(myPosition, rightDiagonal, promotions[i]));
//                    }
//                } else {
//                    movesList.add(new ChessMove(myPosition, rightDiagonal, null));
//                }
//            }
//            ChessPosition newPos = new ChessPosition(newRow + teamMod, newCol);
//            if (board.getPiece(newPos) != null) {
//                return movesList;
//            } else {
//                if (promotionIncoming) {
//                    for (int i = 0; i < 4; i++) {
//                        movesList.add(new ChessMove(myPosition, newPos, promotions[i]));
//                    }
//                } else {
//                    movesList.add(new ChessMove(myPosition, newPos, null));
//                }
//            }
//            ChessPosition newerPos = new ChessPosition(newRow + (2 * teamMod), newCol);
//            if (board.getPiece(newerPos) != null) {
//                return movesList;
//            } else {
//                if (!hasMoved) {
//                    movesList.add(new ChessMove(myPosition, newPos, null));
//                }
//            }
//            hop = true;
//        }
    }
}
    //TODO: implement pawns
    /*
     * Pawns' movement is dependent on color, whether or not it has moved yet, and whether or not
     * a piece is diagonal to it. It'll definitely have more complex logic behind it. Also,
     * I'll likely need to add a boolean somewhere to check if a piece has moved before. That
     * will help both with castling AND with pawn's unique double movement. Could also theoretically
     * add a bool specifically for pawns' En Passant.
     * And, of course, will need to implement promotion!
     * Good luck tomorrow me, this will be a challenge!
     */


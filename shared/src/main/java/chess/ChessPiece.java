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
        if(type == PieceType.BISHOP) {
            /*I want to implement a set of values that can be used to track all possible directions
            * That this piece can move, so I am gonna make two arrays. One (directionRow) tracks the
            * vertical direction [so the change made to the piece's Row] and the other (directionCol)
            * tracks the horizontal direction. Bishops can move diagonally, so the possible values would
            * be (1,1) for up and right, (-1,-1) for down and left, (1,-1) for up and left, and (-1,1) for
            * down and right.
             */
            int[] directionRow = {1,-1,-1,1};
            int[] directionCol = {1,1,-1,-1};

            for (int i = 0; i < 4; i++){ //There are 4 ordered pairs of directions to move in
                int newRow = myPosition.getRow();
                int newCol = myPosition.getColumn();

                while(true){
                    newRow += directionRow[i];
                    newCol += directionCol[i];
                    if(newRow <= 0 || newRow > board.getRowBounds() || newCol <= 0 || newCol > board.getColumnBounds()){
                        break;
                    }
                    //TODO: Probably need to implement something in the case of taking pieces as well
                    System.out.printf("(" + newRow + "," + newCol + ")");
                    ChessPosition newPos = new ChessPosition(newRow, newCol);
                    if(board.getPiece(newPos) != null){
                        if(board.getPiece(newPos).getTeamColor() != board.getPiece(myPosition).getTeamColor()){
                            movesList.add(new ChessMove(myPosition, newPos, null));
                        }
                        break;
                    }
                    movesList.add(new ChessMove(myPosition, newPos, null)); //null promotion since this is not a pawn
                }
            }
            return movesList;
        }
        return new ArrayList<>();
        //throw new RuntimeException("Not implemented");
    }
}

import chess.*;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import server.Server;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, DataAccessException {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        Server serverTest = new Server();
        serverTest.run(3306);
    }
}
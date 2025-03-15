package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MySQLGameDataAccess extends GameDataAccess{

    public MySQLGameDataAccess() throws DataAccessException {
        try {
            connect();
            createTables();
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public String clearAll() throws DataAccessException {
        String clearTableSQL = "TRUNCATE TABLE Games";
        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement deleteUsersStatement = conn.prepareStatement(clearTableSQL)) {
            deleteUsersStatement.executeUpdate();
            System.out.println("Games Table deleted successfully!");
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return "";
    }

    @Override
    public Integer getGameIDByName(String gameName) throws DataAccessException{
        String findGameIDSQL = "SELECT gameID FROM Games WHERE gameName = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement createGameStatement = conn.prepareStatement(findGameIDSQL)) {
            createGameStatement.setString(1, gameName);
            ResultSet rs = createGameStatement.executeQuery();
            if(rs.next()) {
                return rs.getInt("gameID");
            } else {
                System.out.println("GameID not found");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error during getGameIDByName" + e.getMessage());
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public Map<Integer, GameData> listGames() throws DataAccessException{
        HashMap<Integer, GameData> games = new HashMap<>();
        String getGamesSQL = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM Games";
        Gson gson = new Gson();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(getGamesSQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int gameID = rs.getInt("gameID");
                String whiteUsername = rs.getString("whiteUsername");
                String blackUsername = rs.getString("blackUsername");
                String gameName = rs.getString("gameName");
                String jsonGame = rs.getString("game");

                ChessGame chessGame = gson.fromJson(jsonGame, ChessGame.class);

                GameData gameData = new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
                games.put(gameID, gameData);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving games: " + e.getMessage());
            throw new DataAccessException(e.getMessage());
        }

        return games;
    }

    @Override
    public Integer generateGameID() throws DataAccessException{
        String getNextIDSQL = "SELECT MAX(gameID) FROM Games";
        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement getNextIDStatement = conn.prepareStatement(getNextIDSQL)) {
            ResultSet rs = getNextIDStatement.executeQuery();
            if(rs.next()) {
                int newID = rs.getInt(1);
                return newID + 1;
            } else {
                return 1; //if there is nothing in the database yet.
            }
        } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void createGame(Integer newGameID, GameData newGame) throws DataAccessException{

        String insertGameSQL = "INSERT INTO Games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement createGameStatement = conn.prepareStatement(insertGameSQL)) {
            createGameStatement.setString(1, null);
            createGameStatement.setString(2, null);
            createGameStatement.setString(3, newGame.gameName());

            var json = new Gson().toJson(newGame.game());
            createGameStatement.setString(4, json);
            
            int rowsAffected = createGameStatement.executeUpdate();
            if(rowsAffected > 0) {
                System.out.println("Game successfully created!");
            } else {
                System.out.println("Game not created :(");
            }
        } catch (SQLException e) {
            System.out.println("Error during createGame");
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public GameData getGame(Integer gameID) throws DataAccessException {
        String findGameSQL = "SELECT * FROM Games WHERE gameID = ?";
        Gson gson = new Gson();
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement findGameStatement = conn.prepareStatement(findGameSQL)) {

            findGameStatement.setInt(1, gameID);
            ResultSet rs = findGameStatement.executeQuery();
            if (rs.next()) {
                String whiteUsername = rs.getString("whiteUsername");
                String blackUsername = rs.getString("blackUsername");
                String gameName = rs.getString("gameName");
                String jsonGame = rs.getString("game");

                ChessGame chessGame = gson.fromJson(jsonGame, ChessGame.class);

                return new GameData(gameID, whiteUsername, blackUsername, gameName, chessGame);
            }
        } catch (SQLException e) {
            System.out.println("SQLException in getGame: " + e.getMessage());
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    @Override
    public void setPlayerTeam(Integer gameID, String username, String teamColor) throws DataAccessException{
        String setPlayerTeamSQL;
        if(Objects.equals(teamColor, "WHITE")) {
            setPlayerTeamSQL = "UPDATE Games SET whiteUsername = ? WHERE gameID = ? AND whiteUsername IS NULL";
        } else if(Objects.equals(teamColor, "BLACK")) {
            setPlayerTeamSQL = "UPDATE Games SET blackUsername = ? WHERE gameID = ? AND blackUsername IS NULL";
        } else{
            throw new WhomstException("Unknown team");
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement setPlayerTeamStatement = conn.prepareStatement(setPlayerTeamSQL)) {
            setPlayerTeamStatement.setString(1, username);
            setPlayerTeamStatement.setInt(2, gameID);
            System.out.println(setPlayerTeamStatement);
            int rowsAffected = setPlayerTeamStatement.executeUpdate();
            if(rowsAffected > 0) {
                System.out.println("Team successfully set!");
            } else {
                System.out.println("Team setting unsuccessful");
                throw new OverlapException("User attempted to join non-null team");
            }

        } catch (SQLException e) {
            System.out.println("Error during setPlayerTeam" + e.getMessage());
            throw new DataAccessException(e.getMessage());
        }
    }

    private void connect() throws SQLException, DataAccessException {
        DatabaseManager.createDatabase();
        try {
            DatabaseManager.getConnection();
            System.out.println("Games connected to the database successfully!");
        } catch (DataAccessException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            var createGamesTable = """
            CREATE TABLE IF NOT EXISTS Games (
                gameID INT NOT NULL AUTO_INCREMENT,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName VARCHAR(255) NOT NULL UNIQUE,
                game longtext NOT NULL,
                PRIMARY KEY (gameID)
            )""";


            try (var createTableStatement = conn.prepareStatement(createGamesTable)) {
                createTableStatement.executeUpdate();
                System.out.println("Games table created successfully!");
            }
        } catch (DataAccessException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }
}

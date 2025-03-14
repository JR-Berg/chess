package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MySQLGameDataAccess extends GameDataAccess{

    public MySQLGameDataAccess() throws SQLException, DataAccessException {
        connect();
        createTables();
    }

    @Override
    public String clearAll() {
        String clearTableSQL = "TRUNCATE TABLE Games";
        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement deleteUsersStatement = conn.prepareStatement(clearTableSQL)) {
            deleteUsersStatement.executeUpdate();
            System.out.println("Games Table deleted successfully!");
        } catch (SQLException e) {
            System.out.println("Error during clearing Games Table.");
            throw new NonSuccessException("Error during clearing Games Table.");
        } catch (DataAccessException e) {
            System.out.println("DataAccessError in clearAll");
            throw new RuntimeException(e);
        }
        return "";
    }

    @Override
    public Integer getGameIDByName(String gameName) {
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
            throw new NonSuccessException("Error during getGameIDByName");
        } catch (DataAccessException e) {
            System.out.println("DataAccessError in getGameIDByName");
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<Integer, GameData> listGames() {
        HashMap<Integer, GameData> games = new HashMap<>();
        String getGamesSQL = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";
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
        } catch(DataAccessException e) {
            System.out.println("DataAccessException in listGames");
            throw new NonSuccessException("DataAccessException in listGames");
        }

        return games;
    }

    @Override
    public Integer generateGameID() {
        return 0;
        //This function is unnecessary for my SQL implementation, but needs to
        //Remain here so I can swap from Memory Data to SQL seamlessly.
    }

    @Override
    public void createGame(Integer newGameID, GameData newGame) {

        String insertGameSQL = "INSERT INTO Games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement createGameStatement = conn.prepareStatement(insertGameSQL)) {
            createGameStatement.setString(1, null);
            createGameStatement.setString(2, null);
            createGameStatement.setString(3, newGame.gameName());

            var json = new Gson().toJson(newGame);
            createGameStatement.setString(4, json);
            
            int rowsAffected = createGameStatement.executeUpdate();
            if(rowsAffected > 0) {
                System.out.println("Game successfully created!");
            } else {
                System.out.println("Game not created :(");
            }
        } catch (SQLException e) {
            System.out.println("Error during createGame" + e.getMessage());
            throw new NonSuccessException("Error during createGame");
        } catch (DataAccessException e) {
            System.out.println("DataAccessError in createGame");
            throw new RuntimeException(e);
        }
    }

    @Override
    public GameData getGame(Integer gameID) {
        return null;
    }

    @Override
    public void setPlayerTeam(Integer gameID, String username, String teamColor) {

    }

    public void connect() throws SQLException, DataAccessException {
        DatabaseManager.createDatabase();
        try {
            // Establish the connection and return it
            Connection conn = DatabaseManager.getConnection();
            System.out.println("Connected to the database successfully!");
        } catch (DataAccessException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }
    }

    public void createTables() throws SQLException {
        try (var conn = DatabaseManager.getConnection()) {
            var createDbStatement = conn.prepareStatement("CREATE DATABASE IF NOT EXISTS chess");
            createDbStatement.executeUpdate();

            conn.setCatalog("chess");

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

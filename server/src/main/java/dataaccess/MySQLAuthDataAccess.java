package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySQLAuthDataAccess extends AuthDataAccess {
    public MySQLAuthDataAccess() throws SQLException, DataAccessException {
        connect();
        createTables();
    }

    @Override
    public AuthData getAuth(String authToken) {
        String checkAuthSQL = "SELECT * FROM Auth WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement checkAuthStatement = conn.prepareStatement(checkAuthSQL)) {
            checkAuthStatement.setString(1, authToken);
            ResultSet rs = checkAuthStatement.executeQuery();
            if (rs.next()) {
                String username = rs.getString("username");
                return new AuthData(authToken, username);
            }
        } catch (SQLException e) {
            System.out.println("SQL error: " + e.getMessage());
        } catch (DataAccessException e) {
            System.out.println("DataAccessException: " + e.getMessage());
        }
        System.out.println("AuthToken does not exist.");
        return null;
    }

    @Override
    public AuthData createAuth(String username) {
        String authToken = UUID.randomUUID().toString();
        String insertNewAuthSQL = "INSERT INTO Auth (authToken, username) VALUES (?, ?)" +
                "ON DUPLICATE KEY UPDATE authToken = ?";
        AuthData authData = new AuthData(authToken, username);
        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement createAuthStatement = conn.prepareStatement(insertNewAuthSQL)) {
            createAuthStatement.setString(1, authToken);
            createAuthStatement.setString(2, username);
            createAuthStatement.setString(3, authToken);

            int rowsAffected = createAuthStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("AuthToken created successfully!");
                return authData;
            } else {
                System.out.println("Error: AuthToken creation failed.");
                throw new NonSuccessException("AuthToken creation failed.");
            }

        } catch (SQLException e) {
            System.out.println("Error during AuthToken creation");
            throw new NonSuccessException("Error during AuthToken creation.");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteAuth(String authToken) {
        String deleteAuthSQL = "DELETE FROM Auth WHERE authToken = ?";
        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement deleteAuthStatement = conn.prepareStatement(deleteAuthSQL)) {
            deleteAuthStatement.setString(1, authToken);

            int rowsAffected = deleteAuthStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("AuthToken deleted successfully!");
            } else {
                System.out.println("Error: AuthToken deletion failed.");
                throw new NonSuccessException("AuthToken deletion failed.");
            }

        } catch (SQLException e) {
            System.out.println("Error during AuthToken deletion");
            throw new NonSuccessException("Error during AuthToken deletion.");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String clearAll() {
        String clearTableSQL = "TRUNCATE TABLE Auth";
        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement deleteAuthStatement = conn.prepareStatement(clearTableSQL)) {
            deleteAuthStatement.executeUpdate();
            System.out.println("Auth Table deleted successfully!");
        } catch (SQLException e) {
            System.out.println("Error during clearing AuthTable.");
            throw new NonSuccessException("Error during clearing AuthTable.");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        return "";
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

            var createAuthTable = """
            CREATE TABLE IF NOT EXISTS Auth (
                authToken VARCHAR(255) NOT NULL,
                username VARCHAR(255) NOT NULL UNIQUE,
                PRIMARY KEY(username)
            )""";


            try (var createTableStatement = conn.prepareStatement(createAuthTable)) {
                createTableStatement.executeUpdate();
                System.out.println("Auth Table created successfully!");
            }
        } catch (DataAccessException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }
}

package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLUserDataAccess extends UserDataAccess{
    public MySQLUserDataAccess() throws SQLException, DataAccessException {
        connect();
        createTables();
    }
    @Override
    public UserData createUser(String username, String password, String email) {
        if (isUsernameTaken(username)) {
            System.out.println("Username is already taken.");
            throw new NonSuccessException("Username is already taken.");
        }
        UserData userData = new UserData(username, password, email);
        // Insert the new user into the database
        String insertUserSQL = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertUserSQL)) {

            // Set the parameters for the prepared statement
            pstmt.setString(1, username);
            pstmt.setString(2, password);  // You might want to hash the password before storing it
            pstmt.setString(3, email);

            // Execute the insertion
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User registered successfully!");
                return userData;
            } else {
                System.out.println("Error: User registration failed.");
                throw new NonSuccessException("User registration failed.");
            }

        } catch (SQLException e) {
            System.out.println("Error during registration: " + e.getMessage());
            throw new NonSuccessException("Error during registration");
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public String clearAll() {
        return "";
    }

    @Override
    public Boolean checkPassword(String username, String password) {
        return null;
    }
    private boolean isUsernameTaken(String username) {
        String checkUsernameSQL = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkUsernameSQL)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                return true;  // Username is taken
            }

        } catch (SQLException e) {
            System.out.println("Error checking username availability: " + e.getMessage());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        return false;  // Username is not taken
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

            var createUsersTable = """
            CREATE TABLE IF NOT EXISTS Users (
                username VARCHAR(255) NOT NULL UNIQUE,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL UNIQUE
            )""";


            try (var createTableStatement = conn.prepareStatement(createUsersTable)) {
                createTableStatement.executeUpdate();
                System.out.println("Table created successfully!");
            }
        } catch (DataAccessException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }
}

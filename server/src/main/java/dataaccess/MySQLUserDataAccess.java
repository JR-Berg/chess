package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class MySQLUserDataAccess extends UserDataAccess{
    public MySQLUserDataAccess() throws SQLException, DataAccessException {
        connect();
        createTables();
    }

    @Override
    public UserData createUser(String username, String password, String email) {
        if (checkUsername(username) != null) {
            System.out.println("Username is already taken.");
            throw new NonSuccessException("Username is already taken.");
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println(hashedPassword);
        UserData userData = new UserData(username, hashedPassword, email);
        // Insert the new user into the database
        String insertUserSQL = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement createUserStatement = conn.prepareStatement(insertUserSQL)) {

            // Set the parameters for the prepared statement
            createUserStatement.setString(1, username);
            createUserStatement.setString(2, hashedPassword);
            createUserStatement.setString(3, email);

            // Execute the insertion
            int rowsAffected = createUserStatement.executeUpdate();
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
        return checkUsername(username);
    }

    @Override
    public String clearAll() {

        return "";
    }

    @Override
    public Boolean checkPassword(String username, String providedClearTextPassword) {
        UserData userData = checkUsername(username);
        if(userData == null) {
            return false;
        }
        String hashedPassword = userData.password();
        System.out.println("Logged in successfully!");
        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    private UserData checkUsername(String username) {
        String checkUsernameSQL = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement checkUsernameStatement = conn.prepareStatement(checkUsernameSQL)) {

            checkUsernameStatement.setString(1, username);
            ResultSet rs = checkUsernameStatement.executeQuery();
            if (rs.next()) {
                String password = rs.getString("password");
                String email = rs.getString("email");
                return new UserData(username, password, email);  // Username exists
            }

        } catch (SQLException e) {
            System.out.println("Error checking username availability: " + e.getMessage());
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        return null;  // Username does not exist
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
                email VARCHAR(255) NOT NULL,
                PRIMARY KEY (username)
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

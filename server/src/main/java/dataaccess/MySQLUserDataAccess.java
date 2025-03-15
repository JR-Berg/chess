package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLUserDataAccess extends UserDataAccess{
    public MySQLUserDataAccess() throws DataAccessException {
        try {
            connect();
            createTables();
        } catch(SQLException | DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public UserData createUser(String username, String password, String email) throws DataAccessException{
        if (checkUsername(username) != null) {
            System.out.println("Username is already taken.");
            throw new NonSuccessException("Username is already taken.");
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        System.out.println(hashedPassword);
        UserData userData = new UserData(username, hashedPassword, email);
        // Insert the new user into the database
        String insertUserSQL = "INSERT INTO Users (username, password, email) VALUES (?, ?, ?)";

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
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        return checkUsername(username);
    }

    @Override
    public String clearAll() throws DataAccessException{
        String clearTableSQL = "TRUNCATE TABLE Users";
        try(Connection conn = DatabaseManager.getConnection();
            PreparedStatement deleteUsersStatement = conn.prepareStatement(clearTableSQL)) {
            deleteUsersStatement.executeUpdate();
            System.out.println("Users Table deleted successfully!");
        } catch (SQLException e) {
            System.out.println("Error during clearing Users Table.");
            throw new DataAccessException(e.getMessage());
        }
        return "";
    }

    @Override
    public Boolean checkPassword(String username, String providedClearTextPassword) throws DataAccessException{
        UserData userData = checkUsername(username);
        if(userData == null) {
            return false;
        }
        String hashedPassword = userData.password();
        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    private UserData checkUsername(String username) throws DataAccessException{
        String checkUsernameSQL = "SELECT * FROM Users WHERE username = ?";

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
            throw new DataAccessException(e.getMessage());
        }

        return null;  // Username does not exist
    }

    private void connect() throws SQLException, DataAccessException {
        DatabaseManager.createDatabase();
        try {
            DatabaseManager.getConnection();
            System.out.println("Users connected to the database successfully!");
        } catch (DataAccessException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }
    }

    private void createTables() throws SQLException {
        try (var conn = DatabaseManager.getConnection()) {
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

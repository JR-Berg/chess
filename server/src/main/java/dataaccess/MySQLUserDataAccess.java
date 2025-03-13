package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLUserDataAccess extends UserDataAccess{
    @Override
    public UserData createUser(String username, String password, String email) {
        return null;
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

    public Connection connect() throws SQLException, DataAccessException {
        DatabaseManager.createDatabase();
        try {
            // Establish the connection and return it
            Connection conn = DatabaseManager.getConnection();
            System.out.println("Connected to the database successfully!");
            return conn;
        } catch (DataAccessException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
            return null;
        }
    }
}

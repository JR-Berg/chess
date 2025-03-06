package dataaccess;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import dataaccess.NonSuccessException;
import dataaccess.UserDataAccess;
import model.AuthData;
import model.UserData;

public class MemoryUserDataAccess extends UserDataAccess {
    // In-memory storage (username -> UserData)
    private final Map<String, UserData> usersDB = new HashMap<>();

    // Method to check if a username already exists
    public boolean usernameExists(String username) {
        return usersDB.containsKey(username);
    }

    // Method to add a new user
    public UserData createUser(String username, String password, String email) {
        if (usernameExists(username)) {
            throw new NonSuccessException("User already exists!"); //TODO: Better error
        }
        String authToken = createAuth();
        UserData user = new UserData(username, password, email);
        usersDB.put(username, user);
        return getUser(username);
    }
    public String createAuth() {
        return UUID.randomUUID().toString();
    }
    // Method to retrieve a user by username
    public UserData getUser(String username) {
        return usersDB.get(username);
    }
}

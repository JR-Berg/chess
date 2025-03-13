package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import java.sql.Connection;
import java.sql.SQLException;

public class UserServices {
    private static Boolean isDatabaseTested = false;
    private final MySQLUserDataAccess userDataAccess;
    private final AuthDataAccess authDataAccess;
    private final GameDataAccess gameDataAccess;
    public UserServices(MySQLUserDataAccess userDataAccess, AuthDataAccess authDataAccess, GameDataAccess gameDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;

        if (!isDatabaseTested) {
            try {
                testDatabaseConnection();
                isDatabaseTested = true;
            } catch (SQLException | DataAccessException e) {
                System.out.println("Database connection failed during initialization: " + e.getMessage());
            }
        }

    }

    public RegisterResult register(RegisterRequest registerRequest) {
        if(registerRequest.username() == null || registerRequest.email() == null || registerRequest.password() == null) {
            throw new BadDataException("Invalid input");
        }
        UserData user = userDataAccess.getUser(registerRequest.username());
        if(user != null){
            throw new NonSuccessException("Username Taken!");
        }
        user = userDataAccess.createUser(registerRequest.username(), registerRequest.password(), registerRequest.email());
        AuthData auth = authDataAccess.createAuth(user.username());
        return new RegisterResult(user.username(), auth.authToken());
    }

    public String clearAll() {
        userDataAccess.clearAll();
        authDataAccess.clearAll();
        gameDataAccess.clearAll();
        return "";
    }

    public LoginResult login(LoginRequest loginRequest) {
        if(loginRequest.username() == null || loginRequest.password() == null) {
            throw new BadDataException("Not enough data!");
        }
        UserData user = userDataAccess.getUser(loginRequest.username());
        if(user == null) {
            throw new WhomstException("I don't know this user");
        }
        Boolean correctPassword = userDataAccess.checkPassword(loginRequest.username(), loginRequest.password());
        if(correctPassword){
            AuthData auth = authDataAccess.createAuth(user.username());
            return new LoginResult(user.username(), auth.authToken());
        }
        else{
            throw new NonSuccessException("Incorrect password!");
        }
    }

    public void logout(LogoutRequest logoutRequest) {
        if(logoutRequest.authToken() == null) {
            throw new BadDataException("No AuthToken!");
        }
        AuthData auth = checkAuth(logoutRequest.authToken());
        authDataAccess.deleteAuth(auth.authToken());
    }

    private AuthData checkAuth(String authToken){
        AuthData auth = authDataAccess.getAuth(authToken);
        if(auth == null) {
            throw new BadAuthException("AuthData invalid!");
        }
        return auth;
    }
    public void testDatabaseConnection() throws SQLException, DataAccessException {
        Connection conn = userDataAccess.connect();
        if (conn != null) {
            System.out.println("Successfully connected to the database!");
        } else {
            System.out.println("Failed to connect to the database.");
        }
    }


}

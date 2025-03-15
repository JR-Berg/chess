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
    private final UserDataAccess userDataAccess;
    private final AuthDataAccess authDataAccess;
    private final GameDataAccess gameDataAccess;
    public UserServices(UserDataAccess userDataAccess, AuthDataAccess authDataAccess, GameDataAccess gameDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
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

    public String clearAll() throws DataAccessException{
        userDataAccess.clearAll();
        authDataAccess.clearAll();
        gameDataAccess.clearAll();
        return "";
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException{
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

    public void logout(LogoutRequest logoutRequest) throws DataAccessException{
        if(logoutRequest.authToken() == null) {
            throw new BadDataException("No AuthToken!");
        }
        AuthData auth = checkAuth(logoutRequest.authToken());
        authDataAccess.deleteAuth(auth.authToken());
    }

    private AuthData checkAuth(String authToken) throws DataAccessException{
        AuthData auth = authDataAccess.getAuth(authToken);
        if(auth == null) {
            throw new BadAuthException("AuthData invalid!");
        }
        return auth;
    }


}

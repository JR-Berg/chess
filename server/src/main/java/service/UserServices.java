package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

public class UserServices {
    //TODO: Make checking auth its own function probably
    private UserDataAccess userDataAccess;
    private AuthDataAccess authDataAccess;
    private GameDataAccess gameDataAccess;
    public UserServices(UserDataAccess userDataAccess, AuthDataAccess authDataAccess, GameDataAccess gameDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;
    }

    public RegisterResult register(RegisterRequest registerRequest) {
        if(registerRequest.username() == null || registerRequest.email() == null || registerRequest.password() == null) {
            throw new BadDataException("Invalid input");
        }
        UserData user = userDataAccess.getUser(registerRequest.username());
        if(user != null){
            throw new NonSuccessException("Username Taken!"); //TODO: Make better error
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
        UserData user = userDataAccess.getUser(loginRequest.username());
        if(user == null){
            throw new NonSuccessException("Username does not exist!"); //TODO: Make better error
        }
        Boolean correctPassword = userDataAccess.checkPassword(loginRequest.username(), loginRequest.password());
        if(correctPassword){
            AuthData auth = authDataAccess.createAuth(user.username());
            return new LoginResult(user.username(), auth.authToken());
        }
        else{
            throw new NonSuccessException("Incorrect password!"); //TODO: Make better error.
        }
    }

    public void logout(LogoutRequest logoutRequest) {
        AuthData auth = authDataAccess.getAuth(logoutRequest.authToken());
        if(auth == null){
            throw new NonSuccessException("Invalid AuthData"); //TODO: Make better error.
        }
        authDataAccess.deleteAuth(auth.authToken());
    }
}

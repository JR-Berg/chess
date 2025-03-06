package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.NonSuccessException;
import dataaccess.UserDataAccess;
import model.AuthData;
import model.UserData;
import request.RegisterRequest;
import result.RegisterResult;

public class UserServices {
    private UserDataAccess userDataAccess;
    private AuthDataAccess authDataAccess;
    public UserServices(UserDataAccess userDataAccess, AuthDataAccess authDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public RegisterResult register(RegisterRequest registerRequest) {
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
    }
}

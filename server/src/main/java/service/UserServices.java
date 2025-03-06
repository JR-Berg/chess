package service;

import dataaccess.DataAccessException;
import dataaccess.NonSuccessException;
import dataaccess.UserDataAccess;
import model.UserData;
import request.RegisterRequest;
import result.RegisterResult;

public class UserServices {
    private UserDataAccess userDataAccess;
    public UserServices(UserDataAccess userDataAccess) {
        this.userDataAccess = userDataAccess;
    }

    public RegisterResult register(RegisterRequest registerRequest) {
        UserData user = userDataAccess.getUser(registerRequest.username());
        if(user != null){
            throw new NonSuccessException("Username Taken!"); //TODO: Make better error
        }
        user = userDataAccess.createUser(registerRequest.username(), registerRequest.password());
        RegisterResult result = new RegisterResult(user.username(), )
    }
}

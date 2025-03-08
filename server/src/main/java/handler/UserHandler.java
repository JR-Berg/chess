package handler;

import com.google.gson.Gson;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import service.UserServices;

public class UserHandler {
    Gson serializer = new Gson();
    UserServices userServices;
    public UserHandler(UserServices userServices) {
        this.userServices = userServices;
    }

    public String registerUser(String requestBody) {
        RegisterRequest registerRequest = serializer.fromJson(requestBody, request.RegisterRequest.class);
        RegisterResult registerResult = userServices.register(registerRequest);
        return serializer.toJson(registerResult);
    }
    public void clearApplication() {
        userServices.clearAll();
    }
    public String loginUser(String requestBody) {
        LoginRequest loginRequest = serializer.fromJson(requestBody, request.LoginRequest.class);
        LoginResult loginResult = userServices.login(loginRequest);
        return serializer.toJson(loginResult);
    }

    public void logoutUser(String requestHeader) {
        LogoutRequest logoutRequest = new LogoutRequest(requestHeader);
        userServices.logout(logoutRequest);
    }
}

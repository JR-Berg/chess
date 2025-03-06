package handler;

import com.google.gson.Gson;
import model.UserData;
import request.RegisterRequest;
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
    public String clearApplication() {
        userServices.clearAll();
        return "";
    }
}

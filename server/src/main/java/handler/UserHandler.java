package handler;

import com.google.gson.Gson;
import model.UserData;
import request.RegisterRequest;
import result.RegisterResult;

public class UserHandler {
    Gson serializer = new Gson();
    public String registerUser(String requestBody) {
        RegisterRequest registerRequest = serializer.fromJson(requestBody, request.RegisterRequest.class);
        RegisterResult registerResult = service.UserServices.register(registerRequest);
        //TODO: implement return function
    }
}

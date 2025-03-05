package handler;

import com.google.gson.Gson;
import model.UserData;

public class UserHandler {
    Gson serializer = new Gson();
    public String registerUser(String requestBody) {
        UserData registerRequest = serializer.fromJson(requestBody, UserData.class);
        Boolean registerResult = service.UserServices.register(registerRequest);
        //TODO: implement return function
    }
}

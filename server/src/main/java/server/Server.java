package server;

import com.google.gson.Gson;
import dataaccess.AuthDataAccess;
import dataaccess.GameDataAccess;
import dataaccess.UserDataAccess;
import handler.UserHandler;
import model.UserData;
import service.UserServices;
import spark.*;

public class Server {
    //private UserHandler userHandler;
    public int run(int desiredPort) {
        UserDataAccess userDataAccess = new dataaccess.MemoryUserDataAccess();
        AuthDataAccess userAuthAccess = new dataaccess.MemoryAuthDataAccess();
        GameDataAccess gameDataAccess = new dataaccess.MemoryGameDataAccess();
        UserServices userServices = new UserServices(userDataAccess, userAuthAccess, gameDataAccess);
        UserHandler userHandler = new UserHandler(userServices);

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", (req, res) -> {
            return registerUser(req.body(), userHandler);
        });
        Spark.delete("/db", (req, res) -> {
            return clearApplication(userHandler);
        });
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private String registerUser(String requestBody, UserHandler userHandler) {
        return userHandler.registerUser(requestBody);
    }
    private String clearApplication(UserHandler userHandler) {
        userHandler.clearApplication();
        return "";
    }
}

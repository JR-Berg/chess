package server;

import com.google.gson.Gson;
import dataaccess.AuthDataAccess;
import dataaccess.GameDataAccess;
import dataaccess.UserDataAccess;
import handler.GameHandler;
import handler.UserHandler;
import model.UserData;
import service.GameServices;
import service.UserServices;
import spark.*;

public class Server {
    //TODO: Go through and add Success Booleans to all of the objects in request and results.
    public int run(int desiredPort) {
        UserDataAccess userDataAccess = new dataaccess.MemoryUserDataAccess();
        AuthDataAccess userAuthAccess = new dataaccess.MemoryAuthDataAccess();
        GameDataAccess gameDataAccess = new dataaccess.MemoryGameDataAccess();
        UserServices userServices = new UserServices(userDataAccess, userAuthAccess, gameDataAccess);
        UserHandler userHandler = new UserHandler(userServices);
        GameServices gameServices = new GameServices(userDataAccess, userAuthAccess, gameDataAccess);
        GameHandler gameHandler = new GameHandler(gameServices);

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", (req, res) -> {
            return registerUser(req.body(), userHandler);
        });
        Spark.delete("/db", (req, res) -> {
            return clearApplication(userHandler);
        });
        Spark.post("/session", (req, res) -> {
            return loginUser(req.body(), userHandler);
        });
        Spark.delete("/session", (req, res) -> {
            return logoutUser(req.headers("authorization"), userHandler);
        });
        Spark.get("/game", (req, res) -> {
            return listGames(req.headers("authorization"), gameHandler);
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
    private String loginUser(String requestBody, UserHandler userHandler) {
        return userHandler.loginUser(requestBody);
    }
    private String logoutUser(String requestHeader, UserHandler userHandler) {
        userHandler.logoutUser(requestHeader);
        return "";
    }
    private String listGames(String requestHeader, GameHandler gameHandler) {
        return gameHandler.listGames(requestHeader);
    }
}

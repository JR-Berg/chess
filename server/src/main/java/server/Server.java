package server;

import com.google.gson.Gson;
import dataaccess.*;
import handler.GameHandler;
import handler.UserHandler;
import model.UserData;
import service.GameServices;
import service.UserServices;
import spark.*;

import static spark.Spark.halt;

public class Server {
    //TODO: Go through and add Success Booleans to all of the objects in request and results.
    public int run(int desiredPort) {
        UserDataAccess userDataAccess = new MemoryUserDataAccess();
        AuthDataAccess userAuthAccess = new MemoryAuthDataAccess();
        GameDataAccess gameDataAccess = new MemoryGameDataAccess();
        UserServices userServices = new UserServices(userDataAccess, userAuthAccess, gameDataAccess);
        UserHandler userHandler = new UserHandler(userServices);
        GameServices gameServices = new GameServices(userDataAccess, userAuthAccess, gameDataAccess);
        GameHandler gameHandler = new GameHandler(gameServices);

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //Register function
        Spark.post("/user", (req, res) -> {
            String ret = "";
            try {
                ret = registerUser(req.body(), userHandler);
            } catch(NonSuccessException e){ //Error 403, username taken
                halt(403, "{ \"message\": \"Error: already taken\" }");
            } catch(BadDataException e) { //Error 400, bad request (for example, didn't input enough data)
                halt(400, "{ \"message\": \"Error: bad request\" }");
            }
            return ret;
        });

        //Clear Application
        Spark.delete("/db", (req, res) -> {
            String ret = "";
            try {
                ret = clearApplication(userHandler);
            } catch(NonSuccessException e) { //Error 500, server error
                halt(); //TODO: Implement error 500
            }
            return ret;
        });
        Spark.post("/session", (req, res) -> {
            String ret = "";
            try {
                ret = loginUser(req.body(), userHandler);
            }
            catch(NonSuccessException e) {//TODO better error
                halt(401,"{ \"message\": \"Error: unauthorized\" }");
            }
            return ret;
        });
        Spark.delete("/session", (req, res) -> {
            return logoutUser(req.headers("authorization"), userHandler);
        });
        Spark.get("/game", (req, res) -> {
            return listGames(req.headers("authorization"), gameHandler);
        });
        Spark.post("/game", (req, res) -> {
            return createGame(req.headers("authorization"), req.body(), gameHandler);
        });
        Spark.put("/game", (req, res) -> {
            return joinGame(req.headers("authorization"), req.body(), gameHandler);
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
    private String createGame(String requestHeader, String requestBody, GameHandler gameHandler) {
        return gameHandler.createGame(requestHeader, requestBody);
    }
    private String joinGame(String requestHeader, String requestBody, GameHandler gameHandler){
        return gameHandler.joinGame(requestHeader, requestBody);
    }
}

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

        //Login
        Spark.post("/session", (req, res) -> {
            String ret = "";
            try {
                ret = loginUser(req.body(), userHandler);
            } catch(NonSuccessException e) { //Error 401, unauthorized (bad authToken)
                halt(401,"{ \"message\": \"Error: unauthorized\" }");
            } catch(WhomstException e) { //Also error 401, but wrong username
                halt(401, "{ \"message\": \"Error: unauthorized\" }");
            } catch(BadDataException e) { //Error 400, not enough input probably
                halt(400, "{ \"message\": \"Error: bad request\" }");
            }
            return ret;
        });

        //Logout
        Spark.delete("/session", (req, res) -> {
            String ret = "";
            try {
                ret = logoutUser(req.headers("authorization"), userHandler);
            } catch(BadAuthException e) { //Error 401, unauthorized (bad authToken)
                halt(401, "{ \"message\": \"Error: unauthorized\" }");
            }
            return ret;
        });

        //List games
        Spark.get("/game", (req, res) -> {
            String ret = "";
            try {
                ret = listGames(req.headers("authorization"), gameHandler);
            } catch(BadAuthException e) { //Error 401, unauthorized (bad AuthToken)
                halt(401, "{ \"message\": \"Error: unauthorized\" }");
            } //TODO: Implement error 500
            return ret;
        });

        //Create Game
        Spark.post("/game", (req, res) -> {
            String ret = "";
            try {
                ret = createGame(req.headers("authorization"), req.body(), gameHandler);
            } catch(BadDataException e) { //Error 400, bad request (not enough data)
                halt(400, "\"Error: bad request\"");
            } catch(BadAuthException e) { //Error 401, unauthorized
                halt(401, "{ \"message\": \"Error: unauthorized\" }");
            } //TODO: implement error 500
            return ret;
        });

        //Join game
        Spark.put("/game", (req, res) -> {
            String ret = "";
            try {
                ret = joinGame(req.headers("authorization"), req.body(), gameHandler);
            } catch(BadDataException e) { //Error 400, bad request (like not enough data input)
                halt(400, "{ \"message\": \"Error: bad request\" }");
            } catch(BadAuthException e) { //Error 401, unauthorized (bad AuthData)
                halt(401, "{ \"message\": \"Error: unauthorized\" }");
            } catch(OverlapException e) { //Error 403, attempted to join team already taken
                halt(403, "{ \"message\": \"Error: already taken\" }");
            } catch(WhomstException e) { //Error 400 again, attempted to join a bad team (like green)
                halt(400, "{ \"message\": \"Error: bad request\" }");
            }//TODO: Implement error 500
            return ret;
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

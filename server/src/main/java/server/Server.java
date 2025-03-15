package server;

import dataaccess.*;
import handler.GameHandler;
import handler.UserHandler;
import service.GameServices;
import service.UserServices;
import spark.*;



import static spark.Spark.halt;

public class Server {
    UserDataAccess userDataAccess;
    AuthDataAccess userAuthAccess;
    GameDataAccess gameDataAccess;
    UserServices userServices;
    UserHandler userHandler;
    GameServices gameServices;
    GameHandler gameHandler;

    private void initializeServices() throws DataAccessException {
        userDataAccess = new MySQLUserDataAccess();
        userAuthAccess = new MySQLAuthDataAccess();
        gameDataAccess = new MySQLGameDataAccess();
        userServices = new UserServices(userDataAccess, userAuthAccess, gameDataAccess);
        userHandler = new UserHandler(userServices);
        gameServices = new GameServices(userDataAccess, userAuthAccess, gameDataAccess);
        gameHandler = new GameHandler(gameServices);
    }
    public int run(int desiredPort) {
        try {
            initializeServices();
        } catch(DataAccessException e) {
            halt(500, "DataAccessException while instantiating Data Access classes.");
        }

        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        Spark.post("/user", (req, res) -> { //Register function
            String ret = "";
            try {
                ret = registerUser(req.body(), userHandler);
            } catch(NonSuccessException e){ //Error 403, username taken
                halt(403, "{ \"message\": \"Error: already taken\" }");
            } catch(BadDataException e) { //Error 400, bad request (for example, didn't input enough data)
                halt(400, "{ \"message\": \"Error: bad request\" }");
            } catch(DataAccessException e) {
                halt(500, "{ \"message\": \"Error: " + e.getMessage() + "\" }");
            }
            return ret;
        });
        Spark.delete("/db", (req, res) -> { //Clear Application
            String ret = "";
            try {
                ret = clearApplication(userHandler);
            } catch(DataAccessException e) { //Error 500, server error
                halt(500, "{ \"message\": \"Error: " + e.getMessage() + "\" }"); //Implement error 500
            }
            return ret;
        });
        Spark.post("/session", (req, res) -> { //Login
            String ret = "";
            try {
                ret = loginUser(req.body(), userHandler);
            } catch(NonSuccessException | WhomstException e) { //Error 401, unauthorized (bad authToken)
                halt(401,"{ \"message\": \"Error: unauthorized\" }");
            } catch(BadDataException e) { //Error 400, not enough input probably
                halt(400, "{ \"message\": \"Error: bad request\" }");
            } catch(DataAccessException e) {
                halt(500, "{ \"message\": \"Error: " + e.getMessage() + "\" }");
            }
            return ret;
        });
        Spark.delete("/session", (req, res) -> { //Logout
            String ret = "";
            try {
                ret = logoutUser(req.headers("authorization"), userHandler);
            } catch(BadAuthException e) { //Error 401, unauthorized (bad authToken)
                halt(401, "{ \"message\": \"Error: unauthorized\" }");
            } catch(DataAccessException e) {
                halt(500, "{ \"message\": \"Error: " + e.getMessage() + "\" }");
            }
            return ret;
        });
        Spark.get("/game", (req, res) -> { //List games
            String ret = "";
            try {
                ret = listGames(req.headers("authorization"), gameHandler);
            } catch(BadAuthException e) { //Error 401, unauthorized (bad AuthToken)
                halt(401, "{ \"message\": \"Error: unauthorized\" }");
            } catch(DataAccessException e) {
                halt(500, "{ \"message\": \"Error: " + e.getMessage() + "\" }");
            }
            return ret;
        });
        Spark.post("/game", (req, res) -> { //Create Game
            String ret = "";
            try {
                ret = createGame(req.headers("authorization"), req.body(), gameHandler);
            } catch(BadDataException e) { //Error 400, bad request (not enough data)
                halt(400, "\"Error: bad request\"");
            } catch(BadAuthException e) { //Error 401, unauthorized
                halt(401, "{ \"message\": \"Error: unauthorized\" }");
            } catch(NonSuccessException e) { //Error 403, game name taken
                halt(403, "{ \"message\": \"Error: already taken\" }");
            } catch(DataAccessException e) {
                halt(500, "{ \"message\": \"Error: " + e.getMessage() + "\" }");
            }
            return ret;
        });
        Spark.put("/game", (req, res) -> { //Join game
            String ret = "";
            try {
                ret = joinGame(req.headers("authorization"), req.body(), gameHandler);
            } catch(BadDataException | WhomstException e) { //Error 400, bad request (like not enough data input)
                halt(400, "{ \"message\": \"Error: bad request\" }");
            } catch(BadAuthException e) { //Error 401, unauthorized (bad AuthData)
                halt(401, "{ \"message\": \"Error: unauthorized\" }");
            } catch(OverlapException e) { //Error 403, attempted to join team already taken
                halt(403, "{ \"message\": \"Error: already taken\" }");
            } catch(DataAccessException e) {
                halt(500, "{ \"message\": \"Error: " + e.getMessage() + "\" }");
            }
            return ret;
        });
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private String registerUser(String requestBody, UserHandler userHandler) throws DataAccessException{
        return userHandler.registerUser(requestBody);
    }
    private String clearApplication(UserHandler userHandler) throws DataAccessException{
        userHandler.clearApplication();
        return "";
    }
    private String loginUser(String requestBody, UserHandler userHandler) throws DataAccessException{
        return userHandler.loginUser(requestBody);
    }
    private String logoutUser(String requestHeader, UserHandler userHandler) throws DataAccessException{
        userHandler.logoutUser(requestHeader);
        return "";
    }
    private String listGames(String requestHeader, GameHandler gameHandler) throws DataAccessException{
        return gameHandler.listGames(requestHeader);
    }
    private String createGame(String requestHeader, String requestBody, GameHandler gameHandler) throws DataAccessException{
        return gameHandler.createGame(requestHeader, requestBody);
    }
    private String joinGame(String requestHeader, String requestBody, GameHandler gameHandler) throws DataAccessException{
        return gameHandler.joinGame(requestHeader, requestBody);
    }
}

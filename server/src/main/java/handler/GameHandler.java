package handler;

import com.google.gson.Gson;
import request.CreateGameRequest;
import request.ListGamesRequest;
import result.ListGamesResult;
import service.GameServices;

public class GameHandler {
    Gson serializer = new Gson();
    GameServices gameServices;
    public GameHandler(GameServices gameServices) {
        this.gameServices = gameServices;
    }
    public String listGames(String requestHeader){
        String authToken = requestHeader;
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        ListGamesResult listGamesResult = gameServices.listGames(listGamesRequest);
        return serializer.toJson(listGamesResult);
    }

    public String createGame(String requestHeader, String requestBody) {
        String authToken = requestHeader;
        CreateGameRequest emptyGameRequest = serializer.fromJson(requestBody, CreateGameRequest.class);
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, emptyGameRequest.gameName());
        return serializer.toJson(gameServices.createGame(createGameRequest));
    }

    public String joinGame(String requestHeader, String requestBody) {
        String authToken = requestHeader;
        JoinGameRequest
    }
}

package handler;

import com.google.gson.Gson;
import request.CreateGameRequest;
import request.JoinGameRequest;
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
        ListGamesRequest listGamesRequest = new ListGamesRequest(requestHeader);
        ListGamesResult listGamesResult = gameServices.listGames(listGamesRequest);
        return serializer.toJson(listGamesResult);
    }

    public String createGame(String requestHeader, String requestBody) {
        CreateGameRequest emptyGameRequest = serializer.fromJson(requestBody, CreateGameRequest.class);
        CreateGameRequest createGameRequest = new CreateGameRequest(requestHeader, emptyGameRequest.gameName());
        return serializer.toJson(gameServices.createGame(createGameRequest));
    }

    public String joinGame(String requestHeader, String requestBody) {
        JoinGameRequest partialRequest = serializer.fromJson(requestBody, JoinGameRequest.class);
        JoinGameRequest joinGameRequest = new JoinGameRequest(requestHeader, partialRequest.playerColor(), partialRequest.gameID());
        return serializer.toJson(gameServices.joinGame(joinGameRequest));
    }
}

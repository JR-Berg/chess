package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
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
    public String listGames(String requestHeader) throws DataAccessException {
        ListGamesRequest listGamesRequest = new ListGamesRequest(requestHeader);
        ListGamesResult listGamesResult = gameServices.listGames(listGamesRequest);
        return serializer.toJson(listGamesResult);
    }

    public String createGame(String requestHeader, String requestBody) throws DataAccessException {
        CreateGameRequest emptyGameRequest = serializer.fromJson(requestBody, CreateGameRequest.class);
        CreateGameRequest createGameRequest = new CreateGameRequest(requestHeader, emptyGameRequest.gameName());
        return serializer.toJson(gameServices.createGame(createGameRequest));
    }

    public String joinGame(String requestHeader, String requestBody) throws DataAccessException{
        JoinGameRequest partialRequest = serializer.fromJson(requestBody, JoinGameRequest.class);
        JoinGameRequest joinGameRequest = new JoinGameRequest(requestHeader, partialRequest.playerColor(), partialRequest.gameID());
        return serializer.toJson(gameServices.joinGame(joinGameRequest));
    }
}

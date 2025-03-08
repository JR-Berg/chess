package handler;

import com.google.gson.Gson;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.ListGamesResult;
import service.GameServices;

import java.util.Collection;
import java.util.Set;

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
        //Collection<GameData> gamesList = listGamesResult.games().values();
        //return serializer.toJson(gamesList);
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
        JoinGameRequest partialRequest = serializer.fromJson(requestBody, JoinGameRequest.class);
        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, partialRequest.playerColor(), partialRequest.gameID());
        return serializer.toJson(gameServices.joinGame(joinGameRequest));
    }
}

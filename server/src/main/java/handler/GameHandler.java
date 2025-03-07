package handler;

import com.google.gson.Gson;
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
}

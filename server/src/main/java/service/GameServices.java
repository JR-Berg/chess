package service;

import dataaccess.AuthDataAccess;
import dataaccess.GameDataAccess;
import dataaccess.NonSuccessException;
import dataaccess.UserDataAccess;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.ListGamesRequest;
import result.CreateGameResult;
import result.ListGamesResult;

import java.util.Map;

public class GameServices {
    private UserDataAccess userDataAccess;
    private AuthDataAccess authDataAccess;
    private GameDataAccess gameDataAccess;
    public GameServices(UserDataAccess userDataAccess, AuthDataAccess authDataAccess, GameDataAccess gameDataAccess) {
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
        this.gameDataAccess = gameDataAccess;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest){
        AuthData auth = checkAuth(listGamesRequest.authToken());
        Map<Integer, GameData> games = gameDataAccess.listGames();
        return new ListGamesResult(games, true);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) {
        AuthData auth = checkAuth(createGameRequest.authToken());
        Integer gameId = gameDataAccess.getGameIDByName(createGameRequest.gameName());
        if(gameId != null) {
            throw new NonSuccessException("gameName already exists!"); //TODO: make better error
        }
        Integer newGameID = gameDataAccess.generateGameID();
        GameData newGame = new GameData(newGameID, createGameRequest.gameName(),);
    }

    private AuthData checkAuth(String authToken){
        AuthData auth = authDataAccess.getAuth(listGamesRequest.authToken());
        if(auth == null) {
            throw new NonSuccessException("AuthData invalid!"); //TODO: Make better error
        }
        return auth;
    }
}

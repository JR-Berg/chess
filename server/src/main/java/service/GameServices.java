package service;

import dataaccess.AuthDataAccess;
import dataaccess.GameDataAccess;
import dataaccess.NonSuccessException;
import dataaccess.UserDataAccess;
import model.AuthData;
import model.GameData;
import request.ListGamesRequest;
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
        AuthData auth = authDataAccess.getAuth(listGamesRequest.authToken());
        if(auth == null) {
            throw new NonSuccessException("AuthData invalid!"); //TODO: Make better error
        }
        Map<Integer, GameData> games = gameDataAccess.listGames();
        return new ListGamesResult(games, true);
    }
}

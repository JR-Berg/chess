package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.CreateGameResult;
import result.JoinGameResult;
import result.ListGamesResult;

import java.util.Collection;
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
        if(listGamesRequest.authToken() == null) {
            throw new BadDataException("No AuthToken!");
        }
        AuthData auth = checkAuth(listGamesRequest.authToken());
        Map<Integer, GameData> games = gameDataAccess.listGames();
        Collection<GameData> gamesList = games.values();
        return new ListGamesResult(gamesList);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) {
        if(createGameRequest.gameName() == null || createGameRequest.authToken() == null) {
            throw new BadDataException("Not enough data!");
        }
        AuthData auth = checkAuth(createGameRequest.authToken());
        Integer gameId = gameDataAccess.getGameIDByName(createGameRequest.gameName());
        if(gameId != null) {
            throw new NonSuccessException("gameName already exists!");
        }
        Integer newGameID = gameDataAccess.generateGameID();
        ChessGame chessGame = new ChessGame();
        GameData newGame = new GameData(newGameID,null,null, createGameRequest.gameName(), chessGame);
        gameDataAccess.createGame(newGameID, newGame);
        return new CreateGameResult(newGameID, true);
    }
    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) {
        if(joinGameRequest.gameID() == null || joinGameRequest.authToken() == null || joinGameRequest.playerColor() == null) {
            throw new BadDataException("Not enough data!");
        }
        AuthData auth = checkAuth(joinGameRequest.authToken());
        Integer gameID = joinGameRequest.gameID();
        GameData gameData = gameDataAccess.getGame(gameID);
        if(gameData == null){
            throw new NonSuccessException("gameID invalid!");
        }
        gameDataAccess.setPlayerTeam(gameID, auth.username(), joinGameRequest.playerColor());
        return new JoinGameResult(true);
    }

    private AuthData checkAuth(String authToken){
        AuthData auth = authDataAccess.getAuth(authToken);
        if(auth == null) {
            throw new BadAuthException("AuthData invalid!");
        }
        return auth;
    }
}

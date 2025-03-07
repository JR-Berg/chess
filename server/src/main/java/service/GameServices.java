package service;

import chess.ChessGame;
import dataaccess.AuthDataAccess;
import dataaccess.GameDataAccess;
import dataaccess.NonSuccessException;
import dataaccess.UserDataAccess;
import model.AuthData;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.CreateGameResult;
import result.JoinGameResult;
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
        ChessGame chessGame = new ChessGame();
        GameData newGame = new GameData(newGameID,"","", createGameRequest.gameName(), chessGame);
        gameDataAccess.createGame(newGameID, newGame);
        return new CreateGameResult(newGameID, true);
    }
    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) {
        AuthData auth = checkAuth(joinGameRequest.authToken());
        Integer gameID = joinGameRequest.gameID();
        GameData gameData = gameDataAccess.getGame(gameID);
        if(gameData == null){
            throw new NonSuccessException("gameID invalid!"); //TODO: make better error
        }
        gameDataAccess.setPlayerTeam(gameID, auth.username(), joinGameRequest.playerColor());
        return new JoinGameResult(true);
    }

    private AuthData checkAuth(String authToken){
        AuthData auth = authDataAccess.getAuth(authToken);
        if(auth == null) {
            throw new NonSuccessException("AuthData invalid!"); //TODO: Make better error
        }
        return auth;
    }


}

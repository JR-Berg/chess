package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import result.ListGamesResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MemoryGameDataAccess extends GameDataAccess {
    private final Map<Integer, GameData> gamesDB = new HashMap<>();

    public String clearAll(){
        gamesDB.clear();
        return "";
    }
    public Map<Integer, GameData> listGames(){
        return new HashMap<>(gamesDB);
    }

    @Override
    public Integer generateGameID() {
        return gamesDB.size() + 1;
    }

    @Override
    public void createGame(Integer newGameID, GameData newGame) {
        gamesDB.put(newGameID, newGame);
    }

    @Override
    public GameData getGame(Integer gameID) {
        return gamesDB.get(gameID);
    }

    @Override
    public void setPlayerTeam(Integer gameID, String username, String teamColor) {
        GameData gameData = gamesDB.get(gameID);
        GameData newGameData;
        if(Objects.equals(teamColor, "WHITE")) {
            newGameData = new GameData(gameID, username, gameData.blackUsername(), gameData.gameName(), gameData.game());
        }
        else {
            newGameData = new GameData(gameID, gameData.whiteUsername(), username, gameData.gameName(), gameData.game());
        }
        gamesDB.put(gameID, newGameData);
    }

    public Integer getGameIDByName(String gameName) {
        for (Map.Entry<Integer, GameData> entry : gamesDB.entrySet()) {
            if (entry.getValue().gameName().equals(gameName)) {
                return entry.getKey();  // Return the gameID
            }
        }
        return null;  // Return null if no game with the given name was found
    }

}

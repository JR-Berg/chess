package dataaccess;

import model.GameData;
import model.UserData;
import result.ListGamesResult;

import java.util.HashMap;
import java.util.Map;

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

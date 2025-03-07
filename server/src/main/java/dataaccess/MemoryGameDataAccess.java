package dataaccess;

import model.GameData;
import model.UserData;
import result.ListGamesResult;

import java.util.HashMap;
import java.util.Map;

public class MemoryGameDataAccess extends GameDataAccess {
    private final Map<Integer, GameData> gameDB = new HashMap<>();

    public String clearAll(){
        gameDB.clear();
        return "";
    }
    public Map<Integer, GameData> listGames(){
        return new HashMap<>(gameDB);
    }
}

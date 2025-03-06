package dataaccess;

import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryGameDataAccess extends GameDataAccess {
    private final Map<String, GameData> gameDB = new HashMap<>();

    public String clearAll(){
        gameDB.clear();
        return "";
    }
}

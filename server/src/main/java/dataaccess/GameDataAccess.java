package dataaccess;

import model.GameData;
import result.ListGamesResult;

import java.util.Map;

public abstract class GameDataAccess {
    public abstract String clearAll();
    public abstract Integer getGameIDByName(String gameName);
    public abstract Map<Integer, GameData> listGames();

    public abstract Integer generateGameID();
}

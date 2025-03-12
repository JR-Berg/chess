package dataaccess;

import model.GameData;

import java.util.Map;

public class MySQLGameDataAccess extends GameDataAccess{
    @Override
    public String clearAll() {
        return "";
    }

    @Override
    public Integer getGameIDByName(String gameName) {
        return 0;
    }

    @Override
    public Map<Integer, GameData> listGames() {
        return Map.of();
    }

    @Override
    public Integer generateGameID() {
        return 0;
    }

    @Override
    public void createGame(Integer newGameID, GameData newGame) {

    }

    @Override
    public GameData getGame(Integer gameID) {
        return null;
    }

    @Override
    public void setPlayerTeam(Integer gameID, String username, String teamColor) {

    }
}

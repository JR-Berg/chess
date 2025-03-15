package dataaccess;

import model.GameData;

import java.util.Map;

public abstract class GameDataAccess {
    public abstract String clearAll() throws DataAccessException;
    public abstract Integer getGameIDByName(String gameName) throws DataAccessException;
    public abstract Map<Integer, GameData> listGames() throws DataAccessException;

    public abstract Integer generateGameID() throws DataAccessException;

    public abstract void createGame(Integer newGameID, GameData newGame) throws DataAccessException;

    public abstract GameData getGame(Integer gameID) throws DataAccessException;

    public abstract void setPlayerTeam(Integer gameID, String username, String teamColor) throws DataAccessException;
}

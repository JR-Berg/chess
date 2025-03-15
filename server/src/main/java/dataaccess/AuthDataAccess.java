package dataaccess;

import model.AuthData;

public abstract class AuthDataAccess {
    public abstract AuthData getAuth(String authToken) throws DataAccessException;
    public abstract AuthData createAuth(String username) throws DataAccessException;
    public abstract void deleteAuth(String authToken) throws DataAccessException;
    public abstract String clearAll() throws DataAccessException;
}

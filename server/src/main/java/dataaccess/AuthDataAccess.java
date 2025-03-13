package dataaccess;

import model.AuthData;

public abstract class AuthDataAccess {
    public abstract AuthData getAuth(String authToken);
    public abstract AuthData createAuth(String username);
    public abstract void deleteAuth(String authToken);
    public abstract String clearAll();
}

package dataaccess;

import model.AuthData;

public abstract class AuthDataAccess {
    public abstract Boolean getAuth(String authToken);
    public abstract AuthData createAuth(String username);
    public abstract void deleteAuth(String username);

}

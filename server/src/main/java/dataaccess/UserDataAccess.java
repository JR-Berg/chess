package dataaccess;

import model.UserData;

public abstract class UserDataAccess {
    public abstract UserData createUser(String username, String password, String email);

    public abstract UserData getUser(String username);
    public abstract String clearAll() throws DataAccessException;

    public abstract Boolean checkPassword(String username, String password);
}

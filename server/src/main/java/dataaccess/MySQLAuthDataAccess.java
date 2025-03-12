package dataaccess;

import model.AuthData;

public class MySQLAuthDataAccess extends AuthDataAccess {
    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public AuthData createAuth(String username) {
        return null;
    }

    @Override
    public void deleteAuth(String username) {

    }

    @Override
    public String clearAll() {
        return "";
    }
}

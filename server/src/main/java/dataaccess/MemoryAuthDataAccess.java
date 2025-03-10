package dataaccess;

import java.util.HashMap;
import java.util.UUID;

import model.AuthData;

public class MemoryAuthDataAccess extends AuthDataAccess{

    private final HashMap<String, AuthData> authsDB = new HashMap<>();
    @Override
    public AuthData getAuth(String authToken) {
        return authsDB.get(authToken);
    }

    @Override
    public AuthData createAuth(String username) {
        String authToken = UUID.randomUUID().toString();
        AuthData newAuth = new AuthData(authToken, username);
        authsDB.put(authToken, newAuth);
        return newAuth;
    }

    @Override
    public void deleteAuth(String authToken) {
        authsDB.remove(authToken);
    }

    public String clearAll() {
        authsDB.clear();
        return "";
    }
}

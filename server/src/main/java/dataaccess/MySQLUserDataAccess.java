package dataaccess;

import model.UserData;

public class MySQLUserDataAccess extends UserDataAccess{
    @Override
    public UserData createUser(String username, String password, String email) {
        return null;
    }

    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public String clearAll() {
        return "";
    }

    @Override
    public Boolean checkPassword(String username, String password) {
        return null;
    }
}

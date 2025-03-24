import dataaccess.DataAccessException;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    private String registerUser() throws DataAccessException {
        var path = "/user";
        return "";
    }
    private String clearApplication() throws DataAccessException{
        var path = "/db";
        return "";
    }
    private String loginUser() throws DataAccessException{
        var path = "/session";
        return "";
    }
    private String logoutUser() throws DataAccessException{
        var path = "/session";
        return "";
    }
    private String listGames() throws DataAccessException{
        var path = "/game";
        return "";
    }
    private String createGame() throws DataAccessException{
        var path = "/game";
        return "";
    }
    private String joinGame() throws DataAccessException{
        var path = "/game";
        return "";
    }
}

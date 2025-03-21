package service;

import dataaccess.*;
import model.GameData;
import org.junit.jupiter.api.*;
import request.*;
import result.*;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTests {
    static UserServices userServices;
    static GameServices gameServices;
    static MemoryUserDataAccess mockUserDataAccess;
    static MemoryAuthDataAccess mockAuthDataAccess;
    static MemoryGameDataAccess mockGameDataAccess;

    @BeforeAll
    public static void setUp() throws SQLException {
        mockUserDataAccess = new MemoryUserDataAccess();
        mockAuthDataAccess = new MemoryAuthDataAccess();
        mockGameDataAccess = new MemoryGameDataAccess();
        userServices = new UserServices(mockUserDataAccess, mockAuthDataAccess, mockGameDataAccess);
        gameServices = new GameServices(mockUserDataAccess, mockAuthDataAccess, mockGameDataAccess);
    }
    @AfterEach
    public void clearStuff() throws DataAccessException{
        userServices.clearAll();
    }


    @Test
    @Order(1)
    public void testRegister() throws DataAccessException{
        //Register a user and confirm they were registered
        RegisterRequest request = new RegisterRequest("testUser", "password", "test@example.com");
        RegisterResult result = userServices.register(request);
        assertNotNull(result);
    }

    @Test
    @Order(2)
    public void testFailRegister() throws DataAccessException{
        //Set up 3 register requests, register first 2 users.
        RegisterRequest request = new RegisterRequest("testUser", "password", "test@example.com");
        RegisterRequest request2 = new RegisterRequest("test2user", "password", "test@example.com");
        RegisterRequest request3 = new RegisterRequest("test2user", "password", "test@example.com");
        RegisterResult result = userServices.register(request);
        RegisterResult result2 = userServices.register(request2);

        //Ensure first 2 users were registered.
        assertNotNull(result);
        assertNotNull(result2);

        //Ensure 3rd user will not register due to username reuse.
        try {
            userServices.register(request3);
            fail("Expected NonSuccessException to be thrown");
        } catch (NonSuccessException e) {
            assertEquals("Username Taken!", e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testClear() throws DataAccessException{
        //Register 2 users.
        RegisterRequest request = new RegisterRequest("testUser", "password", "test@example.com");
        RegisterRequest request2 = new RegisterRequest("test2user", "password", "test@example.com");
        RegisterResult result = userServices.register(request);
        RegisterResult result2 = userServices.register(request2);

        //Ensure 2 users were registered
        assertNotNull(result);
        assertNotNull(result2);

        //Clear database.
        userServices.clearAll();

        //Register a third user with the first user's username.
        RegisterRequest request3 = new RegisterRequest("test2user", "password", "test@example.com");
        RegisterResult result3 = userServices.register(request3);

        //Ensure third user was registered.
        assertNotNull(result3);

    }

    @Test
    @Order(4)
    public void testLogin() throws DataAccessException{
        //Register 2 users, ensure they were registered properly.
        RegisterRequest request = new RegisterRequest("testUser", "password", "test@example.com");
        RegisterRequest request2 = new RegisterRequest("test2user", "password", "test@example.com");
        RegisterResult result = userServices.register(request);
        RegisterResult result2 = userServices.register(request2);
        assertNotNull(result);
        assertNotNull(result2);

        //Correctly login first user, ensure they were logged in
        LoginRequest lRequest = new LoginRequest("testUser", "password");
        LoginResult lResult = userServices.login(lRequest);
        assertNotNull(lResult);
    }

    @Test
    @Order(5)
    public void testBadLogin() throws DataAccessException{
        //Register 2 users, ensure they were registered properly.
        RegisterRequest request = new RegisterRequest("testUser", "password", "test@example.com");
        RegisterRequest request2 = new RegisterRequest("test2user", "password", "test@example.com");
        RegisterResult result = userServices.register(request);
        RegisterResult result2 = userServices.register(request2);
        assertNotNull(result);
        assertNotNull(result2);

        //Correctly login first user, ensure they were logged in
        LoginRequest lRequest = new LoginRequest("testUser", "password");
        LoginResult lResult = userServices.login(lRequest);
        assertNotNull(lResult);

        //Incorrectly login second user, ensure their login fails.
        LoginRequest lRequest2 = new LoginRequest("test2user", "wrongPassword");
        try {
            userServices.login(lRequest2);
            fail("Expected NonSuccessException to be thrown");
        } catch (NonSuccessException e) {
            assertEquals("Incorrect password!", e.getMessage());
        }
    }

    @Test
    @Order(6)
    public void testLogout() throws DataAccessException{
        //Register 2 users and ensure they were registered properly.
        RegisterRequest request = new RegisterRequest("testUser", "password", "test@example.com");
        RegisterRequest request2 = new RegisterRequest("test2user", "password", "test@example.com");
        RegisterResult result = userServices.register(request);
        RegisterResult result2 = userServices.register(request2);
        assertNotNull(result);
        assertNotNull(result2);

        //Login both users properly
        LoginRequest lRequest = new LoginRequest("testUser", "password");
        LoginRequest lRequest2 = new LoginRequest("test2user", "password");
        LoginResult lResult = userServices.login(lRequest);
        LoginResult lResult2 = userServices.login(lRequest2);
        assertNotNull(lResult);
        assertNotNull(lResult2);

        //Attempt to log out first user and ensure their authData is null
        String authToken = lResult.authToken();
        LogoutRequest loutRequest = new LogoutRequest(authToken);
        userServices.logout(loutRequest);
        assertNull(mockAuthDataAccess.getAuth(authToken));

        //Ensure second user is still logged in
        assertNotNull(mockAuthDataAccess.getAuth(lResult2.authToken()));
    }

    @Test
    @Order(7)
    public void testBadLogout() throws DataAccessException{
        //Register 2 users and ensure they were registered properly.
        RegisterRequest request = new RegisterRequest("testUser", "password", "test@example.com");
        RegisterRequest request2 = new RegisterRequest("test2user", "password", "test@example.com");
        RegisterResult result = userServices.register(request);
        RegisterResult result2 = userServices.register(request2);
        assertNotNull(result);
        assertNotNull(result2);

        //Login both users properly
        LoginRequest lRequest = new LoginRequest("testUser", "password");
        LoginRequest lRequest2 = new LoginRequest("test2user", "password");
        LoginResult lResult = userServices.login(lRequest);
        LoginResult lResult2 = userServices.login(lRequest2);
        assertNotNull(lResult);
        assertNotNull(lResult2);

        //Attempt to log out first user and ensure their authData is null
        String authToken = lResult.authToken();
        LogoutRequest loutRequest = new LogoutRequest(authToken);
        userServices.logout(loutRequest);
        assertNull(mockAuthDataAccess.getAuth(authToken));

        //Ensure second user is still logged in
        assertNotNull(mockAuthDataAccess.getAuth(lResult2.authToken()));

        //Attempt a logout with a bad auth token
        LogoutRequest invalidLogoutRequest = new LogoutRequest("invalid_token");
        try {
            userServices.logout(invalidLogoutRequest);
            fail("expected error");
        } catch(BadAuthException e) {
            assertEquals("AuthData invalid!", e.getMessage());
        }
    }

    @Test
    @Order(8)
    public void testEmptyListGame() throws DataAccessException{
        //Register one user, ensure user was registered
        RegisterRequest request = new RegisterRequest("testUser", "password", "test@example.com");
        RegisterResult result = userServices.register(request);
        assertNotNull(result);

        //Attempt to list games
        String authToken = result.authToken();
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        ListGamesResult listGamesResult = gameServices.listGames(listGamesRequest);

        //Ensure result is not null
        assertNotNull(listGamesResult);

        //Ensure list of games is empty.
        assertTrue(listGamesResult.games().isEmpty());
    }

    @Test
    @Order(9)
    public void testUnauthorizedListGame() throws DataAccessException{
        //Register one user, ensure user was registered
        RegisterRequest request = new RegisterRequest("testUser", "password", "test@example.com");
        RegisterResult result = userServices.register(request);
        assertNotNull(result);

        //Attempt to list games
        String authToken = "BadAuthToken";
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        try {
            gameServices.listGames(listGamesRequest);
        } catch(BadAuthException e) {
            assertEquals("AuthData invalid!", e.getMessage());
        }
    }
    @Test
    public void testCreateGame() throws DataAccessException{
        //Register a user and confirm they were registered
        RegisterRequest request = new RegisterRequest("testUser", "password", "test@example.com");
        RegisterResult result = userServices.register(request);
        assertNotNull(result);

        //Create a game and ensure game is not null
        String authToken = result.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "testGame");
        CreateGameResult createGameResult = gameServices.createGame(createGameRequest);
        assertNotNull(createGameResult);
    }

    @Test
    @Order(10)
    public void testCreateBadGame() throws DataAccessException{
        //Register a user and confirm they were registered
        RegisterRequest request = new RegisterRequest("testUser", "password", "test@example.com");
        RegisterResult result = userServices.register(request);
        assertNotNull(result);

        //Create a game and ensure game is not null
        String authToken = result.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "testGame");
        CreateGameResult createGameResult = gameServices.createGame(createGameRequest);
        assertNotNull(createGameResult);

        //Create game with same name
        CreateGameRequest createBadGameRequest = new CreateGameRequest(authToken, "testGame");

        //Catch failure due to same name
        try {
            gameServices.createGame(createBadGameRequest);
            fail("expected error");
        } catch(NonSuccessException e) {
            assertEquals("gameName already exists!", e.getMessage());
        }
    }

    @Test
    @Order(12)
    public void testListGamesIncrease() throws DataAccessException{
        //Register one user, ensure user was registered
        RegisterRequest request = new RegisterRequest("testUser", "password", "test@example.com");
        RegisterResult result = userServices.register(request);
        assertNotNull(result);

        //Attempt to list games
        String authToken = result.authToken();
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        ListGamesResult listGamesResult = gameServices.listGames(listGamesRequest);
        //Ensure result is not null
        assertNotNull(listGamesResult);
        //Ensure list of games is empty.
        assertTrue(listGamesResult.games().isEmpty());

        //Create a game and ensure it is not null
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "testGame");
        CreateGameResult createGameResult = gameServices.createGame(createGameRequest);
        assertNotNull(createGameResult);

        //List games now that we have one, ensure not null and ensure not empty
        ListGamesRequest listGamesRequest1 = new ListGamesRequest(authToken);
        ListGamesResult listGamesResult1 = gameServices.listGames(listGamesRequest1);
        assertNotNull(listGamesResult1);
        assertFalse(listGamesResult1.games().isEmpty());

        //Create 2 more games and ensure they are not null
        CreateGameRequest createGameRequest2 = new CreateGameRequest(authToken, "testGame2");
        CreateGameResult createGameResult2 = gameServices.createGame(createGameRequest2);
        assertNotNull(createGameResult2);
        CreateGameRequest createGameRequest3 = new CreateGameRequest(authToken, "testGame3");
        CreateGameResult createGameResult3 = gameServices.createGame(createGameRequest3);
        assertNotNull(createGameResult3);

        //Test to ensure ListGames is still listing games.
        ListGamesRequest listGamesRequest2 = new ListGamesRequest(authToken);
        ListGamesResult listGamesResult2 = gameServices.listGames(listGamesRequest2);
        assertNotNull(listGamesResult2);
        assertEquals(3, listGamesResult2.games().size());
    }

    @Test
    @Order(13)
    public void testJoinGame() throws DataAccessException{
        //Register a user and confirm they were registered
        RegisterRequest request = new RegisterRequest("testUser", "password", "test@example.com");
        RegisterResult result = userServices.register(request);
        assertNotNull(result);

        //Create a game and ensure game is not null
        String authToken = result.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "testGame");
        CreateGameResult createGameResult = gameServices.createGame(createGameRequest);
        assertNotNull(createGameResult);

        //Attempt to join game on white team, ensure that joinGame is not null
        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, "WHITE", createGameResult.gameID());
        JoinGameResult joinGameResult = gameServices.joinGame(joinGameRequest);
        assertNotNull(joinGameResult);

        //Check if our game's white team player is now our username
        String username = result.username();
        GameData gameData = mockGameDataAccess.getGame(createGameResult.gameID());
        assertEquals(gameData.whiteUsername(), username);
    }

    @Test
    @Order(14)
    public void testBadJoinGame() throws DataAccessException{
        //Register a user and confirm they were registered
        RegisterRequest request = new RegisterRequest("testUser", "password", "test@example.com");
        RegisterResult result = userServices.register(request);
        assertNotNull(result);

        //Create a game and ensure game is not null
        String authToken = result.authToken();
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "testGame");
        CreateGameResult createGameResult = gameServices.createGame(createGameRequest);
        assertNotNull(createGameResult);

        //Attempt to join game on white team, ensure that joinGame is not null
        JoinGameRequest joinGameRequest = new JoinGameRequest(authToken, "WHITE", createGameResult.gameID());
        JoinGameResult joinGameResult = gameServices.joinGame(joinGameRequest);
        assertNotNull(joinGameResult);

        //Check if our game's white team player is now our username
        String username = result.username();
        GameData gameData = mockGameDataAccess.getGame(createGameResult.gameID());
        assertEquals(gameData.whiteUsername(), username);

        //Attempt to join that same game on white team again
        JoinGameRequest badJoinGameRequest = new JoinGameRequest(authToken, "WHITE", createGameResult.gameID());
        try {
            gameServices.joinGame(badJoinGameRequest);
            fail("should have thrown error");
        } catch(NonSuccessException e) {
            assertEquals("Team already taken!", e.getMessage());
        }
    }
}

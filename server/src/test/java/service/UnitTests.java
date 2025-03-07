package service;

import com.google.gson.Gson;
import dataaccess.MemoryAuthDataAccess;
import dataaccess.MemoryGameDataAccess;
import dataaccess.MemoryUserDataAccess;
import dataaccess.NonSuccessException;
import model.GameData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import request.*;
import result.*;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTests {
    static UserServices userServices;
    static GameServices gameServices;
    static MemoryUserDataAccess mockUserDataAccess;
    static MemoryAuthDataAccess mockAuthDataAccess;
    static MemoryGameDataAccess mockGameDataAccess;
    Gson serializer = new Gson();

    @BeforeAll
    public static void setUp() {
        mockUserDataAccess = new MemoryUserDataAccess();
        mockAuthDataAccess = new MemoryAuthDataAccess();
        mockGameDataAccess = new MemoryGameDataAccess();
        userServices = new UserServices(mockUserDataAccess, mockAuthDataAccess, mockGameDataAccess);
        gameServices = new GameServices(mockUserDataAccess, mockAuthDataAccess, mockGameDataAccess);
    }

    @Test
    public void testRegister() {
        //Set up 3 register requests, register first 2 users.
        RegisterRequest request = new RegisterRequest("testuser", "password", "test@example.com");
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
        } catch (NonSuccessException e) { //TODO: Replace this error with proper error
            assertEquals("Username Taken!", e.getMessage());
        }
    }

    @Test
    public void testClear() {
        //Register 2 users.
        RegisterRequest request = new RegisterRequest("testuser", "password", "test@example.com");
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
    public void testLogin() {
        //Register 2 users, ensure they were registered properly.
        RegisterRequest request = new RegisterRequest("testuser", "password", "test@example.com");
        RegisterRequest request2 = new RegisterRequest("test2user", "password", "test@example.com");
        RegisterResult result = userServices.register(request);
        RegisterResult result2 = userServices.register(request2);
        assertNotNull(result);
        assertNotNull(result2);

        //Correctly login first user, ensure they were logged in
        LoginRequest lRequest = new LoginRequest("testuser", "password");
        LoginResult lResult = userServices.login(lRequest);
        assertNotNull(lResult);

        //Incorrectly login second user, ensure their login fails.
        LoginRequest lRequest2 = new LoginRequest("test2user", "wrongPassword");
        try {
            userServices.login(lRequest2);
            fail("Expected NonSuccessException to be thrown");
        } catch (NonSuccessException e) { //TODO: Replace this error with proper error
            assertEquals("Incorrect password!", e.getMessage());
        }
    }

    @Test
    public void testLogout() {
        //Register 2 users and ensure they were registered properly.
        RegisterRequest request = new RegisterRequest("testuser", "password", "test@example.com");
        RegisterRequest request2 = new RegisterRequest("test2user", "password", "test@example.com");
        RegisterResult result = userServices.register(request);
        RegisterResult result2 = userServices.register(request2);
        assertNotNull(result);
        assertNotNull(result2);

        //Login both users properly
        LoginRequest lRequest = new LoginRequest("testuser", "password");
        LoginRequest lRequest2 = new LoginRequest("test2user", "password");
        LoginResult lResult = userServices.login(lRequest);
        LoginResult lResult2 = userServices.login(lRequest2);
        assertNotNull(lResult);
        assertNotNull(lResult2);

        //Attempt to logout first user and ensure their authData is null
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
            fail("expected error"); //TODO make better message
        } catch(NonSuccessException e) { //TODO make better error
            assertEquals("Invalid AuthData", e.getMessage());
        }
    }

    @Test
    public void testEmptyListGame() {
        //Register one user, ensure user was registered
        RegisterRequest request = new RegisterRequest("testuser", "password", "test@example.com");
        RegisterResult result = userServices.register(request);
        assertNotNull(result);

        //Attempt to list games
        String authToken = result.authToken();
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        ListGamesResult listGamesResult = gameServices.listGames(listGamesRequest);

        //Ensure result is not null
        assertNotNull(listGamesResult);

        //Ensure result confirms our success, and ensure list of games is empty.
        assertTrue(listGamesResult.success());
        assertTrue(listGamesResult.games().isEmpty());
    }

    @Test
    public void testCreateGame() {
        //Register a user and confirm they were registered
        RegisterRequest request = new RegisterRequest("testuser", "password", "test@example.com");
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
            fail("expected error"); //TODO make better message
        } catch(NonSuccessException e) { //TODO make better error
            assertEquals("gameName already exists!", e.getMessage());
        }
    }

    @Test
    public void testListGamesIncrease() {
        //Register one user, ensure user was registered
        RegisterRequest request = new RegisterRequest("testuser", "password", "test@example.com");
        RegisterResult result = userServices.register(request);
        assertNotNull(result);

        //Attempt to list games
        String authToken = result.authToken();
        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        ListGamesResult listGamesResult = gameServices.listGames(listGamesRequest);
        //Ensure result is not null
        assertNotNull(listGamesResult);
        //Ensure result confirms our success, and ensure list of games is empty.
        assertTrue(listGamesResult.success());
        assertTrue(listGamesResult.games().isEmpty());

        //Create a game and ensure it is not null
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "testGame");
        CreateGameResult createGameResult = gameServices.createGame(createGameRequest);
        assertNotNull(createGameResult);

        //List games now that we have one, ensure not null and ensure not empty
        ListGamesRequest listGamesRequest1 = new ListGamesRequest(authToken);
        ListGamesResult listGamesResult1 = gameServices.listGames(listGamesRequest1);
        assertNotNull(listGamesResult1);
        assertTrue(listGamesResult1.success());
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
        assertTrue(listGamesResult2.success());
        assertEquals(3, listGamesResult2.games().size());
    }

    @Test
    public void testJoinGame() {
        //Register a user and confirm they were registered
        RegisterRequest request = new RegisterRequest("testuser", "password", "test@example.com");
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
            fail("should have thrown error"); //TODO make better?
        } catch(NonSuccessException e) { //TODO make better error
            assertEquals("Team already taken!", e.getMessage());
        }
    }
}

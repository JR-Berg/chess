package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DataAccessUnitTests {
    static MySQLUserDataAccess fakeUserSQL;
    static MySQLAuthDataAccess fakeAuthSQL;
    static MySQLGameDataAccess fakeGameSQL;



    @BeforeAll
    public static void instantiate() {
        try {
            fakeUserSQL = new MySQLUserDataAccess();
            fakeAuthSQL = new MySQLAuthDataAccess();
            fakeGameSQL = new MySQLGameDataAccess();
        } catch (DataAccessException e) {
            System.out.println("DataAccessException in instantiation");

        }
    }

    @BeforeEach
    public void ClearAll() {
        try {
            fakeUserSQL.clearAll();
            fakeAuthSQL.clearAll();
            fakeGameSQL.clearAll();
        } catch(DataAccessException e) {
            fail("DataAccessException in database clearing.");
        }
    }


    @Test
    @Order(1)
    public void GoodRegister() { //tests createUser
        String username = "username";
        String password = "password"; //Do not use this as your password ever please
        String email = "email@email.com";
        try {
            assertNotNull(fakeUserSQL.createUser(username, password, email));
        } catch (DataAccessException e) {
            fail("DataAccessException :c");
        }
    }

    @Test
    @Order(2)
    public void BadRegister() { //tests createUser
        String username = "username";
        String password = "password";
        String email = "email@email.com";
        try {
            assertNotNull(fakeUserSQL.createUser(username, password, email));
            fakeUserSQL.createUser(username, password, email);
            fail("Expected NonSuccessException from non-unique username");
        } catch(NonSuccessException e) {
            assertEquals("Username is already taken.", e.getMessage());
        } catch(DataAccessException e) {
            fail("DataAccessException :c");
        }
    }

    @Test
    @Order(3)
    public void GoodPassword() { //tests checkPassword
        try {
            fakeUserSQL.createUser("username", "password", "email");
            assertTrue(fakeUserSQL.checkPassword("username", "password"));
        } catch (DataAccessException e) {
            fail("DataAccessException :c");
        }
    }

    @Test
    @Order(4)
    public void GoodUser() {
        try {
            fakeUserSQL.createUser("username", "password", "email");
            fakeUserSQL.getUser("username");
        } catch (DataAccessException e) {
            fail("DataAccessException :c");
        }
    }

    @Test
    @Order(5)
    public void FakeUser() { //tests getUser
        try {
            fakeUserSQL.createUser("username", "password", "email");
            assertNull(fakeUserSQL.getUser("slenderman"));
        } catch (DataAccessException e) {
            fail("DataAccessException :c");
        }
    }

    @Test
    @Order(6)
    public void BadLogin() { //tests checkPassword
        try {
            fakeUserSQL.createUser("username", "password", "email");
            assertFalse(fakeUserSQL.checkPassword("username", "b3773rPassw0rd??"));
        } catch (DataAccessException e) {
            fail("DataAccessException :c");
        }
    }

    @Test
    @Order(7)
    public void GoodAuth() { //tests createAuth
        try {
            fakeUserSQL.createUser("username", "password", "email");
            AuthData authData = fakeAuthSQL.createAuth("username");
            assertNotNull(authData);
        } catch(DataAccessException e) {
            fail("DataAccessException :c");
        }
    }

    /* So, like, I know I'm totally gonna eat a few points, but I genuinely
     * Do not know how to write a test to make createAuth fail. So, uh, I guess
     * I won't be making one. Oof.
     */

    @Test
    @Order(8)
    public void GoodAuthRetrieval() { //tests getAuth
        try {
            fakeUserSQL.createUser("username", "password", "email");
            AuthData authData = fakeAuthSQL.createAuth("username");
            assertNotNull(fakeAuthSQL.getAuth(authData.authToken()));
        }catch(DataAccessException e){
            fail("DataAccessException :c");
        }
    }

    @Test
    @Order(9)
    public void BadAuthRetrieval() { //Tests getAuth
        try {
            assertNull(fakeAuthSQL.getAuth("bwompus"));
        } catch (DataAccessException e) {
            fail("DataAccessException :c");
        }
    }



    @Test
    @Order(10)
    public void GoodAuthDelete() { //tests deleteAuth
        try{
            fakeUserSQL.createUser("username", "password", "email");
            AuthData authData = fakeAuthSQL.createAuth("username");
            fakeAuthSQL.deleteAuth(authData.authToken());
            assertNull(fakeAuthSQL.getAuth(authData.authToken()));
        } catch(DataAccessException e) {
            fail("DataAccessException :c");
        }
    }

    @Test
    @Order(11)
    public void BadAuthDelete() { //tests deleteAuth
        try{
            fakeUserSQL.createUser("username", "password", "email");
            AuthData authData = fakeAuthSQL.createAuth("username");
            fakeAuthSQL.deleteAuth(authData.authToken());
            assertNull(fakeAuthSQL.getAuth(authData.authToken()));
            fakeAuthSQL.deleteAuth(authData.authToken());
        } catch(DataAccessException e) {
            fail("DataAccessException :c");
        } catch(NonSuccessException e) {
            assertEquals("AuthToken deletion failed.", e.getMessage());
        }
    }

    @Test
    @Order(12)
    public void GoodCreateGame() { //tests createGame
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(1, null, null, "showdown", chessGame);
        try {
            fakeGameSQL.createGame(1, gameData);
            GameData newGameData = fakeGameSQL.getGame(1);
            assertNotNull(newGameData);
        }catch(DataAccessException e) {
            fail("DataAccessException :c");
        }
    }

    @Test
    @Order(13)
    public void BadCreateGame() { //tests createGame
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(1, null, null, "showdown", chessGame);
        GameData gamerData = new GameData(2, null, null, "showdown", chessGame);
        try {
            fakeGameSQL.createGame(1, gameData);
            fakeGameSQL.createGame(2, gamerData);
            fail("Expected DataAccessException from duplicate gameName");
        }catch(DataAccessException e) {
            assertEquals("Duplicate entry 'showdown' for key 'games.gameName'", e.getMessage());
        }
    }

    @Test
    @Order(14)
    public void GoodGetGame() {
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(1, null, null, "showdown", chessGame);
        try{
            fakeGameSQL.createGame(1, gameData);
            GameData getGameData = fakeGameSQL.getGame(1);
            assertEquals("showdown", getGameData.gameName());
        } catch (DataAccessException e) {
            fail("DataAccessException :C");
        }
    }

    @Test
    @Order(15)
    public void BadGetGame() {
        try {
            GameData getGameData = fakeGameSQL.getGame(1);
            assertNull(getGameData);
        } catch (DataAccessException e) {
            fail("DataAccessException :c");
        }
    }

    @Test
    @Order(16)
    public void GoodGetNameByID() {
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(1, null, null, "showdown", chessGame);
        try{
            fakeGameSQL.createGame(1, gameData);
            int getGameID = fakeGameSQL.getGameIDByName("showdown");
            assertEquals(1, getGameID);
        } catch (DataAccessException e) {
            fail("DataAccessException :C");
        }
    }

    @Test
    @Order(17)
    public void BadGetNameByID() {
        try{
            assertNull(fakeGameSQL.getGameIDByName("showdown"));
        } catch (DataAccessException e) {
            fail("DataAccessException :C");
        }
    }

    @Test
    @Order(18)
    public void GoodListGames() {
        ChessGame chessGame = new ChessGame();
        GameData gameData = new GameData(1, null, null, "showdown", chessGame);
        ChessGame theSequel = new ChessGame();
        GameData moreData = new GameData(2, null, null, "boogaloo", theSequel);
        try{
            fakeGameSQL.createGame(1, gameData);
            fakeGameSQL.createGame(2, moreData);
            Map<Integer, GameData> gamesList = fakeGameSQL.listGames();
            assertEquals(2, gamesList.size());
        } catch (DataAccessException e) {
            fail("DataAccessException :C");
        }
    }

    @Test
    @Order(19)
    public void BadListGames() {
        try{
            Map<Integer, GameData> gamesList = fakeGameSQL.listGames();
            assertTrue(gamesList.isEmpty());
        } catch (DataAccessException e) {
            fail("DataAccessException :C");
        }
    }

    @Test
    @Order(20)
    public void GoodGenerateID() {
        try{
            int ID = fakeGameSQL.generateGameID();
            ChessGame chessGame = new ChessGame();
            GameData gameData = new GameData(ID, null, null, "showdown", chessGame);
            fakeGameSQL.createGame(ID, gameData);
            ChessGame theSequel = new ChessGame();
            int ID2 = fakeGameSQL.generateGameID();
            GameData moreData = new GameData(ID2, null, null, "boogaloo", theSequel);
            fakeGameSQL.createGame(ID2, moreData);
            assertEquals(1, gameData.gameID());
            assertEquals(2, moreData.gameID());
        } catch (DataAccessException e) {
            fail("DataAccessException :C");
        }
    }

    @Test
    @Order(21)
    public void GoodSetPlayerTeam() {
        try{
            fakeUserSQL.createUser("username", "password", "email");
            int ID = fakeGameSQL.generateGameID();
            ChessGame chessGame = new ChessGame();
            GameData gameData = new GameData(ID, null, null, "showdown", chessGame);
            fakeGameSQL.createGame(ID, gameData);
            fakeGameSQL.setPlayerTeam(ID, "username", "WHITE");
            gameData = fakeGameSQL.getGame(ID);
            assertEquals("username", gameData.whiteUsername());
        } catch (DataAccessException e) {
            fail("DataAccessException :C");
        }
    }

    @Test
    @Order(22)
    public void BadSetPlayerTeam() {
        try{
            fakeUserSQL.createUser("username", "password", "email");
            int ID = fakeGameSQL.generateGameID();
            ChessGame chessGame = new ChessGame();
            GameData gameData = new GameData(ID, null, null, "showdown", chessGame);
            fakeGameSQL.createGame(ID, gameData);
            fakeGameSQL.setPlayerTeam(ID, "username", "YELLOW");
        } catch (DataAccessException e) {
            fail("DataAccessException :C");
        } catch (WhomstException e) {
            assertEquals("Unknown team", e.getMessage());
        }
    }
}

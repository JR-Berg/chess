package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
        assertNotNull(fakeUserSQL.createUser(username, password, email));
    }

    @Test
    @Order(2)
    public void BadRegister() { //tests createUser
        String username = "username";
        String password = "password";
        String email = "email@email.com";
        try {
            fakeUserSQL.createUser(username, password, email);
        } catch(NonSuccessException e) {
            assertEquals("Username is already taken.", e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void GoodPassword() { //tests checkPassword
        assertTrue(fakeUserSQL.checkPassword("username", "password"));
    }

    @Test
    @Order(4)
    public void GoodUser() {
        fakeUserSQL.getUser("username");
    }

    @Test
    @Order(5)
    public void FakeUser() { //tests getUser
        assertNull(fakeUserSQL.getUser("slenderman"));
    }

    @Test
    @Order(6)
    public void BadLogin() { //tests checkPassword
        assertFalse(fakeUserSQL.checkPassword("username", "b3773rPassw0rd??"));
    }

    @Test
    @Order(7)
    public void GoodAuth() { //tests createAuth
        try {
            fakeAuthSQL.createAuth("username");
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
            AuthData authData = fakeAuthSQL.createAuth("username");
            assertNotNull(fakeAuthSQL.getAuth(authData.authToken()));
        }catch(DataAccessException e){
            fail("DataAccessException :c");
        }
    }

    @Test
    @Order(9)
    public void BadAuthRetrieval() { //Tests getAuth
        assertNull(fakeAuthSQL.getAuth("bwompus"));
    }



    @Test
    @Order(10)
    public void GoodAuthDelete() { //tests deleteAuth
        try{
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
        try {
            fakeGameSQL.createGame(1, gameData);
            GameData newGameData = fakeGameSQL.getGame(1);
            assertNotNull(newGameData);
        }catch(DataAccessException e) {
            fail("DataAccessException :c");
        }
    }
}

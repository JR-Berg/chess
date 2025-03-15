package dataaccess;

import model.AuthData;
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
        fakeUserSQL.createUser(username, password, email);
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
        fakeUserSQL.checkPassword("username", "password");
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

    @Test
    @Order(8)
    public void BadAuth() { //tests CreateAuth
        try {
            assertNull(fakeAuthSQL.createAuth("Chuck Norris"));
        }catch(DataAccessException e) {
            fail("DataAccessException :c");
        }
    }


}

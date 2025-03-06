package service;

import com.google.gson.Gson;
import dataaccess.MemoryAuthDataAccess;
import dataaccess.MemoryGameDataAccess;
import dataaccess.MemoryUserDataAccess;
import dataaccess.NonSuccessException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class UnitTests {
    static UserServices userServices;
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
    }

    @Test
    public void testRegister() {
        RegisterRequest request = new RegisterRequest("testuser", "password", "test@example.com");
        RegisterRequest request2 = new RegisterRequest("test2user", "password", "test@example.com");
        RegisterRequest request3 = new RegisterRequest("test2user", "password", "test@example.com");
        RegisterResult result = userServices.register(request);
        RegisterResult result2 = userServices.register(request2);

        assertNotNull(result);
        assertNotNull(result2);
        try {
            userServices.register(request3);
            fail("Expected NonSuccessException to be thrown");
        } catch (NonSuccessException e) { //TODO: Replace this error with proper error
            assertEquals("Username Taken!", e.getMessage());
        }
    }

    @Test
    public void testClear() {
        RegisterRequest request = new RegisterRequest("testuser", "password", "test@example.com");
        RegisterRequest request2 = new RegisterRequest("test2user", "password", "test@example.com");
        RegisterResult result = userServices.register(request);
        RegisterResult result2 = userServices.register(request2);

        assertNotNull(result);
        assertNotNull(result2);

        userServices.clearAll();


        RegisterRequest request3 = new RegisterRequest("test2user", "password", "test@example.com");
        RegisterResult result3 = userServices.register(request3);

        assertNotNull(result3);

    }

    @Test
    public void testLogin() {
        RegisterRequest request = new RegisterRequest("testuser", "password", "test@example.com");
        RegisterRequest request2 = new RegisterRequest("test2user", "password", "test@example.com");
        RegisterResult result = userServices.register(request);
        RegisterResult result2 = userServices.register(request2);

        assertNotNull(result);
        assertNotNull(result2);

        LoginRequest lRequest = new LoginRequest("testuser", "password");
        LoginRequest lRequest2 = new LoginRequest("test2user", "wrongPassword");
        LoginResult lResult = userServices.login(lRequest);

        assertNotNull(lResult);

        try {
            userServices.login(lRequest2);
            fail("Expected NonSuccessException to be thrown");
        } catch (NonSuccessException e) { //TODO: Replace this error with proper error
            assertEquals("Incorrect password!", e.getMessage());
        }
    }

    @Test
    public void testLogout() {
        RegisterRequest request = new RegisterRequest("testuser", "password", "test@example.com");
        RegisterRequest request2 = new RegisterRequest("test2user", "password", "test@example.com");
        RegisterResult result = userServices.register(request);
        RegisterResult result2 = userServices.register(request2);

        assertNotNull(result);
        assertNotNull(result2);

        LoginRequest lRequest = new LoginRequest("testuser", "password");
        LoginRequest lRequest2 = new LoginRequest("test2user", "password");
        LoginResult lResult = userServices.login(lRequest);
        LoginResult lResult2 = userServices.login(lRequest2);

        assertNotNull(lResult);
        assertNotNull(lResult2);

        String authToken = lResult.authToken();
        LogoutRequest loutRequest = new LogoutRequest(authToken);
        LogoutResult loutResult = userServices.logout(loutRequest);

        assertNotNull(loutResult);
    }
}

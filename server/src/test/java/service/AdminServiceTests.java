package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class AdminServiceTests {
    private final DataAccess dataAccess = new MemoryDataAccess();
    AdminService service = new AdminService();

//    @BeforeAll
//    static void createDatabase() {
//        dataAccess = new MemoryDataAccess();
//    }
//    // @BeforeEach
//
//    // @AftereEach
//
//
//    @AfterAll
//    static void deleteDatabase() {
//
//    }

    @Test
    void clear() {
        // adding a user to the database directly
        UserData user = new UserData("Paige", "LadyPhilclagen@gmail.com", "password1234:)");
        AuthData auth = new AuthData(user.username(), "1234");
        dataAccess.createUser(user, auth);
        dataAccess.addAuthData(auth);

        // making sure the user and authtoken are not in the database
        service.clear();
        assertNull(dataAccess.getUser(user.username()));
        assertNull(dataAccess.getAuthData(auth.authToken()));
    }

}

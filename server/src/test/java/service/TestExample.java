package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestExample {
//    private String text;

//    @BeforeAll
//    static void createDatabase() {
//
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
//    public TestExample(String base) {this.text = base;}

    @Test
    void register() throws AlreadyTakenException {
        var dataAccess = new MemoryDataAccess();
        UserService userService = new UserService();

        // adding two users, and making sure they are in the database
        UserData user = new UserData("paige", "LadyPhilclagen@gmail.com", "password1234:)");
        userService.registerUser(user);
        UserData user2 = new UserData("vivian", "VarclinPeever@gmail.com", "password2468**");
        userService.registerUser(user2);
        assertEquals(dataAccess.getUser(user.username()), user);
        assertEquals(dataAccess.getUser(user2.username()), user2);

        // trying to add a user that is already added
        assertThrows(AlreadyTakenException.class, () -> {
            userService.registerUser(new UserData("vivian", "VarclinPeever@gmail.com", "password2468**"));
        });
    }

    @Test
    void clear() {
        var dataAccess = new MemoryDataAccess();
        AdminService service = new AdminService();
        // adding a user to the database directly
        UserData user = new UserData("paige", "LadyPhilclagen@gmail.com", "password1234:)");
        AuthData auth = new AuthData(user.username(), "1234");
        dataAccess.createUser(user, auth);

        // making sure the user and authtoken are not in the database
        service.clear();
        assertNull(dataAccess.getUser(user.username()));
        assertNull(dataAccess.getAuthData(auth.token()));
    }

}

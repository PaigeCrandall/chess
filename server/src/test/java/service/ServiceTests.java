package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
//    private DataAccess text;
//
//    @BeforeAll
//    static void createDatabase() {
//        var dataAccess = new MemoryDataAccess();
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
    void register() throws Exception {
        var dataAccess = new MemoryDataAccess();
        UserService userService = new UserService();

        // adding two users, and making sure they are in the database
        UserData user = new UserData("Monroe", "LadyPhilclagen@gmail.com", "password1234:)");
        userService.registerUser(user);
        UserData user2 = new UserData("Vivian", "VarclinPeever@gmail.com", "password2468**");
        userService.registerUser(user2);
        assertEquals(dataAccess.getUser(user.username()), user);
        assertEquals(dataAccess.getUser(user2.username()), user2);

        // trying to add a user that is already added
        assertThrows(AlreadyTakenException.class, () -> {
            userService.registerUser(new UserData("Vivian", "VarclinPeever@gmail.com", "password2468**"));
        });

        assertThrows(BadRequestException.class, () -> {
            userService.registerUser(new UserData(null, "VarclinPeever@gmail.com", "password2468**"));
        });
    }

    @Test
    void clear() {
        var dataAccess = new MemoryDataAccess();
        AdminService service = new AdminService();
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

    @Test
    void logout() throws Exception {
        var dataAccess = new MemoryDataAccess();
        UserService userService = new UserService();

        // create a new user
        UserData user = new UserData("Gracie", "LadyPhilclagen@gmail.com", "password1234:)");
        String token = userService.createAuthToken();
        AuthData auth = new AuthData(user.username(), token);
        dataAccess.createUser(user, auth);
        dataAccess.addAuthData(auth);
        assertNotNull(dataAccess.getAuthData(token));

        // log the user out
        userService.logout(token);
        assertNull(dataAccess.getAuthData(token));

        // unauthorized user tries to log out ?
        assertThrows(UnauthorizedException.class, () -> {
            userService.logout(token);
        });

    }

}

package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    private final DataAccess dataAccess = new MemoryDataAccess();
    private final UserService userService = new UserService();

    UserData validRegisterUser(String username) {
        UserData user = new UserData(username, "email.address@gmail.com", "password1234:)");
        try {
            userService.registerUser(user);
            return user;
        }
        catch(Exception e) {
            System.out.println("Couldn't add user");
            return null;
        }
    }

    @Test
    void register() throws AlreadyTakenException, BadRequestException {
        // adding two users, and making sure they are in the database
        UserData user1 = validRegisterUser("Monroe");
        UserData user2 = validRegisterUser("Vivian");
        assertEquals(dataAccess.getUser(user1.username()), user1);
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
    void logout() throws UnauthorizedException {
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

        // unauthorized user tries to log out
        // not sure when that could happen, but we're going with it
        assertThrows(UnauthorizedException.class, () -> {
            userService.logout(token);
        });
    }

    @Test
    void login() throws Exception {
        // create a new user
        UserData user = new UserData("Gracie", "LadiSacraneli@gmail.com", "greatness1357*");
        String token = userService.createAuthToken();
        AuthData auth = new AuthData(user.username(), token);
        dataAccess.createUser(user, auth);
        dataAccess.addAuthData(auth);
        assertNotNull(dataAccess.getUser(user.username()));
        userService.logout(token);


        // normal login
        LoginResult result = userService.login(new LoginRequest(user.username(), user.password()));
        assertEquals(result.authToken(), dataAccess.getAuthData(result.authToken()).authToken());

        // user does not exist
        assertThrows(UnauthorizedException.class, () -> {
            userService.login(new LoginRequest("Bobby", "bobbyBike123*"));
        });

        // user password doesn't match
        assertThrows(UnauthorizedException.class, () -> {
            userService.login(new LoginRequest(user.username(), "bobbyBike123*"));
        });

        // bad request
        assertThrows(BadRequestException.class, () -> {
            userService.login(new LoginRequest("Fatima", null));
        });

    }
}

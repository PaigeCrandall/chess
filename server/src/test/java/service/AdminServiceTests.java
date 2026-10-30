package service;

import dataaccess.*;
import model.*;
import org.eclipse.jetty.security.authentication.AuthorizationService;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class AdminServiceTests {
    private static AdminService service;
    private static DataAccess dataAccess;

    @BeforeAll
    static void setUp() throws DataAccessException {
        dataAccess = new SqlDataAccess();
        service = new AdminService();
    }

    @Test
    void clear() throws DataAccessException {
        // adding a user to the database directly
        UserData user = new UserData("Paige", "LadyPhilclagen@gmail.com", "password1234:)");
        AuthData auth = new AuthData(user.username(), new UserService().createAuthToken());
        dataAccess.createUser(user, auth);
        dataAccess.addAuthData(auth);

        // making sure the user and authtoken are not in the database
        service.clear();
        assertNull(dataAccess.getUser(user.username()));
        assertNull(dataAccess.getAuthData(auth.authToken()));
    }

}

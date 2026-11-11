import client.ChessClient;
import client.ServerFacade;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.UnauthorizedException;
import model.AuthData;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {

    private static Server server;
    public static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }
//
    @BeforeEach
    public void clearDatabase() throws Exception {
        facade.clear();
    }


    @Test
    public void register() throws Exception {
        AuthData auth = facade.register("Paige", "palm@byu.edu", "1234:)");
        Assertions.assertNotNull(auth);

        // already exists
        assertThrows(AlreadyTakenException.class, () -> facade.register("Paige", "palm@byu.edu", "1234:)"));

        // bad request
        assertThrows(BadRequestException.class, () -> facade.register("", "palm@byu.edu", ""));
    }

    @Test
    public void loginOut() throws Exception {
        AuthData auth = facade.register("Paige", "palm@byu.edu", "1234:)");
        Assertions.assertNotNull(auth);

        facade.logout(auth.authToken());
        assertThrows(UnauthorizedException.class, () -> facade.logout(auth.authToken()));

        AuthData auth2 = facade.login("Paige", "1234:)");
        Assertions.assertNotNull(auth2);
    }

}

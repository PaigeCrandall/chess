package client;

import exceptions.*;
import model.AuthData;
import model.results.ListGamesResult;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


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

    @BeforeEach
    public void clearDatabase() throws Exception {
        facade.clear();
    }


    @Test
    public void register() throws Exception {
        AuthData auth = facade.register("Paige", "palm@byu.edu", "1234:)");
        assertNotNull(auth);

        // already exists
        assertThrows(AlreadyTakenException.class, () -> facade.register("Paige", "palm@byu.edu", "1234:)"));

        // bad request
        assertThrows(BadRequestException.class, () -> facade.register("", "palm@byu.edu", ""));
    }

    @Test
    public void loginOut() throws Exception {
        AuthData auth = facade.register("Paige", "palm@byu.edu", "1234:)");
        assertNotNull(auth);

        facade.logout(auth.authToken());
        assertThrows(UnauthorizedException.class, () -> facade.logout(auth.authToken()));

        AuthData auth2 = facade.login("Paige", "1234:)");
        assertNotNull(auth2);
    }

    @Test
    public void clear() throws Exception {
        AuthData auth = facade.register("Paige", "palm@byu.edu", "1234:)");
        facade.logout(auth.authToken());
        facade.clear();

        assertThrows(UnauthorizedException.class, () -> facade.login("Paige", "1234:)"));
    }

    @Test
    public void createAndListGames() throws Exception {
        assertThrows(UnauthorizedException.class, () -> facade.listGames("1234"));
        assertThrows(UnauthorizedException.class, () -> facade.createGame("HackersGame", "1234"));
        String auth = facade.register("Paige", "palm@byu.edu", "1234:)").authToken();

        // getting a list before games are created
        ListGamesResult list = facade.listGames(auth);
        assertEquals(0, list.games().size());

        // trying to create a bad game
        assertThrows(BadRequestException.class, () -> facade.createGame("", auth));

        // creating 3 good games
        facade.createGame("Awesome Game", auth).gameID();
        int gameId1 = facade.createGame("Awesome-er Game", auth).gameID();
        assertTrue(gameId1>0);
        facade.createGame("Owesome Gam (THE most superior)", auth);
        ListGamesResult list2 = facade.listGames(auth);
        assertEquals(3, list2.games().size());
    }


    @Test
    public void joinGame() throws Exception {
        assertThrows(UnauthorizedException.class, () -> facade.joinGame(0, "WHITE", "1234"));
        String auth = facade.register("Paige", "palm@byu.edu", "1234:)").authToken();

        // successfully join a game
        int gameID = facade.createGame("Awesome Game", auth).gameID();
        facade.joinGame(gameID, "WHITE", auth);

        // trying to join a game that doesn't exist
        assertThrows(DataAccessException.class, () -> facade.joinGame(90, "BLACK", auth));
    }

}

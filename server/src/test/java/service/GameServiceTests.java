package service;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.SqlDataAccess;
import model.*;
import model.requests.*;
import model.results.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.exceptions.AlreadyTakenException;
import service.exceptions.BadRequestException;
import service.exceptions.UnauthorizedException;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
//    private final DataAccess dataAccess = new MemoryDataAccess();
    private static DataAccess dataAccess;
    private static UserService userService;
    private static GameService gameService;

    @BeforeAll
    static void setUp() throws DataAccessException {
        dataAccess = new SqlDataAccess();
        gameService = new GameService();
        userService = new UserService();
    }


    public String addUserAndReturnAuthToken(String name) throws DataAccessException {
        UserData user = new UserData(name, "awesome@gmail.com", "passcode6:)");
        String token = userService.createAuthToken();
        AuthData auth = new AuthData(user.username(), token);
        dataAccess.createUser(user, auth);
        dataAccess.addAuthData(auth);
        assertNotNull(dataAccess.getAuthData(token));
        return token;
    }

    public void addThreeValidGames(String auth) throws Exception {
        CreateGameResult response = gameService.createGame(new CreateGameRequest("chess-mania"), auth);
        assertNotNull(dataAccess.getGame(response.gameID()));
        CreateGameResult response2 = gameService.createGame(new CreateGameRequest("chess-aria"), auth);
        assertNotNull(dataAccess.getGame(response2.gameID()));
        CreateGameResult response3 = gameService.createGame(new CreateGameRequest("chess-capita"), auth);
        assertNotNull(dataAccess.getGame(response3.gameID()));
    }

    @Test
    public void createGame() throws Exception {

        // set up an authorized user who can add the game
        String token = addUserAndReturnAuthToken("Jacob");

        // add three valid games
        addThreeValidGames(token);

        // invalid request
        assertThrows(BadRequestException.class, () -> gameService.createGame(new CreateGameRequest(null), token));

        // unauthorized user tries to make one
        userService.logout(token);
        assertThrows(UnauthorizedException.class, () -> gameService.createGame(new CreateGameRequest("chess-no-calla"), token));
        dataAccess.clear();
    }

    @Test
    public void listGames() throws Exception  {
        String token = addUserAndReturnAuthToken("Ali");
        addThreeValidGames(token);

        ListGamesResult listResult = gameService.listGames(token);
        assertEquals(3, listResult.games().size());

        var serializer = new Gson();
        GameService gameService = new GameService();
        ListGamesResult result = gameService.listGames(token);
        System.out.println(serializer.toJson(result));

        // unauthorized user
        userService.logout(token);
        assertThrows(UnauthorizedException.class, () -> gameService.listGames(token) );
        dataAccess.clear();
    }

    @Test
    public void joinGame() throws Exception  {
        String token = addUserAndReturnAuthToken("Lenae");
        addThreeValidGames(token);

        // try joining validly
        gameService.joinGame(token, new JoinGameRequest("WHITE", 1));
        gameService.joinGame(token, new JoinGameRequest("BLACK", 3));
        assertEquals("Lenae", dataAccess.getGame(1).whiteUsername());
        assertEquals("Lenae", dataAccess.getGame(3).blackUsername());


        // try joining invalidly
        assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(token, new JoinGameRequest("WHITE", 1)));

        assertThrows(BadRequestException.class, () -> gameService.joinGame(token, new JoinGameRequest("BLUE", 1)));
        assertThrows(BadRequestException.class, () -> gameService.joinGame(token, new JoinGameRequest("BLACK", null)));

        userService.logout(token);
        assertThrows(UnauthorizedException.class, () -> gameService.joinGame(token, new JoinGameRequest("BLACK", 2)));
        dataAccess.clear();
    }

}

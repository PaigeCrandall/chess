package service;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.*;
import model.requests.*;
import model.results.*;
import org.junit.jupiter.api.Test;
import service.exceptions.BadRequestException;
import service.exceptions.UnauthorizedException;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    private final DataAccess dataAccess = new MemoryDataAccess();
    private final GameService gameService = new GameService();
    private final UserService userService = new UserService();

    @Test
    public void createGame() throws Exception {

        // set up an authorized user who can add the game
        UserData user = new UserData("Jacob", "jwc@gmail.com", "passcode6:)");
        String token = userService.createAuthToken();
        AuthData auth = new AuthData(user.username(), token);
        dataAccess.createUser(user, auth);
        dataAccess.addAuthData(auth);
        assertNotNull(dataAccess.getAuthData(token));


        // add three valid games
        CreateGameResult response = gameService.createGame(new CreateGameRequest("chess-mania"), token);
        assertNotNull(dataAccess.getGame(response.gameID()));

        CreateGameResult response2 = gameService.createGame(new CreateGameRequest("chess-aria"), token);
        assertNotNull(dataAccess.getGame(response2.gameID()));
        CreateGameResult response3 = gameService.createGame(new CreateGameRequest("chess-capita"), token);
        assertNotNull(dataAccess.getGame(response3.gameID()));

        // invalid request
        assertThrows(BadRequestException.class, () -> {
            gameService.createGame(new CreateGameRequest(null), token);
        });

        // unauthorized user tries to make one
        userService.logout(token);
        assertThrows(UnauthorizedException.class, () -> {
            gameService.createGame(new CreateGameRequest("chess-no-calla"), token);
        });

        System.out.println("Games Added:");
        dataAccess.printGames();

    }

    @Test
    public void listGames() throws Exception  {
        // set up an authorized user who can add the game
        UserData user = new UserData("Ali", "awesome@gmail.com", "passcode7:)");
        String token = userService.createAuthToken();
        AuthData auth = new AuthData(user.username(), token);
        dataAccess.createUser(user, auth);
        dataAccess.addAuthData(auth);
        assertNotNull(dataAccess.getAuthData(token));

        // add three valid games
        CreateGameResult response = gameService.createGame(new CreateGameRequest("chess-mania"), token);
        assertNotNull(dataAccess.getGame(response.gameID()));

        CreateGameResult response2 = gameService.createGame(new CreateGameRequest("chess-aria"), token);
        assertNotNull(dataAccess.getGame(response2.gameID()));
        CreateGameResult response3 = gameService.createGame(new CreateGameRequest("chess-capita"), token);
        assertNotNull(dataAccess.getGame(response3.gameID()));

        ListGamesResult result = gameService.listGames(token);
        assertEquals(3, result.games().size());
        System.out.println(result);

        // unauthorized user
        userService.logout(token);
        assertThrows(UnauthorizedException.class, () -> {
            gameService.listGames(token);
        });
    }
}

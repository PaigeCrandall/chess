package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.*;
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


        // add two valid games
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
}

package service;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.*;
import model.requests.*;
import model.results.*;
import org.junit.jupiter.api.Test;
import service.exceptions.AlreadyTakenException;
import service.exceptions.BadRequestException;
import service.exceptions.UnauthorizedException;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    private final DataAccess dataAccess = new MemoryDataAccess();
    private final GameService gameService = new GameService();
    private final UserService userService = new UserService();

    public String addUserAndReturnAuthToken(String name) {
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
        assertThrows(BadRequestException.class, () -> {
            gameService.createGame(new CreateGameRequest(null), token);
        });

        // unauthorized user tries to make one
        userService.logout(token);
        assertThrows(UnauthorizedException.class, () -> {
            gameService.createGame(new CreateGameRequest("chess-no-calla"), token);
        });
    }

    @Test
    public void listGames() throws Exception  {
        String token = addUserAndReturnAuthToken("Ali");
        addThreeValidGames(token);

        ListGamesResult result = gameService.listGames(token);
        assertEquals(3, result.games().size());

//        System.out.println(result);

        // unauthorized user
        userService.logout(token);
        assertThrows(UnauthorizedException.class, () -> {
            gameService.listGames(token);
        });
    }

    @Test
    public void joinGame() throws Exception  {
        String token = addUserAndReturnAuthToken("Lenae");
        addThreeValidGames(token);

        // try joining validly
        gameService.joinGame(token, new JoinGameRequest("WHITE", 1));
        gameService.joinGame(token, new JoinGameRequest("BLACK", 3));

        // try joining invalidly
        assertThrows(AlreadyTakenException.class, () -> {
            gameService.joinGame(token, new JoinGameRequest("WHITE", 1));
        });

        assertThrows(BadRequestException.class, () -> {
            gameService.joinGame(token, new JoinGameRequest("BLUE", 1));
        });
        assertThrows(BadRequestException.class, () -> {
            gameService.joinGame(token, new JoinGameRequest("BLACK", null));
        });

        userService.logout(token);
        assertThrows(UnauthorizedException.class, () -> {
            gameService.joinGame(token, new JoinGameRequest("BLACK", 2));
        });
    }
}

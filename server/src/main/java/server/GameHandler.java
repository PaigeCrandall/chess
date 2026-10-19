package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.*;
import org.jetbrains.annotations.NotNull;
import service.*;
import service.exceptions.BadRequestException;
import service.exceptions.UnauthorizedException;

import java.util.Map;

public class GameHandler {

    public static void createGame(@NotNull Context ctx) {
        var serializer = new Gson();

        try {
            String jsonBody = ctx.body();
            String authToken = ctx.header("authorization");
            CreateGameRequest request = serializer.fromJson(jsonBody, CreateGameRequest.class);
            GameService gameService = new GameService();
            CreateGameResult createGameResult = gameService.createGame(request, authToken);

            ctx.status(200);
            ctx.result(serializer.toJson(createGameResult));
            ctx.contentType("application/json");
        }
        catch(UnauthorizedException e) {
            ctx.status(401);
            Map<String,String> r = Map.of("message", e.getMessage());
            ctx.result(serializer.toJson(r));
            ctx.contentType("application/json");
        }
        catch(BadRequestException e) {
            ctx.status(400);
            Map<String,String> r = Map.of("message", e.getMessage());
            ctx.result(serializer.toJson(r));
            ctx.contentType("application/json");
        }
    }
}

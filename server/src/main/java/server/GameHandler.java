package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.requests.*;
import model.results.*;
import org.jetbrains.annotations.NotNull;
import service.*;
import service.exceptions.*;

import java.util.Map;

public class GameHandler extends Handler {

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
        catch(Exception e) { errorHandling(ctx, e); }
    }

    public static void getAllGames(@NotNull Context ctx) {
        var serializer = new Gson();
        try {
            GameService gameService = new GameService();

            String authToken = ctx.header("authorization");
            ListGamesResult listResult = gameService.listGames(authToken);

            ctx.status(200);
            ctx.result(serializer.toJson(listResult));
            ctx.contentType("application/json");
        }
        catch(Exception e) { errorHandling(ctx, e); }
    }

    public static void joinGame(@NotNull Context ctx) {
        var serializer = new Gson();
        try {
            GameService gameService = new GameService();

            String jsonBody = ctx.body();
            String authToken = ctx.header("authorization");
            JoinGameRequest request = serializer.fromJson(jsonBody, JoinGameRequest.class);
            gameService.joinGame(authToken, request);
            var r = Map.of();
            ctx.status(200);
            ctx.result(serializer.toJson(r));
            ctx.contentType("application/json");
        }
        catch(Exception e) { errorHandling(ctx, e); }
    }
}

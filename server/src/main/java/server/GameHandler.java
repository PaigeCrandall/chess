package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import model.requests.*;
import model.results.*;
import service.*;

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
            sendGoodResult(ctx, serializer.toJson(createGameResult));
        }
        catch(Exception e) { errorHandling(ctx, e); }
    }

    public static void getAllGames(@NotNull Context ctx) {
        var serializer = new Gson();
        try {
            GameService gameService = new GameService();
            String authToken = ctx.header("authorization");
            ListGamesResult listResult = gameService.listGames(authToken);
            System.out.println("Handler call");
            System.out.println(serializer.toJson(listResult));
            System.out.println("length: " + listResult.games().size());
            sendGoodResult(ctx, serializer.toJson(listResult));
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
            sendGoodResult(ctx, serializer.toJson(r));
        }
        catch(Exception e) { errorHandling(ctx, e); }
    }
}

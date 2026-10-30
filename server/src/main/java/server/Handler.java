package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import service.exceptions.*;

import java.util.Map;

public class Handler {

    public static void errorHandling(@NotNull Context ctx, Exception e) {
        switch (e) {
            case UnauthorizedException _ -> ctx.status(401);
            case BadRequestException _ -> ctx.status(400);
            case AlreadyTakenException _ -> ctx.status(403);
            case DataAccessException _ -> ctx.status(500);
            case null, default -> ctx.status(500);
        }
        var serializer = new Gson();
        Map<String,String> r = Map.of("message", (e != null ? e.getMessage() : "Error: server did not execute correctly."));
        ctx.result(serializer.toJson(r));
        ctx.contentType("application/json");
    }

    public static void sendGoodResult(@NotNull Context ctx, String jsonResult) {
        ctx.status(200);
        ctx.result(jsonResult);
        ctx.contentType("application/json");
    }

}

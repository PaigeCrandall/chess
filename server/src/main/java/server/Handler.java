package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import service.exceptions.AlreadyTakenException;
import service.exceptions.BadRequestException;
import service.exceptions.UnauthorizedException;

import java.util.Map;

public class Handler {

    public static void errorHandling(@NotNull Context ctx, Exception e) {
        switch (e) {
            case UnauthorizedException unauthorizedException -> ctx.status(401);
            case BadRequestException badRequestException -> ctx.status(400);
            case AlreadyTakenException alreadyTakenException -> ctx.status(403);
            case null, default -> ctx.status(500);
        }
        var serializer = new Gson();
        Map<String,String> r = Map.of("message", (e != null ? e.getMessage() : ""));
        ctx.result(serializer.toJson(r));
        ctx.contentType("application/json");
    }

}

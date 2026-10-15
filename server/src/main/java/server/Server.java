package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Endpoints
        // clear
        javalin.delete("/db", Server::clearDatabase);

    }

    private static void clearDatabase(@NotNull Context ctx) {

        var r = Map.of();
        var serializer = new Gson();
        ctx.json(serializer.toJson(r));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}

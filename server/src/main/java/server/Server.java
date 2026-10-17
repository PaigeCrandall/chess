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
        javalin.delete("/db", Handler::clearDatabase); // clear
        javalin.post("/user", Handler::registerUser); // register
        javalin.delete("/session", Handler::logoutUser); // logout
        javalin.post("/session", Handler::loginUser); // login
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}

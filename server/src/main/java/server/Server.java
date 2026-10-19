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
        javalin.delete("/db", AdminHandler::clearDatabase); // clear
        javalin.post("/user", UserHandler::registerUser); // register
        javalin.delete("/session", UserHandler::logoutUser); // logout
        javalin.post("/session", UserHandler::loginUser); // login
        javalin.post("/game", GameHandler::createGame); // create game
        javalin.get("/game", GameHandler::getAllGames); // list games
        javalin.put("/game", GameHandler::joinGame); // list games
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}

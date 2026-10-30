package server;

import dataaccess.DataAccess;
import dataaccess.sql.SqlDataAccess;
import io.javalin.*;
import service.AdminService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        try {
            DataAccess dataAccess = new SqlDataAccess();

            AdminService adminService = new AdminService(dataAccess);
            AdminHandler adminHandler = new AdminHandler(adminService);
            UserService userService = new UserService(dataAccess);
            UserHandler userHandler = new UserHandler(userService);
            GameService gameService = new GameService(dataAccess);
            GameHandler gameHandler = new GameHandler(gameService);

            // Endpoints
            javalin.delete("/db", adminHandler::clearDatabase); // clear
            javalin.post("/user", userHandler::registerUser); // register
            javalin.delete("/session", userHandler::logoutUser); // logout
            javalin.post("/session", userHandler::loginUser); // login
            javalin.post("/game", gameHandler::createGame); // create game
            javalin.get("/game", gameHandler::getAllGames); // list games
            javalin.put("/game", gameHandler::joinGame); // list games


        } catch (Exception e) {
            System.out.println("There was a problem getting the server and dataAccess started.\n" + e);
        }
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}

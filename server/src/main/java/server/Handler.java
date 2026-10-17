package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.*;
import org.jetbrains.annotations.NotNull;
import service.AdminService;
import service.AlreadyTakenException;
import service.UserService;

import java.util.Map;

public class Handler {

    public static void clearDatabase(@NotNull Context ctx) {
        AdminService adminService = new AdminService();
        adminService.clear();
        var r = Map.of();
        var serializer = new Gson();
        ctx.json(serializer.toJson(r));
    }

    public static void registerUser(@NotNull Context ctx) {
        var serializer = new Gson();
        try {
            UserData request = ctx.bodyAsClass(UserData.class);
            UserService userService = new UserService();
            AuthData registerResult = userService.registerUser(request);
            serializer.toJson(registerResult);

            ctx.status(200);
            ctx.json(serializer.toJson(registerResult));
        }
        catch(AlreadyTakenException e) {
            Map<String,String> r = Map.of("message", e.getMessage());
            ctx.status(403);
            ctx.json(serializer.toJson(r));
        }
    }


}

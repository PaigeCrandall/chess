package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.*;
import org.jetbrains.annotations.NotNull;
import service.AdminService;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.UserService;

import java.util.Map;

public class Handler {

    public static void clearDatabase(@NotNull Context ctx) {
        AdminService adminService = new AdminService();
        adminService.clear();
        var r = Map.of();
        var serializer = new Gson();
        ctx.result(serializer.toJson(r));
        ctx.contentType("application/json");
    }

    public static void registerUser(@NotNull Context ctx) {
        var serializer = new Gson();
        try {
            String jsonBody = ctx.body();
            UserData request = serializer.fromJson(jsonBody, UserData.class);
            UserService userService = new UserService();
            AuthData registerResult = userService.registerUser(request);
            ctx.status(200);
            ctx.result(serializer.toJson(registerResult));
            ctx.contentType("application/json");
        }
        catch(AlreadyTakenException e) {
            Map<String,String> r = Map.of("message", e.getMessage());
            ctx.status(403);
            ctx.result(serializer.toJson(r));
            ctx.contentType("application/json");
        }
        catch(BadRequestException e) {
            Map<String,String> r = Map.of("message", e.getMessage());
            ctx.status(400);
            ctx.result(serializer.toJson(r));
            ctx.contentType("application/json");
        }
        catch(Exception e) {
            Map<String,String> r = Map.of("message", e.getMessage());
            ctx.status(500);
            ctx.result(serializer.toJson(r));
            ctx.contentType("application/json");
        }
    }


}

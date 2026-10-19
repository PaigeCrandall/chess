package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.*;
import model.requests.LoginRequest;
import model.results.LoginResult;
import org.jetbrains.annotations.NotNull;
import service.*;
import service.exceptions.*;

import java.util.Map;

public class UserHandler extends Handler {

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
        catch(Exception e) { errorHandling(ctx, e); }
    }


    public static void logoutUser(@NotNull Context ctx) {
        var serializer = new Gson();
        try {
            String token = ctx.header("authorization");
            UserService userService = new UserService();
            userService.logout(token);

            ctx.status(200);
            var r = Map.of();
            ctx.result(serializer.toJson(r));
            ctx.contentType("application/json");
        }
        catch(Exception e) { errorHandling(ctx, e); }
    }

    public static void loginUser(@NotNull Context ctx) {
        var serializer = new Gson();
        try {
            String jsonBody = ctx.body();
            LoginRequest request = serializer.fromJson(jsonBody, LoginRequest.class);
            UserService userService = new UserService();
            LoginResult result = userService.login(request);

            ctx.status(200);
            ctx.result(serializer.toJson(result));
            ctx.contentType("application/json");
        }
        catch(Exception e) { errorHandling(ctx, e); }
    }

}

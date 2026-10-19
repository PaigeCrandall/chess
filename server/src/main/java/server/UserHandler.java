package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

import model.*;
import model.requests.*;
import model.results.*;
import service.*;

import java.util.Map;

public class UserHandler extends Handler {

    public static void registerUser(@NotNull Context ctx) {
        var serializer = new Gson();
        try {
            String jsonBody = ctx.body();
            UserData request = serializer.fromJson(jsonBody, UserData.class);
            UserService userService = new UserService();
            AuthData registerResult = userService.registerUser(request);
            sendGoodResult(ctx, serializer.toJson(registerResult));
        }
        catch(Exception e) { errorHandling(ctx, e); }
    }


    public static void logoutUser(@NotNull Context ctx) {
        var serializer = new Gson();
        try {
            String token = ctx.header("authorization");
            UserService userService = new UserService();
            userService.logout(token);

            var r = Map.of();
            sendGoodResult(ctx, serializer.toJson(r));
        }
        catch(Exception e) { errorHandling(ctx, e); }
    }

    public static void loginUser(@NotNull Context ctx) {
        var serializer = new Gson();
        try {
            String jsonBody = ctx.body();
            LoginRequest request = serializer.fromJson(jsonBody, LoginRequest.class);
            UserService userService = new UserService();
            LoginResult loginResult = userService.login(request);
            sendGoodResult(ctx, serializer.toJson(loginResult));
        }
        catch(Exception e) { errorHandling(ctx, e); }
    }
}

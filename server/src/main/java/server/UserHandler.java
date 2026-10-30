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
    UserService userService;
    UserHandler(UserService userServiceImplementation) {
        userService = userServiceImplementation;
    }

    public void registerUser(@NotNull Context ctx) {
        var serializer = new Gson();
        try {
            String jsonBody = ctx.body();
            UserData request = serializer.fromJson(jsonBody, UserData.class);
            AuthData registerResult = userService.registerUser(request);
            sendGoodResult(ctx, serializer.toJson(registerResult));
        }
        catch(Exception e) { errorHandling(ctx, e); }
    }


    public void logoutUser(@NotNull Context ctx) {
        var serializer = new Gson();
        try {
            String token = ctx.header("authorization");
            userService.logout(token);

            var r = Map.of();
            sendGoodResult(ctx, serializer.toJson(r));
        }
        catch(Exception e) { errorHandling(ctx, e); }
    }

    public void loginUser(@NotNull Context ctx) {
        var serializer = new Gson();
        try {
            String jsonBody = ctx.body();
            LoginRequest request = serializer.fromJson(jsonBody, LoginRequest.class);
            LoginResult loginResult = userService.login(request);
            sendGoodResult(ctx, serializer.toJson(loginResult));
        }
        catch(Exception e) { errorHandling(ctx, e); }
    }
}

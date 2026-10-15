package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import service.AdminService;

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
        String information = ctx.body();
        UserData registerRequest = ctx.bodyAsClass(UserData.class);

        var r = Map.of();
        var serializer = new Gson();
        ctx.json(serializer.toJson(r));
    }


}

package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import service.AdminService;

import java.util.Map;

public class AdminHandler extends Handler {

    public static void clearDatabase(@NotNull Context ctx) {
        AdminService adminService = new AdminService();
        adminService.clear();
        var r = Map.of();
        var serializer = new Gson();
        sendGoodResult(ctx, serializer.toJson(r));
    }
}

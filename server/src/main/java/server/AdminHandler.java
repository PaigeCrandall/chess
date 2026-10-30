package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import service.AdminService;

import java.util.Map;

public class AdminHandler extends Handler {
    AdminService adminService;

    AdminHandler(AdminService adminServiceImplementation) {
        adminService = adminServiceImplementation;
    }

    public void clearDatabase(@NotNull Context ctx) {
        try {
            adminService.clear();
            var r = Map.of();
            var serializer = new Gson();
            sendGoodResult(ctx, serializer.toJson(r));
        }
        catch(Exception e) { errorHandling(ctx, e); }
    }
}

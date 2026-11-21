package websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.messages.ServerMessage;

import java.io.IOException;

public class WebSocketHandler  implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final InterestedPartiesManager connections = new InterestedPartiesManager();

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        System.out.println(ctx.message());
        try {
            greetings("hello back! -WS", ctx.session);
        } catch (Exception e) {
            System.out.println("there was a problem sending hello back from server");
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    public void greetings(String message, Session session) throws IOException {
        session.getRemote().sendString(message);
//        connections.add(1, session);
//        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
//        connections.broadcast(1, null, notification);
    }
}
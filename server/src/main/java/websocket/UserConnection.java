package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;

public record UserConnection(String username, String authToken, Role role, Session session) {
    public enum Role {
        WHITE_PLAYER,
        BLACK_PLAYER,
        OBSERVER
    }

    public void send(ServerMessage serverMessage) throws IOException {
        String jsonMessage = new Gson().toJson(serverMessage);
        session.getRemote().sendString(jsonMessage);
    }

}

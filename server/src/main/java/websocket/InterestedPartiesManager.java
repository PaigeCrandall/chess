package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterestedPartiesManager {
    public final Map<Integer, List<UserConnection>> connections = new HashMap<>();

    public void add(Integer gameID, UserConnection connection) {
        connections.computeIfAbsent(gameID, k -> new ArrayList<>()).add(connection);
    }

    public void remove(Integer gameID, UserConnection connection) {
        connections.get(gameID).remove(connection);
    }

    public boolean contains(int gameId, String username) {
        for(UserConnection connection : connections.get(gameId)) {
            if (connection.username().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public UserConnection getConnection(int gameId, String username) {
        for(UserConnection connection : connections.get(gameId)) {
            if (connection.username().equals(username)) {
                return connection;
            }
        }
        return null;
    }

    public void broadcast(Integer gameID, List<UserConnection> excludeConnections, ServerMessage notification) throws IOException {
        List<UserConnection> interestedParties = connections.get(gameID);
        for (UserConnection userConnection : interestedParties) {
            if (userConnection.session().isOpen()) {
                if (!excludeConnections.contains(userConnection)) {
                    userConnection.session().getRemote().sendString(new Gson().toJson(notification, ServerMessage.class));
                }
            }
        }
    }
}

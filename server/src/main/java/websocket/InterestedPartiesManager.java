package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InterestedPartiesManager {
    public final Map<Integer, List<Session>> connections = new HashMap<>();

    public void add(Integer gameID, Session session) {
        connections.computeIfAbsent(gameID, k -> new ArrayList<>()).add(session);
    }

    public void remove(Integer gameID, Session session) {
        connections.get(gameID).remove(session);
    }

    public void broadcast(Integer gameID, Session excludeSession, ServerMessage notification) throws IOException {
        List<Session> interestedParties = connections.get(gameID);
        for (Session partySession : interestedParties) {
            if(partySession.isOpen()) {
                if (!partySession.equals(excludeSession)) {
                    partySession.getRemote().sendString(new Gson().toJson(notification, ServerMessage.class));
                }
            }
        }
    }
}

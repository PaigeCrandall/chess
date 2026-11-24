package websocket.messages;

import chess.GameSerializer;
import com.google.gson.Gson;
import model.GameData;

import java.util.Locale;
import java.util.Objects;

/**
 * Represents a Message the server can send through a WebSocket
 * <p>
 * Note: You can add to this class, but you should not alter the existing
 * methods.
 */
public class ServerMessage {
    ServerMessageType serverMessageType;
    String errorMessage;
    String message;
    String game;

    public enum ServerMessageType {
        LOAD_GAME,
        ERROR,
        NOTIFICATION
    }

    public ServerMessage(ServerMessageType type, String message) {
        this.serverMessageType = type;
        this.game = null;
        this.errorMessage = null;
        this.message = null;

        if (type==ServerMessageType.LOAD_GAME) {
            this.game = message;
        }
        else if (type==ServerMessageType.ERROR) {
            this.errorMessage = message;
        }
        else if (type==ServerMessageType.NOTIFICATION) {
            this.message = message;
        }
    }

    public ServerMessageType getServerMessageType() {
        return this.serverMessageType;
    }

    public String getServerMessage() {
        return this.message;
    }

    public String getErrorMessageMessage() {
        return this.errorMessage;
    }

    public GameData getGame() {
        if (game!=null) {
            Gson gson = new GameSerializer().createChessSerializer();
            return gson.fromJson(game, GameData.class);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ServerMessage that)) {
            return false;
        }
        return getServerMessageType() == that.getServerMessageType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getServerMessageType());
    }
}

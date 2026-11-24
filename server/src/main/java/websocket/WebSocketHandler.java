package websocket;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import exceptions.DataAccessException;
import exceptions.UnauthorizedException;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.*;

public class WebSocketHandler  implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final InterestedPartiesManager userConnections = new InterestedPartiesManager();

    DataAccess dataAccess;
    public WebSocketHandler(DataAccess dataAccessImplementation) {
        this.dataAccess = dataAccessImplementation;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        String jsonCommand = ctx.message();
        UserGameCommand userCommand = new Gson().fromJson(jsonCommand, UserGameCommand.class);
//        System.out.println(ctx.message());
        try {
            if (userCommand.getCommandType().equals(UserGameCommand.CommandType.CONNECT)) {
                connectUser(userCommand, ctx.session);
            }
//          session.getRemote().sendString("Hell back -WS");
        } catch (Exception e) {
            System.out.println("there was a problem sending hello back from server");
            System.out.println(e);
        }
    }

    @Override
    public void handleClose(@NotNull WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    public UserConnection formatUserConnection(UserGameCommand command, Session session) throws DataAccessException {
        String username = dataAccess.getAuthData(command.getAuthToken()).username();
        UserConnection.Role role = UserConnection.Role.OBSERVER;
        if (((dataAccess.getGame(command.getGameID()).whiteUsername()!=null) &&
                (dataAccess.getGame(command.getGameID()).whiteUsername().equals(dataAccess.getAuthData(command.getAuthToken()).username()))) ||
                ((dataAccess.getGame(command.getGameID()).blackUsername()!=null) &&
                (dataAccess.getGame(command.getGameID()).blackUsername().equals(dataAccess.getAuthData(command.getAuthToken()).username())))) {
            role = UserConnection.Role.PLAYER;
        }
        UserConnection userConnection = new UserConnection(username, command.getAuthToken(), role, session);
        userConnections.add(command.getGameID(), userConnection);
        return userConnection;
    }

    public void sendLoadGame(UserConnection userToSendTo, int gameID) throws DataAccessException, IOException {
        GameData game = dataAccess.getGame(gameID);
        ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME, new Gson().toJson(game));
        userToSendTo.send(loadGame);
    }


    public void connectUser(UserGameCommand command, Session session) throws Exception {
        if ((dataAccess.getAuthData(command.getAuthToken())==null)||(dataAccess.getGame(command.getGameID())==null)) {
            throw new UnauthorizedException("unauthorized");
        }
        // add user to the game connections
        UserConnection userConnection = formatUserConnection(command, session);

        // send notification to all the other players and observers
        List<UserConnection> excludeSender = List.of(userConnection);
        String message = userConnection.username() + " has joined the game as a " + userConnection.role().name().toLowerCase() + "!";
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        userConnections.broadcast(command.getGameID(), excludeSender, notification);

        // send a load game to the new player/observer
        sendLoadGame(userConnection, command.getGameID());
    }
}
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
        System.out.print("Websocket connected :)\n");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(@NotNull WsMessageContext ctx) {
        String jsonCommand = ctx.message();
        UserGameCommand userCommand = new Gson().fromJson(jsonCommand, UserGameCommand.class);
//        System.out.println(ctx.message());
        try {
            switch(userCommand.getCommandType()) {
                case UserGameCommand.CommandType.CONNECT: connectUser(userCommand, ctx.session); break;
                case UserGameCommand.CommandType.LEAVE: removeUserFromGame(userCommand, ctx.session); break;
            }
        } catch (UnauthorizedException e) {
            System.out.println("Error: Unauthorized " + e);
        } catch (Exception e) {
            System.out.println("Error sending message back from server " + e);
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
                (dataAccess.getGame(command.getGameID()).whiteUsername().equals(dataAccess.getAuthData(command.getAuthToken()).username())))) {
            role = UserConnection.Role.WHITE_PLAYER;
        }
        if ((dataAccess.getGame(command.getGameID()).blackUsername()!=null) &&
                (dataAccess.getGame(command.getGameID()).blackUsername().equals(dataAccess.getAuthData(command.getAuthToken()).username()))) {
            role = UserConnection.Role.BLACK_PLAYER;
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

    public void checkUserPermissions(UserGameCommand command) throws Exception {
        if ((dataAccess.getAuthData(command.getAuthToken())==null)||(dataAccess.getGame(command.getGameID())==null)) {
            throw new UnauthorizedException("unauthorized");
        }
    }


    public void connectUser(UserGameCommand command, Session session) throws Exception {
        checkUserPermissions(command);

        // add user to connections
        UserConnection userConnection = formatUserConnection(command, session);

        // send notification to all the other players and observers
        List<UserConnection> excludeSender = List.of(userConnection);
        String message = userConnection.username() + " has joined the game as a " + userConnection.role().name().toLowerCase();
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        userConnections.broadcast(command.getGameID(), excludeSender, notification);

        // send load game to the player
        sendLoadGame(userConnection, command.getGameID());
    }

    public void removeUserFromGame(UserGameCommand command, Session session) throws Exception {
        checkUserPermissions(command);
        if (!userConnections.contains(command.getGameID(), dataAccess.getAuthData(command.getAuthToken()).username())) {
            throw new UnauthorizedException("the user is not in the game, so they cannot be removed from it.");
        }

        // get the user's connection
        UserConnection userConnection = userConnections.getConnection(command.getGameID(), dataAccess.getAuthData(command.getAuthToken()).username());

        // send notification to all the other players and observers
        List<UserConnection> excludeSender = List.of(userConnection);
        String message = userConnection.username() + " has left the game.";
        ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
        userConnections.broadcast(command.getGameID(), excludeSender, notification);

        // remove player from the game
        if (!userConnection.role().equals(UserConnection.Role.OBSERVER)) {
            GameData gameToLeave = dataAccess.getGame(command.getGameID());
            GameData updatedGame = getUpdatedGame(gameToLeave, userConnection);
            dataAccess.updateGame(updatedGame);
        }

        // remove the user from the connections
        userConnections.remove(command.getGameID(), userConnection);

        if (session.isOpen()) {
            session.close();
        }
    }


    @NotNull
    private static GameData getUpdatedGame(GameData gameToLeave, UserConnection userConnection) {
        String newWhiteUsername = gameToLeave.whiteUsername();
        String newBlackUsername = gameToLeave.blackUsername();
        if (userConnection.role().equals(UserConnection.Role.BLACK_PLAYER)) {
            newBlackUsername = null;
        }
        if (userConnection.role().equals(UserConnection.Role.WHITE_PLAYER)) {
            newWhiteUsername = null;
        }
        return new GameData(
                gameToLeave.gameID(),
                newWhiteUsername,
                newBlackUsername,
                gameToLeave.gameName(),
                gameToLeave.game(),
                gameToLeave.winner()
        );
    }
}
package client;

import com.google.gson.Gson;
import jakarta.websocket.*;
import websocket.commands.UserGameCommand;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebSocketFacade extends Endpoint {
    Session session;
    ServerMessageObserver notificationHandler;

    WebSocketFacade(String url, ServerMessageObserver notificationHandler) throws Exception {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
//                  ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);
                    ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, message);
                    notificationHandler.notify(notification);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    // here would be methods for connect, resign, leave, move
    public void sendHello(String greeting) {
        try {
            this.session.getBasicRemote().sendText(greeting);
        }
        catch (Exception e) {
            System.out.println("error in sending greeting");
        }
    }

    public void sendConnectRequest(int gameId, String authToken) {
        try {
            UserGameCommand connectCommand = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameId);
            String jsonCommand = new Gson().toJson(connectCommand);
            this.session.getBasicRemote().sendText(jsonCommand);
        }
        catch (Exception e) {
            System.out.println("error in sending greeting");
        }
    }


}

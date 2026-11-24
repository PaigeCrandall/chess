package client;

import exceptions.*;
import model.AuthData;
import model.results.*;

public class ServerFacade {
    HttpFacade httpHandler;
    WebSocketFacade wsHandler;
    String url;

    public ServerFacade(String serverUrl) {
        this.url = serverUrl;
        this.httpHandler = new HttpFacade(url);
        this.wsHandler = null;
    }

    // endpoints
    public void clear() throws Exception {
        httpHandler.clear();
    }

    public AuthData register(String username, String email, String password) throws Exception {
        return httpHandler.register(username, email, password);
    }

    public AuthData login(String username, String password) throws Exception {
        return httpHandler.login(username, password);
    }

    public void logout(String authToken) throws Exception {
        httpHandler.logout(authToken);
    }

    public ListGamesResult listGames(String authToken) throws Exception {
        return httpHandler.listGames(authToken);
    }

    public CreateGameResult createGame(String gameName, String authToken) throws Exception {
        return httpHandler.createGame(gameName, authToken);
    }

    public void joinGame(Integer gameID, String color, String authToken) throws Exception {
        httpHandler.joinGame(gameID, color, authToken);
    }

//    public void sendHelloWS(Integer gameID, ServerMessageObserver notificationHandler) throws Exception {
//        wsHandler = new WebSocketFacade(url, notificationHandler);
//        String hello = "hello! -Client"; // usually I would send a UseCommand
//        wsHandler.sendHello(hello);
//    }

    public void wsConnect(Integer gameID, ServerMessageObserver notificationHandler, String authToken) throws Exception {
        wsHandler = new WebSocketFacade(url, notificationHandler);
        wsHandler.sendConnectRequest(gameID, authToken);
    }

    public void leaveGame(String authToken, int gameId) throws UnauthorizedException {
        if (wsHandler!= null) {
            wsHandler.sendLeaveGameRequest(authToken, gameId);
        }
        else {
            throw new UnauthorizedException("User tried to leave a game that they were not playing");
        }
    }

    public void resignGame(String authToken, int gameId) throws UnauthorizedException {
        if (wsHandler!= null) {
//            wsHandler.sendLeaveGameRequest(authToken, gameId);
        }
        else {
            throw new UnauthorizedException("User tried to resign from a game that they were not playing");
        }
    }

}

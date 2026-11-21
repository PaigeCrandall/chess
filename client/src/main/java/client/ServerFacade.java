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

    public void joinGame(Integer gameID, String color, String authToken, ServerMessageObserver notificationHandler) throws Exception {
        httpHandler.joinGame(gameID, color, authToken);
        wsHandler = new WebSocketFacade(url, notificationHandler);
    }

}

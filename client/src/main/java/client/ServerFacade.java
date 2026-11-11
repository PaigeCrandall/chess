package client;

import chess.ChessGame;
import chess.GameSerializer;
import com.google.gson.Gson;
import exceptions.*;
import model.AuthData;
import model.requests.JoinGameRequest;
import model.results.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.Map;

public class ServerFacade {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    String url;

    public ServerFacade(String serverUrl) {
        this.url = serverUrl;
    }

    public void throwsExceptions(int status) throws Exception {
        if (status == 401) {
            throw new UnauthorizedException("401 Error: unauthorized");
        }
        else if (status == 400) {
            throw new BadRequestException("400 Error: bad request");
        }
        else if (status == 403) {
            throw new AlreadyTakenException("403 Error: already taken");
        }
        else if (status == 500) {
            throw new DataAccessException("500 Error: failed to access data");
        }
    }

    public HttpResponse<String> postHTTP(String urlString, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        throwsExceptions(httpResponse.statusCode());
        return httpResponse;
    }

    public void deleteHTTP(String urlString, String headerName, String headerValue) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .header("Content-Type", "application/json");
        if (!headerName.isEmpty()&&!headerValue.isEmpty()) {
            builder.header(headerName, headerValue);
        }
        HttpRequest request = builder.DELETE().build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        throwsExceptions(httpResponse.statusCode());
    }

    public void clear() throws Exception {
        String urlString = String.format(Locale.getDefault(), "%s/db", url);
        deleteHTTP(urlString, "", "");
    }

    public AuthData register(String username, String email, String password) throws Exception {
        String urlString = String.format(Locale.getDefault(), "%s/user", url);
        var body = Map.of(
                "username", username,
                "email", email,
                "password", password
        );
        var serializer = new Gson();
        HttpResponse<String> httpResponse = postHTTP(urlString, serializer.toJson(body));
        return serializer.fromJson(httpResponse.body(), AuthData.class);
    }

    public AuthData login(String username, String password) throws Exception {
        String urlString = String.format(Locale.getDefault(), "%s/session", url);
        var body = Map.of(
                "username", username,
                "password", password
        );
        var serializer = new Gson();
        HttpResponse<String> httpResponse = postHTTP(urlString, serializer.toJson(body));
        return serializer.fromJson(httpResponse.body(), AuthData.class);
    }

    public void logout(String authToken) throws Exception {
        String urlString = String.format(Locale.getDefault(), "%s/session", url);
        deleteHTTP(urlString, "authorization", authToken);

//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(new URI(urlString))
//                .timeout(java.time.Duration.ofMillis(5000))
//                .header("Content-Type", "application/json")
//                .header("authorization", authToken)
//                .DELETE()
//                .build();
//
//        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
//        throwsExceptions(httpResponse.statusCode());
    }

    public ListGamesResult listGames(String authToken) throws Exception {
        String urlString = String.format(Locale.getDefault(), "%s/game", url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .header("Content-Type", "application/json")
                .header("authorization", authToken)
                .GET()
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        throwsExceptions(httpResponse.statusCode());
        return new GameSerializer().deserializeGamesList(httpResponse.body());
    }

    public CreateGameResult createGame(String gameName, String authToken) throws Exception {
        String urlString = String.format(Locale.getDefault(), "%s/game", url);
        var body = Map.of("gameName", gameName );
        var serializer = new Gson();

        HttpResponse<String> httpResponse = postHTTP(urlString, serializer.toJson(body));
        return serializer.fromJson(httpResponse.body(), CreateGameResult.class);
    }

    public void joinGame(Integer gameID, String color, String authToken) throws Exception {
        JoinGameRequest joinRequest = new JoinGameRequest(color, gameID);
        String urlString = String.format(Locale.getDefault(), "%s/game", url);
        var serializer = new Gson();
        String jsonBody = serializer.toJson(joinRequest);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .header("Content-Type", "application/json")
                .header("authorization", authToken)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        throwsExceptions(httpResponse.statusCode());
    }

}

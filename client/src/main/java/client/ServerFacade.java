package client;

import com.google.gson.Gson;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.DataAccessException;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.results.ListGamesResult;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.Map;

public class ServerFacade {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    String url;

    ServerFacade(String serverUrl) {
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

    public void clear() throws Exception {
        String urlString = String.format(Locale.getDefault(), "%s/db", url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .header("Content-Type", "application/json")
                .DELETE()
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        throwsExceptions(httpResponse.statusCode());
    }

    public AuthData register(String username, String email, String password) throws Exception {
        String urlString = String.format(Locale.getDefault(), "%s/user", url);
        var body = Map.of(
                "username", username,
                "email", email,
                "password", password
        );
        var serializer = new Gson();
        var jsonBody = serializer.toJson(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        throwsExceptions(httpResponse.statusCode());
        return serializer.fromJson(httpResponse.body(), AuthData.class);
    }

    public AuthData login(String username, String password) throws Exception {
        String urlString = String.format(Locale.getDefault(), "%s/session", url);
        var body = Map.of(
                "username", username,
                "password", password
        );
        var serializer = new Gson();
        var jsonBody = serializer.toJson(body);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        throwsExceptions(httpResponse.statusCode());
        return serializer.fromJson(httpResponse.body(), AuthData.class);
    }

    public void logout(String authToken) throws Exception {
        String urlString = String.format(Locale.getDefault(), "%s/session", url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(urlString))
                .timeout(java.time.Duration.ofMillis(5000))
                .header("Content-Type", "application/json")
                .header("authorization", authToken)
                .DELETE()
                .build();

        HttpResponse<String> httpResponse = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        throwsExceptions(httpResponse.statusCode());
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
        var serializer = new Gson();
        throwsExceptions(httpResponse.statusCode());
        return serializer.fromJson(httpResponse.body(), ListGamesResult.class);
    }

}

package dataaccess;

import dataaccess.memoryDatabase.MemoryDatabase;
import model.*;

import java.util.ArrayList;
import java.util.Collection;

public class MemoryDataAccess implements DataAccess {
    final static MemoryDatabase db = new MemoryDatabase();

    public void printUsers() {
        MemoryDatabase.users.forEach((username, user) -> {
            System.out.println(username + " : " + user.email() + ", " + user.password());
        });
    }

    public void printGames() {
        MemoryDatabase.games.forEach((id, game) -> {
            System.out.println(id + " : " + game.name());
        });
    }

    @Override
    public void clear() {
        MemoryDatabase.users.clear();
        MemoryDatabase.authorization.clear();
        MemoryDatabase.games.clear();
    }

    @Override
    public void createUser(UserData user, AuthData auth) {
        MemoryDatabase.users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return MemoryDatabase.users.getOrDefault(username, null);
    }

    @Override
    public void addAuthData(AuthData auth) {
        MemoryDatabase.authorization.put(auth.authToken(), auth);
    }

    public AuthData getAuthData(String authToken) {
        return MemoryDatabase.authorization.getOrDefault(authToken, null);
    }

    public void deleteAuthToken(String authToken) {
        MemoryDatabase.authorization.remove(authToken);
    }

    public void addGame(GameData game) {
        MemoryDatabase.games.put(game.gameID(), game);
    }

    public GameData getGame(Integer gameId) {
        return MemoryDatabase.games.getOrDefault(gameId, null);
    }

    public Collection<GameData> getAllGames() {
        Collection<GameData> games = new ArrayList<GameData>();
        MemoryDatabase.games.forEach((gameId, game) -> {
            games.add(game);
        });
        return games;
    }



}
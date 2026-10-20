package dataaccess;

import dataaccess.memoryDatabase.MemoryDatabase;
import model.*;

import java.util.ArrayList;

public class MemoryDataAccess implements DataAccess {
    final static MemoryDatabase db = new MemoryDatabase();
    public static int gameID = 1;

    public void printUsers() {
        db.users.forEach((username, user) -> {
            System.out.println(username + " : " + user.email() + ", " + user.password());
        });
    }

    public void printGames() {
        db.games.forEach((id, game) -> {
            System.out.println(id + " " + game.gameName() + " - playerWhite: " + game.whiteUsername() + ", playerBlack: " + game.blackUsername());
        });
    }

    @Override
    public void clear() {
        db.users.clear();
        db.authorization.clear();
        db.games.clear();
    }

    @Override
    public void createUser(UserData user, AuthData auth) {
        db.users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return db.users.getOrDefault(username, null);
    }

    @Override
    public void addAuthData(AuthData auth) {
        db.authorization.put(auth.authToken(), auth);
    }

    public AuthData getAuthData(String authToken) {
        return db.authorization.getOrDefault(authToken, null);
    }

    public void deleteAuthToken(String authToken) {
        db.authorization.remove(authToken);
    }

    public int createGame(String gameName) {
        int newGameID = gameID;
        gameID++;
        GameData gewGame = new GameData(newGameID, null, null, gameName);
        db.games.put(newGameID, gewGame);
        return newGameID;
    }

    public void updateGame(GameData game) {
        db.games.put(game.gameID(),game);
    }

    public GameData getGame(Integer gameId) {
        return db.games.getOrDefault(gameId, null);
    }

    public ArrayList<GameData> getAllGames() {
        ArrayList<GameData> games = new ArrayList<GameData>();
        db.games.forEach((gameId, game) -> {
            games.add(game);
        });
        return games;
    }



}
package dataaccess;

import dataaccess.memorydatabase.MemoryDatabase;
import model.*;

import java.util.ArrayList;

public class MemoryDataAccess implements DataAccess {
    final static MemoryDatabase DB = new MemoryDatabase();
    public static int gameID = 1;

    public void printUsers() {
        DB.users.forEach((username, user) -> {
            System.out.println(username + " : " + user.email() + ", " + user.password());
        });
    }

    public void printGames() {
        DB.games.forEach((id, game) -> {
            System.out.println(id + " " + game.gameName() + " - playerWhite: " + game.whiteUsername() + ", playerBlack: " + game.blackUsername());
        });
    }

    @Override
    public void clear() {
        DB.users.clear();
        DB.authorization.clear();
        DB.games.clear();
    }

    @Override
    public void createUser(UserData user, AuthData auth) {
        DB.users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return DB.users.getOrDefault(username, null);
    }

    @Override
    public void addAuthData(AuthData auth) {
        DB.authorization.put(auth.authToken(), auth);
    }

    public AuthData getAuthData(String authToken) {
        return DB.authorization.getOrDefault(authToken, null);
    }

    public void deleteAuthToken(String authToken) {
        DB.authorization.remove(authToken);
    }

    public int createGame(String gameName) {
        int newGameID = gameID;
        gameID++;
        GameData gewGame = new GameData(newGameID, null, null, gameName);
        DB.games.put(newGameID, gewGame);
        return newGameID;
    }

    public void updateGame(GameData game) {
        DB.games.put(game.gameID(),game);
    }

    public GameData getGame(Integer gameId) {
        return DB.games.getOrDefault(gameId, null);
    }

    public ArrayList<GameData> getAllGames() {
        ArrayList<GameData> games = new ArrayList<GameData>();
        DB.games.forEach((gameId, game) -> {
            games.add(game);
        });
        return games;
    }



}
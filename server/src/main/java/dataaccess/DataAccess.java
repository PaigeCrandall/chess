package dataaccess;

import model.*;

import java.util.ArrayList;

public interface DataAccess {
    public void clear();

    public void printUsers();
    public void printGames();

    public void createUser(UserData user, AuthData auth);
    public UserData getUser(String username);

    public void addAuthData(AuthData auth);
    public void deleteAuthToken(String authToken);
    public AuthData getAuthData(String authToken);

    public int createGame(String gameName);
    public void updateGame(GameData game);
    public GameData getGame(Integer gameId);
    public ArrayList<GameData> getAllGames();
}
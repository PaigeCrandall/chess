package dataaccess;

import model.*;

import java.util.Collection;

public interface DataAccess {
    public void clear();
    public void printUsers();
    public void printGames();

    public void createUser(UserData user, AuthData auth);
    public void addAuthData(AuthData auth);

    public AuthData getAuthData(String authToken);
    public UserData getUser(String username);

    public void deleteAuthToken(String authToken);

    public void addGame(GameData game);
    public GameData getGame(Integer gameId);

    public Collection<GameData> getAllGames();
}
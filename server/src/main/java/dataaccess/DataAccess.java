package dataaccess;

import model.*;

import java.util.ArrayList;

public interface DataAccess {
    void clear() throws DataAccessException;

    void printUsers();
    void printGames();

    void createUser(UserData user, AuthData auth) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    boolean validatePassword(String username, String passwordToCheck) throws DataAccessException;

    void addAuthData(AuthData auth) throws DataAccessException;
    void deleteAuthToken(String authToken);
    AuthData getAuthData(String authToken);

    int createGame(String gameName);
    void updateGame(GameData game);
    GameData getGame(Integer gameId);
    ArrayList<GameData> getAllGames();
}
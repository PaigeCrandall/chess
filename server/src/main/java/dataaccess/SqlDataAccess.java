package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class SqlDataAccess implements DataAccess {
    public SqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    public void clear() {}

    public void printUsers() {}
    public void printGames() {}

    public void createUser(UserData user, AuthData auth) {}
    public UserData getUser(String username) {return new UserData("name", "email", "password");}

    public void addAuthData(AuthData auth) {}
    public void deleteAuthToken(String authToken) {}
    public AuthData getAuthData(String authToken) {return new AuthData("name", "123");}

    public int createGame(String gameName) {return 1;}
    public void updateGame(GameData game) {};
    public GameData getGame(Integer gameId) {return null;}
    public ArrayList<GameData> getAllGames() {return null;}

    private final String[] createStatements = {

    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}

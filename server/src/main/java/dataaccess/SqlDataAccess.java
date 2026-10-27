package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SqlDataAccess implements DataAccess {
    public SqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var userStatement = "TRUNCATE chess.users";
            try (PreparedStatement preparedStatement = conn.prepareStatement(userStatement)) {
                preparedStatement.executeUpdate();
            }
            var authStatement = "TRUNCATE chess.users";
            try (PreparedStatement preparedStatement = conn.prepareStatement(authStatement)) {
                preparedStatement.executeUpdate();
            }
//            var gamesStatement = "TRUNCATE chess.games";
//            try (PreparedStatement preparedStatement = conn.prepareStatement(gamesStatement)) {
//                preparedStatement.executeUpdate();
//            }
        } catch (Exception ex) {
            throw new DataAccessException(String.format("Unable to clear database: %s", ex.getMessage()));
        }
    }

    public boolean validatePassword(String username, String passwordToCheck) {
        String validPassword = getUser(username).password();
        return BCrypt.checkpw(passwordToCheck, validPassword);
    }

    public void printUsers() {}
    public void printGames() {}

    public void createUser(UserData user, AuthData auth) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO chess.users (username, email, password) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, user.username());
                preparedStatement.setString(2, user.email());
                String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());
                preparedStatement.setString(3, hashedPassword);

                preparedStatement.executeUpdate();
            }
        } catch (Exception ex) {
            System.out.println("DataAccessException");
//            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }

    public UserData getUser(String username) {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM chess.users WHERE username=?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(
                                rs.getString("username"),
                                rs.getString("email"),
                                rs.getString("password")
                        );
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println("DataAccessException");
//            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
        return null;
    }

    public void addAuthData(AuthData auth) {}
    public void deleteAuthToken(String authToken) {}
    public AuthData getAuthData(String authToken) {return new AuthData("name", "123");}

    public int createGame(String gameName) {return 1;}
    public void updateGame(GameData game) {}
    public GameData getGame(Integer gameId) {return null;}
    public ArrayList<GameData> getAllGames() {return null;}

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
              `id` int NOT NULL AUTO_INCREMENT,
              `username` varchar(256) NOT NULL,
              `email` varchar(256) NOT NULL,
              `password` varchar(256) NOT NULL,
              PRIMARY KEY (`id`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS authorization (
              `token` int NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`token`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
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

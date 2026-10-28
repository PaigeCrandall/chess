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
import java.util.List;
import static java.sql.Types.NULL;

public class SqlDataAccess implements DataAccess {
    public SqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    public void clear() throws DataAccessException {
        for (String tableName : List.of("chess.users", "chess.authorization")) {
            var statement = "TRUNCATE " + tableName;
            updateDatabase(statement);
        }
    }

    public boolean validatePassword(String username, String passwordToCheck) throws DataAccessException {
        String validPassword = getUser(username).password();
        return BCrypt.checkpw(passwordToCheck, validPassword);
    }

    public void printUsers() {}
    public void printGames() {}

    public void createUser(UserData user, AuthData auth) throws DataAccessException {
        var statement = "INSERT INTO chess.users (username, email, password) VALUES (?, ?, ?)";
        updateDatabase(statement, user.username(), user.email(), BCrypt.hashpw(user.password(), BCrypt.gensalt()));
    }

    public UserData getUser(String username) throws DataAccessException {
        var statement = "SELECT * FROM chess.users WHERE username='" + username + "'";
        return (UserData) readFromDatabase(statement, "UserData");
    }

    public void addAuthData(AuthData auth) throws DataAccessException {
        var statement = "INSERT INTO chess.authorization (authToken, username) VALUES (?, ?)";
        updateDatabase(statement, auth.authToken(), auth.username());
    }

    public void deleteAuthToken(String authToken) throws DataAccessException {
        var statement = "Delete FROM chess.authorization WHERE authToken='" + authToken + "'";
        updateDatabase(statement);
    }

    public AuthData getAuthData(String authToken) throws DataAccessException {
        var statement = "SELECT * FROM chess.authorization WHERE authToken='" + authToken + "'";
        return (AuthData) readFromDatabase(statement, "AuthData");
    }

    public int createGame(String gameName) {return 1;}
    public void updateGame(GameData game) {}
    public GameData getGame(Integer gameId) {return null;}
    public ArrayList<GameData> getAllGames() {return null;}

    private void updateDatabase(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    switch (param) {
                        case String p -> preparedStatement.setString(i + 1, p);
                        case Integer p -> preparedStatement.setInt(i + 1, p);
                        case null -> preparedStatement.setNull(i + 1, NULL);
                        default -> {}
                    }
                }
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }
    }

    private Object readFromDatabase(String statement, String type) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(statement);
            ResultSet rs = preparedStatement.executeQuery()) {

                if (rs.next()) {
                    if (type.equals("AuthData")) {
                        return new AuthData(
                            rs.getString("authToken"),
                            rs.getString("username")
                    );
                    } else if (type.equals("UserData")) {
                        return new UserData(
                                rs.getString("username"),
                                rs.getString("email"),
                                rs.getString("password")
                        );
                    }
                }

        } catch (SQLException e) {
            throw new DataAccessException(String.format("Unable to configure database: %s", e.getMessage()));
        }
        return null;
    }

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
              `authToken` int NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
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

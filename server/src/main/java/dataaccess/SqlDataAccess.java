package dataaccess;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SqlDataAccess implements DataAccess {
    public SqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    public void clear() throws DataAccessException {
        for (String tableName : List.of("chess.users", "chess.authorization", "chess.games")) {
            var statement = "TRUNCATE " + tableName;
            updateDatabase(statement);
        }
    }

    public boolean validatePassword(String username, String passwordToCheck) throws DataAccessException {
        String validPassword = getUser(username).password();
        return BCrypt.checkpw(passwordToCheck, validPassword);
    }

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

    public int createGame(String gameName) throws DataAccessException {
        var statement = "INSERT INTO chess.games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";

        ChessGame newGame = new ChessGame();
        var serializer = new Gson();
        String jsonGameInfo = serializer.toJson(newGame);

        return updateDatabase(statement, null, null, gameName, jsonGameInfo);
    }

    public void updateGame(GameData game) throws DataAccessException {
        var serializer = new Gson();
        var statement = "UPDATE games SET whiteUsername = ?, blackUsername = ?," +
                "gameName = ?, game = ? WHERE gameID = ?;";
        updateDatabase(statement, game.whiteUsername(), game.blackUsername(),
                game.gameName(), serializer.toJson(game.game()), game.gameID());
    }

    public GameData getGame(Integer gameId)  throws DataAccessException {
        var statement = "SELECT * FROM chess.games WHERE gameID='" + gameId + "';";
        return (GameData) readFromDatabase(statement, "GameData");
    }

    public ArrayList<GameData> getAllGames() throws DataAccessException {
        ArrayList<GameData> gamesList = new ArrayList<>();
        var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM chess.games;";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        gamesList.add(new GameData(
                                rs.getInt("gameID"),
                                rs.getString("whiteUsername"),
                                rs.getString("blackUsername"),
                                rs.getString("gameName"),
                                deserializeGame(rs.getString("game"))
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database: %s, %s", statement, e.getMessage()));
        }

        return gamesList;
    }


    public static Gson createChessSerializer() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ChessPiece.class,
                (JsonDeserializer<ChessPiece>) (jsonString, _, ctx) -> {
                    JsonObject jsonObject = jsonString.getAsJsonObject();
                    ChessGame.TeamColor color = ctx.deserialize(jsonObject.get("color"), ChessGame.TeamColor.class);
                    ChessPiece.PieceType type = ctx.deserialize(jsonObject.get("type"), ChessPiece.PieceType.class);
                    return new ChessPiece(color, type);
                });

        gsonBuilder.registerTypeAdapter(ChessGame.class,
                (JsonDeserializer<ChessGame>) (jsonString, _, ctx) -> {
                    JsonObject jsonObject = jsonString.getAsJsonObject();
                    ChessBoard board = ctx.deserialize(jsonObject.get("board"), ChessBoard.class);
                    ChessGame.TeamColor team = ctx.deserialize(jsonObject.get("team"), ChessGame.TeamColor.class);
                    return new ChessGame(board, team);
                });

        return gsonBuilder.create();
    }

    public static ChessGame deserializeGame(String jsonGameInfo) {
        Gson gameSerializer =  createChessSerializer();
        return gameSerializer.fromJson(jsonGameInfo, ChessGame.class);
    }

    private int updateDatabase(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
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

                ResultSet rs = preparedStatement.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
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
                    switch (type) {
                        case "AuthData" -> {
                            return new AuthData(
                                    rs.getString("username"),
                                    rs.getString("authToken")
                            );
                        }
                        case "UserData" -> {
                            return new UserData(
                                    rs.getString("username"),
                                    rs.getString("email"),
                                    rs.getString("password")
                            );
                        }
                        case "GameData" -> {
                            return new GameData(
                                    rs.getInt("gameID"),
                                    rs.getString("whiteUsername"),
                                    rs.getString("blackUsername"),
                                    rs.getString("gameName"),
                                    deserializeGame(rs.getString("game"))
                            );
                        }
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
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,
            """
            CREATE TABLE IF NOT EXISTS games (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `game` TEXT,
              PRIMARY KEY (`gameID`)
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

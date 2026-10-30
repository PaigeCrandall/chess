package dataaccess;

import chess.*;
import dataaccess.sql.DatabaseManager;
import dataaccess.sql.SqlDataAccess;
import model.*;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DataAccessTests {
    private static DataAccess dataAccess;

    @BeforeAll
    static void setUp() throws DataAccessException {
        dataAccess = new SqlDataAccess();
    }

    @AfterEach
    void clearData() throws DataAccessException {
        dataAccess.clear();
    }

    @Test
    void setup() {
        try (Connection conn = DatabaseManager.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            List<String> tablesToCheck = List.of("users", "authorization", "games");
            for (String tableName : tablesToCheck) {
                try (ResultSet rs = meta.getTables(null, null, tableName, null)) {
                    assertTrue(rs.next());
                }
            }
        }
        catch(Exception e) {
            System.out.println("Database connection issue");
        }
    }

    @Test
    void clear() {
        try (Connection conn = DatabaseManager.getConnection()) {
            dataAccess.createUser(new UserData("Vivian", "aspentree@tropic.com", "bluebirds"), new AuthData("1234", "Paige"));
            dataAccess.clear();
            List<String> tablesToClear = List.of("chess.users", "chess.authorization", "chess.games");
            for (String tableName : tablesToClear) {
                String statement = "SELECT * FROM " + tableName;
                PreparedStatement preppedStatement = conn.prepareStatement(statement);
                ResultSet result = preppedStatement.executeQuery();
                assertFalse(result.next());
            }
        }
        catch(Exception e) {
            System.out.println("Database connection issue");
        }
    }

    @Test
    void addAndGetUser() {
        try {
            dataAccess.createUser(new UserData("Paige", "palmtree@tropic.com", "turtles"), new AuthData("1234", "Paige"));
            assertNotNull(dataAccess.getUser("Paige"));
        }
        catch (DataAccessException e) {
            System.out.println("DataAccessException e = " + e);
        }
    }

    @Test
    void addAndGetAndDeleteAuth() {
        try {
            dataAccess.createUser(new UserData("Paige", "palmtree@tropic.com", "turtles"), new AuthData("1234", "Paige"));
            dataAccess.addAuthData(new AuthData("Paige", "1234"));
            dataAccess.createUser(new UserData("Vivian", "aspentree@tropic.com", "bluebirds"), new AuthData("5678", "Paige"));
            dataAccess.addAuthData(new AuthData("Vivian", "5678"));
            assertNotNull(dataAccess.getAuthData("1234"));
            assertNotNull(dataAccess.getAuthData("5678"));

            dataAccess.deleteAuthToken("5678");
            assertNull(dataAccess.getAuthData("5678"));
        }
        catch (DataAccessException e) {
            System.out.println("DataAccessException e = " + e);
        }
    }

    @Test
    public void getAndCreateGame() {
        try {
            dataAccess.createGame("Champions");
            assertNotNull(dataAccess.getGame(1));
            assertEquals("Champions", dataAccess.getGame(1).gameName());
            assertNull(dataAccess.getGame(1).whiteUsername());
            assertNull(dataAccess.getGame(1).blackUsername());
            assertNotNull(dataAccess.getGame(1).game());
        }
        catch (DataAccessException e) {
            System.out.println("DataAccessException e = " + e);
        }
    }

    @Test
    void listGames() {
        try {
            dataAccess.createGame("Champions");
            dataAccess.createGame("Losers");
            dataAccess.createGame("Fighters");
            dataAccess.createGame("Wizards");
            assertEquals(4, dataAccess.getAllGames().size());
        }
        catch (DataAccessException e) {
            System.out.println("DataAccessException e = " + e);
        }
    }

    @Test
    void joinGameUpdate() {
        try {
            int id = dataAccess.createGame("Champions");
            GameData game1 = dataAccess.getGame(id);
            assertNull(game1.blackUsername());
            dataAccess.updateGame(new GameData(game1.gameID(), game1.whiteUsername(), "Phyllis", game1.gameName(), new ChessGame()));
            GameData updatedGame1 = dataAccess.getGame(id);
            assertEquals("Phyllis", updatedGame1.blackUsername());
            assertNull(updatedGame1.whiteUsername());
            dataAccess.updateGame(new GameData(updatedGame1.gameID(), "Paige", updatedGame1.blackUsername(), updatedGame1.gameName(), new ChessGame()));
            assertEquals("Paige", dataAccess.getGame(id).whiteUsername());
        }
        catch (DataAccessException e) {
            System.out.println("DataAccessException e = " + e);
        }
    }

    @Test
    void makeMoveUpdate() {
        try {
            int id = dataAccess.createGame("Losers");
            GameData startGame = dataAccess.getGame(id);
            assertNotNull(startGame.game());
            assertNull(dataAccess.getGame(id).game().getBoard().getPiece(new ChessPosition(3,2)));
            startGame.game().makeMove(new ChessMove(new ChessPosition(2,2), new ChessPosition(3,2)));
            GameData newGame = new GameData(startGame.gameID(), startGame.whiteUsername(), startGame.blackUsername(), startGame.gameName(), startGame.game());
            dataAccess.updateGame(newGame);
            assertNotNull(dataAccess.getGame(id).game().getBoard().getPiece(new ChessPosition(3,2)));
        }
        catch (DataAccessException e) {
            System.out.println("DataAccessException e = " + e);
        }
        catch (chess.InvalidMoveException e) {
            System.out.println("InvalidMoveException e = " + e);
        }
    }

}


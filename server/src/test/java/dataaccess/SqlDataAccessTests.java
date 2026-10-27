package dataaccess;

import model.*;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SqlDataAccessTests {

    @AfterEach
    void clearData() {
        try {
            DataAccess dataAccess = new SqlDataAccess();
            dataAccess.clear();
        }
        catch(Exception e) {
            System.out.println("Database did not clear!");
        }
    }

    @Test
    void setup() {
        try (Connection conn = DatabaseManager.getConnection()) {
            DataAccess dataAccess = new SqlDataAccess();
            DatabaseMetaData meta = conn.getMetaData();
            List<String> tablesToCheck = List.of("users", "authorization");
            for (String tableName : tablesToCheck) {
                try (ResultSet rs = meta.getTables(null, null, tableName, null)) {
                    assertTrue(rs.next());
                }
            }
        }
        catch(Exception e) {
            System.out.println("Database connection issue");
        }
        // dataAccess.createUser(new UserData("Paige", "palmtree@tropic.com", "turtles"), new AuthData("1234", "Paige"));
    }

    @Test
    void clear() {
        try (Connection conn = DatabaseManager.getConnection()) {
            DataAccess dataAccess = new SqlDataAccess();
            dataAccess.createUser(new UserData("Vivian", "aspentree@tropic.com", "bluebirds"), new AuthData("1234", "Paige"));
            dataAccess.clear();
            List<String> tablesToClear = List.of("chess.users", "chess.authorization");
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
            DataAccess dataAccess = new SqlDataAccess();
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
            DataAccess dataAccess = new SqlDataAccess();
            dataAccess.createUser(new UserData("Paige", "palmtree@tropic.com", "turtles"), new AuthData("1234", "Paige"));
            dataAccess.addAuthData(new AuthData("1234", "Paige"));
            dataAccess.createUser(new UserData("Vivian", "aspentree@tropic.com", "bluebirds"), new AuthData("5678", "Paige"));
            dataAccess.addAuthData(new AuthData("5678", "Vivian"));
            assertNotNull(dataAccess.getAuthData("1234"));
            assertNotNull(dataAccess.getAuthData("5678"));

            dataAccess.deleteAuthToken("5678");
            assertNull(dataAccess.getAuthData("5678"));
        }
        catch (DataAccessException e) {
            System.out.println("DataAccessException e = " + e);
        }
    }

}


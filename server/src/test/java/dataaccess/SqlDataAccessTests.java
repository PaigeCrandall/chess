package dataaccess;

import model.*;
import org.junit.jupiter.api.*;
import service.AdminService;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SqlDataAccessTests {
//    private final DataAccess dataAccess = new SqlDataAccess();
    AdminService service = new AdminService();

    @AfterEach
    void clearData() throws DataAccessException {
        try {
            DataAccess dataAccess = new SqlDataAccess();
            dataAccess.clear();
        }
        catch(Exception e) {
            System.out.println("Database did not clear!");
        }
    }

    @Test
    void setup() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
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
    void clear() throws DataAccessException {
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
    void addAndGetUser() throws DataAccessException {
        try {
            DataAccess dataAccess = new SqlDataAccess();
            dataAccess.createUser(new UserData("Paige", "palmtree@tropic.com", "turtles"), new AuthData("1234", "Paige"));
            assertNotNull(dataAccess.getUser("Paige"));
        }
        catch (DataAccessException e) {
            System.out.println("DataAccessException e = " + e);
        }
    }



}


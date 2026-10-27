package dataaccess;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;
import service.AdminService;

public class DataAccessTests {
//    private final DataAccess dataAccess = new SqlDataAccess();
    AdminService service = new AdminService();

    @Test
    void setup() {
        try {
            DataAccess dataAccess = new SqlDataAccess();
        }
        catch (DataAccessException e) {
            System.out.println("dataAccess Exception e = " + e);
        }
    }
}


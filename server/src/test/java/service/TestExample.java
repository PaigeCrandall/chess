package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class TestExample {
//    private String text;

//    @BeforeAll
//    static void createDatabase() {
//
//    }
//    // @BeforeEach
//
//    // @AftereEach
//
//
//    @AfterAll
//    static void deleteDatabase() {
//
//    }
//    public TestExample(String base) {this.text = base;}

    @Test
    void register() {
        var dataAccess = new MemoryDataAccess();
        AdminService service = new AdminService();

        UserData user = new UserData("paige", "LadyPhilclagen@gmail.com", "password1234:)");
        dataAccess.register(user, null); // AuthData is null right now
        UserData user2 = new UserData("vivian", "VarclinPeever@gmail.com", "password2468**");
        dataAccess.register(user2, null); // AuthData is null right now

        assertEquals(dataAccess.getUser(user.username()), user);

//        dataAccess.printUsers();

        service.clear();

        assertNull(dataAccess.getUser(user.username()));
    }

}

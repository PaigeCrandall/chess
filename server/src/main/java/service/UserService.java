package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.UserData;

public class UserService {
    DataAccess database;
    public UserService() {
        this.database = new MemoryDataAccess();
    }
    public void registerUser(UserData registerRequest) {
        // check if user already exists later
        // UserData newUser = database.getUser(registerRequest.username());

        database.register(registerRequest, null); // AuthToken is null right now
    }
}

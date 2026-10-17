package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.*;

import java.util.UUID;

public class UserService {
    DataAccess database;
    public UserService() {
        this.database = new MemoryDataAccess();
    }

    public String createAuthToken() {
        return UUID.randomUUID().toString();
    }


    public AuthData registerUser(UserData registerRequest) throws Exception {
        if ((registerRequest.username()==null)||(registerRequest.password()==null)||registerRequest.email()==null) {
            throw new BadRequestException("bad request");
        }
        if (database.getUser(registerRequest.username()) == null) {
            AuthData authToken = new AuthData(registerRequest.username(), createAuthToken());
            database.addAuthData(authToken);
            database.createUser(registerRequest, authToken);
            return authToken;
        }
        else {
            throw new AlreadyTakenException(String.format("%s is not available", registerRequest.username()));
        }
    }
}

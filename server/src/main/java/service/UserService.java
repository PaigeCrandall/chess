package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import model.*;

import java.util.UUID;

public class UserService {
    DataAccess dataAccess;
    public UserService() {
        this.dataAccess = new MemoryDataAccess();
    }

    public String createAuthToken() {
        return UUID.randomUUID().toString();
    }


    public AuthData registerUser(UserData registerRequest) throws Exception {
        if ((registerRequest.username()==null)||(registerRequest.password()==null)||registerRequest.email()==null) {
            throw new BadRequestException("bad request");
        }
        if (dataAccess.getUser(registerRequest.username()) == null) {
            AuthData authToken = new AuthData(registerRequest.username(), createAuthToken());
            dataAccess.addAuthData(authToken);
            dataAccess.createUser(registerRequest, authToken);
            return authToken;
        }
        else {
            throw new AlreadyTakenException(String.format("%s is not available", registerRequest.username()));
        }
    }

    public void logout(String auth) throws UnauthorizedException {
        if (dataAccess.getAuthData(auth)==null) {
            throw new UnauthorizedException("unauthorized");
        }
        else {
            dataAccess.deleteAuthToken(auth);
        }
    }
}

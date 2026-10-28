package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.SqlDataAccess;
import model.*;
import model.requests.LoginRequest;
import model.results.LoginResult;
import service.exceptions.AlreadyTakenException;
import service.exceptions.BadRequestException;
import service.exceptions.UnauthorizedException;

import java.util.UUID;

public class UserService {
    DataAccess dataAccess;

    public UserService() {
        this.dataAccess = new MemoryDataAccess();
    }

    public String createAuthToken() {
        return UUID.randomUUID().toString();
    }


    public AuthData registerUser(UserData registerRequest) throws AlreadyTakenException, BadRequestException, DataAccessException {
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

    public void logout(String auth) throws UnauthorizedException, DataAccessException {
        if (dataAccess.getAuthData(auth)==null) {
            throw new UnauthorizedException("unauthorized");
        }
        else {
            dataAccess.deleteAuthToken(auth);
        }
    }

    public LoginResult login(LoginRequest loginRequest) throws BadRequestException, UnauthorizedException, DataAccessException {
        if (loginRequest.username()==null||loginRequest.password()==null) {
            throw new BadRequestException("bad request");
        }
        if ((dataAccess.getUser(loginRequest.username())==null)) {
            throw new UnauthorizedException("username does not exist");
        }
        if (!dataAccess.validatePassword(loginRequest.username(), loginRequest.password())) {
            throw new UnauthorizedException("unauthorized");
        }
        String authToken = createAuthToken();
        dataAccess.addAuthData(new AuthData(loginRequest.username(), authToken));
        return new LoginResult(loginRequest.username(), authToken);
    }
}

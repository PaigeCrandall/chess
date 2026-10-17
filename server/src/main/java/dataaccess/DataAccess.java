package dataaccess;

import model.AuthData;
import model.UserData;

public interface DataAccess {
    public void clear();
    public void printUsers();

    public void createUser(UserData user, AuthData auth);
    public void addAuthData(AuthData auth);

    public AuthData getAuthData(String authToken);
    public UserData getUser(String username);

    public void deleteAuthToken(String authToken);
}
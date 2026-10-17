package dataaccess;

import dataaccess.memoryDatabase.MemoryDatabase;
import model.*;

public class MemoryDataAccess implements DataAccess {
    final static MemoryDatabase db = new MemoryDatabase();

    public void printUsers() {
        MemoryDatabase.users.forEach((username, user) -> {
            System.out.println(username + " : " + user.email() + ", " + user.password());
        });
    }

    @Override
    public void clear() {
        MemoryDatabase.users.clear();
    }

    @Override
    public void createUser(UserData user, AuthData auth) {
        MemoryDatabase.users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return MemoryDatabase.users.get(username);
    }

    @Override
    public void addAuthData(AuthData auth) {
        MemoryDatabase.authorization.put(auth.authToken(), auth);
    }

    public AuthData getAuthData(String authToken) {
        return MemoryDatabase.authorization.get(authToken);
    }

}
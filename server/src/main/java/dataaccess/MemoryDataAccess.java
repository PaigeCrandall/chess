package dataaccess;

import dataaccess.memoryDatabase.MemoryDatabase;
import model.*;

public class MemoryDataAccess implements DataAccess {
    final MemoryDatabase db = new MemoryDatabase();

    public void printUsers() {
        db.users.forEach((username, user) -> {
            System.out.println(username + " : " + user.email() + ", " + user.password());
        });
    }

    @Override
    public void clear() {
        db.users.clear();
    }

    @Override
    public void register(UserData user, AuthData token) {
        db.users.put(user.username(), user);
    }

    @Override
    public UserData getUser(String username) {
        return db.users.get(username);
    }
}
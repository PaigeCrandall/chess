package dataaccess;

import model.AuthData;
import model.UserData;

public interface DataAccess {
    public void clear();

    public void register(UserData user, AuthData token);

    public UserData getUser(String username);
}

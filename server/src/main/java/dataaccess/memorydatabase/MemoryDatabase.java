package dataaccess.memorydatabase;

import model.*;

import java.util.HashMap;
import java.util.Map;

public class MemoryDatabase {
    public Map<String, UserData> users = new HashMap<>();

    public Map<String, AuthData> authorization = new HashMap<>();

    public Map<Integer, GameData> games = new HashMap<>();
}
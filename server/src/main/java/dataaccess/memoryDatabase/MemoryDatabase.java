package dataaccess.memoryDatabase;

import model.*;

import java.util.HashMap;
import java.util.Map;

public class MemoryDatabase {
    public static Map<String, UserData> users = new HashMap<>();

    public static Map<String, AuthData> authorization = new HashMap<>();
}
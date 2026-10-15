package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;

public class AdminService {
    DataAccess database;

    public AdminService() {
        this.database = new MemoryDataAccess();
    }

    public void clear() {
        database.clear();
    }
}

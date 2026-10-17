package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;

public class AdminService {
    DataAccess dataAccess;

    public AdminService() {
        this.dataAccess = new MemoryDataAccess();
    }

    public void clear() {
        dataAccess.clear();
    }
}

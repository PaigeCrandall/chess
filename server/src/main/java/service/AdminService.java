package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;

public class AdminService {
    DataAccess dataAccess;

    public AdminService() {
        this.dataAccess = new MemoryDataAccess();
    }

    public void clear() throws DataAccessException {
        dataAccess.clear();
    }
}

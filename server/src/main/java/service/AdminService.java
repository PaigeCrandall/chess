package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.SqlDataAccess;

public class AdminService {
    DataAccess dataAccess;
    public AdminService() throws DataAccessException {
        this.dataAccess = new SqlDataAccess();
    }

    public void clear() throws DataAccessException {
        dataAccess.clear();
    }
}

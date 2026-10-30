package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.SqlDataAccess;

public class AdminService {
    DataAccess dataAccess;
    public AdminService(DataAccess dataAccessImplementation) throws DataAccessException {
        this.dataAccess = dataAccessImplementation;
    }

    public void clear() throws DataAccessException {
        dataAccess.clear();
    }
}

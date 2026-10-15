package service;

import dataaccess.DataAccess;

public class AdminService {
    DataAccess data;

    AdminService(DataAccess implementation) {
        this.data = implementation;
    }

    public void clear() {
        data.clear();
    }
}

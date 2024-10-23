package org.ashwin.flash.database.service;

public class StorageService {

    private static StorageService service;
    private final StorageManager storageManager;

    private StorageService() {
        storageManager = StorageManager.getInstance();
    }

    public static synchronized StorageService getInstance() {
        if (service == null) {
            service = new StorageService();
        }
        return service;
    }

    public boolean createDatabase(String databaseName) {
        return storageManager.createDatabaseDirectory(databaseName);
    }

}

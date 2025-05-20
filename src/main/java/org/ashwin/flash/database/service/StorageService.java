package org.ashwin.flash.database.service;

import org.ashwin.flash.config.FlashProperties;
import org.ashwin.flash.database.core.Database;

import java.io.File;

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

    public boolean isDatabaseExists(String databaseName) {
        String databasePath = FlashProperties.DATA_DIR.getDefaultValue() + File.separator + databaseName;
        return new File(databasePath).exists();
    }

    public boolean createDatabase(String databaseName) {
        return storageManager.createDatabase(databaseName);
    }

    public Database getDatabase(String databaseName) {
        return storageManager.getDatabase(databaseName);
    }

    public void deleteDatabase(String databaseName) {
        storageManager.deleteDatabase(databaseName);
    }

    public void shutdown() {
        storageManager.shutdown();
    }

}

package org.ashwin.flash.database.service;

import org.ashwin.flash.config.FlashProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class StorageManager {

    private static final Logger logger = LoggerFactory.getLogger(StorageManager.class);
    private static StorageManager manager = null;

    private StorageManager() { init(); }

    public static synchronized StorageManager getInstance() {
        if (manager == null){
            manager = new StorageManager();
        }
        return manager;
    }

    private void init() {
        createDataDirectory();
    }

    private void createDataDirectory() {
        File dataDirectory = new File(FlashProperties.DATA_DIR.getDefaultValue());
        if (!dataDirectory.exists()) {
            dataDirectory.mkdirs();
            logger.info("Data directory created: " + dataDirectory.getAbsolutePath());
        }
    }

    public boolean createDatabaseDirectory(String databaseName) {
        String databasePath = FlashProperties.DATA_DIR.getDefaultValue() + File.separator + databaseName;
        File dataDirectory = new File(databasePath);

        if (dataDirectory.exists()) {
            logger.info("Database directory already exists: " + dataDirectory.getAbsolutePath());
            return false;
        }

        dataDirectory.mkdirs();
        return true;
    }

}

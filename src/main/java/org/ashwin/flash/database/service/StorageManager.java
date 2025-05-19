package org.ashwin.flash.database.service;

import org.ashwin.flash.config.FlashProperties;
import org.ashwin.flash.database.engine.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

class StorageManager {

    private static final Logger logger = LoggerFactory.getLogger(StorageManager.class);
    private static StorageManager manager = null;
    private Map<String, Database> databases;

    private StorageManager() { init(); }

    static synchronized StorageManager getInstance() {
        if (manager == null){
            manager = new StorageManager();
        }
        return manager;
    }

    private void init() {
        databases = new HashMap<>();
        createDataDirectory();
        loadDatabases();
    }

    private void createDataDirectory() {
        File dataDirectory = new File(FlashProperties.DATA_DIR.getDefaultValue());
        if (!dataDirectory.exists()) {
            dataDirectory.mkdirs();
            logger.info("Data directory created: " + dataDirectory.getAbsolutePath());
        }
    }

    private void loadDatabases() {
        File dataDirectory = new File(FlashProperties.DATA_DIR.getDefaultValue());
        File[] files = dataDirectory.listFiles(File::isDirectory);
        if (files != null) {
            for (File file : files) {
                Database database = new Database(file.getName());
                databases.put(file.getName(), database);
            }
        }
    }

    public boolean createDatabase(String databaseName) {
        String databasePath = FlashProperties.DATA_DIR.getDefaultValue() + File.separator + databaseName;
        File dataDirectory = new File(databasePath);

        if (dataDirectory.exists()) {
            logger.info("Database directory already exists: " + dataDirectory.getAbsolutePath());
            return false;
        }

        dataDirectory.mkdirs();
        logger.info("Database directory created: " + dataDirectory.getAbsolutePath());

        String SSTABLE_DIR = databasePath + File.separator + "sstables";
        File sstableDirectory = new File(SSTABLE_DIR);
        sstableDirectory.mkdirs();
        logger.info("SSTable directory created: " + sstableDirectory.getAbsolutePath());

        databases.put(databaseName, new Database(databaseName));
        return true;
    }

    public Database getDatabase(String databaseName) {
        return databases.get(databaseName);
    }

    public void deleteDatabase(String databaseName) {
        String databasePath = FlashProperties.DATA_DIR.getDefaultValue() + File.separator + databaseName;
        File dataDirectory = new File(databasePath);

        if (!dataDirectory.exists()) {
            logger.info("Database directory does not exist: " + dataDirectory.getAbsolutePath());
            return;
        }

        File[] files = dataDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }

        dataDirectory.delete();
        databases.remove(databaseName);
        logger.info("Database directory deleted: " + dataDirectory.getAbsolutePath());
    }

}

package org.ashwin.flash.database.service;

import java.io.File;
import org.ashwin.flash.config.FlashProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StorageManagerTest {

    private final StorageManager storageManager = StorageManager.getInstance();
    private static final String DATABASE_NAME = "testDB";

    @BeforeEach
    public void createDirectories() {
        storageManager.createDatabase(DATABASE_NAME);
    }

    @AfterEach
    public void deleteDirectories() {
        storageManager.deleteDatabase(DATABASE_NAME);
    }

    @Test
    public void test_createDatabaseDirectory_whenDirectoryExists_returnsFalse() {
        String databasePath = FlashProperties.DATA_DIR.getDefaultValue() + File.separator + DATABASE_NAME;
        File dataDirectory = new File(databasePath);

        boolean result = storageManager.createDatabase(DATABASE_NAME);

        assertFalse(result, "Method should return false for existing directory");
        assertTrue(dataDirectory.exists(), "Directory should still exist");
    }

    @Test
    public void test_createDatabaseDirectory_whenDirectoryDoesNotExist() {
        String expectedPath = FlashProperties.DATA_DIR.getDefaultValue() + File.separator + DATABASE_NAME;
        File testDir = new File(expectedPath);

        storageManager.deleteDatabase(DATABASE_NAME);

        boolean result = storageManager.createDatabase(DATABASE_NAME);

        assertTrue(result, "createDatabaseDirectory should return true when creating a new directory");
        assertTrue(testDir.exists(), "The database directory should be created");
    }


    @Test
    public void test_getInstance_createsNewInstanceWhenNull() {
        StorageManager instance = StorageManager.getInstance();
        assertNotNull(instance, "getInstance should return a non-null StorageManager instance");

        StorageManager secondInstance = StorageManager.getInstance();
        assertSame(instance, secondInstance, "Subsequent calls to getInstance should return the same instance");
    }
}

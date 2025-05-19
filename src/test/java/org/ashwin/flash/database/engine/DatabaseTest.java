package org.ashwin.flash.database.engine;

import org.ashwin.flash.database.service.StorageService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {
    Database database;

    @BeforeEach
    public void setup(){
        database = new Database("testDB");
    }

    @AfterAll
    public static void cleanup(){
        StorageService.getInstance().deleteDatabase("testDB");
    }

    @Test
    public void testDatabaseCreation(){
        assertEquals("testDB", database.getDatabaseName(), "Database 'test' should be created");
    }

    @Test
    public void testInsertion(){
        database.put("key1", "value1".getBytes());
        byte[] value = database.get("key1");
        assertEquals("value1", new String(value, StandardCharsets.UTF_8), "The value for 'key1' should be 'value1'");
    }

    @Test
    public void test_NonExistentKey(){
        byte[] value = database.get("key2");
        assertNull(value, "Querying a non-existent key should return null");
    }

    @Test
    public void test_Update(){
        database.put("key1", "value1".getBytes());
        database.put("key1", "value2".getBytes());
        byte[] value = database.get("key1");
        assertEquals("value2", new String(value, StandardCharsets.UTF_8), "The value for 'key1' should be 'value2'");
    }


}

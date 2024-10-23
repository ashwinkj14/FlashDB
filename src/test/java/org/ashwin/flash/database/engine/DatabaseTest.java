package org.ashwin.flash.database.engine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {
    Database database;

    @BeforeEach
    public void setup(){
        database = new Database("test");
    }

    @Test
    public void testDatabaseCreation(){
        assertEquals("test", database.getDatabaseName(), "Database 'test' should be created");
    }

    @Test
    public void testInsertion(){
        database.put("key1", "value1");
        String value = database.get("key1");
        assertEquals("value1", value, "The value for 'key1' should be 'value1'");
    }

    @Test
    public void testNonExistentKey(){
        String value = database.get("key2");
        assertNull(value, "Querying a non-existent key should return null");
    }

    @Test
    public void testUpdate(){
        database.put("key1", "value1");
        database.put("key1", "value2");
        String value = database.get("key1");
        assertEquals("value2", value, "The value for 'key1' should be 'value2'");
    }


}

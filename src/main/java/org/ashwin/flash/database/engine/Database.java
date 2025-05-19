package org.ashwin.flash.database.engine;

import org.ashwin.flash.config.FlashProperties;

public class Database {

    private final String name;
    private final LSMTree lsmTree;

    private void validateKey(String key) {
        if (key == null || key.isEmpty()){
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
    }

    public Database(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Database name cannot be null or empty");
        }
        this.name = name;
        lsmTree = new LSMTree(Integer.parseInt(FlashProperties.MEMTABLE_CAPACITY.getDefaultValue()), name);
    }

    public String getDatabaseName() {
        return name;
    }

    public byte[] get(String key) {
        validateKey(key);
        return lsmTree.get(key);
    }

    public void put(String key, byte[] value) {
        validateKey(key);
        lsmTree.put(key, value);
    }

    public boolean delete(String key) {
        validateKey(key);
        return lsmTree.delete(key);
    }


}

package org.ashwin.flash.database.core;

import org.ashwin.flash.config.FlashProperties;
import org.ashwin.flash.database.scheduler.CompactionScheduler;

import java.util.concurrent.TimeUnit;

public class Database {

    private final String name;
    private final LSMTree lsmTree;
    private final CompactionScheduler compactionScheduler;

    public Database(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Database name cannot be null or empty");
        }
        this.name = name;
        lsmTree = new LSMTree((int)(FlashProperties.MEMTABLE_CAPACITY.getDefaultValue()), name);
        compactionScheduler = new CompactionScheduler(lsmTree.getCompactor());
        compactionScheduler.startScheduledCompaction(5, 30, TimeUnit.MINUTES);
    }

    private void validateKey(String key) {
        if (key == null || key.isEmpty()){
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
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

    public void shutdown() {
        compactionScheduler.shutdown();
    }


}

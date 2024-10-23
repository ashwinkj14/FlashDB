package org.ashwin.flash.database.engine;

import org.ashwin.flash.config.FlashProperties;
import org.ashwin.flash.database.service.StorageService;

public class Database {

    private final String name;
    private final LSMTree lsmTree;

    public Database(String name) {
        this.name = name;
        lsmTree = new LSMTree(Integer.parseInt(FlashProperties.MEMTABLE_CAPACITY.getDefaultValue()));
        StorageService.getInstance().createDatabase(name);
    }

    public String getDatabaseName() {
        return name;
    }

    public String get(String key) {
        return lsmTree.get(key);
    }

    public void put(String key, String value) {
        lsmTree.put(key, value);
    }


}

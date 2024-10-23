package org.ashwin.flash.database.engine;

import java.util.Map;
import java.util.TreeMap;

public class Memtable {
    private final TreeMap<String, String> memtable;
    private final int capacity;

    public Memtable(int capacity) {
        this.memtable = new TreeMap<>();
        this.capacity = capacity;
    }

    public void put(String key, String value) {
        memtable.put(key, value);
    }

    public String get(String key) {
        return memtable.get(key);
    }

    public int size() {
        return memtable.size();
    }

    public boolean isFull(){
        return memtable.size() >= capacity;
    }

    public Map<String, String> flush() {
        Map<String, String> data = new TreeMap<>(memtable);
        memtable.clear();
        return data;
    }

}

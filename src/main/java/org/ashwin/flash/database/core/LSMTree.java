package org.ashwin.flash.database.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class LSMTree {
    private Memtable memTable;
    private final SSTableManager ssTableManager;
    private final Compactor compactor;

    private final int memTableCapacity;

    private static final Logger logger = LoggerFactory.getLogger(LSMTree.class);

    public LSMTree(int memTableCapacity, String databaseName) {
        this.memTableCapacity = memTableCapacity;
        this.memTable = new Memtable(memTableCapacity);
        this.ssTableManager = new SSTableManager(databaseName);
        compactor = new Compactor(ssTableManager);
    }

    public void put(String key, byte[] value) {
        memTable.put(key, value);

        if (memTable.isFull()){
            flushMemTableToSSTable();
        }
    }

    public byte[] get(String key) {
        if (memTable.containsKey(key)) {
            return memTable.get(key);
        }

        try {
            for (SSTable sstable : ssTableManager.getAllSSTables()) {
                SSTable.Pair pair = sstable.read(key);
                if (pair != null) {
                    return pair.value();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public boolean delete(String key) {
        if (get(key) == null) {
            return false;
        }
        memTable.put(key, null);
        return true;
    }

    private void flushMemTableToSSTable() {
        Map<String, byte[]> data = memTable.flush();
        SSTable ssTable = ssTableManager.createSSTableFromMap(data);
        if (ssTable != null) {
            ssTableManager.addSSTable(ssTable);
            this.memTable = new Memtable(memTableCapacity);
        }
    }

    public Compactor getCompactor() {
        return compactor;
    }


}

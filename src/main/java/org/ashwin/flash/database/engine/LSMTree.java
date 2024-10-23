package org.ashwin.flash.database.engine;

import org.ashwin.flash.config.FlashProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LSMTree {
    private Memtable memTable;
    private final List<SSTable> sstables;
    private final int memTableCapacity;
    private int ssTableCount;
    private static final Logger logger = LoggerFactory.getLogger(LSMTree.class);

    public LSMTree(int memTableCapacity) {
        this.memTableCapacity = memTableCapacity;
        memTable = new Memtable(memTableCapacity);
        sstables = new ArrayList<>();
    }

    public void put(String key, String value) {
        memTable.put(key, value);

        if (memTable.isFull()){
            flushMemTableToSSTable();
        }
    }

    public String get(String key) {
        String value = memTable.get(key);
        if (value != null) {
            return value;
        }

        try {
            for (SSTable sstable : sstables) {
                value = sstable.read(key);
                if (value != null) {
                    return value;
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    private void flushMemTableToSSTable() {
        Map<String, String> data = memTable.flush();
        try {
            String ssTableFilePath = FlashProperties.DATA_DIR.getDefaultValue() + "/ssTable_" + (++ssTableCount) + ".txt";
            SSTable ssTable = new SSTable(ssTableFilePath);
            ssTable.write(data);
            sstables.add(ssTable);
        } catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        this.memTable = new Memtable(memTableCapacity);
    }


}

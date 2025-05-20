package org.ashwin.flash.database.core;

import org.ashwin.flash.config.FlashProperties;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Compactor {

    private final SSTableManager sstableManager;

    public Compactor(SSTableManager sstableManager) {
        this.sstableManager = sstableManager;
    }

    public void compact() {
        List<SSTable> sstables = sstableManager.getAllSSTables();
        if (sstables.size() < 3) {
            return;
        }
        Map<String, byte[]> compacted = new TreeMap<>();

        sstables.sort(Comparator.comparingLong(SSTable::getSequenceNumber));

        for (SSTable sst : sstables) {
            Map<String, byte[]> entries = sst.readAll();

            for (Map.Entry<String, byte[]> entry : entries.entrySet()) {
                String key = entry.getKey();
                byte[] value = entry.getValue();

                compacted.put(key, value);

                if(value == null){
                    compacted.remove(key);
                }
            }
        }
        long size = 0;
        long maxSize = (long) FlashProperties.MAX_SSTABLE_SIZE_BYTES.getDefaultValue();
        Map<String, byte[]> intermediateMap = new TreeMap<>();
        for (String key : compacted.keySet()){
            size += key.length();
            size += compacted.get(key).length;
            if (size >= maxSize){
                SSTable newSSTable = sstableManager.createSSTableFromMap(intermediateMap);
                sstableManager.addSSTable(newSSTable);
                System.out.println("Compaction complete. Creating New SSTable: " + newSSTable.getFileName());
                intermediateMap = new TreeMap<>();
            }
            intermediateMap.put(key, compacted.get(key));
        }
        SSTable newSSTable = sstableManager.createSSTableFromMap(intermediateMap);
        sstableManager.addSSTable(newSSTable);
        System.out.println("Compaction complete. Creating New SSTable: " + newSSTable.getFileName());

        sstableManager.removeSSTables(sstables);
    }
}


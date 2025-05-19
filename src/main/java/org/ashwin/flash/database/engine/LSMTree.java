package org.ashwin.flash.database.engine;

import org.ashwin.flash.config.FlashProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LSMTree {
    private Memtable memTable;
    private final List<SSTable> sstables;
    private final int memTableCapacity;
    private int ssTableCount;
    private static final Logger logger = LoggerFactory.getLogger(LSMTree.class);

    private static final String SSTABLE_PREFIX = "sstable-";
    private static final String SSTABLE_SUFFIX = ".db";
    private final String SSTABLE_DIR;


    public LSMTree(int memTableCapacity, String databaseName) {
        SSTABLE_DIR = FlashProperties.DATA_DIR.getDefaultValue() + File.separator +
                databaseName + File.separator + "sstables";
        this.memTableCapacity = memTableCapacity;
        memTable = new Memtable(memTableCapacity);
        sstables = new ArrayList<>();
        loadExistingSSTables();
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
            for (SSTable sstable : sstables) {
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
        try {
            String ssTableFilePath = SSTABLE_DIR + File.separator + SSTABLE_PREFIX + (++ssTableCount) + SSTABLE_SUFFIX;
            SSTable ssTable = new SSTable(ssTableFilePath);
            ssTable.write(data);
            sstables.add(ssTable);
        } catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        this.memTable = new Memtable(memTableCapacity);
    }

    private void loadExistingSSTables() {
        File dir = new File(SSTABLE_DIR);
        if (!dir.exists() || !dir.isDirectory()) {
            logger.warn("SSTable directory does not exist: {}", SSTABLE_DIR);
            return;
        }

        File[] files = dir.listFiles((d, name) -> name.startsWith(SSTABLE_PREFIX) && name.endsWith(SSTABLE_SUFFIX));

        if (files == null || files.length == 0) {
            logger.info("No SSTable files found to load.");
            return;
        }

        Arrays.sort(files, (f1, f2) -> {
            int id1 = extractSSTableId(f1.getName());
            int id2 = extractSSTableId(f2.getName());
            return Integer.compare(id2, id1);
        });

        for (File file : files) {
            SSTable ssTable = new SSTable(file.getAbsolutePath());
            sstables.add(ssTable);

            ssTableCount = Math.max(ssTableCount, extractSSTableId(file.getName()));
        }

        logger.info("Loaded {} SSTables.", sstables.size());
    }

    private int extractSSTableId(String fileName) {
        try {
            String idPart = fileName.substring(SSTABLE_PREFIX.length(), fileName.length() - SSTABLE_SUFFIX.length());
            return Integer.parseInt(idPart);
        } catch (NumberFormatException e) {
            logger.warn("Invalid SSTable file name format: {}", fileName);
            return 0;
        }
    }

}

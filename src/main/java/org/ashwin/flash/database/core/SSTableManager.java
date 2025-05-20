package org.ashwin.flash.database.core;

import org.ashwin.flash.config.FlashProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SSTableManager {
    private static final Logger logger = LoggerFactory.getLogger(SSTableManager.class);

    private static final String SSTABLE_PREFIX = "sstable-";
    private static final String SSTABLE_SUFFIX = ".db";
    private final String SSTABLE_DIR;

    private final List<SSTable> sstables = new ArrayList<>();

    public SSTableManager(String databaseName) {
        SSTABLE_DIR = FlashProperties.DATA_DIR.getDefaultValue() + File.separator + databaseName + File.separator + "sstables";
        loadExistingSSTables();

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
            long sequenceNumber = extractSSTableId(file.getName());
            SSTable ssTable = new SSTable(sequenceNumber, file.getAbsolutePath());
            sstables.add(ssTable);
        }

        logger.info("Loaded {} SSTables.", sstables.size());
    }

    public synchronized void addSSTable(SSTable sst) {
        sstables.add(sst);
    }

    public synchronized List<SSTable> getAllSSTables() {
        return new ArrayList<>(sstables);
    }

    public synchronized void removeSSTables(List<SSTable> toRemove) {
        sstables.removeAll(toRemove);
        toRemove.forEach(SSTable::delete);
    }

    public SSTable createSSTableFromMap(Map<String, byte[]> data){
        try {
            long sequenceNumber = System.currentTimeMillis();
            String ssTableFilePath = SSTABLE_DIR + File.separator + SSTABLE_PREFIX + (sequenceNumber) + SSTABLE_SUFFIX;
            SSTable ssTable = new SSTable(sequenceNumber, ssTableFilePath);
            ssTable.write(data);
            return ssTable;
        } catch (Exception e){
            logger.error(e.getMessage(), e);
        }
        return null;
    }


}

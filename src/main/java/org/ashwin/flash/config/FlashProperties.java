package org.ashwin.flash.config;

public enum FlashProperties {

    DATA_DIR("flash.data.directory", "data"),
    MEMTABLE_CAPACITY("flash.engine.memtable.capacity", 100),
    MAX_SSTABLE_SIZE_BYTES("flash.engine.sstable.max.size.bytes", 64*1024*1024);

    private final String key;
    private final Object defaultValue;

    FlashProperties(String key, Object defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    FlashProperties(String key) {
        this(key, null);
    }

    public String getKey() {
        return key;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}

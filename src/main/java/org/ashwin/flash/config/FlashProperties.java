package org.ashwin.flash.config;

public enum FlashProperties {

    DATA_DIR("flash.data.directory", "data"),
    MEMTABLE_CAPACITY("flash.engine.memtable.capacity", "100");

    private final String key;
    private final String defaultValue;

    FlashProperties(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    FlashProperties(String key) {
        this(key, null);
    }

    public String getKey() {
        return key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}

package com.chyuck.loghashdb.models;

public class DbEntry {
    private final String key;
    private final String value;

    public DbEntry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}

package com.chyuck.loghashdb.models;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.base.Preconditions;

public class DbEntry {
    private final String key;
    private final String value;

    public DbEntry(String key, String value) {
        Preconditions.checkArgument(StringUtils.isNotBlank(key));
        Preconditions.checkArgument(StringUtils.isNotBlank(value));

        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        DbEntry dbEntry = (DbEntry) o;

        return new EqualsBuilder().append(key, dbEntry.key).append(value, dbEntry.value).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(key).append(value).toHashCode();
    }

    @Override
    public String toString() {
        return String.format("{\"%s\":\"%s\"}", key, value);
    }
}

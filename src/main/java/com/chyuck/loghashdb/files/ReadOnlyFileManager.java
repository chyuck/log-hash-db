package com.chyuck.loghashdb.files;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

import org.apache.commons.lang3.StringUtils;

import com.chyuck.loghashdb.models.DbEntry;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;

public class ReadOnlyFileManager {
    protected static final String DELETED_VALUE = StringUtils.EMPTY;

    private final String filePath;

    public ReadOnlyFileManager(String filePath) {
        Preconditions.checkArgument(StringUtils.isNotBlank(filePath));

        this.filePath = filePath;
    }

    public DbEntry get(long position) throws IOException {
        Preconditions.checkArgument(position >= 0);

        try (RandomAccessFile reader = new RandomAccessFile(filePath, "r")) {
            reader.seek(position);

            String key = reader.readUTF();
            String value = reader.readUTF();

            return new DbEntry(key, value);
        }
    }

    public Map<String, Long> loadAllKeysWithPositions() throws IOException {
        return loadAll((value, position) -> position);
    }

    public Map<String, String> loadAllKeysWithValues() throws IOException {
        return loadAll((value, position) -> value);
    }

    private <V> Map<String, V> loadAll(BiFunction<String, Long, V> getResultValue) throws IOException {
        Preconditions.checkNotNull(getResultValue);

        try (RandomAccessFile reader = new RandomAccessFile(filePath, "r")) {
            Map<String, V> results = new HashMap<>();

            for (long position = 0; position < reader.length(); position = reader.getFilePointer()) {

                String key = reader.readUTF();
                Verify.verify(StringUtils.isNotBlank(key));
                String value = reader.readUTF();
                Verify.verifyNotNull(value);

                if (Objects.equals(value, DELETED_VALUE)) {
                    results.remove(key);
                } else {
                    V resultValue = getResultValue.apply(value, position);
                    results.put(key, resultValue);
                }
            }

            return results;
        }
    }

    public String getFilePath() {
        return filePath;
    }
}

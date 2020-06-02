package com.chyuck.loghashdb.services;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import com.chyuck.loghashdb.models.DbEntry;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;

@Service
class FileService implements DisposableBean, Closeable, AutoCloseable {
    private static final String FILE_NAME = "db";
    private static final String DELETED_VALUE = StringUtils.EMPTY;

    private final RandomAccessFile writer;

    public FileService() throws IOException {
        File file = new File(FILE_NAME);
        writer = new RandomAccessFile(file, "rw");
        writer.seek(file.length());
    }

    long update(String key, String value) throws IOException {
        Preconditions.checkArgument(StringUtils.isNotBlank(key));
        Preconditions.checkArgument(StringUtils.isNotBlank(value));

        return append(key, value);
    }

    void delete(String key) throws IOException {
        Preconditions.checkArgument(StringUtils.isNotBlank(key));

        append(key, DELETED_VALUE);
    }

    private synchronized long append(String key, String value) throws IOException {
        Preconditions.checkArgument(StringUtils.isNotBlank(key));
        Preconditions.checkNotNull(value);

        long position = writer.getFilePointer();

        writer.writeUTF(key);
        writer.writeUTF(value);

        return position;
    }

    DbEntry get(long position) throws IOException {
        Preconditions.checkArgument(position >= 0);

        try (RandomAccessFile reader = new RandomAccessFile(FILE_NAME, "r")) {
            reader.seek(position);

            String key = reader.readUTF();
            String value = reader.readUTF();

            return new DbEntry(key, value);
        }
    }

    Map<String, Long> loadAllKeysWithPositions() throws IOException {
        try (RandomAccessFile reader = new RandomAccessFile(FILE_NAME, "r")) {

            Map<String, Long> results = new HashMap<>();

            while (true) {
                long position = reader.getFilePointer();
                if (position >= reader.length())
                    return results;

                String key = reader.readUTF();
                Verify.verify(StringUtils.isNotBlank(key));
                String value = reader.readUTF();
                Verify.verifyNotNull(value);

                if (Objects.equals(value, DELETED_VALUE)) {
                    results.remove(key);
                } else {
                    results.put(key, position);
                }
            }
        }
    }

    @Override
    public void destroy() throws Exception {
        close();
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.chyuck.loghashdb.models.DbEntry;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;

@Service
class FileService implements DisposableBean, Closeable, AutoCloseable {
    private static final String DELETED_VALUE = StringUtils.EMPTY;

    private final RandomAccessFile writer;

    private final String dataFile;

    public FileService(@Value("${app.data.dir}") String directory) throws IOException {
        Preconditions.checkArgument(StringUtils.isNotBlank(directory));

        this.dataFile = directory + "/data";

        makeSureDirectoryExists(directory);
        writer = createWriter(dataFile);
    }

    private static RandomAccessFile createWriter(String dataFile) throws IOException {
        Preconditions.checkArgument(StringUtils.isNotBlank(dataFile));

        File file = new File(dataFile);
        RandomAccessFile writer = new RandomAccessFile(file, "rw");
        writer.seek(file.length());

        return writer;
    }

    private static void makeSureDirectoryExists(String directory) {
        Preconditions.checkArgument(StringUtils.isNotBlank(directory));

        File file = new File(directory);
        file.mkdirs();
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

        try (RandomAccessFile reader = new RandomAccessFile(dataFile, "r")) {
            reader.seek(position);

            String key = reader.readUTF();
            String value = reader.readUTF();

            return new DbEntry(key, value);
        }
    }

    Map<String, Long> loadAllKeysWithPositions() throws IOException {
        try (RandomAccessFile reader = new RandomAccessFile(dataFile, "r")) {

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

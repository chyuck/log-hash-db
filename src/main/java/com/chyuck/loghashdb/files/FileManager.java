package com.chyuck.loghashdb.files;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

public class FileManager extends ReadOnlyFileManager implements Closeable, AutoCloseable {

    private final RandomAccessFile writer;

    public FileManager(String filePath) throws IOException {
        super(filePath);

        this.writer = createWriter(filePath);
    }

    private static RandomAccessFile createWriter(String filePath) throws IOException {
        Preconditions.checkArgument(StringUtils.isNotBlank(filePath));

        File file = new File(filePath);
        RandomAccessFile writer = new RandomAccessFile(file, "rw");
        writer.seek(file.length());

        return writer;
    }

    public long update(String key, String value) throws IOException {
        Preconditions.checkArgument(StringUtils.isNotBlank(key));
        Preconditions.checkArgument(StringUtils.isNotBlank(value));

        return append(key, value);
    }

    public void delete(String key) throws IOException {
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

    @Override
    public void close() throws IOException {
        writer.close();
    }
}

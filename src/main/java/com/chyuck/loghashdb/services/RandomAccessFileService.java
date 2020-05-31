package com.chyuck.loghashdb.services;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import com.chyuck.loghashdb.models.DbEntry;
import com.google.common.base.Preconditions;

@Service
public class RandomAccessFileService implements DisposableBean, Closeable, AutoCloseable {
    private static final String FILE_NAME = "db";

    private final RandomAccessFile writer;

    public RandomAccessFileService() throws IOException {
        File file = new File(FILE_NAME);
        writer = new RandomAccessFile(file, "rw");
        writer.seek(file.length());
    }

    public synchronized long append(DbEntry dbEntry) throws IOException {
        Preconditions.checkNotNull(dbEntry);

        long position = writer.getFilePointer();

        writer.writeUTF(dbEntry.getKey());
        writer.writeUTF(dbEntry.getValue());

        return position;
    }

    public DbEntry get(long position) throws IOException {
        Preconditions.checkArgument(position >= 0);

        try (RandomAccessFile reader = new RandomAccessFile(FILE_NAME, "r")) {
            reader.seek(position);

            String key = reader.readUTF();
            String value = reader.readUTF();

            return new DbEntry(key, value);
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

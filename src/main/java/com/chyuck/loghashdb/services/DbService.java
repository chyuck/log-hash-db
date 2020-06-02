package com.chyuck.loghashdb.services;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chyuck.loghashdb.models.DbEntry;
import com.google.common.base.Preconditions;
import com.google.common.base.Verify;

@Service
public class DbService {
    private final FileService fileService;

    private final Map<String, Long> hashIndex;

    @Autowired
    public DbService(FileService fileService) throws IOException {
        Preconditions.checkNotNull(fileService);

        this.fileService = fileService;

        hashIndex = loadHashIndex();
    }

    private Map<String, Long> loadHashIndex() throws IOException {
        Map<String, Long> keysWithPositions = fileService.loadAllKeysWithPositions();
        Verify.verifyNotNull(keysWithPositions);

        return new ConcurrentHashMap<>(keysWithPositions);
    }

    public DbEntry get(String key) throws IOException {
        Preconditions.checkArgument(StringUtils.isNotBlank(key));

        Long position = hashIndex.get(key);
        if (position == null)
            return null;

        DbEntry dbEntry = fileService.get(position);
        Verify.verifyNotNull(dbEntry);
        Verify.verify(Objects.equals(dbEntry.getKey(), key));
        Verify.verify(StringUtils.isNotBlank(dbEntry.getValue()));

        return dbEntry;
    }

    public DbEntry update(DbEntry dbEntry) throws IOException {
        Preconditions.checkNotNull(dbEntry);
        Preconditions.checkArgument(StringUtils.isNotBlank(dbEntry.getKey()));
        Preconditions.checkArgument(StringUtils.isNotBlank(dbEntry.getValue()));

        long position = fileService.update(dbEntry.getKey(), dbEntry.getValue());
        hashIndex.put(dbEntry.getKey(), position);

        return dbEntry;
    }

    public DbEntry delete(String key) throws IOException {
        Preconditions.checkArgument(StringUtils.isNotBlank(key));

        if (!hashIndex.containsKey(key))
            return null;

        hashIndex.remove(key);

        fileService.delete(key);

        return new DbEntry(key, null);
    }
}

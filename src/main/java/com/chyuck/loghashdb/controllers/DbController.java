package com.chyuck.loghashdb.controllers;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.chyuck.loghashdb.models.DbEntry;
import com.chyuck.loghashdb.services.DbService;
import com.google.common.base.Preconditions;

@RestController
public class DbController {

    private final DbService dbService;

    @Autowired
    public DbController(DbService dbService) {
        Preconditions.checkNotNull(dbService);

        this.dbService = dbService;
    }

    @GetMapping("/db/{key}")
    public DbEntry get(@PathVariable(value = "key") String key) throws IOException {
        if (StringUtils.isBlank(key))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "'key' is not provided");

        DbEntry dbEntry = dbService.get(key);
        if (dbEntry == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "'key' is not found");

        return dbEntry;
    }

    @PostMapping("/db")
    public DbEntry update(@RequestBody DbEntry entry) throws IOException {
        if (entry == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'key'/'value' is not provided");

        if (StringUtils.isBlank(entry.getKey()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'key' is not provided");

        if (StringUtils.isBlank(entry.getValue()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'value' is not provided");

        DbEntry dbEntry = new DbEntry(entry.getKey(), entry.getValue());

        return dbService.update(dbEntry);
    }

    @DeleteMapping("/db/{key}")
    public DbEntry delete(@PathVariable(value = "key") String key) throws IOException {
        if (StringUtils.isBlank(key)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "'key' is not provided");
        }

        DbEntry dbEntry = dbService.delete(key);
        if (dbEntry == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "'key' is not found");

        return dbEntry;
    }
}

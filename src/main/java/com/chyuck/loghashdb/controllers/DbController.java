package com.chyuck.loghashdb.controllers;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.chyuck.loghashdb.models.DbEntry;

@RestController
public class DbController {

    private static final Map<String, String> KEY_VALUES = new ConcurrentHashMap<>();

    @GetMapping("/db/{key}")
    public DbEntry get(@PathVariable(value = "key") String key) {
        if (StringUtils.isBlank(key)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "'key' is not provided");
        }

        String value = KEY_VALUES.get(key);
        if (value == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "'key' is not found");
        }

        return new DbEntry(key, value);
    }

    @PostMapping("/db")
    public DbEntry update(@RequestBody DbEntry entry) {
        if (entry == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'key'/'value' is not provided");
        }

        if (StringUtils.isBlank(entry.getKey())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'key' is not provided");
        }

        if (StringUtils.isBlank(entry.getValue())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'value' is not provided");
        }

        KEY_VALUES.put(entry.getKey(), entry.getValue());

        return new DbEntry(entry.getKey(), entry.getValue());
    }

    @DeleteMapping("/db/{key}")
    public DbEntry delete(@PathVariable(value = "key") String key) {
        if (StringUtils.isBlank(key)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "'key' is not provided");
        }

        if (KEY_VALUES.remove(key) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "'key' is not found");
        }

        return new DbEntry(null, null);
    }
}

package com.chyuck.loghashdb.services;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.chyuck.loghashdb.models.DbEntry;

@SpringBootTest
class FileServiceTests {

    @Autowired
    private FileService fileService;

    @Test
    void testUpdate() throws IOException {
        // act
        long position = fileService.update("TEST_KEY", "TEST_VALUE");

        // assert
        Assertions.assertTrue(position >= 0);
    }

    @Test
    void testDelete() throws IOException {
        // act
        fileService.delete("TEST_KEY");
    }

    @Test
    void testGet() throws IOException {
        // arrange
        long position1 = fileService.update("TEST_KEY1", "TEST_VALUE1");
        long position2 = fileService.update("TEST_KEY2", "TEST_VALUE2");
        long position3 = fileService.update("TEST_KEY3", "TEST_VALUE3");

        // act
        DbEntry result2 = fileService.get(position2);
        DbEntry result3 = fileService.get(position3);
        DbEntry result1 = fileService.get(position1);

        // assert
        Assertions.assertEquals("TEST_KEY1", result1.getKey());
        Assertions.assertEquals("TEST_VALUE1", result1.getValue());
        Assertions.assertEquals("TEST_KEY2", result2.getKey());
        Assertions.assertEquals("TEST_VALUE2", result2.getValue());
        Assertions.assertEquals("TEST_KEY3", result3.getKey());
        Assertions.assertEquals("TEST_VALUE3", result3.getValue());
    }

    @Test
    void testLoadAllKeysWithPositions() throws IOException {
        // act
        Map<String, Long> keysWithPositions = fileService.loadAllKeysWithPositions();
    }
}

package com.chyuck.loghashdb.services;

import java.io.IOException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.chyuck.loghashdb.models.DbEntry;

@SpringBootTest
class RandomAccessFileServiceTests {

    @Autowired
    private RandomAccessFileService randomAccessFileService;

    @Test
    void testAppend() throws IOException {
        // arrange
        DbEntry dbEntry = new DbEntry("TEST_KEY", "TEST_VALUE");

        // act
        long position = randomAccessFileService.append(dbEntry);

        // assert
        Assertions.assertTrue(position >= 0);
    }

    @Test
    void testAppendTombstone() throws IOException {
        // arrange
        DbEntry dbEntry = new DbEntry("TEST_KEY", null);

        // act
        long position = randomAccessFileService.append(dbEntry);

        // assert
        Assertions.assertTrue(position >= 0);
    }

    @Test
    void testGet() throws IOException {
        // arrange
        DbEntry dbEntry1 = new DbEntry("TEST_KEY1", "TEST_VALUE1");
        long position1 = randomAccessFileService.append(dbEntry1);
        DbEntry dbEntry2 = new DbEntry("TEST_KEY2", "TEST_VALUE2");
        long position2 = randomAccessFileService.append(dbEntry2);
        DbEntry dbEntry3 = new DbEntry("TEST_KEY3", "TEST_VALUE3");
        long position3 = randomAccessFileService.append(dbEntry3);

        // act
        DbEntry result2 = randomAccessFileService.get(position2);
        DbEntry result3 = randomAccessFileService.get(position3);
        DbEntry result1 = randomAccessFileService.get(position1);

        // assert
        Assertions.assertEquals(dbEntry1, result1);
        Assertions.assertEquals(dbEntry2, result2);
        Assertions.assertEquals(dbEntry3, result3);
    }
}

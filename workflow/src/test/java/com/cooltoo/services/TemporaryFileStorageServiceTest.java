package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/4/19.
 */
@Transactional
public class TemporaryFileStorageServiceTest extends AbstractCooltooTest{

    private static final Logger logger = LoggerFactory.getLogger(TemporaryFileStorageServiceTest.class.getName());

    @Autowired
    private TemporaryFileStorageService temporaryStorageService;
    @Autowired
    @Qualifier("StorageService")
    private StorageService storageService;

    @Test
    public void testAddTempFile() {
        String token        = "1111111111111111111111";
        String fileName     = "aaa.png";
        String storageDir   = storageService.getStoragePath();
        String temporaryDir = temporaryStorageService.getTmpPath();
        ByteArrayInputStream file = new ByteArrayInputStream(fileName.getBytes());

        //============================
        //     test create
        //============================
        String relativeFilePath = temporaryStorageService.cacheTemporaryFile(token, fileName, file);
        Assert.assertNotNull(relativeFilePath);
        logger.info("relative file path ==>{}", relativeFilePath);

        boolean existRelativeFile = temporaryStorageService.existTmpFile(relativeFilePath);
        Assert.assertEquals(true, existRelativeFile);

        //============================
        //      test clean
        //============================
        temporaryStorageService.cleanTokenCachedFile(token);

        existRelativeFile = temporaryStorageService.existTmpFile(relativeFilePath);
        Assert.assertEquals(false, existRelativeFile);

        //============================
        //    test move
        //============================
        file = new ByteArrayInputStream(fileName.getBytes());

        // create
        relativeFilePath = temporaryStorageService.cacheTemporaryFile(token, fileName, file);
        Assert.assertNotNull(relativeFilePath);
        logger.info("relative file path ==>{}", relativeFilePath);

        // new cache exist
        existRelativeFile = temporaryStorageService.existTmpFile(relativeFilePath);
        Assert.assertEquals(true, existRelativeFile);

        // move to storage
        String       baseUrl = "http://143.23.22.343:8080/ufdsa/tme/";
        String       fileUrl = baseUrl+relativeFilePath;
        List<String> filePath = new ArrayList<>();
        filePath.add(fileUrl);
        Map<String, String> moved = temporaryStorageService.moveToStorage(token, filePath);
        Assert.assertNotNull(moved);
        logger.info("file moved ===>{}", moved);
        boolean exist = storageService.isFileExist(relativeFilePath);
        Assert.assertTrue(exist);

        // test move to temporary
        String  storageFilePath= storageDir + File.separator + relativeFilePath;
        filePath.clear();
        filePath.add(storageFilePath);
        temporaryStorageService.moveToTemporary(token, filePath);
        exist = storageService.isFileExist(relativeFilePath);
        Assert.assertFalse(exist);
        exist = temporaryStorageService.existTmpFile(relativeFilePath);
        Assert.assertTrue(exist);

        // test clean
        temporaryStorageService.cleanTokenCachedFile(token);
        existRelativeFile = temporaryStorageService.existTmpFile(relativeFilePath);
        Assert.assertEquals(false, existRelativeFile);
    }

    @Test
    public void testDeleteStorageFile() {
        String token     = "1234567890";
        String imageName = "fafdsafdsafdsafdsafdsafdsa.png";
        ByteArrayInputStream image = new ByteArrayInputStream(imageName.getBytes());

        // create cache files
        String relative_01 = temporaryStorageService.cacheTemporaryFile(token, imageName, image);
        String relative_02 = temporaryStorageService.cacheTemporaryFile(token, imageName, image);
        String relative_03 = temporaryStorageService.cacheTemporaryFile(token, imageName, image);

        // move cache files to storage
        List<String> tempFile = new ArrayList<>();
        tempFile.add(relative_01);
        tempFile.add(relative_02);
        tempFile.add(relative_03);
        temporaryStorageService.moveToStorage(token, tempFile);

        // judge storage files exist
        boolean exist = storageService.isFileExist(relative_01);
        Assert.assertTrue(exist);
        exist = storageService.isFileExist(relative_02);
        Assert.assertTrue(exist);
        exist = storageService.isFileExist(relative_03);
        Assert.assertTrue(exist);

        // delete storage files
        List<String> storageFile = tempFile;
        temporaryStorageService.deleteStorageFile(storageFile);

        // judge storage files do not exist
        exist = storageService.isFileExist(relative_01);
        Assert.assertFalse(exist);
        exist = storageService.isFileExist(relative_02);
        Assert.assertFalse(exist);
        exist = storageService.isFileExist(relative_03);
        Assert.assertFalse(exist);
    }
}

package com.cooltoo.services.file;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.util.VerifyUtil;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 16/4/26.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup("classpath:/com/cooltoo/services/file_storage_data.xml")
})
public class SecretFileStorageServiceTest extends AbstractCooltooTest {


    private static final Logger logger = LoggerFactory.getLogger(SecretFileStorageServiceTest.class.getName());

    @Autowired
    private SecretFileStorageService secretStorage;

    @Test
    public void testAddDeleteGetExistFile() {
        String officialNginxPath = secretStorage.getNginxRelativePath();
        String fileName = "secret_test_aaa.png";
        ByteArrayInputStream file = new ByteArrayInputStream(fileName.getBytes());

        long fileId = secretStorage.addFile(1, fileName, file);
        Assert.assertTrue(fileId > 0);
        String filePath = secretStorage.getFilePath(1);
        Assert.assertTrue(VerifyUtil.isStringEmpty(filePath));
        filePath = secretStorage.getFilePath(fileId);

        boolean existFile = secretStorage.fileExist(fileId);
        Assert.assertEquals(true, existFile);
        existFile = secretStorage.fileExist(filePath);
        Assert.assertEquals(true, existFile);

        filePath = secretStorage.getFilePath(fileId);
        Assert.assertTrue(filePath.startsWith(officialNginxPath));

        secretStorage.deleteFile(fileId);
        existFile = secretStorage.fileExist(fileId);
        Assert.assertEquals(false, existFile);
        existFile = secretStorage.fileExist(filePath);
        Assert.assertEquals(false, existFile);
    }

    @Test
    public void testDeleteGetFiles() {
        String officialNginxPath = secretStorage.getNginxRelativePath();
        List<Long> fileIds = new ArrayList<>();

        String  fileName  = null;
        ByteArrayInputStream file = null;
        long    fileId    = 0;
        boolean existFile = false;
        String  filePath  = "";
        for (int i=0; i<3; i++) {
            fileName  = "secret_test_aaa"+i+".png";
            file      = new ByteArrayInputStream(fileName.getBytes());
            fileId    = secretStorage.addFile(-1, fileName, file);
            existFile = secretStorage.fileExist(fileId);
            filePath  = secretStorage.getFilePath(fileId);
            Assert.assertTrue(fileId > 0);
            Assert.assertEquals(true, existFile);
            Assert.assertTrue(filePath.startsWith(officialNginxPath));
            fileIds.add(fileId);
            logger.info("fileid={} filepath={}", fileId, filePath);
        }
        Map<Long, String> id2Path = secretStorage.getFilePath(fileIds);
        Assert.assertEquals(3, id2Path.size());
        logger.info("fileid-->filepath={}", id2Path);

        secretStorage.deleteFiles(fileIds);

        for (int i=0; i<3; i++) {
            existFile = secretStorage.fileExist(fileIds.get(i));
            Assert.assertEquals(false, existFile);
        }
    }

    @Test
    public void testGetRelativePath() {
        String relativePath1 = "11/fdsafdjklfjdslajfkdsajflk";
        String relativePath2 = "11/fdsafdjklfjdslajfkdsajflk";
        String absolutePath1 = "http://jkfdsivjnkcanfejwvnid/fjdisfds/11/fdsafdjklfjdslajfkdsajflk";
        String absolutePath2 = "http://jkfdsivjnk.ca/fjdisfds/11/fdsafdjklfjdslajfkdsajflk";
        String relaPath1 = secretStorage.getRelativePathInStorage(absolutePath1);
        Assert.assertEquals(relativePath1, relaPath1);

        String[] array = new String[]{absolutePath1, absolutePath2};
        List<String> absolutePaths = Arrays.asList(array);
        Map<String, String> absolute2relative = secretStorage.getRelativePathInStorage(absolutePaths);
        Assert.assertEquals(relativePath1, absolute2relative.get(absolutePath1));
        Assert.assertEquals(relativePath2, absolute2relative.get(absolutePath2));
    }

    @Test
    public void testGetRelativePathInBase() {
        String nginxPath = secretStorage.getNginxRelativePath();
        String relativePath1 = "11/fdsafdjklfjdslajfkdsajflk";
        String relativePath2 = "11/fdsafdjklfjdslajfkdsajflk";
        String absolutePath1 = "http://jkfdsivjnkcanfejwvnid/fjdisfds/11/fdsafdjklfjdslajfkdsajflk";
        String absolutePath2 = "http://jkfdsivjnk.ca/fjdisfds/11/fdsafdjklfjdslajfkdsajflk";
        String relaPath1 = secretStorage.getRelativePathInBase(absolutePath1);
        Assert.assertEquals(nginxPath+relativePath1, relaPath1);

        String[] array = new String[]{absolutePath1, absolutePath2};
        List<String> absolutePaths = Arrays.asList(array);
        Map<String, String> absolute2relative = secretStorage.getRelativePathInBase(absolutePaths);
        Assert.assertEquals(nginxPath+relativePath1, absolute2relative.get(absolutePath1));
        Assert.assertEquals(nginxPath+relativePath2, absolute2relative.get(absolutePath2));
    }
}

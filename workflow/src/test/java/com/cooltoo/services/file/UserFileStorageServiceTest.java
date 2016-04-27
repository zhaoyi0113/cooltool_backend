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
public class UserFileStorageServiceTest extends AbstractCooltooTest {


    private static final Logger logger = LoggerFactory.getLogger(UserFileStorageServiceTest.class.getName());

    @Autowired
    private UserFileStorageService userStorage;

    @Test
    public void testAddDeleteGetExistFile() {
        String officialNginxPath = userStorage.getNginxRelativePath();
        String fileName = "user_test_aaa.png";
        ByteArrayInputStream file = new ByteArrayInputStream(fileName.getBytes());

        long fileId = userStorage.addFile(1, fileName, file);
        Assert.assertTrue(fileId > 0);
        String filePath = userStorage.getFilePath(1);
        Assert.assertTrue(VerifyUtil.isStringEmpty(filePath));
        filePath = userStorage.getFilePath(fileId);

        boolean existFile = userStorage.fileExist(fileId);
        Assert.assertEquals(true, existFile);
        existFile = userStorage.fileExist(filePath);
        Assert.assertEquals(true, existFile);

        filePath = userStorage.getFilePath(fileId);
        Assert.assertTrue(filePath.startsWith(officialNginxPath));

        userStorage.deleteFile(fileId);
        existFile = userStorage.fileExist(fileId);
        Assert.assertEquals(false, existFile);
        existFile = userStorage.fileExist(filePath);
        Assert.assertEquals(false, existFile);
    }

    @Test
    public void testDeleteGetFiles() {
        String officialNginxPath = userStorage.getNginxRelativePath();
        List<Long> fileIds = new ArrayList<>();

        String  fileName  = null;
        ByteArrayInputStream file = null;
        long    fileId    = 0;
        boolean existFile = false;
        String  filePath  = "";
        for (int i=0; i<3; i++) {
            fileName  = "secret_test_aaa"+i+".png";
            file      = new ByteArrayInputStream(fileName.getBytes());
            fileId    = userStorage.addFile(-1, fileName, file);
            existFile = userStorage.fileExist(fileId);
            filePath  = userStorage.getFilePath(fileId);
            Assert.assertTrue(fileId > 0);
            Assert.assertEquals(true, existFile);
            Assert.assertTrue(filePath.startsWith(officialNginxPath));
            fileIds.add(fileId);
            logger.info("fileid={} filepath={}", fileId, filePath);
        }
        Map<Long, String> id2Path = userStorage.getFilePath(fileIds);
        Assert.assertEquals(3, id2Path.size());
        logger.info("fileid-->filepath={}", id2Path);

        userStorage.deleteFiles(fileIds);

        for (int i=0; i<3; i++) {
            existFile = userStorage.fileExist(fileIds.get(i));
            Assert.assertEquals(false, existFile);
        }
    }

    @Test
    public void testGetRelativePath() {
        String relativePath1 = "11/fdsafdjklfjdslajfkdsajflk";
        String relativePath2 = "11/fdsafdjklfjdslajfkdsajflk";
        String absolutePath1 = "http://jkfdsivjnkcanfejwvnid/fjdisfds/11/fdsafdjklfjdslajfkdsajflk";
        String absolutePath2 = "http://jkfdsivjnk.ca/fjdisfds/11/fdsafdjklfjdslajfkdsajflk";
        String relaPath1 = userStorage.getRelativePathInStorage(absolutePath1);
        Assert.assertEquals(relativePath1, relaPath1);

        String[] array = new String[]{absolutePath1, absolutePath2};
        List<String> absolutePaths = Arrays.asList(array);
        Map<String, String> absolute2relative = userStorage.getRelativePathInStorage(absolutePaths);
        Assert.assertEquals(relativePath1, absolute2relative.get(absolutePath1));
        Assert.assertEquals(relativePath2, absolute2relative.get(absolutePath2));
    }

    @Test
    public void testGetRelativePathInBase() {
        String nginxPath = userStorage.getNginxRelativePath();
        String relativePath1 = "11/fdsafdjklfjdslajfkdsajflk";
        String relativePath2 = "11/fdsafdjklfjdslajfkdsajflk";
        String absolutePath1 = "http://jkfdsivjnkcanfejwvnid/fjdisfds/11/fdsafdjklfjdslajfkdsajflk";
        String absolutePath2 = "http://jkfdsivjnk.ca/fjdisfds/11/fdsafdjklfjdslajfkdsajflk";
        String relaPath1 = userStorage.getRelativePathInBase(absolutePath1);
        Assert.assertEquals(nginxPath+relativePath1, relaPath1);

        String[] array = new String[]{absolutePath1, absolutePath2};
        List<String> absolutePaths = Arrays.asList(array);
        Map<String, String> absolute2relative = userStorage.getRelativePathInBase(absolutePaths);
        Assert.assertEquals(nginxPath+relativePath1, absolute2relative.get(absolutePath1));
        Assert.assertEquals(nginxPath+relativePath2, absolute2relative.get(absolutePath2));
    }
}

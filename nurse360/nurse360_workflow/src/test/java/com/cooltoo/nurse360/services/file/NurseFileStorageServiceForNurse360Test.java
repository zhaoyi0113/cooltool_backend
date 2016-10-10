package com.cooltoo.nurse360.services.file;

import com.cooltoo.nurse360.AbstractCooltooTest;
import com.cooltoo.nurse360.service.file.NurseFileStorageServiceForNurse360;
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
 * Created by zhaolisong on 16/10/9.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup("classpath:/com/cooltoo/nurse360/services/file_storage_data.xml")
})
public class NurseFileStorageServiceForNurse360Test extends AbstractCooltooTest {


    private static final Logger logger = LoggerFactory.getLogger(NurseFileStorageServiceForNurse360Test.class.getName());

    @Autowired
    private NurseFileStorageServiceForNurse360 nurseStorage;

    @Test
    public void testAddDeleteGetExistFile() {
        String officialNginxPath = nurseStorage.getNginxRelativePath();
        String fileName = "user_test_aaa.png";
        ByteArrayInputStream file = new ByteArrayInputStream(fileName.getBytes());

        long fileId = nurseStorage.addFile(1, fileName, file);
        Assert.assertTrue(fileId > 0);
        String filePath = nurseStorage.getFilePath(1);
        Assert.assertTrue(VerifyUtil.isStringEmpty(filePath));
        filePath = nurseStorage.getFilePath(fileId);

        boolean existFile = nurseStorage.fileExist(fileId);
        Assert.assertEquals(true, existFile);
        existFile = nurseStorage.fileExist(filePath);
        Assert.assertEquals(true, existFile);

        filePath = nurseStorage.getFilePath(fileId);
        Assert.assertTrue(filePath.startsWith(officialNginxPath));

        nurseStorage.deleteFile(fileId);
        existFile = nurseStorage.fileExist(fileId);
        Assert.assertEquals(false, existFile);
        existFile = nurseStorage.fileExist(filePath);
        Assert.assertEquals(false, existFile);
    }

    @Test
    public void testDeleteGetFiles() {
        String officialNginxPath = nurseStorage.getNginxRelativePath();
        List<Long> fileIds = new ArrayList<>();

        String  fileName  = null;
        ByteArrayInputStream file = null;
        long    fileId    = 0;
        boolean existFile = false;
        String  filePath  = "";
        for (int i=0; i<3; i++) {
            fileName  = "secret_test_aaa"+i+".png";
            file      = new ByteArrayInputStream(fileName.getBytes());
            fileId    = nurseStorage.addFile(-1, fileName, file);
            existFile = nurseStorage.fileExist(fileId);
            filePath  = nurseStorage.getFilePath(fileId);
            Assert.assertTrue(fileId > 0);
            Assert.assertEquals(true, existFile);
            Assert.assertTrue(filePath.startsWith(officialNginxPath));
            fileIds.add(fileId);
            logger.info("fileid={} filepath={}", fileId, filePath);
        }
        Map<Long, String> id2Path = nurseStorage.getFileUrl(fileIds);
        Assert.assertEquals(3, id2Path.size());
        logger.info("fileid-->filepath={}", id2Path);

        nurseStorage.deleteFiles(fileIds);

        for (int i=0; i<3; i++) {
            existFile = nurseStorage.fileExist(fileIds.get(i));
            Assert.assertEquals(false, existFile);
        }
    }

    @Test
    public void testGetRelativePath() {
        String relativePath1 = "11/fdsafdjklfjdslajfkdsajflk";
        String relativePath2 = "11/fdsafdjklfjdslajfkdsajflk";
        String absolutePath1 = "http://jkfdsivjnkcanfejwvnid/fjdisfds/11/fdsafdjklfjdslajfkdsajflk";
        String absolutePath2 = "http://jkfdsivjnk.ca/fjdisfds/11/fdsafdjklfjdslajfkdsajflk";
        String relaPath1 = nurseStorage.getRelativePathInStorage(absolutePath1);
        Assert.assertEquals(relativePath1, relaPath1.replace('\\', '/'));

        String[] array = new String[]{absolutePath1, absolutePath2};
        List<String> absolutePaths = Arrays.asList(array);
        Map<String, String> absolute2relative = nurseStorage.getRelativePathInStorage(absolutePaths);
        Assert.assertEquals(relativePath1, absolute2relative.get(absolutePath1).replace('\\', '/'));
        Assert.assertEquals(relativePath2, absolute2relative.get(absolutePath2).replace('\\', '/'));
    }

    @Test
    public void testGetRelativePathInBase() {
        String nginxPath = nurseStorage.getNginxRelativePath();
        String relativePath1 = "11/fdsafdjklfjdslajfkdsajflk";
        String relativePath2 = "11/fdsafdjklfjdslajfkdsajflk";
        String absolutePath1 = "http://jkfdsivjnkcanfejwvnid/fjdisfds/11/fdsafdjklfjdslajfkdsajflk";
        String absolutePath2 = "http://jkfdsivjnk.ca/fjdisfds/11/fdsafdjklfjdslajfkdsajflk";
        String relaPath1 = nurseStorage.getRelativePathInBase(absolutePath1);
        Assert.assertEquals(nginxPath+relativePath1, relaPath1.replace('\\', '/'));

        String[] array = new String[]{absolutePath1, absolutePath2};
        List<String> absolutePaths = Arrays.asList(array);
        Map<String, String> absolute2relative = nurseStorage.getRelativePathInBase(absolutePaths);
        Assert.assertEquals(nginxPath+relativePath1, absolute2relative.get(absolutePath1).replace('\\', '/'));
        Assert.assertEquals(nginxPath+relativePath2, absolute2relative.get(absolutePath2).replace('\\', '/'));
    }
}

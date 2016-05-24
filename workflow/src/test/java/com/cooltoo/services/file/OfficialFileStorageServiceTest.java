package com.cooltoo.services.file;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.util.FileUtil;
import com.cooltoo.util.VerifyUtil;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.*;

/**
 * Created by zhaolisong on 16/4/26.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup("classpath:/com/cooltoo/services/file_storage_data.xml")
})
public class OfficialFileStorageServiceTest extends AbstractCooltooTest {

    private static final Logger logger = LoggerFactory.getLogger(OfficialFileStorageServiceTest.class.getName());

    @Autowired
    private OfficialFileStorageService officialStorage;

    @Value("${storage.base.path}")
    private String storageBasePath;

    @Test
    public void testAddDeleteGetExistFile() {
        String officialNginxPath = officialStorage.getNginxRelativePath();
        String fileName = "official_test_aaa.png";
        ByteArrayInputStream file = new ByteArrayInputStream(fileName.getBytes());

        long fileId = officialStorage.addFile(1, fileName, file);
        Assert.assertTrue(fileId > 0);
        String filePath = officialStorage.getFilePath(1);
        Assert.assertTrue(VerifyUtil.isStringEmpty(filePath));
        filePath = officialStorage.getFilePath(fileId);

        boolean existFile = officialStorage.fileExist(fileId);
        Assert.assertEquals(true, existFile);
        existFile = officialStorage.fileExist(filePath);
        Assert.assertEquals(true, existFile);

        filePath = officialStorage.getFilePath(fileId);
        Assert.assertTrue(filePath.startsWith(officialNginxPath));

        officialStorage.deleteFile(fileId);
        existFile = officialStorage.fileExist(fileId);
        Assert.assertEquals(false, existFile);
        existFile = officialStorage.fileExist(filePath);
        Assert.assertEquals(false, existFile);
    }

    @Test
    public void testGetRelativePath() {
        String relativePath1 = "11/fdsafdjklfjdslajfkdsajflk";
        String relativePath2 = "11/fdsafdjklfjdslajfkdsajflk";
        String absolutePath1 = "http://jkfdsivjnkcanfejwvnid/fjdisfds/11/fdsafdjklfjdslajfkdsajflk";
        String absolutePath2 = "http://jkfdsivjnk.ca/fjdisfds/11/fdsafdjklfjdslajfkdsajflk";
        String relaPath1 = officialStorage.getRelativePathInStorage(absolutePath1);
        Assert.assertEquals(relativePath1, relaPath1.replace('\\', '/'));

        String[] array = new String[]{absolutePath1, absolutePath2};
        List<String> absolutePaths = Arrays.asList(array);
        Map<String, String> absolute2relative = officialStorage.getRelativePathInStorage(absolutePaths);
        Assert.assertEquals(relativePath1, absolute2relative.get(absolutePath1).replace('\\', '/'));
        Assert.assertEquals(relativePath2, absolute2relative.get(absolutePath2).replace('\\', '/'));
    }

    @Test
    public void testGetRelativePathInBase() {
        String nginxPath = officialStorage.getNginxRelativePath();
        String relativePath1 = "11/fdsafdjklfjdslajfkdsajflk";
        String relativePath2 = "11/fdsafdjklfjdslajfkdsajflk";
        String absolutePath1 = "http://jkfdsivjnkcanfejwvnid/fjdisfds/11/fdsafdjklfjdslajfkdsajflk";
        String absolutePath2 = "http://jkfdsivjnk.ca/fjdisfds/11/fdsafdjklfjdslajfkdsajflk";
        String relaPath1 = officialStorage.getRelativePathInBase(absolutePath1);
        Assert.assertEquals(nginxPath+relativePath1, relaPath1.replace('\\', '/'));

        String[] array = new String[]{absolutePath1, absolutePath2};
        List<String> absolutePaths = Arrays.asList(array);
        Map<String, String> absolute2relative = officialStorage.getRelativePathInBase(absolutePaths);
        Assert.assertEquals(nginxPath+relativePath1, absolute2relative.get(absolutePath1).replace('\\', '/'));
        Assert.assertEquals(nginxPath+relativePath2, absolute2relative.get(absolutePath2).replace('\\', '/'));
    }

    @Test
    public void testDeleteGetFiles() {
        String officialNginxPath = officialStorage.getNginxRelativePath();
        List<Long> fileIds = new ArrayList<>();

        String  fileName  = null;
        ByteArrayInputStream file = null;
        long    fileId    = 0;
        boolean existFile = false;
        String  filePath  = "";
        for (int i=0; i<3; i++) {
            fileName  = "official_test_aaa"+i+".png";
            file      = new ByteArrayInputStream(fileName.getBytes());
            fileId    = officialStorage.addFile(-1, fileName, file);
            existFile = officialStorage.fileExist(fileId);
            filePath  = officialStorage.getFilePath(fileId);
            Assert.assertTrue(fileId > 0);
            Assert.assertEquals(true, existFile);
            Assert.assertTrue(filePath.startsWith(officialNginxPath));
            fileIds.add(fileId);
            logger.info("fileid={} filepath={}", fileId, filePath);
        }
        Map<Long, String> id2Path = officialStorage.getFilePath(fileIds);
        Assert.assertEquals(3, id2Path.size());
        logger.info("fileid-->filepath={}", id2Path);

        officialStorage.deleteFiles(fileIds);

        for (int i=0; i<3; i++) {
            existFile = officialStorage.fileExist(fileIds.get(i));
            Assert.assertEquals(false, existFile);
        }
    }

    @Test
    public void testMoveToHereAndDelete() {
        String fileName        = null;
        String fileDir         = "11";
        String destPath        = "";
        File   destFile        = null;
        ByteArrayInputStream file = null;
        List<String> filePaths  = new ArrayList<>();

        // create test data
        for (int i=0; i<3; i++) {
            fileName = "temporary_test_aaa"+i+".png";
            file     = new ByteArrayInputStream(fileName.getBytes());
            destPath = storageBasePath+fileDir+File.separator+fileName;
            destFile = new File(destPath);

            File   dirFile = new File(storageBasePath+fileDir);
            if (!dirFile.exists()) {
                dirFile.mkdir();
            }

            try { FileUtil.writeFile(file, destFile); }
            catch (Exception ex) {
                continue;
            }
            Assert.assertTrue(FileUtil.fileExist(destPath));
            filePaths.add(destPath);
        }

        // test moveFileToHere and fileExist
        Map<String, String> old2New      = officialStorage.moveFileToHere(filePaths);
        List<String>        newFilePaths = new ArrayList<>();
        Set<String> oldFilePath  = old2New.keySet();
        for (String old : oldFilePath) {
            String nuw     = old2New.get(old);
            File   oldPath = new File(old);
            File   newPath = new File(nuw);
            Assert.assertFalse(oldPath.exists());
            Assert.assertTrue(officialStorage.fileExist(nuw));
            newFilePaths.add(nuw);
        }

        // test deleteFile
        for (String newPath : newFilePaths) {
            officialStorage.deleteFile(newPath);
            Assert.assertFalse(officialStorage.fileExist(newPath));
        }
    }
}

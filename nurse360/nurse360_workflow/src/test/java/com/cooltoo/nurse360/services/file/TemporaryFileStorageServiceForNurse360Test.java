package com.cooltoo.nurse360.services.file;

import com.cooltoo.nurse360.AbstractCooltooTest;
import com.cooltoo.nurse360.service.file.TemporaryFileStorageServiceForNurse360;
import com.cooltoo.util.FileUtil;
import com.cooltoo.util.VerifyUtil;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhaolisong on 16/10/9.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup("classpath:/com/cooltoo/nurse360/services/file_storage_data.xml")
})
public class TemporaryFileStorageServiceForNurse360Test extends AbstractCooltooTest {

    @Autowired private TemporaryFileStorageServiceForNurse360 tempStorage;

    @Value("${nurse360.storage.base.path}")
    private String storageBasePath;

    @Test
    public void testAddDeleteExistFile() {
        String fileName = "temporary_test_aaa.png";
        ByteArrayInputStream file = new ByteArrayInputStream(fileName.getBytes());

        String filepath = tempStorage.addFile(fileName, file);
        Assert.assertTrue(!VerifyUtil.isStringEmpty(filepath));

        boolean existFile = tempStorage.fileExist(filepath);
        Assert.assertEquals(true, existFile);

        tempStorage.deleteFile(filepath);
        existFile = tempStorage.fileExist(filepath);
        Assert.assertEquals(false, existFile);
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
        Map<String, String> old2New      = tempStorage.moveFileToHere(filePaths);
        List<String>        newFilePaths = new ArrayList<>();
        Set<String>         oldFilePath  = old2New.keySet();
        for (String old : oldFilePath) {
            String nuw     = old2New.get(old);
            File   oldPath = new File(old);
            File   newPath = new File(nuw);
            Assert.assertFalse(oldPath.exists());
            Assert.assertTrue(tempStorage.fileExist(nuw));
            newFilePaths.add(nuw);
        }

        // test deleteFile
        for (String newPath : newFilePaths) {
            tempStorage.deleteFile(newPath);
            Assert.assertFalse(tempStorage.fileExist(newPath));
        }
    }
}

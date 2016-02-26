package com.cooltoo.services;

import com.cooltoo.entities.FileStorageEntity;
import com.cooltoo.repository.FileStorageRepository;
import com.cooltoo.serivces.StorageService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by yzzhao on 2/26/16.
 */
public class StorageServiceTest extends AbstractCooltooTest {

    @Autowired
    private StorageService storageService;

    @Autowired
    private FileStorageRepository repository;

    @Test
    public void testSaveFile(){
        String fileName = System.currentTimeMillis()+"";
        File file = new File("build/"+fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            long fileId = storageService.saveFile(fileName, inputStream);
            FileStorageEntity entity = repository.findOne(fileId);
            Assert.assertNotNull(entity);
            Assert.assertEquals(fileName, entity.getFileRealname());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}

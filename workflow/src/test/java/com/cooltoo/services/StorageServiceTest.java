package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.entities.FileStorageEntity;
import com.cooltoo.repository.FileStorageRepository;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by yzzhao on 2/26/16.
 */
@Transactional
public class StorageServiceTest extends AbstractCooltooTest {

    @Autowired
    @Qualifier("StorageService")
    private StorageService storageService;

    @Autowired
    private FileStorageRepository repository;

    @Autowired
    @Qualifier("SecretFileStorageService")
    private SecretFileStorageService secretStorageService;

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
            long fileId = storageService.saveFile(0, fileName, inputStream);
            FileStorageEntity entity = repository.findOne(fileId);
            Assert.assertNotNull(entity);
            Assert.assertEquals(fileName, entity.getFileRealname());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testSaveSecretFile() {
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
            long fileId = secretStorageService.saveFile(0, fileName, inputStream);
            FileStorageEntity entity = repository.findOne(fileId);
            Assert.assertNotNull(entity);
            Assert.assertEquals(fileName, entity.getFileRealname());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }
}

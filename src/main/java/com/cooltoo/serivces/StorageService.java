package com.cooltoo.serivces;

import com.cooltoo.entities.FileStorageEntity;
import com.cooltoo.repository.FileStorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by yzzhao on 2/26/16.
 */
@Service("StorageService")
public class StorageService {

    private static final Logger logger = Logger.getLogger(StorageService.class.getName());

    @Autowired
    private FileStorageRepository storageRepository;

    @Value("${storage.path}")
    private String storagePath;

    public long saveFile(String fileName, InputStream inputStream) {
        if(inputStream == null){
            logger.warning("not found inputstream");
            return -1;
        }
        logger.info("save file " + fileName);
        try {
            String sha1 = sha1(fileName);
            String folderName = sha1.substring(0, 2);
            String name = sha1.substring(2);
            String destFielPath = storagePath + File.separator + folderName;
            logger.info("save " + fileName + " to " + destFielPath + "/" + name);
            File dir = new File(destFielPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(destFielPath, name);
            writeToFile(inputStream, file);
            return saveToDB(fileName, destFielPath + File.separator + name);
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return 0;
    }

    public String getFileUrl(long id) {
        return "";
    }

    private long saveToDB(String fileName, String path) {
        FileStorageEntity entity = new FileStorageEntity();
        entity.setFileRealname(fileName);
        entity.setFilePath(path);
        FileStorageEntity saved = storageRepository.save(entity);
        return saved.getId();
    }

    private static void writeToFile(InputStream inputStream, File file) {
        byte buffer[] = new byte[1024];
        try {
            int read = inputStream.read(buffer);
            FileOutputStream outputStream = new FileOutputStream(file);
            while (read > 0) {
                outputStream.write(buffer, 0, read);
                read = inputStream.read(buffer);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

    }

    private static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}

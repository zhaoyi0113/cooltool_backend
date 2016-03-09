package com.cooltoo.serivces;

import com.cooltoo.entities.FileStorageEntity;
import com.cooltoo.repository.FileStorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
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
        try {
            String sha1         = sha1(String.valueOf(System.nanoTime()));
            String folderName   = sha1.substring(0, 2);
            String newFileName  = sha1.substring(2);
            String destFielPath = storagePath + File.separator + folderName;

            logger.info("save " + fileName + " to " + destFielPath + "/" + newFileName);
            File dir = new File(destFielPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(destFielPath, newFileName);
            writeToFile(inputStream, file);

            return saveToDB(fileName, destFielPath + File.separator + newFileName);
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return 0;
    }

    public String getFileUrl(long id) {
        if (storageRepository.exists(id)) {
            FileStorageEntity entity = storageRepository.findOne(id);
            return entity.getFilePath();
        }
        return "";
    }

    public InputStream getFileInputStream(long id){
        if (storageRepository.exists(id)){
            FileStorageEntity entity = storageRepository.findOne(id);
            try {
                logger.info("get resource from path "+entity.getFilePath());
                return new FileInputStream(entity.getFilePath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private long saveToDB(String fileName, String path) {
        FileStorageEntity entity = new FileStorageEntity();
        entity.setFileRealname(fileName);
        entity.setFilePath(path);
        entity = storageRepository.save(entity);
        logger.info("FileId="+entity.getId() +", FileName="+entity.getFileRealname() +", FilePath="+entity.getFilePath());
        return entity.getId();
    }

    private static void writeToFile(InputStream input, File file) throws IOException {
        byte buffer[] = new byte[1024];
        int  read     = input.read(buffer);
        FileOutputStream output = new FileOutputStream(file);
        while (read > 0) {
            output.write(buffer, 0, read);
            read = input.read(buffer);
        }
    }

    private static String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest sha1Digest = MessageDigest.getInstance("SHA1");
        byte[] result = sha1Digest.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}

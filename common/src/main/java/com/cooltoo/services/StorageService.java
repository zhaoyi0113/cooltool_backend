package com.cooltoo.services;

import com.cooltoo.entities.FileStorageEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.FileStorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yzzhao on 2/26/16.
 */
@Service("StorageService")
public class StorageService {

    private static final Logger logger = LoggerFactory.getLogger(StorageService.class.getName());

    @Autowired
    private FileStorageRepository storageRepository;

    @Value("${storage.path}")
    private String storagePath;


    @Value("${storage.url}")
    private String storageUrl;


    public String getStorageUrl() {
        return this.storageUrl;
    }

    public String getStoragePath() {
        return this.storagePath;
    }

    public String getNgnixPathPrefix() {
        return "";
    }

    public long saveFile(long fileId, String fileName, InputStream inputStream) {
        if(inputStream == null){
            logger.info("not found inputstream");
            return -1;
        }
        deleteFileIfExist(fileId);
        try {
            String storageDirectory = getStoragePath() + File.separator;
            String sha1         = sha1(String.valueOf(System.nanoTime()));
            String folderName   = sha1.substring(0, 2);
            String newFileName  = sha1.substring(2);
            String relativePath = folderName + File.separator + newFileName;

            String destFilePath = storageDirectory + folderName;
            logger.info("save " + fileName + " to " + destFilePath + "/" + newFileName);
            File dir = new File(destFilePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(destFilePath, newFileName);
            writeToFile(inputStream, file);

            return saveToDB(fileName, relativePath);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return 0;
    }

//    public String getFileUrl(long id) {
//        if (storageRepository.exists(id)) {
//            FileStorageEntity entity = storageRepository.findOne(id);
//
//            return getStorageUrl() + entity.getId();
//        }
//        return "";
//    }

    public String getFilePath(long id){
        if(storageRepository.exists(id)){
            FileStorageEntity entity = storageRepository.findOne(id);
            return getNgnixPathPrefix()+entity.getFilePath();
        }
        return "";
    }

    public Map<Long, String> getFilePath(List<Long> ids) {
        Map<Long, String> ret = new HashMap<Long, String>();
        if (null == ids) {
            return ret;
        }
        String pathPrefix = getNgnixPathPrefix();

        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "id"));
        List<FileStorageEntity> fileEntites =  storageRepository.findStorageByIdIn(ids, sort);
        for (FileStorageEntity entity : fileEntites) {
            ret.put(entity.getId(), pathPrefix + entity.getFilePath());
        }
        return ret;
    }


    public InputStream getFileInputStream(long id){
        if (storageRepository.exists(id)){
            FileStorageEntity entity = storageRepository.findOne(id);
            try {
                String filePath = getNgnixPathPrefix() + entity.getFilePath();
                logger.info("get resource from path {}", filePath);
                return new FileInputStream(filePath);
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

    private long deleteFileIfExist(long fileId) {
        FileStorageEntity entity = storageRepository.findOne(fileId);
        if (null==entity) {
            return fileId;
        }
        String storageDirectory = getStoragePath() + File.separator;
        String relativePath = entity.getFilePath();
        File imageFile = new File(storageDirectory + relativePath);
        try {
            if (imageFile.exists()) {
                imageFile.delete();
            }
        } catch (Exception ex) {
            logger.error("failed to delete existing file.", ex);
            throw new BadRequestException(ErrorCode.FILE_DELETE_FAILED);
        }
        storageRepository.delete(entity);

        return entity.getId();
    }

    private static void writeToFile(InputStream input, File file) throws IOException {
        byte buffer[] = new byte[1024];
        int  read     = input.read(buffer);
        FileOutputStream output = new FileOutputStream(file);
        while (read > 0) {
            output.write(buffer, 0, read);
//            logger.info("write buffer "+read);
            read = input.read(buffer);
        }
        output.close();
    }

    public void deleteFile(long id){
        FileStorageEntity entity = storageRepository.findOne(id);
        if(entity != null){
            String filePath = entity.getFilePath();
            File file = new File(getStoragePath()+"/"+filePath);
            file.delete();
        }
    }

    public void deleteFiles(List<Long> ids) {
        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "id"));
        List<FileStorageEntity> all = storageRepository.findStorageByIdIn(ids, sort);
        if (null==all || all.isEmpty()) {
            return;
        }
        for (FileStorageEntity one : all) {
            deleteFile(one);
        }
        storageRepository.deleteByIdIn(ids);
    }

    private void deleteFile(FileStorageEntity entity) {
        String storageDirectory = getStoragePath() + File.separator;
        String relativePath = entity.getFilePath();
        File imageFile = new File(storageDirectory + relativePath);
        try {
            if (imageFile.exists()) {
                imageFile.delete();
            }
        } catch (Exception ex) {
            logger.error("failed to delete existing file === " + imageFile, ex);
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

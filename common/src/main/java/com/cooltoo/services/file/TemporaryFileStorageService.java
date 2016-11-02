package com.cooltoo.services.file;

import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.FileStorageDBService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * Created by zhaolisong on 16/4/26.
 */
@Service("TemporaryFileStorageService")
public class TemporaryFileStorageService extends AbstractFileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(TemporaryFileStorageService.class.getName());

    @Value("${storage.tmp.path}")
    private String tmpPath;

    @Value("${storage.base.path}")
    private String storageBasePath;

    @Autowired
    @Qualifier("FileStorageDBService")
    private InterfaceFileStorageDB dbService;

    public InterfaceFileStorageDB getDbService() {
        return dbService;
    }

    @Override
    public String getName() {
        return "temporary";
    }

    @Override
    public String getStoragePath() {
        StringBuilder path = new StringBuilder();
        path.append(storageBasePath);
        path.append(tmpPath);
        logger.info("get temporary storage path={}", path.toString());
        return path.toString();
    }

    @Override
    public String getNginxRelativePath() {
        logger.info("get temporary nginx path={}", tmpPath);
        return tmpPath;
    }

    /** cache file with the token key(return-->relative_path_in_temporary_directory)  */
    public String addFile(String fileName, InputStream file) {
        return addCacheFile(fileName, file);
    }

    /** delete file path(which end with relative path in its storage) */
    @Override
    public boolean deleteFile(String filePath) {
        logger.info("delete temporary file, filepath={}", filePath);
        if (VerifyUtil.isStringEmpty(filePath)) {
            logger.info("filepath is empty");
            return true;
        }
        String relativePath = getRelativePathInStorage(filePath);
        if (VerifyUtil.isStringEmpty(relativePath)) {
            logger.info("decode dir and filename is empty");
            return false;
        }
        return super.deleteFile(relativePath);
    }

    /** delete file paths(which end with relative path in its storage) */
    @Override
    public void deleteFileByPaths(List<String> filePaths) {
        logger.info("delete temporary file, filepath={}", filePaths);
        if (VerifyUtil.isListEmpty(filePaths)) {
            logger.info("filepath is empty");
            return;
        }
        Map<String, String> path2RelativePath = getRelativePathInStorage(filePaths);
        for (String path : filePaths) {
            String relativePath = path2RelativePath.get(path);
            super.deleteFile(relativePath);
        }
    }

    /** srcFileAbsolutePath---->dir/sha1 */
    @Override
    public Map<String, String> moveFileToHere(List<String> srcFileAbsolutePath) {
        return fileUtil.moveFilesToDest(srcFileAbsolutePath, this, false);
    }

    @Deprecated @Override public long addFile(long oldFileId, String fileName, InputStream file) {
        throw new UnsupportedOperationException();
    }
    @Deprecated @Override public boolean deleteFile(long fileId) {
        return true;
    }
    @Deprecated @Override public void deleteFiles(List<Long> fileIds) {
        throw new UnsupportedOperationException();
    }
    @Deprecated @Override public String getFilePath(long fileId) {
        throw new UnsupportedOperationException();
    }
    @Deprecated @Override public Map<Long, String> getFilePath(List<Long> fileIds) {
        throw new UnsupportedOperationException();
    }
    @Deprecated @Override public boolean fileExist(long fileId) {
        throw new UnsupportedOperationException();
    }

}

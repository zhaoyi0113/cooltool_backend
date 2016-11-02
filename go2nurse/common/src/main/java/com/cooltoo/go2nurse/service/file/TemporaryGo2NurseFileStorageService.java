package com.cooltoo.go2nurse.service.file;

import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.file.AbstractFileStorageService;
import com.cooltoo.services.file.InterfaceFileStorageDB;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

/**
 * Created by zhaolisong on 16/4/26.
 */
@Service("TemporaryGo2NurseFileStorageService")
public class TemporaryGo2NurseFileStorageService extends AbstractFileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(TemporaryGo2NurseFileStorageService.class.getName());

    @Value("${go2nurse.storage.tmp.path}")
    private String go2nurseTmpPath;

    @Value("${go2nurse.storage.base.path}")
    private String storageBasePath;

    @Autowired
    @Qualifier("Go2NurseFileStorageService")
    private InterfaceFileStorageDB dbService;

    @Override
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
        path.append(storageBasePath).append(go2nurseTmpPath);
        logger.info("get temporary storage path={}", path.toString());
        return path.toString();
    }

    @Override
    public String getNginxRelativePath() {
        logger.info("get temporary nginx path={}", go2nurseTmpPath);
        return go2nurseTmpPath;
    }

    /** cache file with the token key(return-->relative_path_in_temporary_directory)  */
    public String addFile(String fileName, InputStream file) {
        return addCacheFile(fileName, file);
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
    @Deprecated @Override public Map<Long, String> getFileUrl(List<Long> fileIds, String httpPrefix) {
        throw new UnsupportedOperationException();
    }
    @Deprecated @Override public boolean fileExist(long fileId) {
        throw new UnsupportedOperationException();
    }

}

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

import java.io.File;
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
        logger.info("cache file fileName={} file={}", fileName, file);
        if (null==file) {
            logger.error("the file is invalid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        if (VerifyUtil.isStringEmpty(fileName)) {
            fileName = "tmp";
        }

        Object ret = addFile(-1, fileName, file, false);
        if (!(ret instanceof String)) {
            logger.error("add file failed!");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        String savedRelativePath = (String)ret;
        String cacheFileRelativePath = getNginxRelativePath()+savedRelativePath;
        return cacheFileRelativePath;
    }

    /** srcFileAbsolutePath---->dir/sha1 */
    @Override
    public Map<String, String> moveFileToHere(List<String> srcFileAbsolutePath) {
        logger.info("move file to tmp. file={}", srcFileAbsolutePath);
        if (VerifyUtil.isListEmpty(srcFileAbsolutePath)) {
            logger.info("the files list is empty");
            return new HashMap<>();
        }

        List<String> token2Images = new ArrayList<>();

        Map<String, String> filePath2TempPath = new Hashtable<>();
        Map<String, String> successMoved = new Hashtable<>();
        try {
            for (String filePath : srcFileAbsolutePath) {
                String[] baseurlDirSha1 = fileUtil.decodeFilePath(filePath);
                // relative_file_path_in_storage--->dir/sha1
                String relativeFilePath = baseurlDirSha1[1]
                                        + File.separator
                                        + baseurlDirSha1[2];

                if (token2Images.contains(relativeFilePath)) {
                    logger.warn("the file to move is done already == {}", filePath);
                }

                String temporaryPath    = getStoragePath();
                // dest_file_dir--->temporary_base_path/dir
                String destDirPath      = temporaryPath + baseurlDirSha1[1] + File.separator;
                // dest_file_path--->temporary_base_path/dir/sha1
                String destFilePath     = destDirPath + baseurlDirSha1[2];

                // make the temporary directory if necessary
                File destDir = new File(destDirPath);
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }

                // move storage file to temporary dir
                fileUtil.moveFile(filePath, destFilePath);
                successMoved.put(destFilePath, filePath);

                token2Images.add(relativeFilePath);

                filePath2TempPath.put(filePath, relativeFilePath);
            }
            token2Images.clear();
            return filePath2TempPath;
        }
        catch (Exception ex) {
            logger.error("move file failed!", ex);
            filePath2TempPath.clear();
            fileUtil.moveFiles(successMoved);/* rollback file moved */
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
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

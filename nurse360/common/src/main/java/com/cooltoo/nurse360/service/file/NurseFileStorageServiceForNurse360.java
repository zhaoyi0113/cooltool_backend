package com.cooltoo.nurse360.service.file;

import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.util.FileUtil;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * Created by hp on 2016/10/9.
 */
@Service("NurseFileStorageServiceForNurse360")
public class NurseFileStorageServiceForNurse360 extends AbstractFileStorageServiceForNurse360 {

    private static final Logger logger = LoggerFactory.getLogger(NurseFileStorageServiceForNurse360.class.getName());

    @Value("${nurse360.storage.nurse.path}")
    private String nursePath;

    @Override
    public String getName() {
        return "user";
    }

    @Override
    public String getStoragePath() {
        StringBuilder path = new StringBuilder();
        path.append(super.getStoragePath());
        path.append(nursePath);
        logger.info("get user storage path={}", path.toString());
        return path.toString();
    }

    @Override
    public String getNginxRelativePath() {
        logger.info("get user nginx path={}", nursePath);
        return nursePath;
    }

    /** delete file path(which end with relative path in its storage) */
    @Override
    public boolean deleteFile(String filePath) {
        logger.info("delete user file, filepath={}", filePath);
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

    /** srcFileAbsolutePath---->dir/sha1 */
    @Override
    public Map<String, String> moveFileToHere(List<String> srcFileAbsolutePath) {
        logger.info("move files to user path. files={}", srcFileAbsolutePath);
        if (VerifyUtil.isListEmpty(srcFileAbsolutePath)) {
            logger.info("the files list is empty");
            return new HashMap<>();
        }

        // absolute_or_relative_file_path_in_temporary---->relative_file_path_in_storage
        Map<String, String> filePath2StoragePath = new Hashtable<>();
        Map<String, String> successMoved         = new Hashtable<>();
        try {
            for (String srcFilePath : srcFileAbsolutePath) {
                String[] baseurlDirSha1 = decodeFilePath(srcFilePath);
                // relative_file_path_in_temporary--->dir/sha1
                String relativeFilePath = baseurlDirSha1[1]
                        + File.separator
                        + baseurlDirSha1[2];

                String storagePath  = getStoragePath();
                // dest_file_dir--->storage_base_path/dir
                String destDirPath  = storagePath + baseurlDirSha1[1] + File.separator;
                // dest_file_path--->storage_base_path/dir/sha1
                String destFilePath = destDirPath + baseurlDirSha1[2];

                // make the storage directory if necessary
                File destDir = new File(destDirPath);
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }

                // move temporary file to storage dir
                FileUtil.moveFile(srcFilePath, destFilePath);
                successMoved.put(destFilePath, srcFilePath);

                filePath2StoragePath.put(srcFilePath, relativeFilePath);
            }
            return filePath2StoragePath;
        }
        catch (Exception ex) {
            logger.error("move file failed!", ex);
            filePath2StoragePath.clear();
            rollbackFileMoved(successMoved);
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
    }

    public File createFileInBaseStorage(String prefix, String suffix) {
        String uniqueId = NumberUtil.timeToString(new Date(), NumberUtil.DATE_YYYY_MM_DD);
        uniqueId = uniqueId+"_"+System.nanoTime();
        if (!VerifyUtil.isStringEmpty(prefix)) {
            uniqueId = prefix.trim()+uniqueId;
        }
        if (!VerifyUtil.isStringEmpty(suffix)) {
            uniqueId = uniqueId+suffix;
        }
        File file = new File(super.getStoragePath()+uniqueId);
        return file;
    }
}

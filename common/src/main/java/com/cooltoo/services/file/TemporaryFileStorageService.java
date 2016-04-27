package com.cooltoo.services.file;

import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.util.FileUtil;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Override
    public String getName() {
        return "temporary";
    }

    @Override
    public String getStoragePath() {
        StringBuilder path = new StringBuilder();
        path.append(super.getStoragePath());
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
        logger.info("cache file fileName={} file={}", fileName, file);
        if (null==file) {
            logger.error("the file is invalid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        if (VerifyUtil.isStringEmpty(fileName)) {
            fileName = "tmp";
        }

        String cacheFileRelativePath = null;
        String cacheFilePath         = null;
        try {
            // get the new dir and fileName
            String[] dirAndSha1  = encodeFilePath(fileName);
            // construct the cache directory
            String   fileCacheDir= getStoragePath()+File.separator+dirAndSha1[0];

            // make the cache directory if necessary
            File tmpDir = new File(fileCacheDir);
            if (!tmpDir.exists()) {
                tmpDir.mkdirs();
            }

            // save the file to the cache directory
            File cacheFile = new File(tmpDir, dirAndSha1[1]);
            cacheFilePath  = cacheFile.getAbsolutePath();
            FileUtil.writeFile(file, cacheFile);

            // construct the relative path
            cacheFileRelativePath = getNginxRelativePath()+dirAndSha1[0]+File.separator+dirAndSha1[1];
        }
        catch (Exception ex) {
            logger.error("save the temporary file error", ex);

            // if has error delete the cache file.
            if (!VerifyUtil.isStringEmpty(cacheFilePath)) {
                File deleteF = new File(cacheFilePath);
                if (deleteF.exists()) {
                    deleteF.delete();
                }
            }

            cacheFileRelativePath = "ERROR";
        }
        finally {
            return cacheFileRelativePath;
        }
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
                String[] baseurlDirSha1 = decodeFilePath(filePath);
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
                FileUtil.moveFile(filePath, destFilePath);
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
            rollbackFileMoved(successMoved);
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
    }

    @Deprecated @Override public long addFile(long oldFileId, String fileName, InputStream file) {
        throw new UnsupportedOperationException();
    }
    @Deprecated @Override public boolean deleteFile(long fileId) {
        throw new UnsupportedOperationException();
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

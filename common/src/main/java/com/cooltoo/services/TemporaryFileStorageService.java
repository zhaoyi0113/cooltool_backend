package com.cooltoo.services;

import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.util.FileUtil;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by hp on 2016/4/19.
 */
@Service("TemporaryFileStorageService")
public class TemporaryFileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(TemporaryFileStorageService.class.getName());

    @Autowired
    @Qualifier("StorageService")
    private StorageService storageService;

    @Value("${storage.tmp.path}")
    private String tmpPath;
    @Value("${storage.tmp.url}")
    private String tmpUrl;

    private Map<String, List<String>> token2ImagePath = new Hashtable<>();

    public String getTmpPath() {
        return tmpPath;
    }

    public String getTmpUrl() {
        return tmpUrl;
    }

    /** delete file_saved_in_storage
     * @param storageFiles the absolute_or_relative_file_path_in_storage
     */
    @Transactional
    public List<String> deleteStorageFile(List<String> storageFiles) {
        logger.info("delete files in the storage, file={}", storageFiles);
        if (null==storageFiles) {
            return new ArrayList<>();
        }
        List<String> deleted = new ArrayList<>();
        for (String  fileUrl : storageFiles) {
            String[] baseurlDirShaRealname = decodeFileName(fileUrl);
            // relative_file_path_in_storage--->dir/sha1
            String relativeFilePathInStorage = baseurlDirShaRealname[1]
                    + File.separator
                    + baseurlDirShaRealname[2];
            String storagePath = storageService.getStoragePath();
            // dest_file_path--->storage_base_path/dir/sha1
            String destFilePath = storagePath + File.separator + relativeFilePathInStorage;
            File destFile = new File(destFilePath);
            if (!destFile.exists()) {
                continue;
            }

            if (destFile.delete()) {
                deleted.add(relativeFilePathInStorage);
            }
            else {
                logger.warn("Failed to delete file in storage, file={}", relativeFilePathInStorage);
            }
        }
        return deleted;
    }

    /** cache file with the token key(return-->relative_path_in_temporary_directory)  */
    public String cacheTemporaryFile(String token, String fileName, InputStream file) {
        logger.info("cache file fileName={} file={} by token={}", fileName, file, token);
        if (VerifyUtil.isStringEmpty(token)) {
            logger.error("the token is invalid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
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
            String[] dirAndSha1   = encodeFileName(fileName);

            // construct the relative path
            cacheFileRelativePath = dirAndSha1[0]+ File.separator+dirAndSha1[1];

            // construct the cache directory
            String   fileCacheDir = getTmpPath()+File.separator+dirAndSha1[0];

            // make the cache directory if necessary
            File tmpDir = new File(fileCacheDir);
            if (!tmpDir.exists()) {
                tmpDir.mkdirs();
            }

            // save the file to the cache directory
            File cacheFile = new File(tmpDir, dirAndSha1[1]);
            cacheFilePath  = cacheFile.getAbsolutePath();
            FileUtil.writeFile(file, cacheFile);

            // record it to the cache
            List<String> cacheImagePath = token2ImagePath.get(token);
            if (null==cacheImagePath) {
                cacheImagePath = new ArrayList<>();
                token2ImagePath.put(token, cacheImagePath);
            }
            if (!cacheImagePath.contains(cacheFileRelativePath)) {
                cacheImagePath.add(cacheFileRelativePath);
                logger.info("cache the file to ===> {}", cacheFileRelativePath);
            }
            else {
                logger.info("cache the file rewrite ===> {}", cacheFileRelativePath);
            }
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

    /** move file_cached_in_temporary to storage
     * @param token the user's token;
     * @param fileCached the absolute_or_relative_file_path_in_temporary
     * @return the absolute_or_relative_file_path_in_temporary---->relative_file_path_in_storage
     */
    @Transactional
    public Map<String, String> moveToStorage(String token, List<String> fileCached) {
        logger.info("move temporary file to storage path by token={}", token);
        logger.info("move temporary file={}", fileCached);
        if (VerifyUtil.isStringEmpty(token)) {
            logger.info("the token is invalid");
            return new HashMap<>();
        }
        if (null==fileCached || fileCached.isEmpty()) {
            logger.info("the temporary files list is empty");
            return new HashMap<>();
        }

        List<String> token2CachedFile = token2ImagePath.get(token);
        // this token cache nothing
        if (null==token2CachedFile || token2CachedFile.isEmpty()) {
            logger.warn("the files to move is not in the cache == {}", fileCached);
            return new HashMap<>();
        }

        // absolute_or_relative_file_path_in_temporary---->relative_file_path_in_storage
        Map<String, String> absoluteTempPath2StoragePath = new Hashtable<>();
        Map<String, String> successMoved                 = new Hashtable<>();
        try {
            for (String fileUrl : fileCached) {
                String[] baseurlDirShaRealname = decodeFileName(fileUrl);
                // relative_file_path_in_temporary--->dir/sha1
                String relativeFilePathInTemporary = baseurlDirShaRealname[1]
                                                   + File.separator
                                                   + baseurlDirShaRealname[2];

                if (!token2CachedFile.contains(relativeFilePathInTemporary)) {
                    logger.warn("the file to move is not in the cache == {}", fileUrl);
                    continue;
                }

                // src_file_path--->tmp_base_path/dir/sha1
                String srcFilePath  = getTmpPath() + File.separator + relativeFilePathInTemporary;
                String storagePath  = storageService.getStoragePath();
                // dest_file_dir--->storage_base_path/dir
                String destDirPath  = storagePath + File.separator + baseurlDirShaRealname[1];
                // dest_file_path--->storage_base_path/dir/sha1
                String destFilePath = destDirPath + File.separator + baseurlDirShaRealname[2];

                // make the storage directory if necessary
                File destDir = new File(destDirPath);
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }

                // move temporary file to storage dir
                FileUtil.moveFile(srcFilePath, destFilePath);
                successMoved.put(destFilePath, srcFilePath);

                String relativeFilePathInStorage = relativeFilePathInTemporary;
                absoluteTempPath2StoragePath.put(fileUrl, relativeFilePathInStorage);
                //String fileInStorage = relativeFilePathInStorage;
                //logger.info("file storage save {}<===>{}", "temporary_file", fileInStorage);
                //storageService.saveToDB("temporary_file", fileInStorage);
            }
            return absoluteTempPath2StoragePath;
        }
        catch (Exception ex) {
            logger.error("move file failed!", ex);

            absoluteTempPath2StoragePath.clear();
            rollbackFileMove(successMoved);
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
    }

    /** move file_saved_in_storage to temporary
     * @param token the user's token;
     * @param storageFiles the absolute_or_relative_file_path_in_storage
     */
    @Transactional
    public Map<String, String> moveToTemporary(String token, List<String> storageFiles) {
        logger.info("move storage file to temporary path by token={}", token);
        logger.info("move storage file={}", storageFiles);
        if (VerifyUtil.isStringEmpty(token)) {
            logger.info("the token is invalid");
            return new HashMap<>();
        }
        if (null==storageFiles || storageFiles.isEmpty()) {
            logger.info("the storage files list is empty");
            return new HashMap<>();
        }

        List<String> token2Images = token2ImagePath.get(token);
        // make cache for token
        if (null==token2Images) {
            token2Images = new ArrayList<>();
            token2ImagePath.put(token, token2Images);
        }

        Map<String, String> absoluteStoragePaht2TempPath   = new Hashtable<>();
        Map<String, String> successMoved = new Hashtable<>();
        try {
            for (String fileUrl : storageFiles) {
                String[] baseurlDirShaRealname = decodeFileName(fileUrl);
                // relative_file_path_in_storage--->dir/sha1
                String relativeFilePathInStorage = baseurlDirShaRealname[1]
                                                 + File.separator
                                                 + baseurlDirShaRealname[2];

                if (token2Images.contains(relativeFilePathInStorage)) {
                    logger.warn("the file to move is done already == {}", fileUrl);
                }

                // src_file_path--->storage_base_path/dir/sha1
                String srcFilePath      = storageService.getStoragePath() + File.separator + relativeFilePathInStorage;
                String temporaryPath    = getTmpPath();
                // dest_file_dir--->temporary_base_path/dir
                String destDirPath      = temporaryPath + File.separator + baseurlDirShaRealname[1];
                // dest_file_path--->temporary_base_path/dir/sha1
                String destFilePath     = destDirPath + File.separator + baseurlDirShaRealname[2];

                // make the temporary directory if necessary
                File destDir = new File(destDirPath);
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }

                // move storage file to temporary dir
                FileUtil.moveFile(srcFilePath, destFilePath);
                successMoved.put(destFilePath, srcFilePath);

                token2Images.add(relativeFilePathInStorage);

                String relativeFilePathInTemporary = relativeFilePathInStorage;
                absoluteStoragePaht2TempPath.put(fileUrl, relativeFilePathInTemporary);
            }
            return absoluteStoragePaht2TempPath;
        }
        catch (Exception ex) {
            logger.error("move file failed!", ex);
            absoluteStoragePaht2TempPath.clear();
            rollbackFileMove(successMoved);
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
    }

    /** file moved map(after_move_path-->before_move_path) */
    private void rollbackFileMove(Map<String, String> dest2SrcFileMoved) {
        logger.info("rollbacke file moved");
        if (null==dest2SrcFileMoved || dest2SrcFileMoved.isEmpty()) {
            logger.info("rollbacke file list is empty");
            return;
        }
        Set<String> destKeys = dest2SrcFileMoved.keySet();
        for (String dest : destKeys) {
            String src = dest2SrcFileMoved.get(dest);
            try {
                FileUtil.moveFile(dest, src);
            }
            catch (Exception e) {
                logger.warn("move file {} to {} failed!", dest, src);
            }
        }
        throw new BadRequestException(ErrorCode.DATA_ERROR);
    }

    /** tmp_base_path/token_cached_relative_path */
    public void cleanTokenCachedFile(String token) {
        logger.info("clean temporary by token={}", token);
        if (VerifyUtil.isStringEmpty(token)) {
            logger.warn("the token is invalid");
        }

        List<String> fileCached = token2ImagePath.get(token);
        if (null==fileCached || fileCached.isEmpty()) {
            logger.warn("no file cached by this token");
            token2ImagePath.remove(token);
            return;
        }

        String basePath = getTmpPath()+File.separator;
        for (String relativePath : fileCached) {
            String filePath = basePath+relativePath;
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
                logger.info("delete file====>{}", file);
            }
            else {
                logger.info("not exist file==>{}", file);
            }
        }

        token2ImagePath.remove(token);
    }

    /** tmp_base_path/relative_path */
    public boolean existTmpFile(String relativeTmpFilePath) {
        logger.info("judge the file {} is exist", relativeTmpFilePath);
        if (VerifyUtil.isStringEmpty(relativeTmpFilePath)) {
            logger.error("the relative file path is invalid");
            return false;
        }
        String tmpDir = getTmpPath();
        String tmpFile= tmpDir+File.separator+relativeTmpFilePath;
        File file = new File(tmpFile);
        boolean exist = file.exists();
        logger.info("judge the file {} is exist==>{}", relativeTmpFilePath, exist);
        return exist;
    }

    /** url,dir,sha1 */
    private String[] decodeFileName(String fileName) {
        logger.info("deconstruct file name={}", fileName);
        if (VerifyUtil.isStringEmpty(fileName)) {
            logger.error("the file name is invalid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        StringBuilder splash = new StringBuilder();
        splash.append('\\').append('\\');
        fileName = fileName.replaceAll(splash.toString(), "/");
        logger.info("after repalce \\ to /  ={}", fileName);

        String[] component    = fileName.split("/");
        String   tmpFileName  = component[component.length-1];
        String   tmpDirectory = component[component.length-2];

        int      baseUrlEndIdx= fileName.indexOf(tmpDirectory+"/"+tmpFileName);
        String   baseUrl      = "";
        if (baseUrlEndIdx>1) {
            baseUrl = fileName.substring(0, baseUrlEndIdx-1);
        }

        String sha1 = tmpFileName;
        return new String[]{baseUrl, tmpDirectory, sha1};
    }

    /** dir, sha1 */
    private String[] encodeFileName(String fileName) throws NoSuchAlgorithmException {
        logger.info("construct file name={}", fileName);
        if (VerifyUtil.isStringEmpty(fileName)) {
            logger.error("the file name is invalid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        long   nanoTime         = System.nanoTime();
        String strNanoTime      = fileName+"_"+nanoTime;
        String sha1             = VerifyUtil.sha1(strNanoTime);
        String newDir           = sha1.substring(0, 2);
        String newFileName      = sha1.substring(2);
        return new String[]{newDir, newFileName};
    }
}

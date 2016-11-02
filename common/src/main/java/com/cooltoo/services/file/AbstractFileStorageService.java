package com.cooltoo.services.file;

import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.util.FileUtil;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.util.*;

/**
 * Created by zhaolisong on 16/4/25.
 */
public abstract class AbstractFileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractFileStorageService.class.getName());


    public final FileUtil fileUtil = FileUtil.getInstance();

    public String getName() {
        return "abstract";
    }

    abstract public String getStoragePath();

    abstract public String getNginxRelativePath();

    abstract public InterfaceFileStorageDB getDbService();

    public String addCacheFile(String fileName, InputStream file) {
        logger.info("cache file");
        if (null==file) {
            logger.error("the file is invalid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        if (VerifyUtil.isStringEmpty(fileName)) {
            fileName = "tmp_cache";
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

    public long addNewFile(String fileName, InputStream file) {
        return addFile(-1, fileName, file);
    }

    public long addFile(long oldFileId, String fileName, InputStream file) {
        return (Long)addFile(oldFileId, fileName, file, true);
    }

    public Object addFile(long oldFileId, String fileName, InputStream file, boolean needDBSave) {
        logger.info("add file oldFileId={} filename={} file={} to fileStorage={}, needDBSave={}",
                oldFileId, fileName, file, getName(), needDBSave);
        if (null==file) {
            logger.warn("file is empty, not found input stream");
            if (needDBSave) {
                return -1L;
            }
            else {
                return "";
            }
        }
        if (!deleteFile(oldFileId)) {
            if (needDBSave) {
                return -1L;
            }
            else {
                return "";
            }
        }

        File destFile = null;
        try {
            String[] dirAndSHA1   = fileUtil.encodeFilePath(fileName);
            String   folderName   = dirAndSHA1[0];
            String   newFileName  = dirAndSHA1[1];
            String   relativePath = folderName + File.separator + newFileName;

            String destFileDir = getStoragePath() + folderName;
            logger.info("save " + fileName + " to " + destFileDir + "/" + newFileName);
            File dir = new File(destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            destFile = new File(destFileDir, newFileName);
            fileUtil.writeFile(file, destFile);

            if (needDBSave) {
                return dbRecordAdd(fileName, relativePath);
            }
            else {
                return relativePath;
            }
        } catch (Exception ex) {
            logger.error("failed to add official file", ex);

            // if has error delete the cache file.
            if (null!=destFile) {
                if (destFile.exists()) {
                    destFile.delete();
                }
            }
        }

        if (needDBSave) {
            return -1L;
        }
        else {
            return "";
        }
    }

    public boolean deleteFile(long fileId) {
        logger.info("delete file by file id={}", fileId);
        String filePath = dbRecordGet(fileId);
        boolean delete = deleteFile(filePath);
        if (delete) {
            dbRecordDelete(fileId);
        }
        return delete;
    }

    public void deleteFiles(List<Long> fileIds) {
        Map<Long, String> id2Path = dbRecordGet(fileIds);
        if (id2Path.isEmpty()) {
            return;
        }

        Set<Long> keys = id2Path.keySet();
        for (Long one : keys) {
            String filepath = id2Path.get(one);
            deleteFile(filepath);
        }
        dbRecordDelete(fileIds);
    }

    /** do not invoke this method, except TemporaryFileStorageService */
    public boolean deleteFile(String fileRelativePath) {
        logger.info("delete file by file filepath={}", fileRelativePath);
        if (VerifyUtil.isStringEmpty(fileRelativePath)) {
            logger.info("filepath not exist");
            return true;
        }
        fileRelativePath = fileUtil.getCooltooFileRelativePath("", fileRelativePath);

        String absolutePath = getStoragePath() + fileRelativePath;
        File deleteFile = new File(absolutePath);

        logger.info("absolute filepath={}", absolutePath);
        boolean delete;
        try {
            if (deleteFile.exists()) {
                delete = deleteFile.delete();
                logger.info("delete file status={}", delete);
            }
            else {
                logger.info("file not exist");
                delete = true;
            }
        }
        catch (Exception ex) {
            logger.error("failed to delete existing file.", ex);
            throw new BadRequestException(ErrorCode.FILE_DELETE_FAILED);
        }
        return delete;
    }

    /** do not invoke this method, except TemporaryFileStorageService */
    public void deleteFileByPaths(List<String> fileRelativePaths) {
        if (VerifyUtil.isListEmpty(fileRelativePaths)) {
            return;
        }
        for (String relPath : fileRelativePaths) {
            boolean success = deleteFile(relPath);
            if (!success) {
                logger.warn("fail to delete file={}", relPath);
            }
        }
    }

    /**
     * get file relative path with nginx prefix path
     * @param fileId file id in file_storage table
     * @return
     */
    public String getFilePath(long fileId) {
        logger.info("get file path by fileId={}", fileId);
        String filePath = dbRecordGet(fileId);
        if (VerifyUtil.isStringEmpty(filePath)) {
            return "";
        }
        logger.info("relative file path={}", filePath);
        filePath = getNginxRelativePath()+filePath;
        logger.info("nginx relative file path={}", filePath);
        return filePath;
    }

    /**
     * get file relative path with nginx prefix path
     * @param fileIds file ids in file_storage table
     * @return
     */
    public Map<Long, String> getFilePath(List<Long> fileIds) {
        if (VerifyUtil.isListEmpty(fileIds)) {
            logger.warn("file ids is empty");
            return new HashMap<>();
        }
        List<Long> validIds = new ArrayList<>();
        for (Long fileId : fileIds) {
            if (validIds.contains(fileId)) {
                continue;
            }
            validIds.add(fileId);
        }
        logger.info("get file path by valid fileIds={}", validIds);

        String nginxRelativePath = getNginxRelativePath();
        logger.info("nginx relative path={}", nginxRelativePath);

        Map<Long, String> id2Path =  dbRecordGet(validIds);
        Set<Long> ids = id2Path.keySet();
        for (Long id : ids) {
            String relativePath = id2Path.get(id);
            id2Path.put(id, nginxRelativePath + relativePath);
        }
        return id2Path;
    }

    /**
     * get file url with nginx prefix path
     * @param fileId file id in file_storage table
     * @return
     */
    public String getFileURL(long fileId, String httpPrefix) {
        logger.info("get file path by fileId={}", fileId);
        String filePath = dbRecordGet(fileId);
        if (VerifyUtil.isStringEmpty(filePath)) {
            return "";
        }
        logger.info("relative file path={}", filePath);
        filePath = getNginxRelativePath()+filePath;
        logger.info("nginx relative file path={}", filePath);
        filePath = httpPrefix + filePath;
        logger.info("nginx file url={}", filePath);
        return filePath;
    }


    /**
     * get file relative path with nginx prefix path
     * @param fileIds file ids in file_storage table
     * @return
     */
    public Map<Long, String> getFileUrl(List<Long> fileIds, String httpPrefix) {
        if (VerifyUtil.isListEmpty(fileIds)) {
            logger.warn("file ids is empty");
            return new HashMap<>();
        }
        List<Long> validIds = new ArrayList<>();
        for (Long fileId : fileIds) {
            if (validIds.contains(fileId)) {
                continue;
            }
            validIds.add(fileId);
        }
        logger.info("get file path by valid fileIds={}", validIds);

        String nginxRelativePath = getNginxRelativePath();
        logger.info("nginx relative path={}", nginxRelativePath);

        Map<Long, String> id2Path =  dbRecordGet(validIds);
        Set<Long> ids = id2Path.keySet();
        for (Long id : ids) {
            String relativePath = id2Path.get(id);
            id2Path.put(id, httpPrefix + nginxRelativePath + relativePath);
        }
        return id2Path;
    }

    /** (official/temporary/user/secret)_storage_path/dir/sha1 */
    public String getRelativePathInBase(String filePath) {
        return fileUtil.getCooltooFileRelativePath(getNginxRelativePath(), filePath);
    }

    /** file_path ----> (official/temporary/user/secret)_storage_path/dir/sha1 */
    public Map<String, String> getRelativePathInBase(List<String> filePath) {
        return fileUtil.getCooltooFileRelativePath(getNginxRelativePath(), filePath);
    }

    /** filePath --> dir/sha1 */
    public String getRelativePathInStorage(String filePath) {
        return fileUtil.getCooltooFileRelativePath("", filePath);
    }

    /** filePath --> dir/sha1 */
    public Map<String, String> getRelativePathInStorage(List<String> filePaths) {
        return fileUtil.getCooltooFileRelativePath("", filePaths);
    }

    abstract public Map<String, String> moveFileToHere(List<String> srcFileAbsolutePath);

    public boolean fileExist(long fileId) {
        logger.info("file {} is exist?", fileId);
        String filePath = dbRecordGet(fileId);
        if (VerifyUtil.isStringEmpty(filePath)) {
            logger.info("file path is empty");
            return false;
        }
        return fileUtil.cooltooFileExistInPath(getStoragePath(), filePath);
    }

    /** file moved map(after_move_path-->before_move_path) */
    public boolean fileExist(String fileRelativePath) {
        return fileUtil.cooltooFileExistInPath(getStoragePath(), fileRelativePath);
    }

    /** file moved map(after_move_path-->before_move_path) */
    protected void rollbackFileMoved(Map<String, String> dest2SrcFileMoved) {
        fileUtil.moveFiles(dest2SrcFileMoved);
    }

    @Transactional
    private long dbRecordAdd(String fileRealName, String filePath) {
        return getDbService().addRecord(fileRealName, filePath);
    }

    private String dbRecordGet(long fileRecordId) {
        String filePath = getDbService().getFilePath(fileRecordId);
        return filePath;
    }

    private Map<Long, String> dbRecordGet(List<Long> fileRecordIds) {
        Map<Long, String> fileId2FilePath = getDbService().getFilePath(fileRecordIds);
        return fileId2FilePath;
    }

    @Transactional
    private void dbRecordDelete(long fileRecordId) {
        getDbService().deleteRecord(fileRecordId);
    }

    @Transactional
    private void dbRecordDelete(List<Long> fileRecordIds) {
        getDbService().deleteRecord(fileRecordIds);
    }
}

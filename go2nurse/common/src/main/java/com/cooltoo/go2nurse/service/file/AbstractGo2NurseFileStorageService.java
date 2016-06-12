package com.cooltoo.go2nurse.service.file;

import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.service.Go2NurseFileStorageDBService;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.cooltoo.util.FileUtil;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by hp on 2016/6/8.
 */
public abstract class AbstractGo2NurseFileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(AbstractGo2NurseFileStorageService.class.getName());

    @Autowired
    private Go2NurseUtility utility;

    @Autowired
    private Go2NurseFileStorageDBService dbService;

    @Value("${go2nurse.storage.base.path}")
    private String storageBasePath;

    public String getName() {
        return "abstract";
    }

    public String getStoragePath() {
        logger.info("get storage base path={}", storageBasePath);
        return storageBasePath;
    }

    abstract public String getNginxRelativePath();

    public long addNewFile(String fileName, InputStream file){
        return addFile(-1, fileName, file);
    }

    public long addFile(long oldFileId, String fileName, InputStream file) {
        logger.info("add file oldFileId={} filename={} file={}", oldFileId, fileName, file);
        if (null==file) {
            logger.warn("file is empty, not found input stream");
            return -1;
        }
        if (!deleteFile(oldFileId)) {
            return -1;
        }

        try {
            String[] dirAndSHA1   = encodeFilePath(fileName);
            String   folderName   = dirAndSHA1[0];
            String   newFileName  = dirAndSHA1[1];
            String   relativePath = folderName + File.separator + newFileName;

            String destFileDir = getStoragePath() + folderName;
            logger.info("save " + fileName + " to " + destFileDir + "/" + newFileName);
            File dir = new File(destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File destFile = new File(destFileDir, newFileName);
            FileUtil.writeFile(file, destFile);

            return dbRecordAdd(fileName, relativePath);
        } catch (Exception ex) {
            logger.error("failed to add official file", ex);
        }
        return -1;
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

    protected void deleteFileByPaths(List<String> fileRelativePaths) {
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

    /** do not invoke this method, except TemporaryFileStorageService */
    protected boolean deleteFile(String fileRelativePath) {
        logger.info("delete file by file filepath={}", fileRelativePath);
        if (VerifyUtil.isStringEmpty(fileRelativePath)) {
            logger.info("filepath not exist");
            return true;
        }

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

    /**
     * get file relative path with nginx prefix path
     * @param fileId file id in file_storage table
     * @return
     */
    public String getFileUrl(long fileId) {
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
     * get file url with nginx prefix path
     * @param fileId file id in file_storage table
     * @return
     */
    public String getFileURL(long fileId) {
        logger.info("get file path by fileId={}", fileId);
        String filePath = dbRecordGet(fileId);
        if (VerifyUtil.isStringEmpty(filePath)) {
            return "";
        }
        logger.info("relative file path={}", filePath);
        filePath = getNginxRelativePath()+filePath;
        logger.info("nginx relative file path={}", filePath);
        filePath = utility.getHttpPrefix()+filePath;
        logger.info("nginx file url={}", filePath);
        return filePath;
    }


    /**
     * get file relative path with nginx prefix path
     * @param fileIds file ids in file_storage table
     * @return
     */
    public Map<Long, String> getFileUrl(List<Long> fileIds) {
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
            id2Path.put(id, utility.getHttpPrefix()+ nginxRelativePath + relativePath);
        }
        return id2Path;
    }

    /** (official/temporary/user/secret)_storage_path/dir/sha1 */
    public String getRelativePathInBase(String filePath) {
        logger.info("get relative path in base, filepath={}", filePath);
        String relativePathInStorage = getRelativePathInStorage(filePath);
        if (VerifyUtil.isStringEmpty(relativePathInStorage)) {
            return "";
        }
        return getNginxRelativePath()+relativePathInStorage;
    }

    /** (official/temporary/user/secret) ----> xxx_storage_path/dir/sha1 */
    public Map<String, String> getRelativePathInBase(List<String> filePath) {
        logger.info("get relative path in base, filepath={}", filePath);
        Map<String, String> relativePathInStorage = getRelativePathInStorage(filePath);
        if (VerifyUtil.isMapEmpty(relativePathInStorage)) {
            return new HashMap<>();
        }

        Map<String, String> relativePathsInBase = new HashMap<>();
        Set<String> paths = relativePathInStorage.keySet();
        for (String path : paths) {
            String relativePath = relativePathInStorage.get(path);
            String relativePathInBase = getNginxRelativePath() + relativePath;
            relativePathsInBase.put(path, relativePathInBase);
        }
        return relativePathsInBase;
    }

    /** filePath --> dir/sha1 */
    public String getRelativePathInStorage(String filePath) {
        logger.info("get filepath's relative path in {} stroage, filepath={}", getName(), filePath);
        if (VerifyUtil.isStringEmpty(filePath)) {
            logger.info("filepath is empty");
            return "";
        }
        String[] baseurlDirSha1 = decodeFilePath(filePath);
        if (VerifyUtil.isStringEmpty(baseurlDirSha1[1]) && VerifyUtil.isStringEmpty(baseurlDirSha1[2])) {
            logger.info("decode dir and filename is empty");
            return "";
        }
        String relativePath = baseurlDirSha1[1] + File.separator + baseurlDirSha1[2];
        logger.info("relative path={}", relativePath);
        return relativePath;
    }

    /** filePath --> dir/sha1 */
    public Map<String, String> getRelativePathInStorage(List<String> filePaths) {
        logger.info("get files relative path in {} storage", getName());

        Map<String, String> fileRelativePaths = new HashMap<>();
        if (VerifyUtil.isListEmpty(filePaths)) {
            logger.info("the file list is empty");
            return fileRelativePaths;
        }

        for (String filepath : filePaths) {
            String relativePath = getRelativePathInStorage(filepath);
            if (VerifyUtil.isStringEmpty(relativePath)) {
                logger.info("decode file={} dir and filename is empty", filepath);
                fileRelativePaths.clear();
                return fileRelativePaths;
            }
            fileRelativePaths.put(filepath, relativePath);
        }

        return fileRelativePaths;
    }

    abstract public Map<String, String> moveFileToHere(List<String> srcFileAbsolutePath);

    public boolean fileExist(long fileId) {
        logger.info("file {} is exist?", fileId);
        String filePath = dbRecordGet(fileId);
        if (VerifyUtil.isStringEmpty(filePath)) {
            logger.info("file path is empty");
            return false;
        }
        return fileExist(filePath);
    }

    public boolean fileExist(String fileRelativePath) {
        logger.info("file {} is exist?", fileRelativePath);
        if (VerifyUtil.isStringEmpty(fileRelativePath)) {
            logger.info("file path is empty");
            return false;
        }
        String[] baseurlDirSha1 = decodeFilePath(fileRelativePath);
        logger.info("baseUrl={} dir={} filename={}",
                baseurlDirSha1[0], baseurlDirSha1[1], baseurlDirSha1[2]);
        String filePath = getStoragePath()+baseurlDirSha1[1]+File.separator+baseurlDirSha1[2];
        return FileUtil.fileExist(filePath);
    }

    /** file moved map(after_move_path-->before_move_path) */
    protected void rollbackFileMoved(Map<String, String> dest2SrcFileMoved) {
        logger.info("rollbacke file moved");
        if (VerifyUtil.isMapEmpty(dest2SrcFileMoved)) {
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
    }

    /** dir, sha1 */
    protected String[] encodeFilePath(String fileName) throws NoSuchAlgorithmException {
        logger.info("construct file name={}", fileName);
        if (VerifyUtil.isStringEmpty(fileName)) {
            fileName = "unknow";
            logger.warn("the file name is invalid, set it to={}", fileName);
        }

        long   nanoTime         = System.nanoTime();
        String strNanoTime      = fileName+"_"+nanoTime;
        String sha1             = VerifyUtil.sha1(strNanoTime);
        String newDir           = sha1.substring(0, 2);
        String newFileName      = sha1.substring(2);
        return new String[]{newDir, newFileName};
    }

    /** url,dir,sha1 */
    protected String[] decodeFilePath(String filepath) {
        logger.info("deconstruct file name={}", filepath);
        if (VerifyUtil.isStringEmpty(filepath)) {
            logger.error("the file name is invalid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        StringBuilder splash = new StringBuilder();
        splash.append('\\').append('\\');
        filepath = filepath.replaceAll(splash.toString(), "/");
        logger.info("after repalce \\ to /  ={}", filepath);

        String[] component    = filepath.split("/");
        if (component.length<2) {
            return new String[]{"", "", component[0]};
        }
        String   tmpFileName  = component[component.length-1];
        String   tmpDirectory = component[component.length-2];

        int      baseUrlEndIdx= filepath.indexOf(tmpDirectory+"/"+tmpFileName);
        String   baseUrl      = "";
        if (baseUrlEndIdx>1) {
            baseUrl = filepath.substring(0, baseUrlEndIdx-1);
        }

        String sha1 = tmpFileName;
        return new String[]{baseUrl, tmpDirectory, sha1};
    }

    /** url,dir,sha1 */
    protected Map<String, String[]> decodeFilePaths(List<String> filepaths) {
        if (VerifyUtil.isListEmpty(filepaths)) {
            return new HashMap<>();
        }
        Map<String, String[]> path2UrlDirSha1 = new HashMap<>();
        for (String filepath : filepaths) {
            String[] urlDirSha1 = decodeFilePath(filepath);
            path2UrlDirSha1.put(filepath, urlDirSha1);
        }
        return path2UrlDirSha1;
    }
    @Transactional
    private long dbRecordAdd(String fileRealName, String filePath) {
        return dbService.recordFileStorage(fileRealName, filePath);
    }

    private String dbRecordGet(long fileRecordId) {
        String filePath = dbService.getFilePath(fileRecordId);
        return filePath;
    }

    private Map<Long, String> dbRecordGet(List<Long> fileRecordIds) {
        Map<Long, String> fileId2FilePath = dbService.getFilePath(fileRecordIds);
        return fileId2FilePath;
    }

    @Transactional
    private void dbRecordDelete(long fileRecordId) {
        dbService.deleteRecord(fileRecordId);
    }

    @Transactional
    private void dbRecordDelete(List<Long> fileRecordIds) {
        dbService.deleteRecord(fileRecordIds);
    }

}

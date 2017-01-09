package com.cooltoo.util;

import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.file.AbstractFileStorageService;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by hp on 2016/4/21.
 */
public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class.getName());

    public static FileUtil getInstance() {
        return new FileUtil();
    }

    private FileUtil() {}

    public String getFileName(String path) {
        String fileName = path;
        int lastIndexOfSlash = fileName.lastIndexOf('\\');
        int lastIndexOfBackslash = fileName.lastIndexOf('/');
        if (lastIndexOfBackslash>0) {
            fileName = fileName.substring(lastIndexOfBackslash+1);
        }
        else if (lastIndexOfSlash>0) {
            fileName = fileName.substring(lastIndexOfSlash+1);
        }
        else {
            fileName = System.currentTimeMillis()+"";
        }
        return fileName;
    }

    public void writeFile(InputStream input, File outputFile) throws IOException {
        logger.info("write from input--->output={}", outputFile);
        File outputDir = outputFile.getParentFile();
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        OutputStream output = null;
        output = new FileOutputStream(outputFile);
        byte[] buffer = new byte[1024];

        int read = 0;
        while ((read = input.read(buffer))>0) {
            output.write(buffer, 0, read);
        }
        output.flush();
        output.close();
    }

    public boolean copyFile(File fromFile, File toFile) {
        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = new FileInputStream(fromFile);
            out = new FileOutputStream(toFile);
            return copyStream(in, out);
        }
        catch (Exception e) {
            logger.error("failed! copy file from {} to {}, exception is {}",
                    fromFile.getAbsoluteFile(),
                    toFile.getAbsoluteFile(),
                    e.getMessage());
            return false;
        }
        finally {
            if (out != null) {
                try { out.close(); } catch (IOException ioe) {}
            }
            if (in != null) {
                try { in.close(); } catch (IOException ioe) {}
            }
        }
    }

    public boolean copyStream(InputStream in, OutputStream out) {
        final int MAX = 4096;
        byte[] buf = new byte[MAX];
        try {
            for (int bytesRead = in.read(buf, 0, MAX); bytesRead != -1; bytesRead = in.read(buf, 0, MAX)) {
                out.write(buf, 0, bytesRead);
            }
            return true;
        }
        catch (IOException ioe) {
            logger.error("failed! copy stream, exception is {}", ioe.getMessage());
            return false;
        }
    }

    public boolean moveFile(String srcPath, String destPath) {
        logger.info("move file  " + srcPath + " to " + destPath);
        File src = new File(srcPath);
        File dest = new File(destPath);

        if (dest.exists()) {
            logger.warn("dest file is exist, destFile={}", dest);
            dest.delete();
        }
        if (!dest.getParentFile().exists()) {
            if (!dest.getParentFile().mkdirs()) {
                logger.error("make parent directory failed!");
                return false;
            }
        }
        if (!(src.isFile() && src.exists())) {
            logger.error("src file is not file, or not existed!");
            return false;
        }

        boolean success = copyFile(src, dest);
        success = success && deleteFile(srcPath);
        if (!success) {
            logger.error("Failed to move "+src+" to "+dest);
        }
        return success;
    }

    public void moveFiles(Map<String, String> src2dest) {
        logger.info("rollback file moved");
        if (VerifyUtil.isMapEmpty(src2dest)) {
            logger.info("rollback file list is empty");
            return;
        }
        Set<String> srcKeys = src2dest.keySet();
        for (String src : srcKeys) {
            String dest = src2dest.get(src);
            moveFile(src, dest);
        }
    }

    public boolean fileExist(String fileAbsolutePath) {
        logger.info("file exist, file path={}", fileAbsolutePath);
        if (VerifyUtil.isStringEmpty(fileAbsolutePath)) {
            logger.warn("file path is empty");
            return false;
        }
        boolean exist = (new File(fileAbsolutePath)).exists();
        logger.info("exist? {}", exist);
        return exist;
    }

    public boolean deleteFile(String fileAbsolutePath) {
        logger.info("file delete, file path={}", fileAbsolutePath);
        if (VerifyUtil.isStringEmpty(fileAbsolutePath)) {
            logger.warn("file path is empty");
            return true;
        }
        File file = new File(fileAbsolutePath);
        boolean exist = file.exists();
        if (!exist) {
            return true;
        }
        if (file.isDirectory()) {
            return true;
        }
        return file.delete();
    }

    /*=================================================================
     *       cooltoo storage file system structure utilities
     *=================================================================*/
    public static final String CooltooFileFormatter = "^[0-9|a-f|A-F]{2,2}/[0-9|a-f|A-F]+$";

    /**
     * 例如：
     * parentPath：/data/storage/
     * fileRelativePath：http://www.baidu.com/website/xx/xxxxxxxx
     *
     * 判断文件目录（例如：/data/storage/xx）下是否有文件 xxxxxxxx
     * @param parentPath 文件目录（例如：/data/storage/）
     * @param fileRelativePath 文件路径（例如：http://www.baidu.com/website/xx/xxxxxxxx）
     */
    public boolean cooltooFileExistInPath(String parentPath, String fileRelativePath) {
        logger.info("file {} is exist?", fileRelativePath);
        if (VerifyUtil.isStringEmpty(fileRelativePath)) {
            logger.info("relative file path is empty");
            return false;
        }
        if (VerifyUtil.isStringEmpty(parentPath)) {
            logger.info("storage path is empty");
            return false;
        }

        String[] baseurlDirSha1 = decodeFilePath(fileRelativePath);
        String filePath = parentPath + baseurlDirSha1[1] + File.separator + baseurlDirSha1[2];
        return fileExist(filePath);
    }

    /**
     * 例如：
     * filePath：http://www.baidu.com/website/xx/xxxxxxxx
     * filePath：/data/storage_go2nurse/site/xx/xxxxxxxx
     * baseDirectory：official
     * 返回：
     * official/xx/xxxxxxxx
     *
     * 获取文件在 cooltoo 中的相对路径
     * @param baseDirectory 分类目录
     * @param filePath 文件目录（例如：http://www.baidu.com/website/xx/xxxxxxxx）
     * @return 文件在 cooltoo 中的相对路径（例如：分类目录/xx/xxxxxxxx；分类目录为空，返回 xx/xxxxxxxx）
     */
    public String getCooltooFileRelativePath(String baseDirectory, String filePath) {
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
        if (!VerifyUtil.isStringEmpty(baseDirectory)) {
            relativePath = baseDirectory + relativePath;
        }
        return relativePath;
    }

    /**
     * 例如：
     * filePath：http://www.baidu.com/website/xx/xxxxxxxx
     * filePath：/data/storage_go2nurse/site/xx/xxxxxxxx
     * 返回：
     * xx/xxxxxxxx
     *
     * 获取文件在 cooltoo 中的相对路径
     * @param filePaths 文件目录（例如：http://www.baidu.com/website/xx/xxxxxxxx）
     * @return 文件和文件在 cooltoo 中的相对路径的映射关系
     *          （例如：http://www.baidu.com/website/xx/xxxxxxxx<--->xx/xxxxxxxx）
     */
    public Map<String, String> getCooltooFileRelativePath(String baseDirectory, List<String> filePaths) {
        Map<String, String> fileRelativePaths = new HashMap<>();
        if (VerifyUtil.isListEmpty(filePaths)) {
            logger.info("the file list is empty");
            return fileRelativePaths;
        }

        for (String filepath : filePaths) {
            String relativePath = getCooltooFileRelativePath(baseDirectory, filepath);
            if (VerifyUtil.isStringEmpty(relativePath)) {
                logger.info("decode file={} dir and filename is empty", filepath);
                fileRelativePaths.clear();
                return fileRelativePaths;
            }
            fileRelativePaths.put(filepath, relativePath);
        }

        return fileRelativePaths;
    }

    /**
     * 对文件进行编码，返回为文件所在的子路径和文件名两部分
     * @param fileName 文件名
     * @return  [dir, sha1]
     */
    public String[] encodeFilePath(String fileName) throws NoSuchAlgorithmException {
        if (VerifyUtil.isStringEmpty(fileName)) {
            fileName = "unknow";
            logger.warn("the file name is invalid, set it to={}", fileName);
        }
        if (VerifyUtil.isStringEmpty(fileName)) {
            fileName = System.currentTimeMillis()+"_"+System.nanoTime();
        }
        else {
            if (fileName.length()>50) {
                fileName = fileName.substring(0, 50);
            }
        }

        long   nanoTime     = System.nanoTime();
        String strNanoTime  = fileName+"_"+nanoTime;
        String sha1         = VerifyUtil.sha1(strNanoTime);
        String newDirecotry = sha1.substring(0, 2);
        String newFileName  = sha1.substring(2);
        return new String[]{newDirecotry, newFileName};
    }

    /**
     * 对 cooltoo 文件路径进行解码，返回为文件所在的子路径和文件名两部分
     * @param cooltooFilePath cooltoo 文件路径
     * @return  [url 前缀, dir 子目录, sha1 文件名]
     */
    public String[] decodeFilePath(String cooltooFilePath) {
        if (VerifyUtil.isStringEmpty(cooltooFilePath)) {
            logger.error("the file path is empth");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        StringBuilder splash = new StringBuilder();
        splash.append('\\').append('\\');
        cooltooFilePath = cooltooFilePath.replaceAll(splash.toString(), "/");

        String[] component    = cooltooFilePath.split("/");
        if (component.length<2) {
            return new String[]{"", "", component[0]};
        }
        String   tmpFileName  = component[component.length-1];
        String   tmpDirectory = component[component.length-2];

        int      baseUrlEndIdx= cooltooFilePath.indexOf(tmpDirectory+"/"+tmpFileName);
        String   baseUrl      = "";
        if (baseUrlEndIdx>1) {
            baseUrl = cooltooFilePath.substring(0, baseUrlEndIdx-1);
        }

        String sha1 = tmpFileName;
        return new String[]{baseUrl, tmpDirectory, sha1};
    }

    /**
     * 对 cooltoo 文件路径进行解构，返回为文件所在的子路径和文件名两部分
     * @param cooltooFilePaths cooltoo 文件路径
     * @return  源文件路径与解构后 [url 前缀, dir 子目录, sha1 文件名] 的映射关系
     */
    public Map<String, String[]> decodeFilePaths(List<String> cooltooFilePaths) {
        if (VerifyUtil.isListEmpty(cooltooFilePaths)) {
            return new HashMap<>();
        }
        Map<String, String[]> path2UrlDirSha1 = new HashMap<>();
        for (String tmpPath : cooltooFilePaths) {
            String[] urlDirSha1 = decodeFilePath(tmpPath);
            path2UrlDirSha1.put(tmpPath, urlDirSha1);
        }
        return path2UrlDirSha1;
    }

    /** srcFileAbsolutePath---->dir/sha1 */
    /**
     * 文件系统中的文件移动到 dest 中
     * @param srcFileAbsolutePath 文件系统路径
     * @param dest 目标文件系统
     * @return  源文件路径与移动后路径的映射
     */
    public Map<String, String> moveFilesToDest(List<String> srcFileAbsolutePath,
                                               AbstractFileStorageService dest,
                                               boolean needEveryFileMovedSuccessfully) {
        if (VerifyUtil.isListEmpty(srcFileAbsolutePath)) {
            logger.info("the files list is empty");
            return new HashMap<>();
        }
        if (null==dest) {
            logger.info("the dest storage is empty");
            return new HashMap<>();
        }
        logger.info("move files to dest={}. files={}", dest.getName(), srcFileAbsolutePath);

        // absolute_or_relative_file_path_in_temporary---->relative_file_path_in_storage
        String currentMoving = "currentMoving";
        Map<String, String> filePath2StoragePath = new Hashtable<>();
        Map<String, String> successMoved         = new Hashtable<>();
        File fileExist;
        try {
            for (String srcFilePath : srcFileAbsolutePath) {
                fileExist = new File(srcFilePath);
                if (!fileExist.exists()) {
                    logger.warn("file not exist. file is ={}", srcFilePath);
                    continue;
                }
                currentMoving = srcFilePath;
                String[] baseurlDirSha1 = decodeFilePath(srcFilePath);
                // relative_file_path_in_temporary--->dir/sha1
                String relativeFilePath = baseurlDirSha1[1]
                        + File.separator
                        + baseurlDirSha1[2];

                String storagePath  = dest.getStoragePath();
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
                boolean movedSuccessfully = moveFile(srcFilePath, destFilePath);
                if (needEveryFileMovedSuccessfully && !movedSuccessfully) {
                    throw new IOException("move file failed!");
                }
                successMoved.put(destFilePath, srcFilePath);

                filePath2StoragePath.put(srcFilePath, relativeFilePath);
            }
            return filePath2StoragePath;
        }
        catch (Exception ex) {
            logger.error("move file="+currentMoving+" failed! exception="+ex);
            filePath2StoragePath.clear();
            moveFiles(successMoved);/* rollback file moved */
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
    }

    /**
     * 将 cooltoo 文件系统中的文件从 src 移动到 dest 中
     * @param pathsInStorage cooltoo 文件路径
     * @param src 源文件系统
     * @param dest 目标文件系统
     * @return  文件在目标文件系统中的路径
     */
    public List<String> moveFileFromSrcToDest(List<String> pathsInStorage,
                                              AbstractFileStorageService src,
                                              AbstractFileStorageService dest) {
        if (VerifyUtil.isListEmpty(pathsInStorage)) {
            logger.info("nothing need to move");
            return new ArrayList<>();
        }
        if (null==src) {
            logger.info("src is empty");
            return new ArrayList<>();
        }
        if (null==dest) {
            logger.info("dest is empty");
            return new ArrayList<>();
        }
        logger.debug("move files to from={} to={}", src.getName(), dest.getName());

        List<String> srcFilesInStorage = new ArrayList<>();
        for (String tmp : pathsInStorage) {
            String relativePath = src.getRelativePathInStorage(tmp);
            if (VerifyUtil.isStringEmpty(relativePath)) {
                continue;
            }
            srcFilesInStorage.add(src.getStoragePath() + relativePath);
        }
        if (!VerifyUtil.isListEmpty(srcFilesInStorage)) {
            dest.moveFileToHere(srcFilesInStorage);
        }

        return srcFilesInStorage;
    }

}

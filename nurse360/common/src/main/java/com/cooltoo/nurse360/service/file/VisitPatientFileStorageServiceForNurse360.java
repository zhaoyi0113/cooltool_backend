package com.cooltoo.nurse360.service.file;

import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.util.*;

/**
 * Created by zhaolisong on 20/01/2017.
 */
@Service("VisitPatientFileStorageServiceForNurse360")
public class VisitPatientFileStorageServiceForNurse360 extends AbstractFileStorageServiceForNurse360 {

    private static final Logger logger = LoggerFactory.getLogger(VisitPatientFileStorageServiceForNurse360.class.getName());

    @Value("${nurse360.storage.visit.patient.path}")
    private String visitPatient;

    @Override
    public String getName() {
        return "visit_patient";
    }

    @Override
    public String getStoragePath() {
        StringBuilder path = new StringBuilder();
        path.append(super.getStoragePath());
        path.append(visitPatient);
        logger.info("get visit_patient storage path={}", path.toString());
        return path.toString();
    }

    @Override
    public String getNginxRelativePath() {
        logger.info("get visit_patient nginx path={}", visitPatient);
        return visitPatient;
    }

    /**
     * delete file path(which end with relative path in its storage)
     * @param filePath deleted file path, path format is
     *                  userId_patientId_pageIndex_
     */
    @Override
    public boolean deleteFile(String filePath) {
        this.deleteFileByPaths(Arrays.asList(new String[]{filePath}));
        return true;
    }

    /**
     * delete file paths(which end with relative path in its storage)
     * @param filePaths deleted file paths, path format is
     *                  userId_patientId_pageIndex_
     */
    @Override
    public void deleteFileByPaths(List<String> filePaths) {
        logger.info("delete visit_patient file, filepath={}", filePaths);
        if (VerifyUtil.isListEmpty(filePaths)) {
            logger.info("filepath is empty");
            return;
        }

        // userId_patientId --> pageIndex_
        Map<String, List<String>> dirAndFiles = parseFilePath(filePaths);
        // userId_patientId --> pageIndex_ is empty
        if (dirAndFiles.isEmpty()) {
            return;
        }

        Map<String, List<PageFile>> dirAndPages = parsePageFile(dirAndFiles, false);
        if (dirAndPages.isEmpty()) {
            return;
        }

        // delete files
        Set<String> dirs = dirAndPages.keySet();
        for (String dir : dirs) {
            List<PageFile> pages = dirAndPages.get(dir);
            for (PageFile page : pages) {
                page.file.delete();
            }
        }
    }


    /**
     * exist file paths(which end with relative path in its storage)
     * @param filePaths deleted file paths, path format is
     *                  userId_patientId_pageIndex_
     */
    public Map<String, Boolean>  isFilePathExist(List<String> filePaths) {
        Map<String, List<String>> dirAndFiles = parseFilePath(filePaths);
        Map<String, Boolean> dirExisted = new HashMap<>();
        if (null==dirAndFiles) {
            return dirExisted;
        }

        // delete files
        Set<String> dirs = dirAndFiles.keySet();
        for (String dir : dirs) {
            // dest_visit_patient_dir--->visit_patient_base_path/
            String visitRecordPath = getStoragePath();
            // dest_relative_dir--->userId_patientId/
            String destRelativeDirPath = dir;
            // visit_patient directory existed
            File destDir = new File(visitRecordPath + destRelativeDirPath);

            String dirName = dir.substring(0, dir.length() - 1);
            if (destDir.exists() && destDir.isDirectory()) {
                String[] fileNames = destDir.list();
                if (null != fileNames && fileNames.length > 0) {
                    dirExisted.put(dirName, true);
                }
                else {
                    dirExisted.put(dirName, false);
                }
            }
            else {
                dirExisted.put(dirName, false);
            }
        }

        return dirExisted;
    }

    /**
     * get files (which end with relative path in its storage)
     * @param filePath deleted file paths, path format is
     *                  userId_patientId_pageIndex_
     */
    public List<PageFile> getFileAfterRecord(String filePath, long recordId) {
        Map<String, List<String>> dirAndFiles = parseFilePath(Arrays.asList(new String[]{filePath}));
        Map<String, List<PageFile>> dirAndPageFiles = parsePageFile(dirAndFiles, false);

        String dir = filePath + File.separator;
        List<PageFile> pageFiles = dirAndPageFiles.get(dir);

        List<PageFile> pagesAfterRecordId = new ArrayList<>();

        for (PageFile page : pageFiles) {
            if (page.containRecord(recordId)>=0) {
                pagesAfterRecordId.add(page);
            }
        }
        return pagesAfterRecordId;
    }

    /**
     * update file paths(which end with relative path in its storage)
     * @param filePath deleted file paths, path format is
     *                  userId_patientId_pageIndex_
     */
    public PageFile getLastFile(String filePath) {
        Map<String, List<String>> dirAndFiles = parseFilePath(Arrays.asList(new String[]{filePath}));
        Map<String, List<PageFile>> dirAndPageFiles = parsePageFile(dirAndFiles, false);

        String dir = filePath + File.separator;
        List<PageFile> pageFiles = dirAndPageFiles.get(dir);

        PageFile lastPage = null;
        for (PageFile page : pageFiles) {
            if (null==lastPage) {
                lastPage = page;
                continue;
            }

            if (lastPage.pageIndex<page.pageIndex) {
                lastPage = page;
            }
        }
        return lastPage;
    }

    /**
     * get file paths(which end with relative path in its storage)
     * @param filePaths deleted file paths, path format is
     *                  userId_patientId_pageIndex_
     */
    public Map<String, List<String>> getFileUrl(List<String> filePaths, boolean fillEmpty, String nginxPrefix) {
        Map<String, List<String>> dirAndFiles = parseFilePath(filePaths);
        Map<String, List<PageFile>> dirAndPageFiles = parsePageFile(dirAndFiles, fillEmpty);

        Map<String, List<String>> dirAndUrls = new HashMap<>();
        if (!dirAndPageFiles.isEmpty()) {
            Set<String> keys = dirAndPageFiles.keySet();
            for (String key : keys) {
                List<PageFile> pages = dirAndPageFiles.get(key);
                List<String> pageUrls = dirAndUrls.get(key.substring(0, key.length()-1));
                if (null==pageUrls) {
                    pageUrls = new ArrayList<>();
                    dirAndUrls.put(key.substring(0, key.length()-1), pageUrls);
                }
                for (PageFile page : pages) {
                    pageUrls.add(nginxPrefix + getNginxRelativePath() + key + page.fileName);
                }
            }
        }

        return dirAndUrls;
    }

    /**
     * get file paths(which end with relative path in its storage)
     * @param dirAndFiles dir-->file prefix, path format is
     *                  [userId_patientId --> [pageIndex_]]
     * @return dir-->page file, path format is
     *                  [userId_patientId --> [page file]]
     */
    private Map<String, List<PageFile>> parsePageFile(Map<String, List<String>> dirAndFiles, boolean fillEmpty) {
        Map<String, List<PageFile>> dirAndPage = new HashMap<>();
        if (null==dirAndFiles) {
            return dirAndPage;
        }

        // delete files
        Set<String> dirs = dirAndFiles.keySet();
        for (String dir : dirs) {
            final List<String> pageIndexPrefix = dirAndFiles.get(dir);

            // dest_visit_patient_dir--->visit_patient_base_path/
            String visitRecordPath    = getStoragePath();
            // dest_relative_dir--->userId_patientId/
            String destRelativeDirPath= dir;
            // visit_patient directory existed
            File destDir = new File(visitRecordPath + destRelativeDirPath);
            if (!destDir.exists()) {
                continue;
            }

            File[] files = destDir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if (null==pathname) {
                        return false;
                    }
                    String name = pathname.getName();
                    if (!name.matches("^\\d+_\\d+_\\d+_\\d+_\\d+$")) {
                        return false;
                    }
                    if (fillEmpty) {
                        return true;
                    }
                    int pageIndex = name.indexOf("_");
                    if (pageIndex>=1) {
                        return pageIndexPrefix.contains(name.substring(0, pageIndex+1));
                    }
                    return false;
                }
            });

            List<PageFile> pages = dirAndPage.get(dir);
            if (null==pages) {
                pages = new ArrayList<>();
                dirAndPage.put(dir, pages);
            }
            if (null!=files) {
                for (File file : files) {
                    PageFile pageFile = new PageFile(file);
                    pages.add(pageFile);
                }
                Collections.sort(pages, new PageSorter());
            }
        }

        return dirAndPage;
    }

    /**
     * parse file paths(which end with relative path in its storage)
     * @param filePaths file paths, path format is [userId_patientId_pageIndex_]
     * @return dir-->file prefix, path format is
     *                  [userId_patientId --> [pageIndex_]]
     */
    private Map<String, List<String>> parseFilePath(List<String> filePaths) {

        // userId_patientId --> pageIndex_
        Map<String, List<String>> dirAndFiles = new HashMap<>();
        if (filePaths.isEmpty()) {
            return dirAndFiles;
        }

        // parse file paths
        for (String filePath : filePaths) {
            //[userId, patientId, pageIndex]
            String[] recordFileParts = filePath.split("_");
            if (recordFileParts.length<2) {
                logger.warn("file name is invalid, file={}", filePath);
                continue;
            }

            // dest_relative_dir--->userId_patientId/
            String destRelativeDirPath      =
                    recordFileParts[0] + "_" +
                    recordFileParts[1] + File.separator;

            List<String> filesPrefix = dirAndFiles.get(destRelativeDirPath);
            if (null==filesPrefix) {
                filesPrefix = new ArrayList<>();
                dirAndFiles.put(destRelativeDirPath, filesPrefix);
            }

            if (recordFileParts.length>=3) {
                filesPrefix.add(recordFileParts[2] + "_");
            }
        }
        return dirAndFiles;
    }

    /**
     * srcFileAbsolutePath---->dir/userId_patientId/
     * @param srcVisitRecordFileAbsolutePath path list, path format is
     *                                       vr_userId_patientId_pageIndex_1stRecordId_1stLineIndex_2ndRecordId_2ndLineIndex
     */
    @Override
    public Map<String, String> moveFileToHere(List<String> srcVisitRecordFileAbsolutePath) {
        logger.info("move file to visit_patient. file={}", srcVisitRecordFileAbsolutePath);
        if (VerifyUtil.isListEmpty(srcVisitRecordFileAbsolutePath)) {
            logger.info("the files list is empty");
            return new HashMap<>();
        }

        Map<String, String> filePath2VisitPatientPath = new Hashtable<>();
        Map<String, String> successMoved = new Hashtable<>();
        try {
            for (String filePath : srcVisitRecordFileAbsolutePath) {
                String pageFileName = filePath.substring(filePath.indexOf("vr_"));
                //[tempFile_vr, userId, patientId, pageIndex, 1stRecordId, 1stLineIndex, 2ndRecordId, 2ndLineIndex]
                String[] recordFileParts = pageFileName.split("_");
                if (recordFileParts.length!=8) {
                    continue;
                }


                // dest_visit_patient_dir--->visit_patient_base_path/
                String visitRecordPath    = getStoragePath();
                // dest_relative_dir--->userId_patientId/
                String destRelativeDirPath      =
                        recordFileParts[1] + "_" +
                        recordFileParts[2] + File.separator;
                // dest_relative_file_path--->pageIndex_1stRecordId_1stLineIndex_2ndRecordId_2ndLineIndex
                String destRelativeFilePath =
                        recordFileParts[3] + "_" +
                        recordFileParts[4] + "_" +
                        recordFileParts[5] + "_" +
                        recordFileParts[6] + "_" +
                        recordFileParts[7];

                // make the visit_patient directory if necessary
                File destDir = new File(visitRecordPath + destRelativeDirPath);
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }

                // move storage file to visit_patient dir
                fileUtil.moveFile(filePath, visitRecordPath + destRelativeDirPath + destRelativeFilePath);

                // cache moved file
                successMoved.put(visitRecordPath + destRelativeDirPath + destRelativeFilePath, filePath);
                filePath2VisitPatientPath.put(filePath, getNginxRelativePath() + destRelativeDirPath + destRelativeFilePath);
            }
            return filePath2VisitPatientPath;
        }
        catch (Exception ex) {
            logger.error("move file failed!", ex);
            filePath2VisitPatientPath.clear();
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
    @Deprecated @Override public Map<Long, String> getFileUrl(List<Long> fileIds) {
        throw new UnsupportedOperationException();
    }
    @Deprecated @Override public boolean fileExist(long fileId) {
        throw new UnsupportedOperationException();
    }

    class PageFile {
        int pageIndex = -1;
        int firstRecordId = -1;
        int firstLine = -1;
        int secondRecordId = -1;
        int secondLine= -1;
        String fileName = "";
        File file = null;

        public PageFile(File file) {
            if (null==file) {
                return;
            }
            this.fileName = file.getName();
            this.file = file;
            String[] nameParts = this.fileName.split("_");
            if (nameParts.length==5) {
                pageIndex = VerifyUtil.parseIntIds(nameParts[0]).get(0);
                firstRecordId = VerifyUtil.parseIntIds(nameParts[1]).get(0);
                firstLine = VerifyUtil.parseIntIds(nameParts[2]).get(0);
                secondRecordId = VerifyUtil.parseIntIds(nameParts[3]).get(0);
                secondLine = VerifyUtil.parseIntIds(nameParts[4]).get(0);
            }

        }

        public int containRecord(long recordId) {
            if (secondRecordId<recordId) {
                return -1;
            }
            if (firstRecordId<=recordId && recordId<=secondRecordId) {
                return 0;
            }
            if (firstRecordId>recordId) {
                return 1;
            }
            return -1;
        }
    }

    class PageSorter implements Comparator<PageFile> {

        @Override
        public int compare(PageFile o1, PageFile o2) {
            return o1.pageIndex - o2.pageIndex;
        }
    }
}

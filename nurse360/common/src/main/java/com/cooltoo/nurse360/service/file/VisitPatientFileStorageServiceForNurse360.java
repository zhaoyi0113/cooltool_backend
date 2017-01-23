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
     * @param fileDir get file dir path, dir path format is userId_patientId
     * @param prefix file prefix, file prefix format is vendorType_vendorId_departId
     */
    public void deleteFileByPaths(String fileDir, String prefix) {
        logger.info("delete visit_patient file, fileDir={} prefix={}", fileDir, prefix);
        if (VerifyUtil.isStringEmpty(fileDir)) {
            logger.info("fileDir is empty");
            return;
        }

        List<PageFile> pages = parsePageFile(fileDir, prefix);
        if (pages.isEmpty()) {
            return;
        }

        // delete files
        for (PageFile page : pages) {
            page.file.delete();
        }
    }

    /**
     * delete file paths(which end with relative path in its storage)
     * @param fileDir get file dir path, dir path format is userId_patientId
     * @param prefix file prefix, file prefix format is vendorType_vendorId_departId
     * @param pageIndex page index, file prefix format is vendorType_vendorId_departId
     */
    public void deleteFileByPaths(String fileDir, String prefix, long pageIndex) {
        logger.info("delete visit_patient file, fileDir={} prefix={}", fileDir, prefix);
        if (VerifyUtil.isStringEmpty(fileDir)) {
            logger.info("fileDir is empty");
            return;
        }

        List<PageFile> pages = parsePageFile(fileDir, prefix);
        if (pages.isEmpty()) {
            return;
        }

        // delete files
        for (PageFile page : pages) {
            if (page.pageIndex>=pageIndex) {
                page.file.delete();
            }
        }
    }


    /**
     * exist file paths(which end with relative path in its storage)
     * @param fileDir get file dir path, dir path format is userId_patientId
     * @param prefix file prefix, file prefix format is vendorType_vendorId_departId
     */
    public boolean isFilePathExist(String fileDir, final String prefix) {
        // dest_visit_patient_dir--->visit_patient_base_path/
        String visitRecordPath = getStoragePath();
        // dest_relative_dir--->userId_patientId/
        String destRelativeDirPath = fileDir;
        // visit_patient directory existed
        File destDir = new File(visitRecordPath + destRelativeDirPath);

        boolean isExisted = false;
        if (destDir.exists() && destDir.isDirectory()) {
            String[] fileNames = destDir.list();
            if (null!=fileNames && fileNames.length!=0) {
                for (String fileName : fileNames) {
                    if (fileName.startsWith(prefix)) {
                        isExisted = true;
                        break;
                    }
                }
            }
        }

        return isExisted;
    }

    /**
     * get files (which end with relative path in its storage)
     * @param fileDir get file dir path, dir path format is userId_patientId
     * @param prefix file prefix, file prefix format is vendorType_vendorId_departId
     * @return page files
     */
    public List<PageFile> getFileAfterRecord(String fileDir, final String prefix, long recordId) {
        List<PageFile> pageFiles = parsePageFile(fileDir, prefix);

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
     * @param fileDir get file dir path, dir path format is userId_patientId
     * @param prefix file prefix, file prefix format is vendorType_vendorId_departId
     */
    public PageFile getLastFile(String fileDir, String prefix) {
        List<PageFile> pageFiles = parsePageFile(fileDir, prefix);

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
     * get file url(which end with relative path in its storage)
     * @param fileDir get file dir path, dir path format is userId_patientId
     * @param prefix file prefix, file prefix format is vendorType_vendorId_departId
     * @param nginxPrefix nginx url prefix, nginx url prefix format is http(s)://ip:port/
     * @return dir-->page file urls, path format is
     *                  [userId_patientId --> [page file urls]]
     */
    public List<String> getFileUrl(String fileDir, String prefix, String nginxPrefix) {
        List<PageFile> pageFiles = parsePageFile(fileDir, prefix);

        List<String> pageUrls = new ArrayList<>();
        if (!pageFiles.isEmpty()) {
            for (PageFile page : pageFiles) {
                pageUrls.add(nginxPrefix + getNginxRelativePath() + fileDir + File.separator + page.fileName);
            }
        }

        return pageUrls;
    }

    /**
     * get file paths(which end with relative path in its storage)
     * @param fileDir get file dir path, dir path format is userId_patientId
     * @param prefix file prefix, file prefix format is vendorType_vendorId_departId
     * @return page files, path format is
     *                  [userId_patientId --> [page file]]
     */
    private List<PageFile> parsePageFile(String fileDir, final String prefix) {
        List<PageFile> pages = new ArrayList<>();
        if (null==fileDir || fileDir.trim().isEmpty()) {
            return pages;
        }

        // dest_visit_patient_dir--->visit_patient_base_path/
        String visitRecordPath    = getStoragePath();
        // dest_relative_dir--->userId_patientId/
        String destRelativeDirPath= fileDir;
        // visit_patient directory existed
        File destDir = new File(visitRecordPath + destRelativeDirPath);
        if (!destDir.exists()) {
            return pages;
        }

        File[] files = destDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                if (null==pathname) {
                    return false;
                }
                String name = pathname.getName();
                if (!name.matches("^[\\d+_]+\\d+$")) {
                    return false;
                }
                if (null==prefix || prefix.trim().isEmpty()) {
                    return false;
                }
                if (!name.startsWith(prefix)) {
                    return false;
                }
                return true;
            }
        });

        if (null!=files) {
            for (File file : files) {
                PageFile pageFile = new PageFile(file);
                pages.add(pageFile);
            }
            Collections.sort(pages, new PageSorter());
        }

        return pages;
    }

    /**
     * srcFileAbsolutePath---->dir/userId_patientId/
     * @param srcVisitRecordFileAbsolutePath
     * path list, path format is
     *      vr_userId_patientId_vendorType_vendorId_departId_pageIndex_1stRecordId_1stLineIndex_2ndRecordId_2ndLineIndex
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
                //[tempFile_vr, userId, patientId, vendorType, vendorId, departId, pageIndex, 1stRecordId, 1stLineIndex, 2ndRecordId, 2ndLineIndex]
                String[] recordFileParts = pageFileName.split("_");
                if (recordFileParts.length!=11) {
                    continue;
                }

                // dest_visit_patient_dir--->visit_patient_base_path/
                String visitRecordPath    = getStoragePath();
                // dest_relative_dir--->userId_patientId/
                String destRelativeDirPath      =
                        recordFileParts[1] + "_" +
                        recordFileParts[2] + File.separator;
                // dest_relative_file_path--->vendorType_vendorId_departId_pageIndex_1stRecordId_1stLineIndex_2ndRecordId_2ndLineIndex
                String destRelativeFilePath =
                        recordFileParts[3] + "_" +
                        recordFileParts[4] + "_" +
                        recordFileParts[5] + "_" +
                        recordFileParts[6] + "_" +
                        recordFileParts[7] + "_" +
                        recordFileParts[8] + "_" +
                        recordFileParts[9] + "_" +
                        recordFileParts[10];

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
    @Deprecated @Override public void deleteFileByPaths(List<String> filePaths) { throw new UnsupportedOperationException(); }
    @Deprecated @Override public String getFilePath(long fileId) {
        throw new UnsupportedOperationException();
    }
    @Deprecated @Override public Map<Long, String> getFileUrl(List<Long> fileIds) {
        throw new UnsupportedOperationException();
    }
    @Deprecated @Override public boolean fileExist(long fileId) {
        throw new UnsupportedOperationException();
    }

    public static class PageFile {
        public int vendorType = -1;
        public long vendorId = -1;
        public long departId = -1;
        public int pageIndex = -1;
        public long firstRecordId = -1;
        public int firstLine = -1;
        public long secondRecordId = -1;
        public int secondLine= -1;
        public String fileName = "";
        public File file = null;

        public PageFile(File file) {
            if (null==file) {
                return;
            }
            this.fileName = file.getName();
            this.file = file;
            String[] nameParts = this.fileName.split("_");
            if (nameParts.length==8) {
                vendorType    = VerifyUtil.parseIntIds( nameParts[0]).get(0);
                vendorId      = VerifyUtil.parseLongIds(nameParts[1]).get(0);
                departId      = VerifyUtil.parseLongIds(nameParts[2]).get(0);
                pageIndex     = VerifyUtil.parseIntIds( nameParts[3]).get(0);
                firstRecordId = VerifyUtil.parseLongIds(nameParts[4]).get(0);
                firstLine     = VerifyUtil.parseIntIds( nameParts[5]).get(0);
                secondRecordId= VerifyUtil.parseLongIds(nameParts[6]).get(0);
                secondLine    = VerifyUtil.parseIntIds( nameParts[7]).get(0);
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

    public static class PageSorter implements Comparator<PageFile> {

        @Override
        public int compare(PageFile o1, PageFile o2) {
            return o1.pageIndex - o2.pageIndex;
        }
    }
}

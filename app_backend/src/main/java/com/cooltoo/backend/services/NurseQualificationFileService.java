package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseQualificationFileBean;
import com.cooltoo.backend.beans.WorkFileTypeBean;
import com.cooltoo.backend.converter.NurseQualificationFileBeanConverter;
import com.cooltoo.backend.entities.NurseQualificationFileEntity;
import com.cooltoo.backend.repository.NurseQualificationFileRepository;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.SecretFileStorageService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/4/4.
 */
@Service("NurseQualificationFileService")
public class NurseQualificationFileService {

    private static final Logger logger = LoggerFactory.getLogger(NurseQualificationFileService.class.getName());

    private static final Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "workfileTypeId"),
                                              new Sort.Order(Sort.Direction.ASC, "timeCreated"));
    @Autowired
    private NurseQualificationFileRepository repository;
    @Autowired
    @Qualifier("SecretFileStorageService")
    private SecretFileStorageService storageService;
    @Autowired
    private WorkFileTypeService workfileTypeService;
    @Autowired
    private NurseQualificationFileBeanConverter beanConverter;

    //=============================================================
    //       get qualification file
    //=============================================================
    public List<NurseQualificationFileBean> getAllFileByQualificationId(long qualificationId) {
        logger.info("get all qualification file by qualification id =={}", qualificationId);
        List<NurseQualificationFileEntity> qualificationFiles  = null;
        List<NurseQualificationFileBean>   qualificationFilesB = null;
        NurseQualificationFileBean         qualificationFileB  = null;
        List<Long>                         imageIds            = null;
        Map<Long, String>                  img2Url             = null;
        List<WorkFileTypeBean>             workFileTypes       = null;

        workFileTypes       = workfileTypeService.getAllWorkFileType();

        qualificationFiles  = repository.findByQualificationId(qualificationId, sort);

        qualificationFilesB = new ArrayList<NurseQualificationFileBean>();
        imageIds            = new ArrayList<Long>();
        for (NurseQualificationFileEntity qualificationFile : qualificationFiles) {
            qualificationFileB = beanConverter.convert(qualificationFile);
            qualificationFilesB.add(qualificationFileB);
            imageIds.add(qualificationFileB.getWorkfileId());
            for (WorkFileTypeBean workfileType : workFileTypes) {
                if (qualificationFileB.getWorkfileTypeId() == workfileType.getId()) {
                    qualificationFileB.setWorkfileType(workfileType);
                    break;
                }
            }
        }

        img2Url = storageService.getFilePath(imageIds);

        for (NurseQualificationFileBean fileBean : qualificationFilesB) {
            String url = img2Url.get(fileBean.getWorkfileId());
            if (!VerifyUtil.isStringEmpty(url)) {
                fileBean.setWorkfileUrl(url);
            }
        }

        return qualificationFilesB;
    }


    //=============================================================
    //       delete qualification file
    //=============================================================

    public NurseQualificationFileBean deleteQualificationFileById(long qualificationFileId) {
        NurseQualificationFileEntity file =  repository.findOne(qualificationFileId);
        if (null==file) {
            logger.info("the record not exist by qualification file id {}", qualificationFileId);
            return null;
        }
        repository.delete(file);
        logger.info("delete the qualification file {}", file);
        return beanConverter.convert(file);
    }

    public List<NurseQualificationFileBean> deleteFileByQualificationId(long qualificationId) {
        List<NurseQualificationFileBean> files = getAllFileByQualificationId(qualificationId);
        if (null==files||files.isEmpty()) {
            logger.info("the record not exist by qualification id {}", qualificationId);
            return null;
        }
        List<Long> imageIds = new ArrayList<Long>();
        for (NurseQualificationFileBean file : files) {
            imageIds.add(file.getWorkfileId());
        }
        repository.deleteByQualificationId(qualificationId);
        storageService.deleteFiles(imageIds);
        logger.info("delete the qualification files ==== {}", files);
        return files;
    }


    //=============================================================
    //       add qualification file
    //=============================================================
    public String addQualificationFile(long qualificationId, WorkFileTypeBean workFileType, String fileName, InputStream file) {
        logger.info("add a qualification file parameters is qualification_id={} type={} fileName={} file={}", qualificationId, workFileType, fileName, file);
        String filePath = null;

        if (qualificationId<=0 || null==workFileType || workFileType.getId()<=0) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        long existsCount = repository.countByQualificationIdAndWorkfileTypeId(qualificationId, workFileType.getId());
        if (existsCount >= workFileType.getMaxFileCount()) {
            logger.info("the file larger than the number limitation.");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        long fileId = 0;
        // set WorkFileId and Status
        if (null==file) {
            logger.info("upload file is null");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        else {
            if (VerifyUtil.isStringEmpty(fileName)) {
                fileName = "tmp_"+workFileType.getName()+System.nanoTime();
            }
            fileId = storageService.saveFile(0, fileName, file);
            if (fileId <= 0) {
                throw new BadRequestException(ErrorCode.WORK_FILE_UPLOAD_FAILED);
            }
            filePath = storageService.getFilePath(fileId);
        }
        NurseQualificationFileEntity entity = new NurseQualificationFileEntity();
        entity.setQualificationId(qualificationId);
        entity.setWorkfileTypeId(workFileType.getId());
        entity.setWorkfileId(fileId);
        entity.setTimeCreated(new Date());
        repository.save(entity);

        return filePath;
    }

    public NurseQualificationFileBean updateQualificationFile(long id, WorkFileTypeBean workFileType, String fileName, InputStream file, Date expiryTime) {
        logger.info("update a qualification file parameters is expiryTime={} type={} fileName={} file={}", expiryTime, workFileType, fileName, file);

        String filePath = "";
        boolean changed = false;

        // check the record exist
        NurseQualificationFileEntity entity = repository.findOne(id);
        if (null== entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        // set WorkFileId
        if (null!=file) {
            if (VerifyUtil.isStringEmpty(fileName)) {
                fileName = "upload_qualification_file_"+workFileType.getName()+System.nanoTime();
            }
            long fileId = storageService.saveFile(entity.getWorkfileId(), fileName, file);
            if (fileId > 0) {
                entity.setWorkfileId(fileId);
                filePath = storageService.getFilePath(fileId);
                changed = true;
            }
            else {
                entity.setWorkfileId(-1);
            }
        }
        // set WorkFileType
        if (null!=workFileType) {
            entity.setWorkfileTypeId(workFileType.getId());
            changed = true;
        }
        // set expiry time
        if (null!=expiryTime) {
            entity.setTimeExpiry(expiryTime);
            changed = true;
        }
        if (changed) {
            entity = repository.save(entity);
        }
        NurseQualificationFileBean bean = beanConverter.convert(entity);
        bean.setWorkfileUrl(filePath);
        return bean;
    }
}

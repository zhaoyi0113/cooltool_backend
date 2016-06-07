package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseQualificationFileBean;
import com.cooltoo.beans.WorkFileTypeBean;
import com.cooltoo.backend.converter.NurseQualificationFileBeanConverter;
import com.cooltoo.backend.entities.NurseQualificationFileEntity;
import com.cooltoo.backend.repository.NurseQualificationFileRepository;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.WorkFileTypeService;
import com.cooltoo.services.file.SecretFileStorageService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

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
    private SecretFileStorageService secretStorage;
    @Autowired
    private WorkFileTypeService workfileTypeService;
    @Autowired
    private NurseQualificationFileBeanConverter beanConverter;

    //=============================================================
    //       get qualification file
    //=============================================================
    public List<NurseQualificationFileBean> getAllFileByQualificationId(long qualificationId) {
        logger.info("get all qualification file by qualification id =={}", qualificationId);
        List<NurseQualificationFileEntity> fileEntities  = repository.findByQualificationId(qualificationId, sort);
        List<NurseQualificationFileBean> fileBeans = new ArrayList<NurseQualificationFileBean>();
        for (NurseQualificationFileEntity file : fileEntities) {
            NurseQualificationFileBean fileBean = beanConverter.convert(file);
            fileBeans.add(fileBean);
        }
        fillOtherProperties(fileBeans);
        return fileBeans;
    }


    public Map<Long, List<NurseQualificationFileBean>> getAllFileByQualificationId(List<Long> qualificationIds) {
        List<NurseQualificationFileEntity> fileEntity     = repository.findByQualificationIdIn(qualificationIds, sort);
        List<NurseQualificationFileBean>   fileBeans = new ArrayList<NurseQualificationFileBean>();
        for (NurseQualificationFileEntity file : fileEntity) {
            NurseQualificationFileBean bean = beanConverter.convert(file);
            fileBeans.add(bean);
        }
        fillOtherProperties(fileBeans);

        Map<Long, List<NurseQualificationFileBean>> qualificatinoId2Files = new HashMap<>();
        for (NurseQualificationFileBean bean : fileBeans) {
            List<NurseQualificationFileBean> files = qualificatinoId2Files.get(bean.getQualificationId());
            if (null==files) {
                files = new ArrayList<>();
                qualificatinoId2Files.put(bean.getQualificationId(), files);
            }
            files.add(bean);
        }

        return qualificatinoId2Files;
    }

    private void fillOtherProperties(List<NurseQualificationFileBean> qualFiles) {
        if (null==qualFiles || qualFiles.isEmpty()) {
            return;
        }
        List<WorkFileTypeBean> workFileTypes = workfileTypeService.getAllWorkFileType();
        Map<Integer, WorkFileTypeBean> workFileTypeId2Bean = new HashMap<>();
        for (WorkFileTypeBean fileType : workFileTypes) {
            workFileTypeId2Bean.put(fileType.getId(), fileType);
        }

        List<Long> imageIds = new ArrayList<Long>();
        for (NurseQualificationFileBean file : qualFiles) {
            imageIds.add(file.getWorkfileId());
        }

        Map<Long, String> imageId2Url = secretStorage.getFilePath(imageIds);
        for (NurseQualificationFileBean file : qualFiles) {
            String url = imageId2Url.get(file.getWorkfileId());
            if (!VerifyUtil.isStringEmpty(url)) {
                file.setWorkfileUrl(url);
            }
            WorkFileTypeBean fileType = workFileTypeId2Bean.get(file.getWorkfileTypeId());
            if (null!=fileType) {
                file.setWorkfileType(fileType);
            }
        }
    }

    //=============================================================
    //       delete qualification file
    //=============================================================
    @Transactional
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
        secretStorage.deleteFiles(imageIds);
        logger.info("delete the qualification files ==== {}", files);
        return files;
    }


    //=============================================================
    //       add qualification file
    //=============================================================
    @Transactional
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
            fileId = secretStorage.addFile(0, fileName, file);
            if (fileId <= 0) {
                throw new BadRequestException(ErrorCode.WORK_FILE_UPLOAD_FAILED);
            }
            filePath = secretStorage.getFilePath(fileId);
        }
        NurseQualificationFileEntity entity = new NurseQualificationFileEntity();
        entity.setQualificationId(qualificationId);
        entity.setWorkfileTypeId(workFileType.getId());
        entity.setWorkfileId(fileId);
        entity.setTimeCreated(new Date());
        repository.save(entity);

        return filePath;
    }

    @Transactional
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
            long fileId = secretStorage.addFile(entity.getWorkfileId(), fileName, file);
            if (fileId > 0) {
                entity.setWorkfileId(fileId);
                filePath = secretStorage.getFilePath(fileId);
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

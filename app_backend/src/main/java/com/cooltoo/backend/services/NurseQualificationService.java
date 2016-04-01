package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseQualificationBean;
import com.cooltoo.backend.beans.WorkFileTypeBean;
import com.cooltoo.backend.converter.NurseQualificationBeanConverter;
import com.cooltoo.backend.entities.NurseQualificationEntity;
import com.cooltoo.backend.repository.NurseQualificationRepository;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.constants.VetStatus;
import com.cooltoo.constants.WorkFileType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.StorageService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by zhaolisong on 16/3/23.
 */
@Service("NurseQualificationService")
public class NurseQualificationService {

    private static final Logger logger = Logger.getLogger(NurseQualificationService.class.getName());

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.ASC,  "name"),
            new Sort.Order(Sort.Direction.DESC, "timeCreated"));

    @Autowired
    private NurseQualificationRepository workfileRepository;

    @Autowired
    private NurseRepository nurseRepository;

    @Autowired
    private WorkFileTypeService workfileTypeService;

    @Autowired
    @Qualifier("StorageService")
    private StorageService storageService;

    @Autowired
    private NurseQualificationBeanConverter workfileBeanConverter;

    //=======================================================
    //         add qualification file
    //=======================================================

    public NurseQualificationBean addWorkFile(long nurseId, String name, String workfileType, String fileName, InputStream file) {
        WorkFileType type = WorkFileType.parseString(workfileType);
        WorkFileTypeBean workFileTypeB = workfileTypeService.getWorkFileTypeByType(type);
        NurseQualificationBean retVal = addWorkFile(nurseId, name, workFileTypeB, fileName, file);
        return retVal;
    }

    private NurseQualificationBean addWorkFile(long nurseId, String name, WorkFileTypeBean workfileType, String fileName, InputStream file) {
        NurseQualificationEntity qualificationEntity = new NurseQualificationEntity();
        logger.info("add nurse id is : " + nurseId);
        logger.info("add nurse qualification name is : " + name);
        logger.info("add nurse work file type is : " + workfileType);
        logger.info("add nurse work file name is : " + fileName);
        logger.info("add nurse work file is null : " + (null==file));

        // check work file type
        if (null==workfileType) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        List<NurseQualificationEntity> qualifications = workfileRepository.findByUserIdAndWorkFileType(nurseId, workfileType.getId(), sort);
        if (qualifications.size()>=workfileType.getMaxFileCount()) {
            logger.info("add nurse qualification file type is reach the max limit number!");
            throw new BadRequestException(ErrorCode.NURSE_QUALIFICATION_IDENTIFICATION_EXIST);
        }

        // is Qualification exist
        //List<NurseQualificationEntity> qualifications = workfileRepository.findNurseQualificationByUserIdAndName(nurseId, name, sort);
        //if (!qualifications.isEmpty()) {
        //    throw new BadRequestException(ErrorCode.NURSE_QUALIFICATION_NAME_EXIST);
        //}

        // set NurseId
        qualificationEntity.setUserId(nurseId);

        // set QualificationName
        //if (VerifyUtil.isStringEmpty(name)) {
        //    throw new BadRequestException(ErrorCode.DATA_ERROR);
        //}
        if (!VerifyUtil.isStringEmpty(name)) {
            qualificationEntity.setName(name);
        }

        // set QualificationType
        qualificationEntity.setWorkFileType(workfileType.getId());

        // set WorkFileId and Status
        if (null==file) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        else {
            if (VerifyUtil.isStringEmpty(fileName)) {
                fileName = "tmp"+System.nanoTime();
            }
            long workFileId = storageService.saveFile(0, fileName, file);
            if (workFileId > 0) {
                qualificationEntity.setWorkFileId(workFileId);
                qualificationEntity.setStatus(VetStatus.NEED_UPLOAD);
            }
            else {
                throw new BadRequestException(ErrorCode.WORK_FILE_UPLOAD_FAILED);
            }
        }

        // set TimeCreated
        qualificationEntity.setTimeCreated(new Date());

        // save record
        qualificationEntity = workfileRepository.save(qualificationEntity);

        // convert to bean
        NurseQualificationBean retVal = workfileBeanConverter.convert(qualificationEntity);
        if (qualificationEntity.getWorkFileId() > 0) {
            String fileURL = storageService.getFilePath(qualificationEntity.getWorkFileId());
            retVal.setWorkFileURL(fileURL);
        }

        logger.info("add nurse qualification file is " + retVal);
        return retVal;
    }



    //=======================================================
    //     delete qualification file
    //=======================================================

    public NurseQualificationBean deleteNurseQualification(long qualificationId) {
        NurseQualificationEntity entity = workfileRepository.findOne(qualificationId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        NurseQualificationBean bean = workfileBeanConverter.convert(entity);
        if (entity.getWorkFileId() > 0) {
            storageService.deleteFile(entity.getWorkFileId());
        }

        workfileRepository.delete(entity);
        return bean;
    }

    public void deleteNurseQualification(String qualificationIds) {
        if(!VerifyUtil.isOccupationSkillIds(qualificationIds)) {
            logger.severe("parameters : " + qualificationIds + "   is error!");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        String[]   stringIds = qualificationIds.split(",");
        List<Long> qualifIds = new ArrayList<Long>();
        List<Long> imageIds  = new ArrayList<Long>();
        for (int i = 0; i < stringIds.length; i ++) {
            String stringId = stringIds[i];
            qualifIds.add(Long.parseLong(stringId));
        }

        List<NurseQualificationEntity> allQualif = workfileRepository.findByIdIn(qualifIds);
        for (NurseQualificationEntity entity : allQualif) {
            imageIds.add(entity.getWorkFileId());
        }
        workfileRepository.deleteByIdIn(qualifIds);

        for (Long imageId : imageIds) {
            storageService.deleteFile(imageId);
        }
        return;
    }

    //=======================================================
    //     get qualification file
    //=======================================================

    public List<NurseQualificationBean> getAllNurseQualifications(long nurseId) {
        List<NurseQualificationEntity> qualifications = workfileRepository.findNurseQualificationByUserId(nurseId, sort);
        return parseEntities(nurseId, qualifications);
    }

    private List<NurseQualificationBean> parseEntities(long userId, List<NurseQualificationEntity> entities) {

        // speak ids/file ids cache
        List<WorkFileTypeBean> fileTypes  = workfileTypeService.getAllWorkFileType();
        List<Long>             ids        = new ArrayList<Long>();
        List<Long>             fileIds    = new ArrayList<Long>();
        NurseQualificationBean bean       = null;

        // convert to bean
        Map<Long, NurseQualificationBean> idToBeanMap = new Hashtable<Long, NurseQualificationBean>();
        for (NurseQualificationEntity entity : entities) {
            bean = workfileBeanConverter.convert(entity);

            // cache the ids and file ids
            idToBeanMap.put(bean.getId(), bean);
            ids.add(bean.getId());
            fileIds.add(bean.getWorkFileId());

            for(WorkFileTypeBean fileType : fileTypes) {
                if (bean.getWorkFileType() == fileType.getId()) {
                    bean.setWorkFileTypeBean(fileType);
                    break;
                }
            }
        }

        // get image url of speak
        Map<Long, String> idToPath = storageService.getFilePath(fileIds);
        for (NurseQualificationEntity entity : entities) {
            long speakId = entity.getId();
            long imageId = entity.getWorkFileId();
            String imageUrl = idToPath.get(imageId);
            if (VerifyUtil.isStringEmpty(imageUrl)) {
                continue;
            }
            bean = idToBeanMap.get(speakId);
            bean.setWorkFileURL(imageUrl);
        }

        // construct return values
        List<NurseQualificationBean> workfileBeans  = new ArrayList<NurseQualificationBean>();
        for (NurseQualificationEntity entity : entities) {
            long speakId = entity.getId();
            bean = idToBeanMap.get(speakId);
            workfileBeans.add(bean);
        }
        return workfileBeans;
    }

    public WorkFileTypeBean getWorkFileTypeBean(String workFileType) {
        WorkFileType filetype = WorkFileType.parseString(workFileType);
        if (null==filetype) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        WorkFileTypeBean workfileType = workfileTypeService.getWorkFileTypeByType(filetype);
        if (null==workfileType) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        return workfileType;
    }

    //=======================================================
    //   update qualification file
    //=======================================================

    public NurseQualificationBean updateWorkFile(long id, String name, String workFileType, String fileName, InputStream file, VetStatus status, String statusDescr, Date expiryTime) {
        WorkFileTypeBean workfileType = workfileTypeService.getWorkFileTypeByType(WorkFileType.OTHER);
        NurseQualificationBean bean = updateNurseQualification(id, name, workfileType, fileName, file, status, statusDescr, expiryTime);
        return bean;
    }

    public NurseQualificationBean updateNurseQualification(long id, String name, WorkFileTypeBean fileType, String fileName, InputStream file, VetStatus status, String statusDescr, Date expiryTime) {
        logger.info("update nurse qualification file type is : " + fileType);
        if (null==fileType) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        NurseQualificationEntity entity = workfileRepository.findOne(id);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!VerifyUtil.isStringEmpty(name)) {
            entity.setName(name);
        }
        if (null!=fileType && fileType.getId()!=(entity.getWorkFileType())) {
            entity.setWorkFileType(fileType.getId());
        }
        if (null!=status && !status.equals(entity.getStatus())) {
            entity.setStatus(status);
        }
        String fileUrl = null;
        if (null!=file && !VerifyUtil.isStringEmpty(fileName)) {
            long fileId = storageService.saveFile(entity.getWorkFileId(), fileName, file);
            fileUrl = storageService.getFilePath(fileId);
            entity.setWorkFileId(fileId);
        }
        if (!VerifyUtil.isStringEmpty(statusDescr)) {
            entity.setStatusDesc(statusDescr);
        }
        if (null!=expiryTime) {
            entity.setTimeExpiry(expiryTime);
        }

        workfileRepository.save(entity);
        NurseQualificationBean bean = workfileBeanConverter.convert(entity);
        if (!VerifyUtil.isStringEmpty(fileUrl)) {
            bean.setWorkFileURL(fileUrl);
        }
        return bean;
    }
}

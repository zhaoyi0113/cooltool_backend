package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseQualificationBean;
import com.cooltoo.backend.beans.WorkFileTypeBean;
import com.cooltoo.backend.converter.NurseQualificationBeanConverter;
import com.cooltoo.backend.entities.NurseQualificationEntity;
import com.cooltoo.backend.repository.NurseQualificationRepository;
import com.cooltoo.constants.VetStatus;
import com.cooltoo.constants.WorkFileType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.backend.repository.NurseRepository;
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

//    @Autowired
//    private NurseBeanConverter nurseBeanConverter;

    public boolean isNurseQualificationOk(long nurseId) {
        // not use realname and identification number to qualification temporarily
//        NurseEntity nurse = nurseRepository.findOne(nurseId);
//        if (null==nurse) {
//            return false;
//        }
//        if (VerifyUtil.isStringEmpty(nurse.getRealName())
//         || VerifyUtil.isStringEmpty(nurse.getIdentification())) {
//            return false;
//        }
        List<NurseQualificationBean> qualifications = getAllNurseQualifications(nurseId);
        if (qualifications.isEmpty()) {
            return false;
        }

        WorkFileTypeBean workType          = null;
        WorkFileTypeBean idType            = null;
        int              workFileCount     = 0;
        int              idFileCount       = 0;
        for (NurseQualificationBean qualification : qualifications) {
            WorkFileTypeBean workfileTypeBean = qualification.getWorkFileTypeBean();
            if (null==workfileTypeBean) {
                continue;
            }
            if (WorkFileType.IDENTIFICATION.equals(workfileTypeBean.getType())) {
                idType = workfileTypeBean;
                if (VetStatus.COMPLETED.equals(qualification.getStatus())) {
                    idFileCount++;
                }
            }
            if (WorkFileType.WORK_FILE.equals(workfileTypeBean.getType())) {
                workType = workfileTypeBean;
                if (VetStatus.COMPLETED.equals(qualification.getStatus())) {
                    workFileCount++;
                }
            }
        }

        if (null!=workType && null!=idType) {
            if (idFileCount<=idType.getMaxFileCount() && idFileCount>=idType.getMinFileCount()) {
                if (workFileCount<=workType.getMaxFileCount() && workFileCount>=workType.getMinFileCount()) {
                    return true;
                }
            }
        }
        return false;
    }

    //=======================================================
    //         add qualification file
    //=======================================================

    public NurseQualificationBean addNurseWorkFile(long nurseId, String name, String workFileName, InputStream workFile) {
        WorkFileTypeBean workfileType = workfileTypeService.getWorkFileTypeByType(WorkFileType.WORK_FILE);
        if (null==workfileType) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        logger.info("add nurse qualification file type is : " + workfileType);

        List<NurseQualificationEntity> qualifications = workfileRepository.findByUserIdAndWorkFileType(nurseId, workfileType.getId(), sort);
        if (qualifications.size()>=workfileType.getMaxFileCount()) {
            logger.info("add nurse qualification file type is reach the max limit number!");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        NurseQualificationBean retVal = addWorkFile(nurseId, name, workfileType, workFileName, workFile);
        return retVal;
    }

    public NurseQualificationBean addNurseIdentificationFile(long nurseId, String name, String idFileName, InputStream idFile) {
        WorkFileTypeBean workfileType = workfileTypeService.getWorkFileTypeByType(WorkFileType.IDENTIFICATION);
        if (null==workfileType) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        logger.info("add nurse qualification file type is : " + workfileType);

        List<NurseQualificationEntity> qualifications = workfileRepository.findByUserIdAndWorkFileType(nurseId, workfileType.getId(), sort);
        if (qualifications.size()>=workfileType.getMaxFileCount()) {
            logger.info("add nurse qualification file type is reach the max limit number!");
            throw new BadRequestException(ErrorCode.NURSE_QUALIFICATION_IDENTIFICATION_EXIST);
        }

        NurseQualificationBean retVal = addWorkFile(nurseId, name, workfileType, idFileName, idFile);
        return retVal;
    }

    private NurseQualificationBean addWorkFile(long nurseId, String name, WorkFileTypeBean workfileType, String fileName, InputStream file) {
        NurseQualificationEntity qualificationEntity = new NurseQualificationEntity();
        logger.info("nurse id is : " + nurseId);
        logger.info("nurse qualification name is : " + name);
        logger.info("nurse work file type is : " + workfileType);
        logger.info("nurse work file name is : " + fileName);
        logger.info("nurse work file is null : " + (null==file));

        // is Qualification exist
        List<NurseQualificationEntity> qualifications = workfileRepository.findNurseQualificationByUserIdAndName(nurseId, name, sort);
        if (!qualifications.isEmpty()) {
            throw new BadRequestException(ErrorCode.NURSE_QUALIFICATION_NAME_EXIST);
        }

        // set NurseId
        qualificationEntity.setUserId(nurseId);

        // set QualificationName
        if (VerifyUtil.isStringEmpty(name)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        qualificationEntity.setName(name);

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
                qualificationEntity.setStatus(VetStatus.WAITING);
            }
            else {
                qualificationEntity.setStatus(VetStatus.NEED_UPLOAD);
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

    //=======================================================
    //   update qualification file
    //=======================================================

    public NurseQualificationBean updateNurseWorkFile(long id, String name, String workFileName, InputStream workFile, VetStatus status) {
        WorkFileTypeBean workfileType = workfileTypeService.getWorkFileTypeByType(WorkFileType.WORK_FILE);
        if (null==workfileType) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        logger.info("update nurse qualification file type is : " + workfileType);
        NurseQualificationBean bean = updateNurseQualification(id, name, workfileType, workFileName, workFile, status);
        return bean;
    }

    public NurseQualificationBean updateNurseIdentificationFile(long id, String name, String idFileName, InputStream idFile, VetStatus status) {
        WorkFileTypeBean workfileType = workfileTypeService.getWorkFileTypeByType(WorkFileType.IDENTIFICATION);
        if (null==workfileType) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        logger.info("update nurse qualification file type is : " + workfileType);
        NurseQualificationBean bean = updateNurseQualification(id, name, workfileType, idFileName, idFile, status);
        return bean;
    }

    public WorkFileTypeBean getWorkFileTypeBean(String workFileType) {
        WorkFileType filetype = WorkFileType.parseString(workFileType);
        WorkFileTypeBean workfileType = workfileTypeService.getWorkFileTypeByType(filetype);
        if (null==workfileType) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        return workfileType;
    }

    public NurseQualificationBean updateNurseQualification(long id, String name, WorkFileTypeBean fileType, String fileName, InputStream file, VetStatus status) {
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

        workfileRepository.save(entity);
        NurseQualificationBean bean = workfileBeanConverter.convert(entity);
        if (!VerifyUtil.isStringEmpty(fileUrl)) {
            bean.setWorkFileURL(fileUrl);
        }
        return bean;
    }
}

package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseQualificationBean;
import com.cooltoo.backend.converter.NurseBeanConverter;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by zhaolisong on 16/3/23.
 */
@Service("NurseQualificationService")
public class NurseQualificationService {

    private static final Logger logger = Logger.getLogger(NurseQualificationService.class.getName());

    @Autowired
    private NurseQualificationRepository qualificationRepository;

    @Autowired
    private NurseRepository nurseRepository;

    @Autowired
    private StorageService storageService;

    @Autowired
    private NurseQualificationBeanConverter qualificationBeanConverter;

    @Autowired
    private NurseBeanConverter nurseBeanConverter;

    private static final Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC,  "name"),
                                              new Sort.Order(Sort.Direction.DESC, "timeCreated"));

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
        List<NurseQualificationEntity> qualifications = qualificationRepository.findNurseQualificationByUserId(nurseId, sort);
        if (qualifications.isEmpty()) {
            return false;
        }
        boolean hasIdentification = false;
        boolean hasWorkFile = false;
        for (NurseQualificationEntity qualification : qualifications) {
            if (WorkFileType.IDENTIFICATION.equals(qualification.getWorkFileType())
             && VetStatus.COMPLETED.equals(qualification.getStatus())) {
                hasIdentification = true;
            }
            if (WorkFileType.WORK_FILE.equals(qualification.getWorkFileType())
             && VetStatus.COMPLETED.equals(qualification.getStatus())) {
                hasWorkFile = true;
            }
        }

        return hasIdentification && hasWorkFile;
    }

    public NurseQualificationBean addNurseWorkFile(long nurseId, String name, String workFileName, InputStream workFile) {
        NurseQualificationBean retVal = addNurseQualification(nurseId, name, WorkFileType.WORK_FILE, workFileName, workFile);
        return retVal;
    }

    public NurseQualificationBean addNurseIdentificationFile(long nurseId, String name, String idFileName, InputStream idFile) {
        NurseQualificationBean retVal = addNurseQualification(nurseId, name, WorkFileType.IDENTIFICATION, idFileName, idFile);
        return retVal;
    }

    private NurseQualificationBean addNurseQualification(long nurseId, String name, WorkFileType fileType, String fileName, InputStream file) {
        NurseQualificationEntity qualificationEntity = new NurseQualificationEntity();
        logger.info("nurse id is : " + nurseId);
        logger.info("nurse qualification name is : " + name);
        logger.info("nurse work file type is : " + fileType);
        logger.info("nurse work file name is : " + fileName);
        logger.info("nurse work file is null : " + (null==file));

        // is Qualification exist
        List<NurseQualificationEntity> qualifications = qualificationRepository.findNurseQualificationByUserIdAndName(nurseId, name, sort);
        if (!qualifications.isEmpty()) {
            throw new BadRequestException(ErrorCode.NURSE_QUALIFICATION_NAME_EXIST);
        }
        // is Identification exist
        qualifications = qualificationRepository.findNurseQualificationByUserId(nurseId, sort);
        if (!qualifications.isEmpty() && WorkFileType.IDENTIFICATION.equals(fileType)) {
            for (NurseQualificationEntity qualification : qualifications) {
                if (WorkFileType.IDENTIFICATION.equals(qualification.getWorkFileType())) {
                    throw new BadRequestException(ErrorCode.NURSE_QUALIFICATION_IDENTIFICATION_EXIST);
                }
            }
        }

        // set NurseId
        if (!nurseRepository.exists(nurseId)) {
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
        }
        qualificationEntity.setUserId(nurseId);

        // set QualificationName
        if (VerifyUtil.isStringEmpty(name)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        qualificationEntity.setName(name);

        // set QualificationType
        qualificationEntity.setWorkFileType(fileType);

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
        qualificationEntity = qualificationRepository.save(qualificationEntity);

        // convert to bean
        NurseQualificationBean retVal = qualificationBeanConverter.convert(qualificationEntity);
        if (qualificationEntity.getWorkFileId() > 0) {
            String fileURL = storageService.getFilePath(qualificationEntity.getWorkFileId());
            retVal.setWorkFileURL(fileURL);
        }
        return retVal;
    }

    public NurseQualificationBean deleteNurseQualification(long qualificationId) {
        NurseQualificationEntity entity = qualificationRepository.findOne(qualificationId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        NurseQualificationBean bean = qualificationBeanConverter.convert(entity);
        if (entity.getWorkFileId() > 0) {
            String url = storageService.getFilePath(entity.getWorkFileId());
            bean.setWorkFileURL(url);
            storageService.deleteFile(entity.getWorkFileId());
        }
        qualificationRepository.delete(entity);
        return bean;
    }

    public List<NurseQualificationBean> getAllNurseQualifications(long nurseId) {
        List<NurseQualificationEntity> qualifications = qualificationRepository.findNurseQualificationByUserId(nurseId, sort);
        List<NurseQualificationBean> retVals = new ArrayList<NurseQualificationBean>();
        String fileURL = null;
        for (NurseQualificationEntity qualification : qualifications) {
            fileURL = null;
            NurseQualificationBean bean = qualificationBeanConverter.convert(qualification);
            if (bean.getWorkFileId() > 0 ) {
                fileURL = storageService.getFilePath(bean.getWorkFileId());
                bean.setWorkFileURL(fileURL);
            }
            retVals.add(bean);
        }
        return retVals;
    }

    public NurseQualificationBean updateNurseWorkFile(long id, String name, String workFileName, InputStream workFile, VetStatus status) {
        NurseQualificationBean bean = updateNurseQualification(id, name, WorkFileType.WORK_FILE, workFileName, workFile, status);
        return bean;
    }

    public NurseQualificationBean updateNurseIdentificationFile(long id, String name, String idFileName, InputStream idFile, VetStatus status) {
        NurseQualificationBean bean = updateNurseQualification(id, name, WorkFileType.IDENTIFICATION, idFileName, idFile, status);
        return bean;
    }

    public NurseQualificationBean updateNurseQualification(long id, String name, WorkFileType fileType, String fileName, InputStream file, VetStatus status) {
        NurseQualificationEntity entity = qualificationRepository.findOne(id);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!VerifyUtil.isStringEmpty(name)) {
            entity.setName(name);
        }
        if (null!=fileType && !fileType.equals(entity.getWorkFileType())) {
            entity.setWorkFileType(fileType);
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

        qualificationRepository.save(entity);
        NurseQualificationBean bean = qualificationBeanConverter.convert(entity);
        if (!VerifyUtil.isStringEmpty(fileUrl)) {
            bean.setWorkFileURL(fileUrl);
        }
        return bean;
    }
}

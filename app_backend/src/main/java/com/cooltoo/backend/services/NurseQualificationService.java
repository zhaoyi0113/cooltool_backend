package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseQualificationBean;
import com.cooltoo.backend.beans.NurseQualificationFileBean;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by zhaolisong on 16/3/23.
 */
@Service("NurseQualificationService")
public class NurseQualificationService {

    private static final Logger logger = LoggerFactory.getLogger(NurseQualificationService.class.getName());

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.ASC,  "name"));

    @Autowired
    private NurseQualificationRepository qualificationRepository;
    @Autowired
    private NurseQualificationBeanConverter beanConverter;
    @Autowired
    private WorkFileTypeService workfileTypeService;
    @Autowired
    private NurseRepository nurseRepository;
    @Autowired
    private NurseQualificationFileService qualificationFileService;
    @Autowired
    @Qualifier("StorageService")
    private StorageService storageService;

    //=======================================================
    //         add qualification file
    //=======================================================

    @Transactional
    public String addWorkFile(long nurseId, String name, String workfileType, String fileName, InputStream file) {
        if (!nurseRepository.exists(nurseId)) {
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
        }

        WorkFileType                   type           = WorkFileType.parseString(workfileType);
        WorkFileTypeBean               workFileTypeB  = workfileTypeService.getWorkFileTypeByType(type);
        // check work file type
        if (null==workFileTypeB) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        List<NurseQualificationEntity> qualifications = qualificationRepository.findNurseQualificationByUserId(nurseId, sort);
        NurseQualificationEntity       qualification  = null;
        // check nurse qualification record
        if (null==qualifications || qualifications.isEmpty()) {
            qualification = new NurseQualificationEntity();
            if (!VerifyUtil.isStringEmpty(name)) {
                qualification.setName(name);
            }
            qualification.setUserId(nurseId);
        }
        else {
            qualification = qualifications.get(0);
        }
        qualification.setStatus(VetStatus.WAITING);
        qualification = qualificationRepository.save(qualification);

        String qualificationPath = qualificationFileService.addQualificationFile(qualification.getId(), workFileTypeB, fileName, file);
        return qualificationPath;
    }

    //=======================================================
    //     delete qualification file
    //=======================================================

    @Transactional
    public NurseQualificationBean deleteNurseQualification(long qualificationId) {
        NurseQualificationEntity entity = qualificationRepository.findOne(qualificationId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        NurseQualificationBean bean = beanConverter.convert(entity);

        // delete images and file_storage record
        List<NurseQualificationFileBean> filesB = null;
        filesB = qualificationFileService.getAllFileByQualificationId(bean.getId());
        if (null!=filesB && !filesB.isEmpty()) {
            List<Long> imageIds = new ArrayList<Long>();
            for (NurseQualificationFileBean fileB : filesB) {
                imageIds.add(fileB.getWorkfileId());
            }
            storageService.deleteFiles(imageIds);
        }

        // delete qualification file record
        qualificationFileService.deleteFileByQualificationId(bean.getId());
        // delete qualification record
        qualificationRepository.delete(entity);
        return bean;
    }

    @Transactional
    public List<NurseQualificationBean> deletNurseQualificationByUserId(long userId) {
        List<NurseQualificationBean> qualificationsB = getAllNurseQualifications(userId);
        if (qualificationsB.isEmpty()) {
            return qualificationsB;
        }

        for (NurseQualificationBean qualification : qualificationsB) {
            deleteNurseQualification(qualification.getId());
        }
        return qualificationsB;
    }

    //=======================================================
    //     get qualification file
    //=======================================================

    public List<NurseQualificationBean> getAllNurseQualifications(long nurseId) {
        List<NurseQualificationEntity> qualifications = qualificationRepository.findNurseQualificationByUserId(nurseId, sort);
        if (null==qualifications || qualifications.isEmpty()) {
            logger.info("The qualification record is empty!");
            return new ArrayList<>();
        }
        if (qualifications.size()>1) {
            logger.info("The qualification record is more than one! " + qualifications);
        }
        return parseEntities(nurseId, qualifications);
    }

    private List<NurseQualificationBean> parseEntities(long userId, List<NurseQualificationEntity> entities) {

        // qualification ids/file ids cache
        NurseQualificationBean           bean      = null;
        List<NurseQualificationBean>     beans     = new ArrayList<NurseQualificationBean>();
        List<NurseQualificationFileBean> files     = null;


        for (NurseQualificationEntity entity : entities) {
            bean = beanConverter.convert(entity);
            files = qualificationFileService.getAllFileByQualificationId(bean.getId());
            bean.setWorkfiles(files);
            beans.add(bean);
        }

        return beans;
    }

    public WorkFileTypeBean getWorkFileTypeBean(String strWorkfileType) {
        WorkFileType enumType = WorkFileType.parseString(strWorkfileType);
        if (null==enumType) {
            logger.info("The workfile type do not have the relative WorkFileType enumeration =={}", strWorkfileType);
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        WorkFileTypeBean workfileType = workfileTypeService.getWorkFileTypeByType(enumType);
        if (null==workfileType) {
            logger.info("The WorkFileType enumeration do not have the relative WorkFileTypeBean =={}", enumType);
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        logger.info("workfiletype {} ===== WorkFileTypeBean {}", strWorkfileType, workfileType);
        return workfileType;
    }

    //=======================================================
    //   update qualification file
    //=======================================================

    public NurseQualificationBean updateQualification(long qualificatinId, String name, VetStatus status, String statusDescr) {
        NurseQualificationEntity entity = qualificationRepository.findOne(qualificatinId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        boolean changed = false;
        if(!VerifyUtil.isStringEmpty(name)) {
            entity.setName(name);
            changed = true;
        }
        if (null!=status && !status.equals(entity.getStatus())) {
            entity.setStatus(status);
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(statusDescr)) {
            entity.setStatusDesc(statusDescr);
            changed = true;
        }

        if (changed) {
            qualificationRepository.save(entity);
        }

        NurseQualificationBean bean = beanConverter.convert(entity);
        return bean;
    }

    public void updateQualificationFile(int qualificationFileId, String workfileType, String fileName, InputStream file, Date expiryTime) {
        WorkFileTypeBean workfileTypeB = getWorkFileTypeBean(workfileType);
        qualificationFileService.updateQualificationFile(qualificationFileId, workfileTypeB, fileName, file, expiryTime);
        return;
    }
}

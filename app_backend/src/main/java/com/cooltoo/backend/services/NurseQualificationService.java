package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseQualificationBean;
import com.cooltoo.backend.beans.NurseQualificationFileBean;
import com.cooltoo.backend.beans.WorkFileTypeBean;
import com.cooltoo.backend.converter.NurseQualificationBeanConverter;
import com.cooltoo.backend.entities.NurseEntity;
import com.cooltoo.backend.entities.NurseQualificationEntity;
import com.cooltoo.backend.repository.NurseQualificationRepository;
import com.cooltoo.backend.repository.NurseRepository;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.VetStatus;
import com.cooltoo.constants.WorkFileType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.StorageService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private NurseQualificationRepository repository;
    @Autowired
    private NurseQualificationBeanConverter beanConverter;
    @Autowired
    private WorkFileTypeService workfileTypeService;
    @Autowired
    private NurseRepository nurseRepository;
    @Autowired
    private NurseHospitalRelationService hospitalRelationService;
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

        List<NurseQualificationEntity> qualifications = repository.findNurseQualificationByUserId(nurseId, sort);
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
        qualification.setTimeCreated(new Date());
        qualification.setTimeProcessed(null);
        qualification = repository.save(qualification);

        String qualificationPath = qualificationFileService.addQualificationFile(qualification.getId(), workFileTypeB, fileName, file);
        return qualificationPath;
    }

    //=======================================================
    //     delete qualification file
    //=======================================================

    @Transactional
    public NurseQualificationBean deleteNurseQualification(long qualificationId) {
        NurseQualificationEntity entity = repository.findOne(qualificationId);
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
        repository.delete(entity);
        return bean;
    }

    @Transactional
    public List<NurseQualificationBean> deleteNurseQualificationByUserId(long userId) {
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

    public long getAllQualificationCount(String status) {
        VetStatus   vetStatus   = VetStatus.parseString(status);
        long resultSet = 0;
        if ("ALL".equalsIgnoreCase(status)) {
            resultSet = repository.count();
        }
        else if (null!=vetStatus) {
            resultSet = repository.countByStatus(vetStatus);
        }

        return resultSet;
    }

    public List<NurseQualificationBean> getAllNurseQualifications(long nurseId) {
        List<NurseQualificationEntity> resultSet = repository.findNurseQualificationByUserId(nurseId, sort);
        if (null==resultSet || resultSet.isEmpty()) {
            logger.info("The qualification record is empty!");
            return new ArrayList<>();
        }
        if (resultSet.size()>1) {
            logger.info("The qualification record is more than one! " + resultSet);
        }

        List<NurseQualificationBean> qualifications = new ArrayList<>();
        for (NurseQualificationEntity result : resultSet) {
            NurseQualificationBean bean = beanConverter.convert(result);
            qualifications.add(bean);
        }

        fillOtherProperties(qualifications);
        return qualifications;
    }

    public List<NurseQualificationBean> getAllQualifications(String status, int pageIndex, int number) {
        logger.info("get qualification by status {} at page {} numberOfPage {}", status, pageIndex, number);
        VetStatus   vetStatus   = VetStatus.parseString(status);
        PageRequest pageRequest = new PageRequest(pageIndex, number, Sort.Direction.DESC, "timeCreated");
        Page<NurseQualificationEntity> resultSet = null;
        if ("ALL".equalsIgnoreCase(status)) {
            resultSet = repository.findAll(pageRequest);
        }
        if (null!=vetStatus) {
            resultSet = repository.findByStatus(vetStatus, pageRequest);
        }
        if (null==resultSet) {
            return new ArrayList<>();
        }

        List<NurseQualificationBean> qualifications = new ArrayList<>();
        for (NurseQualificationEntity result : resultSet) {
            NurseQualificationBean bean = beanConverter.convert(result);
            qualifications.add(bean);
        }

        fillOtherProperties(qualifications);
        return qualifications;
    }

    private void fillOtherProperties(List<NurseQualificationBean> resultSet) {
        if (null==resultSet || resultSet.isEmpty()) {
            return;
        }

        // qualification ids/file ids cache
        List<Long>  userIds  =  new ArrayList<>();
        List<Long>  qulfIds  =  new ArrayList<>();
        for (NurseQualificationBean qualification : resultSet) {
            userIds.add(qualification.getUserId());
            qulfIds.add(qualification.getId());
        }

        List<NurseEntity>                           nurses          = nurseRepository.findByIdIn(userIds);
        List<NurseHospitalRelationBean>             hospitals       = hospitalRelationService.getRelationByNurseIds(userIds);
        Map<Long, NurseEntity>                      userId2Bean     = new HashMap<>();
        Map<Long, NurseHospitalRelationBean>        userId2Hospital = new HashMap<>();
        Map<Long, List<NurseQualificationFileBean>> qulfId2QulfFile = qualificationFileService.getAllFileByQualificationId(qulfIds);

        for (NurseEntity tmp : nurses) {
            userId2Bean.put(tmp.getId(), tmp);
        }
        for (NurseHospitalRelationBean tmp : hospitals) {
            userId2Hospital.put(tmp.getNurseId(), tmp);
        }

        for (NurseQualificationBean tmp : resultSet) {
            NurseEntity                     user  = userId2Bean.get(tmp.getUserId());
            NurseHospitalRelationBean       hosp  = userId2Hospital.get(tmp.getUserId());
            List<NurseQualificationFileBean>files = qulfId2QulfFile.get(tmp.getId());

            tmp.setWorkfiles(files);
            tmp.setUserName(user.getName());
            tmp.setRealName(user.getRealName());
            if (null!=hosp) {
                if (null!=hosp.getHospital()) {
                    tmp.setHospitalId(hosp.getHospitalId());
                    tmp.setHospitalName(hosp.getHospital().getName());
                }
                if (null!=hosp.getDepartment()) {
                    tmp.setDepartmentId(hosp.getDepartmentId());
                    tmp.setDepartmentName(hosp.getDepartment().getName());
                }
            }
        }

        return;
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
        NurseQualificationEntity entity = repository.findOne(qualificatinId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        boolean changed       = false;
        boolean statusChanged = false;
        if(!VerifyUtil.isStringEmpty(name)) {
            entity.setName(name);
            changed = true;
        }
        if (null!=status && !status.equals(entity.getStatus())) {
            entity.setStatus(status);
            statusChanged = true;
        }
        if (!VerifyUtil.isStringEmpty(statusDescr)) {
            entity.setStatusDesc(statusDescr);
            statusChanged = true;
        }

        if (changed || statusChanged) {
            if (statusChanged) {
                entity.setTimeProcessed(new Date());
            }
            repository.save(entity);
        }

        NurseQualificationBean bean = beanConverter.convert(entity);
        return bean;
    }

    public NurseQualificationFileBean updateQualificationFile(int qualificationFileId, String workfileType, String fileName, InputStream file, Date expiryTime) {
        WorkFileTypeBean workfileTypeB = getWorkFileTypeBean(workfileType);
        NurseQualificationFileBean bean = qualificationFileService.updateQualificationFile(qualificationFileId, workfileTypeB, fileName, file, expiryTime);
        return bean;
    }
}

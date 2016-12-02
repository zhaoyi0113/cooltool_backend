package com.cooltoo.services;

import com.cooltoo.beans.NurseQualificationBean;
import com.cooltoo.beans.NurseQualificationFileBean;
import com.cooltoo.beans.WorkFileTypeBean;
import com.cooltoo.converter.NurseQualificationBeanConverter;
import com.cooltoo.entities.NurseEntity;
import com.cooltoo.entities.NurseQualificationEntity;
import com.cooltoo.repository.NurseQualificationRepository;
import com.cooltoo.repository.NurseRepository;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.VetStatus;
import com.cooltoo.constants.WorkFileType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
            new Sort.Order(Sort.Direction.ASC,  "id"));

    @Autowired private NurseQualificationRepository repository;
    @Autowired private NurseQualificationBeanConverter beanConverter;
    @Autowired private WorkFileTypeService workfileTypeService;
    @Autowired private NurseRepository nurseRepository;
    @Autowired private CommonNurseHospitalRelationService hospitalRelationService;
    @Autowired private NurseQualificationFileService qualificationFileService;

    //=======================================================
    //         add qualification file
    //=======================================================

    @Transactional
    public String addWorkFile(long nurseId, String name, String workfileType, String fileName, InputStream file) {
        if (!nurseRepository.exists(nurseId)) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
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


        // check the qualification is single
        qualifications = repository.findNurseQualificationByUserId(nurseId, sort);
        if (qualifications.size()>1) {
            qualification = qualifications.get(0);
            qualifications.remove(0);
            if (!VerifyUtil.isListEmpty(qualifications)) {
                // change to the new qualification id
                for (NurseQualificationEntity tmp : qualifications) {
                    qualificationFileService.changeQualificationId(tmp.getId(), qualification.getId());
                }
                repository.delete(qualifications);
            }
        }


        String qualificationPath = qualificationFileService.addQualificationFile(qualification.getId(), workFileTypeB, fileName, file);
        return qualificationPath;
    }

    //=============================================
    //       add qualification BY administrator
    //=============================================
    @Transactional
    public NurseQualificationBean createQualificationByAdmin(long nurseId) {

        List<NurseQualificationEntity> qualifications = repository.findNurseQualificationByUserId(nurseId, sort);
        NurseQualificationEntity qualification ;
        // check nurse qualification record
        if (null==qualifications || qualifications.isEmpty()) {
            qualification = new NurseQualificationEntity();
            qualification.setUserId(nurseId);
            qualification.setName("");
        }
        else {
            qualification = qualifications.get(0);
        }
        qualification.setStatus(VetStatus.COMPLETED);
        qualification.setStatusDesc("管理员创建");
        qualification.setTimeCreated(new Date());
        qualification.setTimeProcessed(new Date());
        qualification = repository.save(qualification);
        return beanConverter.convert(qualification);
    }

    //=======================================================
    //     delete qualification file
    //=======================================================
    @Transactional
    public NurseQualificationFileBean deleteFileByFileId(long qualificationFileId) {
        // delete qualification file record, and delete images and file_storage record
        return qualificationFileService.deleteFileByFileId(qualificationFileId);
    }

    @Transactional
    public NurseQualificationBean deleteNurseQualification(long qualificationId) {
        NurseQualificationEntity entity = repository.findOne(qualificationId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        NurseQualificationBean bean = beanConverter.convert(entity);

        // delete qualification file record, and delete images and file_storage record
        qualificationFileService.deleteFileByQualificationId(bean.getId());

        // delete qualification record
        repository.delete(entity);
        return bean;
    }

    @Transactional
    public List<NurseQualificationBean> deleteNurseQualificationByUserId(long nurseId) {
        logger.info("nurse={} delete his qualifications", nurseId);
        List<NurseQualificationBean> qualifications = getAllNurseQualifications(nurseId, "");
        if (qualifications.isEmpty()) {
            return qualifications;
        }

        for (NurseQualificationBean qualification : qualifications) {
            deleteNurseQualification(qualification.getId());
        }
        logger.info("nurse qualifications={}.", qualifications);
        return qualifications;
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

    public List<NurseQualificationBean> getAllNurseQualifications(long nurseId, String nginxPrefix) {
        logger.info("nurse={} get his qualifications with nginxPrefix={}.", nurseId, nginxPrefix);
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

        fillOtherProperties(qualifications, nginxPrefix);
        logger.info("nurse qualifications={}.", qualifications);
        return qualifications;
    }

    public Map<Long, List<NurseQualificationFileBean>> getAllNurseQualificationFiles(List<Long> nurseIds, String nginx) {
        Map<Long, List<NurseQualificationFileBean>> nurseToQualificationFile = new HashMap<>();
        if (VerifyUtil.isListEmpty(nurseIds)) {
            return nurseToQualificationFile;
        }
        List<NurseQualificationEntity> qualifications = repository.findByUserIdIn(nurseIds);
        if (VerifyUtil.isListEmpty(qualifications)) {
            return nurseToQualificationFile;
        }
        Map<Long, Long> qualificationIdToUserId = new HashMap<>();
        List<Long> qualificationIds = new ArrayList<>();
        for (NurseQualificationEntity tmp : qualifications) {
            qualificationIdToUserId.put(tmp.getId(), tmp.getUserId());
            if (!qualificationIds.contains(tmp.getId())) {
                qualificationIds.add(tmp.getId());
            }
        }
        Map<Long, List<NurseQualificationFileBean>> qualificationIdToFile = qualificationFileService.getAllFileByQualificationId(qualificationIds, nginx);

        for (Long tmpId : qualificationIds) {
            Long userId = qualificationIdToUserId.get(tmpId);
            List<NurseQualificationFileBean> files = qualificationIdToFile.get(tmpId);

            if (VerifyUtil.isListEmpty(files)) {
                continue;
            }

            List<NurseQualificationFileBean> filesExisted = nurseToQualificationFile.get(userId);
            if (null==filesExisted) {
                filesExisted = files;
                nurseToQualificationFile.put(userId, filesExisted);
            }
            else {
                for (NurseQualificationFileBean tmp : files) {
                    if (!filesExisted.contains(tmp)) {
                        filesExisted.add(tmp);
                    }
                }
            }
        }

        return qualificationIdToFile;
    }

    public List<NurseQualificationBean> getAllQualifications(String status, int pageIndex, int number, String nginxPrefix) {
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

        fillOtherProperties(qualifications, nginxPrefix);
        return qualifications;
    }

    private void fillOtherProperties(List<NurseQualificationBean> resultSet, String nginxPrefix) {
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
        Map<Long, NurseEntity>                      userId2Bean     = new HashMap<>();
        Map<Long, NurseHospitalRelationBean>        userId2Hospital = hospitalRelationService.getRelationMapByNurseIds(userIds, nginxPrefix);
        Map<Long, List<NurseQualificationFileBean>> qulfId2QulfFile = qualificationFileService.getAllFileByQualificationId(qulfIds, nginxPrefix);

        for (NurseEntity tmp : nurses) {
            userId2Bean.put(tmp.getId(), tmp);
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
                    tmp.setDepartmentId(hosp.getDepartment().getId());
                    tmp.setDepartmentName(hosp.getDepartment().getName());
                }
                if (null!=hosp.getParentDepart()) {
                    tmp.setParentDepartmentId(hosp.getParentDepart().getId());
                    tmp.setParentDepartmentName(hosp.getParentDepart().getName());
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
    @Transactional
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

    @Transactional
    public NurseQualificationFileBean updateQualificationFile(int qualificationFileId, String workfileType, String fileName, InputStream file, Date expiryTime, String nginxPrefix) {
        WorkFileTypeBean workfileTypeB = getWorkFileTypeBean(workfileType);
        NurseQualificationFileBean bean = qualificationFileService.updateQualificationFile(qualificationFileId, workfileTypeB, fileName, file, expiryTime, nginxPrefix);
        return bean;
    }
}

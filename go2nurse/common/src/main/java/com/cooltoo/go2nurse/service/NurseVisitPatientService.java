package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.converter.NurseVisitPatientBeanConverter;
import com.cooltoo.go2nurse.entities.NurseVisitPatientEntity;
import com.cooltoo.go2nurse.repository.NurseVisitPatientRepository;
import com.cooltoo.go2nurse.service.file.UserGo2NurseFileStorageService;
import com.cooltoo.go2nurse.service.notification.Notifier;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.cooltoo.util.JSONUtil;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

/**
 * Created by hp on 2016/11/06.
 */
@Service("NurseVisitPatientService")
public class NurseVisitPatientService {

    private static final Logger logger = LoggerFactory.getLogger(NurseVisitPatientService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private NurseVisitPatientRepository repository;
    @Autowired private NurseVisitPatientBeanConverter beanConverter;
    @Autowired private ImageInVisitPatientService imageService;

    @Autowired private UserService userService;
    @Autowired private PatientService patientService;
    @Autowired private NurseServiceForGo2Nurse nurseService;
    @Autowired private ServiceOrderService orderService;

    @Autowired private UserGo2NurseFileStorageService userFileStorage;
    @Autowired private Go2NurseUtility utility;

    @Autowired private Notifier notifier;

    private JSONUtil jsonUtil = JSONUtil.newInstance();

    //===============================================================
    //             get ----  admin using
    //===============================================================

    public long countVisitRecordByCondition(Long userId, Long patientId, Long nurseId, String contentLike) {
        contentLike = VerifyUtil.isStringEmpty(contentLike) ? null : VerifyUtil.reconstructSQLContentLike(contentLike);
        long count = repository.countByConditions(userId, patientId, nurseId, contentLike);
        logger.info("count visit record user={} patientId={} nurseId={} contentLike={}, count is {}",
                userId, patientId, nurseId, contentLike, count);
        return count;
    }

    public List<NurseVisitPatientBean> getVisitRecordByCondition(Long userId, Long patientId, Long nurseId, String contentLike, int pageIndex, int sizePerPage) {
        logger.info("get visit record user={} patientId={} nurseId={} contentLike={} at page={} sizePerPage={}",
                userId, patientId, nurseId, contentLike, pageIndex, sizePerPage);
        List<NurseVisitPatientBean> beans;
        contentLike = VerifyUtil.isStringEmpty(contentLike) ? null : VerifyUtil.reconstructSQLContentLike(contentLike);
        PageRequest request = new PageRequest(pageIndex, sizePerPage, sort);
        Page<NurseVisitPatientEntity> resultSet = repository.findByConditions(userId, patientId, nurseId, contentLike, request);
        beans = entitiesToBeans(resultSet);
        fillOtherProperties(beans);

        logger.warn("visit record count={}", beans.size());
        return beans;
    }


    //===============================================================
    //             get ----  patient using
    //===============================================================

    public List<NurseVisitPatientBean> getVisitRecord(Long userId, Long patientId, Long nurseId, String contentLike, int pageIndex, int sizePerPage) {
        logger.info("nurse={} get visit record (contentLike={}) of user={} patient={} at page={} sizePerPage={}",
                nurseId, contentLike, userId, patientId, pageIndex, sizePerPage);
        List<NurseVisitPatientBean> beans;
        if (null==userId && VerifyUtil.isStringEmpty(contentLike)) {
            beans = new ArrayList<>();
        }
        else {
            contentLike = VerifyUtil.isStringEmpty(contentLike) ? null : VerifyUtil.reconstructSQLContentLike(contentLike);
            PageRequest request = new PageRequest(pageIndex, sizePerPage, sort);
            Page<NurseVisitPatientEntity> resultSet = repository.findByUserNurseStatusNotAndContentLike(userId, patientId, nurseId, CommonStatus.DELETED, contentLike, request);
            beans = entitiesToBeans(resultSet);
            fillOtherProperties(beans);
        }
        logger.warn("visit record count={}", beans.size());
        return beans;
    }

    public NurseVisitPatientBean getVisitRecord(long visitRecordId) {
        logger.info("get nurseVisitPatientRecordId={}", visitRecordId);
        NurseVisitPatientEntity resultSet = repository.findOne(visitRecordId);
        if (null==resultSet) {
            logger.info("there is no record");
            return null;
        }
        List<NurseVisitPatientEntity> entities = new ArrayList<>();
        entities.add(resultSet);

        List<NurseVisitPatientBean> visitRecord = entitiesToBeans(entities);
        fillOtherProperties(visitRecord);
        return visitRecord.get(0);
    }

    public boolean existsVisitRecord(long visitRecordId) {
        return repository.exists(visitRecordId);
    }

    private List<NurseVisitPatientBean> entitiesToBeans(Iterable<NurseVisitPatientEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }

        List<NurseVisitPatientBean> beans = new ArrayList<>();
        for(NurseVisitPatientEntity tmp : entities) {
            NurseVisitPatientBean bean = beanConverter.convert(tmp);
            beans.add(bean);
        }

        return beans;
    }

    private void fillOtherProperties(List<NurseVisitPatientBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }

        List<Long> userIds = new ArrayList<>();
        List<Long> patientIds = new ArrayList<>();
        List<Long> nurseIds = new ArrayList<>();
        List<Long> visitRecordIds = new ArrayList<>();
        List<Long> patientSignId = new ArrayList<>();
        for (NurseVisitPatientBean tmp : beans) {
            if (!userIds.contains(tmp.getUserId())) {
                userIds.add(tmp.getUserId());
            }
            if (!patientIds.contains(tmp.getPatientId())) {
                patientIds.add(tmp.getPatientId());
            }
            if (!visitRecordIds.contains(tmp.getId())) {
                visitRecordIds.add(tmp.getId());
            }
            if (!nurseIds.contains(tmp.getNurseId())) {
                nurseIds.add(tmp.getNurseId());
            }
            if (!patientSignId.contains(tmp.getPatientSign())) {
                patientSignId.add(tmp.getPatientSign());
            }
        }

        Map<Long, UserBean> userIdToBean = userService.getUserIdToBean(userIds);
        Map<Long, PatientBean> patientIdToBean = patientService.getPatientIdToBean(patientIds);
        Map<Long, NurseBean> nurseIdToBean = nurseService.getNurseIdToBean(nurseIds);
        Map<Long, List<String>> visitRecordImage = imageService.getNurseVisitPatientImagesUrl(visitRecordIds);
        Map<Long, String> patientSignImage = imageService.getNurseVisitPatientImageIdToUrl(patientSignId);

        // fill properties
        for (NurseVisitPatientBean tmp : beans) {
            UserBean user = userIdToBean.get(tmp.getUserId());
            tmp.setUser(user);
            PatientBean patient = patientIdToBean.get(tmp.getPatientId());
            tmp.setPatient(patient);
            NurseBean nurse = nurseIdToBean.get(tmp.getNurseId());
            tmp.setNurse(nurse);
            List<String> recordImages = visitRecordImage.get(tmp.getId());
            tmp.setRecordImages(null==recordImages ? new ArrayList<>() : recordImages);
            String patientSign = patientSignImage.get(tmp.getPatientSign());
            tmp.setPatientSignUrl(null==patientSign ? "" : patientSign);
            tmp.setServiceItems(jsonUtil.parseJsonList(tmp.getServiceItem(), NurseVisitPatientServiceItemBean.class));
        }
    }

    private void fillOtherPropertiesForSingle(NurseVisitPatientBean visitPatientRecord) {
        if (null==visitPatientRecord) {
            return;
        }

        Long userId = visitPatientRecord.getUserId();
        Long patientId = visitPatientRecord.getPatientId();
        long nurseId = visitPatientRecord.getNurseId();
        long recordId = visitPatientRecord.getId();
        long patientSign = visitPatientRecord.getPatientSign();

        UserBean user = userService.getUser(userId);
        PatientBean patient = patientService.getOneById(patientId);
        NurseBean nurse = nurseId<=0 ? null : nurseService.getNurseById(nurseId);
        List<String> recordImages = imageService.getNurseVisitPatientImagesUrl(recordId);
        String patientSignUrl = imageService.getNurseVisitPatientImageUrl(patientSign);

        // fill properties
        visitPatientRecord.setUser(user);
        visitPatientRecord.setPatient(patient);
        visitPatientRecord.setNurse(nurse);
        visitPatientRecord.setRecordImages(recordImages);
        visitPatientRecord.setPatientSignUrl(patientSignUrl);
        visitPatientRecord.setServiceItems(jsonUtil.parseJsonList(visitPatientRecord.getServiceItem(), NurseVisitPatientServiceItemBean.class));
    }


    //===============================================================
    //             update
    //===============================================================
    @Transactional
    public List<Long> setDeleteStatusVisitRecordByIds(Long nurseId, List<Long> visitRecordId) {
        logger.info("delete visit record by visitRecordId={}.", visitRecordId);
        List<Long> retValue = new ArrayList<>();
        if (VerifyUtil.isListEmpty(visitRecordId)) {
            return retValue;
        }

        List<NurseVisitPatientEntity> visitRecord = repository.findAll(visitRecordId);
        if (VerifyUtil.isListEmpty(visitRecord)) {
            logger.info("delete nothing");
            return retValue;
        }

        if (null!=nurseId) {
            for (NurseVisitPatientEntity tmp : visitRecord) {
                if (tmp.getNurseId() == nurseId) {
                    continue;
                }
                logger.warn("can not delete visit record that not making by yourself={}", tmp);
                return retValue;
            }
        }

        for (NurseVisitPatientEntity tmp : visitRecord) {
            tmp.setStatus(CommonStatus.DELETED);
            retValue.add(tmp.getId());
        }
        repository.save(visitRecord);


        return retValue;
    }

    @Transactional
    public NurseVisitPatientBean updateVisitRecord(Long nurseId, Long visitRecordId, String visitRecord, String serviceItem) {
        logger.info("update visitRecordId={} with visitRecord={} serviceItem={} nurseId={}",
                visitRecordId, visitRecord, serviceItem, nurseId);
        NurseVisitPatientEntity entity = repository.findOne(visitRecordId);
        if (null==entity) {
            logger.error("visit record is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        if (null!=nurseId && nurseId>0 && entity.getNurseId()!=nurseId) {
            logger.info("visit record not belong to nurse");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        boolean changed = false;
        if (!VerifyUtil.isStringEmpty(visitRecord) && !visitRecord.trim().equals(entity.getVisitRecord())) {
            entity.setVisitRecord(visitRecord.trim());
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(serviceItem) && !serviceItem.trim().equals(entity.getServiceItem())) {
            entity.setServiceItem(serviceItem.trim());
            changed = true;
        }
        if (changed) {
            entity = repository.save(entity);
        }

        NurseVisitPatientBean bean = beanConverter.convert(entity);
        fillOtherPropertiesForSingle(bean);
        return bean;
    }


    //===============================================================
    //             add
    //===============================================================
    @Transactional
    public long addVisitRecord(long nurseId, long userId, long patientId, long orderId, String visitRecord, String serviceItem) {
        logger.info("add visit record with nurseId={} userId={} patientId={} visitRecord={} serviceItem={}",
                nurseId, userId, patientId, visitRecord, (null!=serviceItem));
        if (nurseId>0 && !nurseService.existsNurse(nurseId)) {
            logger.info("nurse not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!userService.existUser(userId)) {
            logger.info("userId not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!patientService.existPatient(patientId)) {
            patientId = 0;
        }
        if (orderId>0 && !orderService.existOrder(orderId)) {
            orderId = 0;
        }
        if (VerifyUtil.isStringEmpty(serviceItem)) {
            logger.info("service itme of visit is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        visitRecord = VerifyUtil.isStringEmpty(visitRecord) ? "" : visitRecord.trim();

        NurseVisitPatientEntity entity = new NurseVisitPatientEntity();
        entity.setNurseId(nurseId<0 ? 0 : nurseId);
        entity.setUserId(userId);
        entity.setPatientId(patientId);
        entity.setOrderId(orderId);
        entity.setVisitRecord(visitRecord.trim());
        entity.setServiceItem(serviceItem);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());
        entity = repository.save(entity);

        return entity.getId();
    }

    @Transactional
    public Map<String, String> addVisitRecorImage(long visitRecordId, String imageName, InputStream image) {
        logger.info("add image to visitRecordId={} name={} image={}", visitRecordId, imageName, (null!=image));

        NurseVisitPatientEntity visitRecord = repository.findOne(visitRecordId);
        if (null==visitRecord) {
            logger.error("visit record is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        long count = imageService.countImage(visitRecordId);
        if (count>=9) {
            logger.warn("the visit record do not need image more than nine");
            return new HashMap<>();
        }

        Map<String, String> idAndUrl = imageService.addImage(visitRecordId, imageName, image);
        return idAndUrl;
    }

    @Transactional
    public Map<String, String> addPatientSignImage(long visitRecordId, String imageName, InputStream image) {
        logger.info("add patient sign image to visitRecordId={} name={} image={}", visitRecordId, imageName, (null!=image));

        NurseVisitPatientEntity visitRecord = repository.findOne(visitRecordId);
        if (null==visitRecord) {
            logger.error("visit record is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (null!=image) {
            if (VerifyUtil.isStringEmpty(imageName)) {
                imageName = "nurse_visit_patient_sign_"+System.currentTimeMillis();
            }
            long imageId = userFileStorage.addFile(visitRecord.getPatientSign(), imageName, image);
            String imageUrl = userFileStorage.getFileURL(imageId, utility.getHttpPrefix());
            if (imageId > 0) {
                visitRecord.setPatientSign(imageId);
                repository.save(visitRecord);
            }
            Map<String, String> idAndUrl = new HashMap<>();
            idAndUrl.put("imageUrl", imageUrl);
            idAndUrl.put("id", visitRecord.getId()+"");
            return idAndUrl;
        }

        throw new BadRequestException(ErrorCode.DATA_ERROR);
    }
}

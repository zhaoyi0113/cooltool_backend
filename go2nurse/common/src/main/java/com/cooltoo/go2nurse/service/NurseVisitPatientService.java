package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.converter.NurseVisitPatientBeanConverter;
import com.cooltoo.go2nurse.entities.NurseVisitPatientEntity;
import com.cooltoo.go2nurse.repository.NurseVisitPatientRepository;
import com.cooltoo.go2nurse.service.file.UserGo2NurseFileStorageService;
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


    private JSONUtil jsonUtil = JSONUtil.newInstance();

    //===============================================================
    //             get ----  admin using
    //===============================================================

    public long countVisitRecordByCondition(Long userId, Long patientId, Long nurseId, String contentLike) {
        contentLike = VerifyUtil.isStringEmpty(contentLike) ? null : VerifyUtil.reconstructSQLContentLike(contentLike);
        long count = repository.countByConditions(userId, patientId, nurseId, contentLike, null, null, null, null);
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
        Page<NurseVisitPatientEntity> resultSet = repository.findByConditions(userId, patientId, nurseId, contentLike, null, null, null, null, request);
        beans = entitiesToBeans(resultSet);
        fillOtherProperties(beans);

        logger.warn("visit record count={}", beans.size());
        return beans;
    }

    public long countVisitRecordByCondition(Long userId, Long patientId, Long nurseId, String contentLike, ServiceVendorType vendorType, Long vendorId, Long vendorDepartId, CommonStatus statusNot) {
        contentLike = VerifyUtil.isStringEmpty(contentLike) ? null : VerifyUtil.reconstructSQLContentLike(contentLike);
        long count = repository.countByConditions(userId, patientId, nurseId, contentLike, vendorType, vendorId, vendorDepartId, statusNot);
        logger.info("count visit record by user={} patientId={} nurseId={} contentLike={} vendorType={} vendorId={} departId={} statusNot={}, count={}",
                userId, patientId, nurseId, contentLike, vendorType, vendorId, vendorDepartId, statusNot, count);
        return count;
    }

    public List<NurseVisitPatientBean> getVisitRecordByCondition(Long userId, Long patientId, Long nurseId, String contentLike, ServiceVendorType vendorType, Long vendorId, Long vendorDepartId, CommonStatus statusNot, int pageIndex, int sizePerPage) {
        logger.info("get visit record user={} patientId={} nurseId={} contentLike={} vendorType={} vendorId={} departId={} statusNot={} at page={} sizePerPage={}",
                userId, patientId, nurseId, contentLike, vendorType, vendorId, vendorDepartId, statusNot, pageIndex, sizePerPage);
        List<NurseVisitPatientBean> beans;
        contentLike = VerifyUtil.isStringEmpty(contentLike) ? null : VerifyUtil.reconstructSQLContentLike(contentLike);
        PageRequest request = new PageRequest(pageIndex, sizePerPage, sort);
        Page<NurseVisitPatientEntity> resultSet = repository.findByConditions(userId, patientId, nurseId, contentLike, vendorType, vendorId, vendorDepartId, statusNot, request);
        beans = entitiesToBeans(resultSet);
        fillOtherProperties(beans);

        logger.warn("visit record count={}", beans.size());
        return beans;
    }


    //===============================================================
    //             get ----  patient using
    //===============================================================
    public boolean isRecordForOrder(long orderId) {
        Boolean recorded = false;
        Map<Long, Boolean> orderRecorded = isRecordForOrder(Arrays.asList(new Long[]{orderId}));
        recorded = orderRecorded.get(orderId);
        recorded = null==recorded ? Boolean.FALSE : recorded;
        logger.debug("the visit patient of order recorded? {}", recorded);
        return recorded;
    }

    public Map<Long, Boolean> isRecordForOrder(List<Long> orderIds) {
        Map<Long, Boolean> orderRecorded = new HashMap<>();
        if (VerifyUtil.isListEmpty(orderIds)) {
            return orderRecorded;
        }
        List<NurseVisitPatientEntity> entities = repository.findByOrderIdIn(orderIds, sort);
        if (!VerifyUtil.isListEmpty(entities)) {
            for (NurseVisitPatientEntity tmp : entities) {
                if (null==tmp) { continue; }
                Boolean recorded = orderRecorded.get(tmp.getOrderId());
                // record already
                if (null!=recorded && Boolean.TRUE.equals(recorded)) { continue; }
                // judge record
                recorded = tmp.getPatientSign()>0;
                orderRecorded.put(tmp.getOrderId(), recorded);
            }
        }
        logger.debug("the visit patient of order recorded. size={}", orderRecorded.size());
        return orderRecorded;
    }

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
        List<Long> patientNurseSignId = new ArrayList<>();
        List<Long> orderIds = new ArrayList<>();
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
            if (!patientNurseSignId.contains(tmp.getPatientSign())) {
                patientNurseSignId.add(tmp.getPatientSign());
            }
            if (!patientNurseSignId.contains(tmp.getNurseSign())) {
                patientNurseSignId.add(tmp.getNurseSign());
            }
            if (!orderIds.contains(tmp.getOrder())) {
                orderIds.add(tmp.getOrderId());
            }
        }

        Map<Long, UserBean> userIdToBean = userService.getUserIdToBean(userIds);
        Map<Long, PatientBean> patientIdToBean = patientService.getPatientIdToBean(patientIds);
        Map<Long, NurseBean> nurseIdToBean = nurseService.getNurseIdToBean(nurseIds);
        Map<Long, Map<Long, String>> visitRecordIdToMapOfImageIdToUrl = new HashMap<>();
        Map<Long, List<String>> visitRecordImage = imageService.getNurseVisitPatientImagesUrl(visitRecordIds, visitRecordIdToMapOfImageIdToUrl);
        Map<Long, String> patientSignImage = imageService.getNurseVisitPatientImageIdToUrl(patientNurseSignId);
        List<ServiceOrderBean> orders = orderService.getOrderByIds(orderIds);
        Map<Long, ServiceOrderBean> orderIdToBean = new HashMap<>();
        if (!VerifyUtil.isListEmpty(orders)) {
            for (ServiceOrderBean tmp : orders) {
                if (null==tmp) { continue; }
                orderIdToBean.put(tmp.getId(), tmp);
            }
        }

        // fill properties
        for (NurseVisitPatientBean tmp : beans) {
            UserBean user = userIdToBean.get(tmp.getUserId());
            tmp.setUser(user);
            PatientBean patient = patientIdToBean.get(tmp.getPatientId());
            tmp.setPatient(patient);
            NurseBean nurse = nurseIdToBean.get(tmp.getNurseId());
            tmp.setNurse(nurse);
            Map<Long, String> recordImageIdToUrl = visitRecordIdToMapOfImageIdToUrl.get(tmp.getId());
            tmp.setRecordImageIdToUrl(recordImageIdToUrl);
            List<String> recordImages = visitRecordImage.get(tmp.getId());
            tmp.setRecordImages(null==recordImages ? new ArrayList<>() : recordImages);
            String patientSign = patientSignImage.get(tmp.getPatientSign());
            tmp.setPatientSignUrl(null==patientSign ? "" : patientSign);
            String nurseSign = patientSignImage.get(tmp.getNurseSign());
            tmp.setNurseSignUrl(null==nurseSign ? "" : nurseSign);
            tmp.setServiceItems(jsonUtil.parseJsonList(tmp.getServiceItem(), NurseVisitPatientServiceItemBean.class));
            ServiceOrderBean order = orderIdToBean.get(tmp.getOrderId());
            tmp.setOrder(order);
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
        long nurseSign = visitPatientRecord.getNurseSign();

        UserBean user = userService.getUser(userId);
        PatientBean patient = patientService.getOneById(patientId);
        NurseBean nurse = nurseId<=0 ? null : nurseService.getNurseById(nurseId);
        List<String> recordImages = imageService.getNurseVisitPatientImagesUrl(recordId);
        String patientSignUrl = imageService.getNurseVisitPatientImageUrl(patientSign);
        String nurseSignUrl   = imageService.getNurseVisitPatientImageUrl(nurseSign);

        // fill properties
        visitPatientRecord.setUser(user);
        visitPatientRecord.setPatient(patient);
        visitPatientRecord.setNurse(nurse);
        visitPatientRecord.setRecordImages(recordImages);
        visitPatientRecord.setPatientSignUrl(patientSignUrl);
        visitPatientRecord.setNurseSignUrl(nurseSignUrl);
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
            logger.error("delete nothing");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        if (null!=nurseId) {
            for (NurseVisitPatientEntity tmp : visitRecord) {
                if (tmp.getNurseId() == nurseId) {
                    continue;
                }
                logger.error("can not delete visit record that not making by yourself={}", tmp);
                throw new BadRequestException(ErrorCode.DATA_ERROR);
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
    public NurseVisitPatientBean updateVisitRecord(Long nurseId, Long visitRecordId,
                                                   String visitRecord, String serviceItem, Date visitTime,
                                                   String address, String patientRecordNo, String note) {
        logger.info("update visitRecordId={} with nurseId={} visitRecord={} serviceItem={} visitTime={} address={} patientRecordNo={} note={}",
                visitRecordId, nurseId, visitRecord, serviceItem, visitTime, address, patientRecordNo, note);
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
        if (null!=visitTime && visitTime.getTime()!=entity.getTime().getTime()) {
            entity.setTime(visitTime);
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(address) && !address.trim().equals(entity.getAddress())) {
            entity.setAddress(address.trim());
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(patientRecordNo) && !patientRecordNo.trim().equals(entity.getPatientRecordNo())) {
            entity.setPatientRecordNo(patientRecordNo.trim());
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(note) && !note.trim().equals(entity.getNote())) {
            entity.setNote(note.trim());
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
    public long addVisitRecord(long nurseId, long userId, long patientId, long orderId,
                               String visitRecord,
                               List<NurseVisitPatientServiceItemBean> serviceItems,
                               Date visitTime,
                               String address, String patientRecordNo, String note) {
        logger.info("add visit record with nurseId={} userId={} patientId={} visitRecord={} serviceItem={}, visitTime={} address={} patientRecordNo{}, note={}",
                nurseId, userId, patientId, visitRecord, serviceItems, visitTime, address, patientRecordNo, note);
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

        ServiceVendorType vendorType = null;
        long vendorId = 0;
        long vendorDepartId = 0;
        if (orderId>0) {
            if (!orderService.existOrder(orderId)) {
                orderId = 0;
            }
            else {
                List<ServiceOrderBean> orders = orderService.getOrderByOrderId(orderId);
                vendorType = orders.get(0).getVendorType();
                vendorId = orders.get(0).getVendorId();
                vendorDepartId = orders.get(0).getVendorId();
                if (VerifyUtil.isStringEmpty(address)) {
                    String orderAddress = orders.get(0).getAddress();
                    if (!VerifyUtil.isStringEmpty(orderAddress)) {
                        address = orderAddress;
                    }
                }
            }
        }
        if (null==vendorType) {
            NurseBean nurse = nurseService.getNurseById(nurseId);
            NurseHospitalRelationBean nurseHospitalDepartment =
                    null==nurse
                            ? null
                            : (NurseHospitalRelationBean) nurse.getProperty(NurseBean.HOSPITAL_DEPARTMENT);
            if (null!=nurse && null!=nurseHospitalDepartment) {
                vendorType = ServiceVendorType.HOSPITAL;
                vendorId = nurseHospitalDepartment.getHospitalId();
                vendorDepartId = nurseHospitalDepartment.getDepartmentId();
            }
            else {
                vendorType = ServiceVendorType.NONE;
                vendorId = 0;
                vendorDepartId = 0;
            }
        }


        String serviceItemsJson = "";
        if (!VerifyUtil.isListEmpty(serviceItems)) {
            serviceItemsJson = jsonUtil.toJsonString(serviceItems);
        }
        if (VerifyUtil.isStringEmpty(serviceItemsJson)) {
            logger.info("service item of visit is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        visitRecord = VerifyUtil.isStringEmpty(visitRecord) ? "" : visitRecord.trim();

        NurseVisitPatientEntity entity = new NurseVisitPatientEntity();
        entity.setNurseId(nurseId<0 ? 0 : nurseId);
        entity.setUserId(userId);
        entity.setPatientId(patientId);
        entity.setOrderId(orderId);
        entity.setVisitRecord(visitRecord.trim());
        entity.setServiceItem(serviceItemsJson);
        entity.setVendorType(vendorType);
        entity.setVendorId(vendorId);
        entity.setVendorDepartId(vendorDepartId);
        entity.setAddress(address);
        entity.setPatientRecordNo(patientRecordNo);
        entity.setNote(note);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(null==visitTime ? new Date() : visitTime);
        entity = repository.save(entity);

        return entity.getId();
    }

    @Transactional
    public Map<String, String> addVisitRecordImage(long nurseId, long visitRecordId, String imageName, InputStream image) {
        logger.info("add image to visitRecordId={} name={} image={} by nurseId={}", visitRecordId, imageName, (null!=image), nurseId);

        NurseVisitPatientEntity visitRecord = repository.findOne(visitRecordId);
        if (null==visitRecord) {
            logger.error("visit record is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (nurseId>0 && nurseId!=visitRecord.getNurseId()) {
            logger.error("visit record is not belong to nurse");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
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
    public Map<String, String> addSignImage(boolean isNurse, long visitRecordId, String imageName, InputStream image) {
        logger.info("add patient/nurse sign image to visitRecordId={} name={} image={}", visitRecordId, imageName, (null!=image));

        NurseVisitPatientEntity visitRecord = repository.findOne(visitRecordId);
        if (null==visitRecord) {
            logger.error("visit record is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (null!=image) {
            if (VerifyUtil.isStringEmpty(imageName)) {
                imageName = "nurse_visit_patient-nurse_sign_"+System.currentTimeMillis();
            }
            long imageId = 0;
            if (!isNurse) {
                imageId = userFileStorage.addFile(visitRecord.getPatientSign(), imageName, image);
            }
            else {
                imageId = userFileStorage.addFile(visitRecord.getNurseSign(), imageName, image);
            }
            String imageUrl = userFileStorage.getFileURL(imageId, utility.getHttpPrefix());
            if (imageId > 0) {
                if (!isNurse) {
                    visitRecord.setPatientSign(imageId);
                    repository.save(visitRecord);
                }
                else {
                    visitRecord.setNurseSign(imageId);
                    repository.save(visitRecord);
                }
            }
            Map<String, String> idAndUrl = new HashMap<>();
            idAndUrl.put("imageUrl", imageUrl);
            idAndUrl.put("id", visitRecord.getId()+"");
            return idAndUrl;
        }

        throw new BadRequestException(ErrorCode.DATA_ERROR);
    }


    //===============================================================
    //             delete
    //===============================================================
    @Transactional
    public List<Long> deleteVisitRecordImage(long nurseId, long visitRecordId) {
        logger.info("delete image to visitRecordId={} by nurseId={}", visitRecordId, nurseId);

        NurseVisitPatientEntity visitRecord = repository.findOne(visitRecordId);
        if (null==visitRecord) {
            logger.error("visit record is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (nurseId>0 && nurseId!=visitRecord.getNurseId()) {
            logger.error("visit record is not belong to nurse");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        List<Long> imageIds = imageService.deleteByNurseVisitPatientId(visitRecordId);
        return imageIds;
    }

    @Transactional
    public List<Long> deleteVisitRecordImage(long nurseId, long visitRecordId, long imageId) {
        logger.info("delete image to visitRecordId={} by nurseId={}", visitRecordId, nurseId);

        NurseVisitPatientEntity visitRecord = repository.findOne(visitRecordId);
        if (null==visitRecord) {
            logger.error("visit record is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (nurseId>0 && nurseId!=visitRecord.getNurseId()) {
            logger.error("visit record is not belong to nurse");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        List<Long> imageIds = imageService.deleteByNurseVisitPatientId(visitRecordId, imageId);
        return imageIds;
    }

    @Transactional
    public long deletePatientSignImage(long nurseId, long visitRecordId) {
        logger.info("delete patient sign image to visitRecordId={} by nurseId={}", visitRecordId, nurseId);

        NurseVisitPatientEntity visitRecord = repository.findOne(visitRecordId);
        if (null==visitRecord) {
            logger.error("visit record is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (nurseId>0 && nurseId!=visitRecord.getNurseId()) {
            logger.error("visit record is not belong to nurse");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        long patientSign = visitRecord.getPatientSign();

        userFileStorage.deleteFile(patientSign);
        visitRecord.setPatientSign(0);
        repository.save(visitRecord);

        return patientSign;
    }

    @Transactional
    public long deleteNurseSignImage(long nurseId, long visitRecordId) {
        logger.info("delete nurse sign image to visitRecordId={} by nurseId={}", visitRecordId, nurseId);

        NurseVisitPatientEntity visitRecord = repository.findOne(visitRecordId);
        if (null==visitRecord) {
            logger.error("visit record is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (nurseId>0 && nurseId!=visitRecord.getNurseId()) {
            logger.error("visit record is not belong to nurse");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        long nurseSign = visitRecord.getNurseSign();

        userFileStorage.deleteFile(nurseSign);
        visitRecord.setNurseSign(0);
        repository.save(visitRecord);

        return nurseSign;
    }

}

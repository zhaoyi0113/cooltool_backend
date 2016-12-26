package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.DenyPatientBean;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.constants.WhoDenyPatient;
import com.cooltoo.go2nurse.converter.DenyPatientBeanConverter;
import com.cooltoo.go2nurse.entities.DenyPatientEntity;
import com.cooltoo.go2nurse.repository.DenyPatientRepository;
import com.cooltoo.services.CommonDepartmentService;
import com.cooltoo.services.CommonNurseHospitalRelationService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by zhaolisong on 14/12/2016.
 */
@Service("DenyPatientService")
public class DenyPatientService {

    private static final Logger logger = LoggerFactory.getLogger(DenyPatientService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private DenyPatientRepository repository;
    @Autowired private DenyPatientBeanConverter beanConverter;

    @Autowired private NurseServiceForGo2Nurse nurseService;
    @Autowired private CommonDepartmentService departmentService;
    @Autowired private CommonNurseHospitalRelationService nurseHospitalRelation;
    @Autowired private ServiceVendorCategoryAndItemService vendorService;
    @Autowired private UserService userService;

    //====================================================================
    //                        Getting Method
    //====================================================================
    public boolean isUserDeniedByNurseOrVendor(long userId, Long patientId, long nurseId) {
        patientId = null==patientId ? 0L : patientId;

        boolean nurseDenied = isNurseDenyPatient(userId, patientId, nurseId);
        logger.debug("nurse={} deny user={} patient={}? {}", nurseId, userId, patientId, nurseDenied);
        if (nurseDenied) {
            throw new BadRequestException(ErrorCode.USER_FORBIDDEN_BY_NURSE);
        }
        NurseHospitalRelationBean hospitalRelation = nurseHospitalRelation.getRelationByNurseId(nurseId, null);
        if (null!=hospitalRelation) {
            Long hospitalId = new Long(hospitalRelation.getHospitalId());
            Long departmentId = new Long(hospitalRelation.getDepartmentId());
            boolean isVendorDenied = isVendorDenyPatient(userId, patientId, ServiceVendorType.HOSPITAL, hospitalId, departmentId);
            logger.debug("hospital={} department={} deny user={} patient={}? {}", hospitalId, departmentId, userId, patientId, isVendorDenied);
            if (isVendorDenied) {
                throw new BadRequestException(ErrorCode.USER_FORBIDDEN_BY_VENDOR);
            }
        }
        return true;
    }

    public boolean isVendorDenyPatient(long userId, long patientId, ServiceVendorType vendorType, long vendorId, long departId) {
        logger.debug("is vendorType={} vendorId={} departId={} deny user={} patient={}",
                vendorType, vendorId, departId, userId, patientId);
        List<DenyPatientEntity> entities = getVendorDenyPatient(userId, patientId, vendorType, vendorId, departId);
        if (VerifyUtil.isListEmpty(entities)) {
            return false;
        }
        for (DenyPatientEntity tmp : entities) {
            if (CommonStatus.ENABLED.equals(tmp.getStatus())) {
                return true;
            }
        }
        return false;
    }

    public boolean isNurseDenyPatient(long userId, long patientId, long nurseId) {
        logger.debug("is nurseId={} deny user={} patient={}", nurseId, userId, patientId);
        List<DenyPatientEntity> entities = getNurseDenyPatient(userId, patientId, nurseId);
        if (VerifyUtil.isListEmpty(entities)) {
            return false;
        }
        for (DenyPatientEntity tmp : entities) {
            if (CommonStatus.ENABLED.equals(tmp.getStatus())) {
                return true;
            }
        }
        return false;
    }

    private List<DenyPatientEntity> getVendorDenyPatient(long userId, long patientId, ServiceVendorType vendorType, long vendorId, long departId) {
        List<DenyPatientEntity> entities = repository.findByConditions(WhoDenyPatient.VENDOR, null, vendorType, vendorId, departId, userId, patientId, sort);
        return entities;
    }

    private List<DenyPatientEntity> getNurseDenyPatient(long userId, long patientId, long nurseId) {
        List<DenyPatientEntity> entities = repository.findByConditions(WhoDenyPatient.NURSE, nurseId, null, null, null, userId, patientId, sort);
        return entities;
    }

    public List<Long> getDeniedUserId(WhoDenyPatient whoDenyPatient, Long nurseId, ServiceVendorType vendorType, Long vendorId, Long departId) {
        logger.debug("deny whoDeny={} nurseId={} vendorType={} vendorId={} departId={}",
                whoDenyPatient, nurseId, vendorType, vendorId, departId);
        List<DenyPatientEntity> entities = repository.findByConditions(whoDenyPatient, nurseId, vendorType, vendorId, departId, null, null, sort);
        List<DenyPatientBean> beans = entitiesToBeans(entities);
        List<Long> deniedUserId = new ArrayList<>();
        for (DenyPatientBean tmp : beans) {
            if (!deniedUserId.contains(tmp.getUserId())) {
                deniedUserId.add(tmp.getUserId());
            }
        }
        return deniedUserId;
    }

    public List<DenyPatientBean> getWhoDenyPatient(long userId, Long patientId, WhoDenyPatient whoDenyPatient) {
        List<DenyPatientEntity> entities = getPatientDenied(userId, patientId, whoDenyPatient);
        List<DenyPatientBean> beans = entitiesToBeans(entities);
        return beans;
    }

    private List<DenyPatientEntity> getPatientDenied(long userId, Long patientId, WhoDenyPatient whoDenyPatient) {
        List<DenyPatientEntity> entities = repository.findByConditions(whoDenyPatient, null, null, null, null, userId, patientId, sort);
        return entities;
    }

    private List<DenyPatientBean> entitiesToBeans(Iterable<DenyPatientEntity> entities) {
        List<DenyPatientBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }

        for (DenyPatientEntity tmp : entities) {
            DenyPatientBean bean = beanConverter.convert(tmp);
            beans.add(bean);
        }

        return beans;
    }

    //====================================================================
    //                        Delete Method
    //====================================================================
    @Transactional
    public void enablePatient(WhoDenyPatient whoDenyPatient,
                              Long nurseId,
                              ServiceVendorType vendorType, Long vendorId, Long departId,
                              Long userId, Long patientId
    ) {
        logger.debug("deny user={} patient={} by whoDeny={} nurseId={} vendorType={} vendorId={} departId={}",
                userId, patientId, whoDenyPatient, nurseId, vendorType, vendorId, departId);
        if (null==userId || null==whoDenyPatient) {
            logger.error("userId or whoDenyPatient is empty!");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        List<DenyPatientEntity> userDenied;
        if (WhoDenyPatient.NURSE.equals(whoDenyPatient)) {
            userDenied  = getNurseDenyPatient(
                    userId,
                    null==patientId ? 0L : patientId,
                    null==nurseId   ? 0L : nurseId);
            if (VerifyUtil.isListEmpty(userDenied)) {
                logger.error("nurse do not forbid user");
                throw new BadRequestException(ErrorCode.NURSE_DO_NOT_FORBID_USER);
            }
        }
        else {
            userDenied = getVendorDenyPatient(
                    userId,
                    null == patientId ? 0L : patientId,
                    null == vendorType ? ServiceVendorType.NONE : vendorType,
                    null == vendorId ? 0L : vendorId,
                    null == departId ? 0L : departId);
            if (VerifyUtil.isListEmpty(userDenied)) {
                logger.error("vendor do not forbid user");
                throw new BadRequestException(ErrorCode.VENDOR_DO_NOT_FORBID_USER);
            }
        }
        if (!VerifyUtil.isListEmpty(userDenied)) {
            repository.delete(userDenied);
        }
        return;
    }


    //====================================================================
    //                        Adding Method
    //====================================================================
    @Transactional
    public DenyPatientBean denyPatient(WhoDenyPatient whoDenyPatient,
                                       Long nurseId,
                                       ServiceVendorType vendorType, Long vendorId, Long departId,
                                       Long userId, Long patientId
    ) {
        logger.debug("deny user={} patient={} by whoDeny={} nurseId={} vendorType={} vendorId={} departId={}",
                userId, patientId, whoDenyPatient, nurseId, vendorType, vendorId, departId);
        if (null==whoDenyPatient) {
            logger.error("whoDeny is empty!");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        if (WhoDenyPatient.NURSE.equals(whoDenyPatient)) {
            if (!nurseService.existsNurse(nurseId)) {
                logger.error("nurse not exist!");
                throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
            }
        }
        else if (WhoDenyPatient.VENDOR.equals(whoDenyPatient)) {
            if (null==vendorType) {
                logger.error("vendor type is empty!");
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
            else if (ServiceVendorType.COMPANY.equals(vendorType)) {
                if (!vendorService.existVendor(vendorId)) {
                    logger.error("vendor not exist!");
                    throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
                }
            }
            else if (ServiceVendorType.HOSPITAL.equals(vendorType)) {
                if (!departmentService.existsDepartment(vendorId.intValue(), departId.intValue())) {
                    logger.error("hospital-department not exist!");
                    throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
                }
            }
        }

        if (!userService.existUser(userId)) {
            logger.error("user not existed!");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        DenyPatientEntity entity = null;
        List<DenyPatientEntity> allDenied = null;
        if (WhoDenyPatient.NURSE.equals(whoDenyPatient)) {
            allDenied = repository.findByConditions(whoDenyPatient, nurseId, null, null, null, userId, patientId, sort);
        }
        else {
            allDenied = repository.findByConditions(whoDenyPatient, null, vendorType, vendorId, departId, userId, patientId, sort);
        }
        if (VerifyUtil.isListEmpty(allDenied)) {
            entity = new DenyPatientEntity();
        } else {
            entity = allDenied.get(allDenied.size() - 1);
        }

        entity.setWhoDenyPatient(whoDenyPatient);
        entity.setUserId(userId);
        entity.setNurseId(null==nurseId ? 0L : nurseId);
        entity.setVendorType(null==vendorType ? ServiceVendorType.NONE : vendorType);
        entity.setVendorId(null==vendorId ? 0L : vendorId);
        entity.setDepartId(null==departId ? 0L : departId);
        entity.setPatientId(null==patientId ? 0L : patientId);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());
        entity = repository.save(entity);

        if (WhoDenyPatient.NURSE.equals(whoDenyPatient)) {
            allDenied = repository.findByConditions(whoDenyPatient, nurseId, null, null, null, userId, patientId, sort);
        }
        else {
            allDenied = repository.findByConditions(whoDenyPatient, null, vendorType, vendorId, departId, userId, patientId, sort);
        }
        if (!VerifyUtil.isListEmpty(allDenied)) {
            for (int i = 0; i < allDenied.size(); i++) {
                DenyPatientEntity tmp = allDenied.get(i);
                if (tmp.getId() == entity.getId()) {
                    allDenied.remove(i);
                    break;
                }
            }
            if (!VerifyUtil.isListEmpty(allDenied)) {
                repository.delete(allDenied);
            }
        }

        return beanConverter.convert(entity);
    }
}

package com.cooltoo.nurse360.service;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.entities.HospitalDepartmentEntity;
import com.cooltoo.nurse360.beans.Nurse360NotificationHospitalRelationBean;
import com.cooltoo.nurse360.converters.Nurse360NotificationHospitalRelationBeanConverter;
import com.cooltoo.nurse360.entities.Nurse360NotificationHospitalRelationEntity;
import com.cooltoo.nurse360.repository.Nurse360NotificationHospitalRelationRepository;
import com.cooltoo.nurse360.repository.Nurse360NotificationRepository;
import com.cooltoo.nurse360.util.Nurse360Utility;
import com.cooltoo.repository.HospitalDepartmentRepository;
import com.cooltoo.services.CommonDepartmentService;
import com.cooltoo.services.CommonHospitalService;
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
 * Created by hp on 2016/6/12.
 */
@Service("NotificationHospitalRelationServiceForNurse360")
public class NotificationHospitalRelationServiceForNurse360 {

    private static final Logger logger = LoggerFactory.getLogger(NotificationHospitalRelationServiceForNurse360.class);

    public static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private Nurse360NotificationRepository notificationRepository;
    @Autowired private Nurse360NotificationHospitalRelationRepository repository;
    @Autowired private Nurse360NotificationHospitalRelationBeanConverter beanConverter;
    @Autowired private CommonHospitalService hospitalService;
    @Autowired private CommonDepartmentService departmentService;
    @Autowired private HospitalDepartmentRepository departmentRepository;
    @Autowired private Nurse360Utility utility;

    //============================================================================
    //                 get
    //============================================================================
    public List<Integer> getHospitalIdByNotificationId(long notificationId, String strStatus) {
        logger.info("get hospitalId by notification id={} and status={}", notificationId, strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Integer> hospitalIds = repository.findHospitalIdByNotificationIdAndStatus(notificationId, status, sort);
        List<Integer> validIds = new ArrayList<>();
        for (Integer tmp : hospitalIds) {
            if (null!=tmp && !validIds.contains(tmp)) {
                validIds.add(tmp);
            }
        }
        logger.info("hospital is {}", validIds);
        return validIds;
    }

    public List<HospitalBean> getHospitalByNotificationId(long notificationId, String strStatus) {
        logger.info("get hospital by notification id={} and status={}", notificationId, strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Integer> hospitalIds = repository.findHospitalIdByNotificationIdAndStatus(notificationId, status, sort);
        List<HospitalBean> hospitals = hospitalService.getHospitalByIds(hospitalIds);
        logger.info("hospital is {}", hospitals);
        return hospitals;
    }

    public List<Integer> getDepartmentIdByNotificationId(long notificationId, String strStatus) {
        logger.info("get departmentId by notification id={} and status={}", notificationId, strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Integer> departmentIds = repository.findDepartmentIdByNotificationIdAndStatus(notificationId, status, sort);
        List<Integer> validIds = new ArrayList<>();
        for (Integer tmp : departmentIds) {
            if (null!=tmp && !validIds.contains(tmp)) {
                validIds.add(tmp);
            }
        }
        logger.info("department is {}", validIds);
        return validIds;
    }

    public List<HospitalDepartmentBean> getDepartmentByNotificationId(long notificationId, String strStatus) {
        logger.info("get department by notification id={} and status={}", notificationId, strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Integer> departmentIds = repository.findDepartmentIdByNotificationIdAndStatus(notificationId, status, sort);
        List<HospitalDepartmentBean> departments = departmentService.getByIds(departmentIds, utility.getHttpPrefixForNurseGo());
        logger.info("department is {}", departments);
        return departments;
    }

    public List<Long> getNotificationInHospitalAndDepartment(Integer hospitalId, Integer departmentId, String strStatus) {
        logger.info("get notificationIds in hospital={} department={} with status={}", hospitalId, departmentId, strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Long> notificationIds = new ArrayList<>();
        if (null==status && !"ALL".equalsIgnoreCase(strStatus)) {
        }
        else {
            if (null==departmentId) {
                notificationIds = repository.findByHospitalIdAndStatus(hospitalId, status);
            }
            else {
                notificationIds = repository.findByHospitalIdAndDepartmentIdAndStatus(hospitalId, departmentId, status);
            }
        }
        logger.info("count is {}", notificationIds.size());
        return notificationIds;
    }

    private List<Nurse360NotificationHospitalRelationBean> entitiesToBeans(Iterable<Nurse360NotificationHospitalRelationEntity> entities) {
        List<Nurse360NotificationHospitalRelationBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }

        for (Nurse360NotificationHospitalRelationEntity tmp : entities) {
            beans.add(beanConverter.convert(tmp));
        }
        return beans;
    }

    //============================================================================
    //                 set
    //============================================================================
    @Transactional
    public long deleteRelationByNotificationIds(List<Long> notificationIds) {
        logger.info("delete relation by notification={}", notificationIds);
        long count = 0;
        if (VerifyUtil.isListEmpty(notificationIds)) {
            logger.info("delete relation by size={}", count);
            return count;
        }

        List<Nurse360NotificationHospitalRelationEntity> relations = repository.findByNotificationIdIn(notificationIds);
        if (!VerifyUtil.isListEmpty(relations)) {
            count = relations.size();
        }
        repository.delete(relations);

        logger.info("delete relation by size={}", count);
        return count;
    }

    //============================================================================
    //                 set
    //============================================================================
    @Transactional
    public List<Nurse360NotificationHospitalRelationBean> setNotificationToHospital(long lNotificationId, int iHospitalId, List<Integer> departmentIds) {
        logger.info("set notification={} to hospital={} and departments={}", lNotificationId, iHospitalId, departmentIds);
        Integer hospitalId = Integer.valueOf(iHospitalId);
        Long notificationId = Long.valueOf(lNotificationId);
        if (!notificationRepository.exists(notificationId)) {
            logger.error("the notification not exist!");
            return new ArrayList<>();
        }
        if (VerifyUtil.isListEmpty(departmentIds)) {
            logger.error("departmentIds is empty");
            return new ArrayList<>();
        }

        boolean hasCooltoo = departmentIds.contains(Integer.valueOf(-1));
        departmentIds.remove(Integer.valueOf(-1));

        List<HospitalDepartmentEntity> departments = departmentRepository.findByIdIn(departmentIds, new Sort(new Sort.Order(Sort.Direction.ASC, "id")));
        if (VerifyUtil.isListEmpty(departments) && !hasCooltoo) {
            logger.error("department not exist");
            return new ArrayList<>();
        }

        if (hasCooltoo) {
            HospitalDepartmentEntity cooltoo = new HospitalDepartmentEntity();
            cooltoo.setId(0);
            cooltoo.setHospitalId(-1);
            departments.add(cooltoo);
        }

        List<Nurse360NotificationHospitalRelationEntity> relations = new ArrayList<>();
        List<Nurse360NotificationHospitalRelationEntity> existedRelations = repository.findByNotificationId(notificationId, sort);
        // add to department
        for (HospitalDepartmentEntity tmpDep : departments) {
            Nurse360NotificationHospitalRelationEntity tmp = null;
            if (VerifyUtil.isListEmpty(existedRelations)) {
                tmp = new Nurse360NotificationHospitalRelationEntity();
            }
            else {
                tmp = existedRelations.remove(0);
            }
            tmp.setNotificationId(notificationId);
            tmp.setHospitalId(tmpDep.getHospitalId());
            tmp.setDepartmentId(tmpDep.getId());
            tmp.setStatus(CommonStatus.ENABLED);
            tmp.setTime(new Date());
            relations.add(tmp);
        }

        relations = repository.save(relations);

        if (!VerifyUtil.isListEmpty(existedRelations)) {
            repository.delete(existedRelations);
        }

        existedRelations = repository.findByNotificationId(notificationId, new Sort(new Sort.Order(Sort.Direction.ASC, "id")));
        for (int i = 0; i < existedRelations.size(); i ++) {
            Nurse360NotificationHospitalRelationEntity tmp1 = existedRelations.get(i);
            boolean exist = false;
            for (Nurse360NotificationHospitalRelationEntity tmp2 : relations) {
                if (tmp1.getId() == tmp2.getId()) {
                    exist = true;
                    break;
                }
            }
            if (exist) {
                existedRelations.remove(tmp1);
                i--;
            }
        }
        if (!VerifyUtil.isListEmpty(existedRelations)) {
            repository.delete(existedRelations);
        }

        List<Nurse360NotificationHospitalRelationBean> beans = entitiesToBeans(relations);
        logger.info("set relation={}", beans);
        return beans;
    }
}

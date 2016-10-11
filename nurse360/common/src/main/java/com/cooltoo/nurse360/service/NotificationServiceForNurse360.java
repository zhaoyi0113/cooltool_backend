package com.cooltoo.nurse360.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.nurse360.beans.Nurse360NotificationBean;
import com.cooltoo.nurse360.converters.Nurse360NotificationBeanConverter;
import com.cooltoo.nurse360.entities.Nurse360NotificationEntity;
import com.cooltoo.nurse360.repository.Nurse360NotificationRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by hp on 2016/6/8.
 */
@Service("NotificationServiceForNurse360")
public class NotificationServiceForNurse360 {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceForNurse360.class);

    private static final Sort notificationSort = new Sort(
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    @Autowired private Nurse360NotificationRepository repository;
    @Autowired private Nurse360NotificationBeanConverter beanConverter;

    //==============================================================
    //                  getter
    //==============================================================

    public boolean existsNotification(long notificationId) {
        boolean exists = repository.exists(notificationId);
        logger.info("exists notification={}, exists={}", notificationId, exists);
        return exists;
    }

    public Nurse360NotificationBean getNotificationById(long notificationId) {
        logger.info("get notification by notificationId={}", notificationId);
        Nurse360NotificationEntity notification = repository.findOne(notificationId);
        if (null==notification) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        List<Nurse360NotificationBean> beans = entitiesToBeans(Arrays.asList(new Nurse360NotificationEntity[]{notification}), true);
        fillOtherProperties(beans);
        return beans.get(0);
    }

    public long countByHospitalDepartmentStatus(Integer hospitalId, Integer departmentId, String strStatus) {
        logger.info("count notification by hospitalId={} departmentId={} statues={}", hospitalId, departmentId, strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        long count = repository.countByHospitalIdAndDepartmentIdAndStatus(hospitalId, departmentId, status);
        logger.info("notification count is {}", count);
        return count;
    }

    public List<Nurse360NotificationBean> getNotificationByHospitalDepartmentStatus(Integer hospitalId, Integer departmentId, String strStatus, int pageIndex, int sizeOfPage) {
        logger.info("get  notification by hospitalId={} departmentId={} status={} at page={} size={}",
                hospitalId, departmentId, strStatus, pageIndex, sizeOfPage);
        // get all notification
        CommonStatus status = CommonStatus.parseString(strStatus);
        PageRequest page = new PageRequest(pageIndex, sizeOfPage, notificationSort);
        Page<Nurse360NotificationEntity> resultSet = repository.findByHospitalIdAndDepartmentIdAndStatus(hospitalId, departmentId, status, page);
        List<Nurse360NotificationBean> beans = entitiesToBeans(resultSet, false);
        fillOtherProperties(beans);
        return beans;
    }

    public List<Nurse360NotificationBean> getNotificationByHospitalDepartmentStatus(Integer hospitalId, List<Integer> departmentIds, String strStatus, int pageIndex, int sizeOfPage) {
        logger.info("get  notification by hospitalId={} departmentIds={} status={} at page={} size={}",
                hospitalId, departmentIds, strStatus, pageIndex, sizeOfPage);
        // get all notification
        CommonStatus status = CommonStatus.parseString(strStatus);
        PageRequest page = new PageRequest(pageIndex, sizeOfPage, notificationSort);

        if (VerifyUtil.isListEmpty(departmentIds)) {
            if (null==departmentIds) {
                departmentIds = new ArrayList<>();
            }
            departmentIds.add(0);
        }

        Page<Nurse360NotificationEntity> resultSet = repository.findByHospitalIdAndDepartmentIdInAndStatus(hospitalId, departmentIds, status, page);
        List<Nurse360NotificationBean> beans = entitiesToBeans(resultSet, false);
        fillOtherProperties(beans);
        return beans;
    }

    public List<Nurse360NotificationBean> getNotificationByStatusAndIds(String strStatus, List<Long> notificationIds) {
        logger.info("get  notification by status={} ids={}", strStatus, notificationIds);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Nurse360NotificationEntity> resultSet = null;
        if (!VerifyUtil.isListEmpty(notificationIds)) {
            if (null == status) {
                if ("ALL".equalsIgnoreCase(strStatus)) {
                    resultSet = repository.findByIdIn(notificationIds, notificationSort);
                }
            } else {
                resultSet = repository.findByStatusAndIdIn(status, notificationIds, notificationSort);
            }
        }
        List<Nurse360NotificationBean> beans = entitiesToBeans(resultSet, true);
        fillOtherProperties(beans);
        logger.info("get notification is {}", beans);
        return beans;
    }

    private List<Nurse360NotificationBean> entitiesToBeans(Iterable<Nurse360NotificationEntity> entities, boolean needContent) {
        List<Nurse360NotificationBean> beans = new ArrayList<>();
        if (null!=entities) {
            Nurse360NotificationBean bean;
            for (Nurse360NotificationEntity entity : entities) {
                bean = beanConverter.convert(entity);
                if (!needContent) {
                    bean.setContent("");
                }
                beans.add(bean);
            }
        }
        return beans;
    }

    private void fillOtherProperties(List<Nurse360NotificationBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }

//        List<Long> imageIds = new ArrayList<>();
//        for (Nurse360NotificationBean bean : beans) {
//            imageIds.add(bean.getImageId());
//        }
//        Map<Long, String> imageId2Path = nurseStorage.getFileUrl(imageIds);
//        for (Nurse360NotificationBean bean : beans) {
//            long imageId = bean.getImageId();
//            String imagePath = imageId2Path.get(imageId);
//            if (VerifyUtil.isStringEmpty(imagePath)) {
//                imagePath = "";
//            }
//            bean.setImageUrl(imagePath);
//        }
    }

    //=================================================================
    //         update
    //=================================================================
    @Transactional
    public Nurse360NotificationBean updateNotification(long notificationId,
                                                       String title, String introduction, String content,
                                                       Integer hospitalId, Integer departmentId,
                                                       String strSignificance, String strStatus) {
        boolean    changed = false;
        String     imgUrl = "";
        logger.info("update notification {} by title={} introduction={} content={} hospitalId={} departmentId={} significance={} status={}",
                notificationId, title, introduction, content, hospitalId, departmentId, strSignificance, strStatus);
        Nurse360NotificationEntity entity = repository.findOne(notificationId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        if (!VerifyUtil.isStringEmpty(title)) {
            if (!title.equals(entity.getTitle())) {
                long count = repository.countByTitle(title);
                if (count<=0) {
                    entity.setTitle(title);
                    changed = true;
                }
            }
        }

        if (!VerifyUtil.isStringEmpty(introduction)) {
            if (!introduction.equals(entity.getIntroduction())) {
                entity.setIntroduction(introduction);
                changed = true;
            }
        }

        if (!VerifyUtil.isStringEmpty(content)) {
            if (!content.equals(entity.getContent())) {
                entity.setContent(content);
                changed = true;
            }
        }

        YesNoEnum significance = YesNoEnum.parseString(strSignificance);
        if (null!=significance && !significance.equals(entity.getSignificance())) {
            entity.setSignificance(significance);
            changed = true;
        }

        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null!=status && !status.equals(entity.getStatus())) {
            entity.setStatus(status);
            changed = true;
        }

        if (null!=hospitalId && hospitalId!=entity.getHospitalId()) {
            entity.setHospitalId(hospitalId);
            changed = true;
        }

        if (null!=departmentId && departmentId!=entity.getDepartmentId()) {
            entity.setDepartmentId(departmentId);
            changed = true;
        }

        if (changed) {
            entity = repository.save(entity);
        }

        Nurse360NotificationBean bean = beanConverter.convert(entity);
        logger.info("updated is {}", bean);
        return bean;
    }

    //=================================================================
    //         add
    //=================================================================
    @Transactional
    public Nurse360NotificationBean addNotification(String title, String introduction, Integer hospitalId, Integer departmentId, String strSignificance) {
        logger.info("add notification : title={} introduction={} hospitalId={} departmentId={} significance={}",
                title, introduction, hospitalId, departmentId, strSignificance);
        title = VerifyUtil.isStringEmpty(title) ? "" : title.trim();
        String imageUrl = null;
        if (VerifyUtil.isStringEmpty(title)) {
            logger.error("add notification : name is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        else if (repository.countByTitle(title)>0) {
            logger.error("add tag : name is exist");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        Nurse360NotificationEntity entity = new Nurse360NotificationEntity();
        entity.setTitle(title);

        if (!VerifyUtil.isStringEmpty(introduction)) {
            entity.setIntroduction(introduction.trim());
        }

        YesNoEnum significance = YesNoEnum.parseString(strSignificance);
        entity.setSignificance(null==significance ? YesNoEnum.NO : significance);
        entity.setHospitalId(hospitalId);
        entity.setDepartmentId(departmentId);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.DISABLED);
        entity = repository.save(entity);

        Nurse360NotificationBean bean = beanConverter.convert(entity);
        logger.info("added is {}", bean);
        return bean;
    }
}

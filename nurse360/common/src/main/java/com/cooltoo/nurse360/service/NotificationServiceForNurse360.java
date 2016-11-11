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

    private static final Sort sort = new Sort(
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

//    public long countByTitleLikeAndStatus(String titleLike, String strStatus) {
//        logger.info("count all notification by titleLike={} status={}", titleLike, strStatus);
//        CommonStatus status = CommonStatus.parseString(strStatus);
//        titleLike = VerifyUtil.isStringEmpty(titleLike) ? null : VerifyUtil.reconstructSQLContentLike(titleLike.trim());
//        long count = repository.countByTitleLikeAndStatus(titleLike, status);
//        logger.info("count is {}", count);
//        return count;
//    }
//
//    public List<Nurse360NotificationBean> getByTitleLikeAndStatus(String titleLike, String strStatus) {
//        logger.info("get all notification by titleLike={} status={}", titleLike, strStatus);
//        CommonStatus status = CommonStatus.parseString(strStatus);
//        titleLike = VerifyUtil.isStringEmpty(titleLike) ? null : VerifyUtil.reconstructSQLContentLike(titleLike.trim());
//        List<Nurse360NotificationEntity> resultSet = repository.findByTitleLikeAndStatus(titleLike, status, sort);
//        List<Nurse360NotificationBean> beans = entitiesToBeans(resultSet, false);
//        fillOtherProperties(beans);
//        logger.info("count is {}", beans.size());
//        return beans;
//    }
//
//    public List<Nurse360NotificationBean> getByTitleLikeAndStatus(String titleLike, String strStatus, int pageIndex, int sizePerPage) {
//        logger.info("get all notification by titleLike={} status={} at page={} sizePerPage={}", titleLike, strStatus, pageIndex, sizePerPage);
//        CommonStatus status = CommonStatus.parseString(strStatus);
//        titleLike = VerifyUtil.isStringEmpty(titleLike) ? null : VerifyUtil.reconstructSQLContentLike(titleLike.trim());
//        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
//        Page<Nurse360NotificationEntity> resultSet = repository.findByTitleLikeAndStatus(titleLike, status, page);
//        List<Nurse360NotificationBean> beans = entitiesToBeans(resultSet, false);
//        fillOtherProperties(beans);
//        logger.info("count is {}", beans.size());
//        return beans;
//    }

    public Nurse360NotificationBean getNotificationById(long notificationId) {
        logger.info("get notification by notificationId={}", notificationId);
        Nurse360NotificationEntity notification = repository.findOne(notificationId);
        if (null==notification) {
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }
        List<Nurse360NotificationBean> beans = entitiesToBeans(Arrays.asList(new Nurse360NotificationEntity[]{notification}), true);
        fillOtherProperties(beans);
        return beans.get(0);
    }

    public List<Long> getNotificationIdByStatusAndIds(String strStatus, List<Long> notificationIds) {
        logger.info("get notificationId by status={} ids={}", strStatus, notificationIds);
        List<Long> resultSet = new ArrayList<>();
        if (VerifyUtil.isListEmpty(notificationIds)) {
            return resultSet;
        }

        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                resultSet = repository.findNotificationIdByStatusAndIdIn(null, notificationIds, sort);
            }
        }
        else {
            resultSet = repository.findNotificationIdByStatusAndIdIn(status, notificationIds, sort);
        }
        logger.info("count is {}", resultSet.size());
        return resultSet;
    }

    public List<Nurse360NotificationBean> getNotificationByStatusAndIds(String strStatus, List<Long> notificationIds) {
        logger.info("get notification by status={} ids={}", strStatus, notificationIds);
        if (VerifyUtil.isListEmpty(notificationIds)) {
            return new ArrayList<>();
        }

        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Nurse360NotificationEntity> resultSet = null;
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                resultSet = repository.findByIdIn(notificationIds, sort);
            }
        }
        else {
            resultSet = repository.findByStatusAndIdIn(status, notificationIds, sort);
        }
        List<Nurse360NotificationBean>   beans = entitiesToBeans(resultSet, false);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public List<Nurse360NotificationBean> getNotificationByIds(List<Long> courseIds, int pageIndex, int sizePerPage) {
        logger.info("get notification by ids={} at pageIndex={} sizePerPage={}", courseIds, pageIndex, sizePerPage);
        if (VerifyUtil.isListEmpty(courseIds)) {
            return new ArrayList<>();
        }
        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        List<Nurse360NotificationEntity> resultSet = repository.findByIdIn(courseIds, page);
        List<Nurse360NotificationBean>   beans = entitiesToBeans(resultSet, false);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
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
                                                       String strSignificance, String strStatus) {
        boolean    changed = false;
        String     imgUrl = "";
        logger.info("update notification {} by title={} introduction={} content={} significance={} status={}",
                notificationId, title, introduction, content, strSignificance, strStatus);
        Nurse360NotificationEntity entity = repository.findOne(notificationId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
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
    public Nurse360NotificationBean addNotification(String title, String introduction, String strSignificance) {
        logger.info("add notification : title={} introduction={} significance={}",
                title, introduction, strSignificance);
        title = VerifyUtil.isStringEmpty(title) ? "" : title.trim();
        String imageUrl = null;
        if (VerifyUtil.isStringEmpty(title)) {
            logger.error("add notification : name is empty");
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_IS_EMPTY);
        }
        else if (repository.countByTitle(title)>0) {
            logger.error("add tag : name is exist");
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_EXISTS_ALREADY);
        }

        Nurse360NotificationEntity entity = new Nurse360NotificationEntity();
        entity.setTitle(title);

        if (!VerifyUtil.isStringEmpty(introduction)) {
            entity.setIntroduction(introduction.trim());
        }

        YesNoEnum significance = YesNoEnum.parseString(strSignificance);
        entity.setSignificance(null==significance ? YesNoEnum.NO : significance);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.DISABLED);
        entity = repository.save(entity);

        Nurse360NotificationBean bean = beanConverter.convert(entity);
        logger.info("added is {}", bean);
        return bean;
    }
}

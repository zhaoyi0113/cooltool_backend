package com.cooltoo.nurse360.service;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.nurse360.beans.Nurse360NotificationBean;
import com.cooltoo.nurse360.converters.Nurse360NotificationBeanConverter;
import com.cooltoo.nurse360.entities.Nurse360NotificationEntity;
import com.cooltoo.nurse360.repository.Nurse360NotificationRepository;
import com.cooltoo.nurse360.service.file.NurseFileStorageServiceForNurse360;
import com.cooltoo.nurse360.service.file.TemporaryFileStorageServiceForNurse360;
import com.cooltoo.nurse360.util.Nurse360Utility;
import com.cooltoo.services.CommonDepartmentService;
import com.cooltoo.services.CommonHospitalService;
import com.cooltoo.util.HtmlParser;
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

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    @Autowired private Nurse360NotificationRepository repository;
    @Autowired private Nurse360NotificationBeanConverter beanConverter;

    @Autowired private CommonHospitalService hospitalService;
    @Autowired private CommonDepartmentService departmentService;
    @Autowired private Nurse360Utility utility;
    @Autowired private NurseFileStorageServiceForNurse360 userStorage;
    @Autowired private TemporaryFileStorageServiceForNurse360 tempStorage;

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
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }
        List<Nurse360NotificationBean> beans = entitiesToBeans(Arrays.asList(new Nurse360NotificationEntity[]{notification}), true);
        fillOtherProperties(beans);

        return beans.get(0);
    }

    public List<HospitalBean> getHospitalByNotificationId(long notificationId) {
        logger.info("get hospital by notification id={}", notificationId);
        Nurse360NotificationEntity entity = repository.findOne(notificationId);
        List<HospitalBean> hospitals = new ArrayList<>();
        if (null!=entity && ServiceVendorType.HOSPITAL.equals(entity.getVendorType())) {
            hospitals = hospitalService.getHospitalByIds(Arrays.asList(new Integer[]{(int)entity.getVendorId()}));
        }
        logger.info("hospital is {}", hospitals);
        return hospitals;
    }

    public List<HospitalDepartmentBean> getDepartmentByNotificationId(long notificationId) {
        logger.info("get department by notification id={}", notificationId);
        Nurse360NotificationEntity entity = repository.findOne(notificationId);
        List<HospitalDepartmentBean> departments = new ArrayList<>();
        if (null!=entity && ServiceVendorType.HOSPITAL.equals(entity.getVendorType())) {
            departments = departmentService.getByIds(
                    Arrays.asList(new Integer[]{(int)entity.getDepartId()}),
                    utility.getHttpPrefixForNurseGo());
        }
        logger.info("department is {}", departments);
        return departments;
    }

    public long countNotificationByConditions(String titleLike,
                                              List<CommonStatus> statuses,
                                              ServiceVendorType vendorType,
                                              Long vendorId,
                                              Long departId
    ) {
        logger.info("count notificationId by titleLike={} statuses={} vendorType={} vendorId={} departId={}",
                titleLike, statuses, vendorType, vendorId, departId);
        if (VerifyUtil.isListEmpty(statuses)) {
            return 0;
        }

        long count = repository.countByConditions(titleLike, statuses, vendorType, vendorId, departId);
        logger.info("count is {}", count);
        return count;
    }

    public List<Nurse360NotificationBean> getNotificationByConditions(String titleLike,
                                                                      List<CommonStatus> statuses,
                                                                      ServiceVendorType vendorType, Long vendorId, Long departId,
                                                                      int pageIndex, int sizePerPage
    ) {
        logger.info("get notificationId by titleLike={} statuses={} vendorType={} vendorId={} departId={}, page={} size={}",
                titleLike, statuses, vendorType, vendorId, departId, pageIndex, sizePerPage);
        if (VerifyUtil.isListEmpty(statuses)) {
            return new ArrayList<>();
        }

        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        Page<Nurse360NotificationEntity> resultSet;
        resultSet = repository.findByConditions(titleLike, statuses, vendorType, vendorId, departId, page);
        List<Nurse360NotificationBean> beans = entitiesToBeans(resultSet, false);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public List<Long> getNotificationIdByVendor(List<CommonStatus> statuses,
                                                ServiceVendorType vendorType,
                                                Long vendorId,
                                                Long departId
    ) {
        logger.info("get notificationId by statuses={} vendorType={} vendorId={} departId={}",
                statuses, vendorType, vendorId, departId);
        List<Long> notificationIds = new ArrayList<>();
        if (VerifyUtil.isListEmpty(statuses)) {
            return notificationIds;
        }

        List<Object> resultSet = repository.findNotificationIdByConditions(statuses, vendorType, vendorId, departId);
        if (!VerifyUtil.isListEmpty(resultSet)) {
            for (Object tmp : resultSet) {
                if (tmp instanceof Long) {
                    notificationIds.add((Long)tmp);
                }
            }
        }
        logger.info("count is {}", notificationIds.size());
        return notificationIds;
    }

    public List<Nurse360NotificationBean> getNotificationByIds(List<Long> notificationIds, int pageIndex, int sizePerPage) {
        logger.info("get notification by ids={} at pageIndex={} sizePerPage={}", notificationIds, pageIndex, sizePerPage);
        if (VerifyUtil.isListEmpty(notificationIds)) {
            return new ArrayList<>();
        }
        PageRequest page = new PageRequest(pageIndex, sizePerPage, sort);
        List<Nurse360NotificationEntity> resultSet = repository.findByIdIn(notificationIds, page);
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
                                                       String strSignificance, String strStatus,
                                                       ServiceVendorType vendorType, Long vendorId, Long departId
    ) {
        boolean changed = false;
        logger.info("update notification={} by title={} introduction={} content={} significance={} status={}, vendorType={} vendorId={} departId={}",
                notificationId, title, introduction, content, strSignificance, strStatus, vendorType, vendorId, departId);
        Nurse360NotificationEntity entity = repository.findOne(notificationId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }

        if (!VerifyUtil.isStringEmpty(title)) {
            if (!title.equals(entity.getTitle())) {
                entity.setTitle(title);
                changed = true;
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

        if (null!=vendorType && !vendorType.equals(entity.getVendorType())) {
            entity.setVendorType(vendorType);
            changed = true;
        }

        if (null!=vendorId && !vendorId.equals(entity.getVendorId())) {
            entity.setVendorId(vendorId);
            changed = true;
        }

        if (null!=departId && !departId.equals(entity.getDepartId())) {
            entity.setDepartId(departId);
            changed = true;
        }

        if (changed) {
            entity = repository.save(entity);
        }

        Nurse360NotificationBean bean = beanConverter.convert(entity);
        logger.info("updated is {}", bean);
        return bean;
    }

    @Transactional
    public Nurse360NotificationBean updateNotification(long notificationId, String content) {
        logger.info("update notification={} by content={}", notificationId, content);
        Nurse360NotificationEntity entity = repository.findOne(notificationId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }

        String originalContent = entity.getContent();
        originalContent = (null==originalContent) ? "" : originalContent;
        content         = (null==content)         ? "" : content;
        if (originalContent.equals(content)) {
            Nurse360NotificationBean bean = beanConverter.convert(entity);
            logger.info("content is the same.");
            return bean;
        }

        if (!VerifyUtil.isStringEmpty(originalContent)) {
            moveOfficialFileToTemporary(originalContent);
        }

        Nurse360NotificationBean bean = beanConverter.convert(entity);
        logger.info("update content is {}", bean);
        return bean;
    }

    private void moveOfficialFileToTemporary(String htmlContent) {
        logger.info("move html img tag src 's images to temporary storage");
        HtmlParser htmlParser = HtmlParser.newInstance();
        // get image tags src attribute url
        List<String> srcUrls = htmlParser.getSrcUrls(htmlContent);

        if (VerifyUtil.isListEmpty(srcUrls)) {
            logger.info("img tag is empty. nothing move to temporary directory");
            return;
        }

        List<String> srcFilesInStorage = new ArrayList<>();
        for (String srcUrl : srcUrls) {
            String relativePath = userStorage.getRelativePathInStorage(srcUrl);
            if (VerifyUtil.isStringEmpty(relativePath)) {
                continue;
            }
            srcFilesInStorage.add(userStorage.getStoragePath() + relativePath);
        }
        tempStorage.moveFileToHere(srcFilesInStorage);
    }

    //=================================================================
    //         add
    //=================================================================
    @Transactional
    public Nurse360NotificationBean addNotification(String title, String introduction, String strSignificance, ServiceVendorType vendorType, long vendorId, long departId) {
        logger.info("add notification by title={} introduction={} significance={} vendorType={} vendorId={} departId={}",
                title, introduction, strSignificance, vendorType, vendorId, departId);

        title = VerifyUtil.isStringEmpty(title) ? "" : title.trim();
        if (VerifyUtil.isStringEmpty(title)) {
            logger.error("add notification : name is empty");
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_IS_EMPTY);
        }
        if (null==vendorType) {
            logger.error("add notification : vendor type is empty");
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_IS_EMPTY);
        }

        Nurse360NotificationEntity entity = new Nurse360NotificationEntity();
        entity.setTitle(title);

        if (!VerifyUtil.isStringEmpty(introduction)) {
            entity.setIntroduction(introduction.trim());
        }

        YesNoEnum significance = YesNoEnum.parseString(strSignificance);
        entity.setSignificance(null==significance ? YesNoEnum.NO : significance);
        entity.setVendorType(vendorType);
        entity.setVendorId(vendorId);
        entity.setDepartId(departId);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.DISABLED);
        entity = repository.save(entity);

        Nurse360NotificationBean bean = beanConverter.convert(entity);
        logger.info("added is {}", bean);
        return bean;
    }
}

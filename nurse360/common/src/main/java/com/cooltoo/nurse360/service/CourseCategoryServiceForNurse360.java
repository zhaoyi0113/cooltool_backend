package com.cooltoo.nurse360.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.nurse360.beans.Nurse360CourseCategoryBean;
import com.cooltoo.nurse360.converters.Nurse360CourseCategoryBeanConverter;
import com.cooltoo.nurse360.entities.Nurse360CourseCategoryEntity;
import com.cooltoo.nurse360.repository.Nurse360CourseCategoryRepository;
import com.cooltoo.nurse360.service.file.NurseFileStorageServiceForNurse360;
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
 * Created by hp on 2016/6/8.
 */
@Service("CourseCategoryServiceForNurse360")
public class CourseCategoryServiceForNurse360 {

    private static final Logger logger = LoggerFactory.getLogger(CourseCategoryServiceForNurse360.class);

    private static final Sort categorySort = new Sort(
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    @Autowired private Nurse360CourseCategoryRepository repository;
    @Autowired private Nurse360CourseCategoryBeanConverter beanConverter;
    @Autowired private NurseFileStorageServiceForNurse360 nurseStorage;

    //==============================================================
    //                  category getter
    //==============================================================

    public boolean existsCategory(long categoryId) {
        boolean exists = repository.exists(categoryId);
        logger.info("exists category={}, exists={}", categoryId, exists);
        return exists;
    }

    public Nurse360CourseCategoryBean getCategoryById(long categoryId) {
        logger.info("get course category by categoryId={}", categoryId);
        Nurse360CourseCategoryEntity category = repository.findOne(categoryId);
        if (null==category) {
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }
        List<Nurse360CourseCategoryBean> beans = entitiesToBeans(Arrays.asList(new Nurse360CourseCategoryEntity[]{category}));
        fillOtherProperties(beans);
        return beans.get(0);
    }

    public long countByStatus(String strStatus) {
        logger.info("count course category by statues={}", strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        long count = 0;
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                count = repository.count();
            }
        }
        else {
            count = repository.countByStatus(status);
        }
        logger.info("course category count is {}", count);
        return count;
    }

    public List<Nurse360CourseCategoryBean> getCategoryByStatus(String strStatus, int pageIndex, int sizeOfPage) {
        logger.info("get course category by status={} at page={} size={}", strStatus, pageIndex, sizeOfPage);
        // get all category
        CommonStatus status = CommonStatus.parseString(strStatus);
        PageRequest page = new PageRequest(pageIndex, sizeOfPage, categorySort);
        Page<Nurse360CourseCategoryEntity> resultSet = null;
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                resultSet = repository.findAll(page);
            }
        }
        else {
            resultSet = repository.findByStatus(status, page);
        }

        List<Nurse360CourseCategoryBean> beans = entitiesToBeans(resultSet);
        fillOtherProperties(beans);
        return beans;
    }

    public List<Nurse360CourseCategoryBean> getCategoryByStatusAndIds(String strStatus, List<Long> categoryIds) {
        logger.info("get course category by status={} ids={}", strStatus, categoryIds);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Nurse360CourseCategoryEntity> resultSet = null;
        if (!VerifyUtil.isListEmpty(categoryIds)) {
            if (null == status) {
                if ("ALL".equalsIgnoreCase(strStatus)) {
                    resultSet = repository.findByIdIn(categoryIds, categorySort);
                }
            } else {
                resultSet = repository.findByStatusAndIdIn(status, categoryIds, categorySort);
            }
        }
        List<Nurse360CourseCategoryBean> beans = entitiesToBeans(resultSet);
        fillOtherProperties(beans);
        logger.info("get course category is {}", beans);
        return beans;
    }

    private List<Nurse360CourseCategoryBean> entitiesToBeans(Iterable<Nurse360CourseCategoryEntity> entities) {
        List<Nurse360CourseCategoryBean> beans = new ArrayList<>();
        if (null!=entities) {
            Nurse360CourseCategoryBean bean;
            for (Nurse360CourseCategoryEntity entity : entities) {
                bean = beanConverter.convert(entity);
                beans.add(bean);
            }
        }
        return beans;
    }

    private void fillOtherProperties(List<Nurse360CourseCategoryBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }

        List<Long> imageIds = new ArrayList<>();
        for (Nurse360CourseCategoryBean bean : beans) {
            imageIds.add(bean.getImageId());
        }
        Map<Long, String> imageId2Path = nurseStorage.getFileUrl(imageIds);
        for (Nurse360CourseCategoryBean bean : beans) {
            long imageId = bean.getImageId();
            String imagePath = imageId2Path.get(imageId);
            if (VerifyUtil.isStringEmpty(imagePath)) {
                imagePath = "";
            }
            bean.setImageUrl(imagePath);
        }
    }

    //=================================================================
    //         update
    //=================================================================
    @Transactional
    public Nurse360CourseCategoryBean updateCategory(long categoryId, String name, String introduction, String strStatus, String imageName, InputStream image) {
        boolean    changed = false;
        String     imgUrl = "";
        logger.info("update category {} by name={} introduction={} status={} imageName={} image={}",
                categoryId, name, introduction, strStatus, imageName, null!=image);
        Nurse360CourseCategoryEntity entity = repository.findOne(categoryId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
        }

        if (!VerifyUtil.isStringEmpty(name)) {
            if (!name.equals(entity.getName())) {
                long count = repository.countByName(name);
                if (count<=0) {
                    entity.setName(name);
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

        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null!=status && !status.equals(entity.getStatus())) {
            entity.setStatus(status);
            changed = true;
        }

        if (null!=image) {
            if (VerifyUtil.isStringEmpty(imageName)) {
                imageName = "category_tmp_" + System.nanoTime();
            }
            long imgId = nurseStorage.addFile(entity.getImageId(), imageName, image);
            if (imgId>0) {
                imgUrl = nurseStorage.getFileURL(imgId);
                entity.setImageId(imgId);
                changed = true;
            }
        }

        if (changed) {
            entity = repository.save(entity);
        }
        Nurse360CourseCategoryBean bean = beanConverter.convert(entity);
        bean.setImageUrl(imgUrl);
        logger.info("updated is {}", bean);
        return bean;
    }

    //=================================================================
    //         add
    //=================================================================
    @Transactional
    public Nurse360CourseCategoryBean addCategory(String name, String introduction, String imageName, InputStream image) {
        logger.info("add tag category : name={} introduction={} imageName={} image={}",
                name, introduction, imageName, (null!=image));
        name = VerifyUtil.isStringEmpty(name) ? "" : name.trim();
        String imageUrl = null;
        if (VerifyUtil.isStringEmpty(name)) {
            logger.error("add category : name is empty");
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_IS_EMPTY);
        }
        else if (repository.countByName(name)>0) {
            logger.error("add tag : name is exist");
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_EXISTS_ALREADY);
        }

        Nurse360CourseCategoryEntity entity = new Nurse360CourseCategoryEntity();
        entity.setName(name);

        if (!VerifyUtil.isStringEmpty(introduction)) {
            entity.setIntroduction(introduction.trim());
        }

        if (null!=image) {
            if (VerifyUtil.isStringEmpty(imageName)) {
                imageName = "category_tmp_" + System.nanoTime();
            }
            long fileId = nurseStorage.addFile(-1, imageName, image);
            if (fileId>0) {
                entity.setImageId(fileId);
                imageUrl = nurseStorage.getFileURL(fileId);
            }
        }

        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity = repository.save(entity);

        Nurse360CourseCategoryBean bean = beanConverter.convert(entity);
        bean.setImageUrl(imageUrl);

        return bean;
    }
}

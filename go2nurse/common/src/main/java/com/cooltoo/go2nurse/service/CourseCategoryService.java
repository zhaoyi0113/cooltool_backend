package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.CourseCategoryBean;
import com.cooltoo.go2nurse.converter.CourseCategoryBeanConverter;
import com.cooltoo.go2nurse.entities.CourseCategoryEntity;
import com.cooltoo.go2nurse.repository.CourseCategoryRepository;
import com.cooltoo.go2nurse.service.file.UserGo2NurseFileStorageService;
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
@Service("CourseCategoryService")
public class CourseCategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CourseCategoryService.class);

    private static final Sort categorySort = new Sort(
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    @Autowired private CourseCategoryRepository repository;
    @Autowired private CourseCategoryBeanConverter beanConverter;
    @Autowired private UserGo2NurseFileStorageService fileStorageService;

    //==============================================================
    //                  category getter
    //==============================================================

    public boolean existsCategory(long categoryId) {
        boolean exists = repository.exists(categoryId);
        logger.info("exists category={}, exists={}", categoryId, exists);
        return exists;
    }

    public CourseCategoryBean getCategoryById(long categoryId) {
        logger.info("get course category by categoryId={}", categoryId);
        CourseCategoryEntity category = repository.findOne(categoryId);
        if (null==category) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        List<CourseCategoryBean> beans = entitiesToBeans(Arrays.asList(new CourseCategoryEntity[]{category}));
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

    public List<CourseCategoryBean> getCategoryByStatus(String strStatus, int pageIndex, int sizeOfPage) {
        logger.info("get course category by status={} at page={} size={}", strStatus, pageIndex, sizeOfPage);
        // get all category
        CommonStatus status = CommonStatus.parseString(strStatus);
        PageRequest page = new PageRequest(pageIndex, sizeOfPage, categorySort);
        Page<CourseCategoryEntity> resultSet = null;
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                resultSet = repository.findAll(page);
            }
        }
        else {
            resultSet = repository.findByStatus(status, page);
        }

        List<CourseCategoryBean> beans = entitiesToBeans(resultSet);
        fillOtherProperties(beans);
        return beans;
    }

    public List<CourseCategoryBean> getCategoryByStatusAndIds(String strStatus, List<Long> categoryIds) {
        logger.info("get course category by status={} ids={}", strStatus, categoryIds);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<CourseCategoryEntity> resultSet = null;
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                resultSet = repository.findByIdIn(categoryIds, categorySort);
            }
        }
        else {
            resultSet = repository.findByStatusAndIdIn(status, categoryIds, categorySort);
        }
        List<CourseCategoryBean> beans = entitiesToBeans(resultSet);
        fillOtherProperties(beans);
        logger.info("get course category is {}", beans);
        return beans;
    }

    public Map<Long, CourseCategoryBean> getIdToBeanByStatusAndIds(String strStatus, List<Long> categoryIds) {
        Map<Long, CourseCategoryBean> result = new HashMap<>();

        List<CourseCategoryBean> beans = getCategoryByStatusAndIds(strStatus, categoryIds);
        for (CourseCategoryBean tmp : beans) {
            result.put(tmp.getId(), tmp);
        }

        return result;
    }

    private List<CourseCategoryBean> entitiesToBeans(Iterable<CourseCategoryEntity> entities) {
        List<CourseCategoryBean> beans = new ArrayList<>();
        if (null!=entities) {
            CourseCategoryBean bean;
            for (CourseCategoryEntity entity : entities) {
                bean = beanConverter.convert(entity);
                beans.add(bean);
            }
        }
        return beans;
    }

    private void fillOtherProperties(List<CourseCategoryBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }

        List<Long> imageIds = new ArrayList<>();
        for (CourseCategoryBean bean : beans) {
            imageIds.add(bean.getImageId());
        }
        Map<Long, String> imageId2Path = fileStorageService.getFileUrl(imageIds);
        for (CourseCategoryBean bean : beans) {
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
    public CourseCategoryBean updateCategory(long categoryId, String name, String introduction, String strStatus, String imageName, InputStream image) {
        boolean    changed = false;
        String     imgUrl = "";
        logger.info("update category {} by name={} introduction={} status={} imageName={} image={}",
                categoryId, name, introduction, strStatus, imageName, null!=image);
        CourseCategoryEntity entity = repository.findOne(categoryId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
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
            long imgId = fileStorageService.addFile(entity.getImageId(), imageName, image);
            if (imgId>0) {
                imgUrl = fileStorageService.getFileURL(imgId);
                entity.setImageId(imgId);
                changed = true;
            }
        }

        if (changed) {
            entity = repository.save(entity);
        }
        CourseCategoryBean bean = beanConverter.convert(entity);
        bean.setImageUrl(imgUrl);
        logger.info("updated is {}", bean);
        return bean;
    }

    //=================================================================
    //         add
    //=================================================================
    @Transactional
    public CourseCategoryBean addCategory(String name, String introduction, String imageName, InputStream image) {
        logger.info("add tag category : name={} introduction={} imageName={} image={}",
                name, introduction, imageName, (null!=image));
        name = VerifyUtil.isStringEmpty(name) ? "" : name.trim();
        String imageUrl = null;
        if (VerifyUtil.isStringEmpty(name)) {
            logger.error("add category : name is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        else if (repository.countByName(name)>0) {
            logger.error("add tag : name is exist");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        CourseCategoryEntity entity = new CourseCategoryEntity();
        entity.setName(name);

        if (!VerifyUtil.isStringEmpty(introduction)) {
            entity.setIntroduction(introduction.trim());
        }

        if (null!=image) {
            if (VerifyUtil.isStringEmpty(imageName)) {
                imageName = "category_tmp_" + System.nanoTime();
            }
            long fileId = fileStorageService.addFile(-1, imageName, image);
            if (fileId>0) {
                entity.setImageId(fileId);
                imageUrl = fileStorageService.getFileURL(fileId);
            }
        }

        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity = repository.save(entity);

        CourseCategoryBean bean = beanConverter.convert(entity);
        bean.setImageUrl(imageUrl);

        return bean;
    }
}

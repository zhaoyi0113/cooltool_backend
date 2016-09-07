package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.ConsultationCategoryBean;
import com.cooltoo.go2nurse.converter.ConsultationCategoryBeanConverter;
import com.cooltoo.go2nurse.entities.ConsultationCategoryEntity;
import com.cooltoo.go2nurse.repository.ConsultationCategoryRepository;
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
 * Created by hp on 2016/7/13.
 */
@Service("ConsultationCategoryService")
public class ConsultationCategoryService {

    private static final Logger logger = LoggerFactory.getLogger(ConsultationCategoryService.class);

    private static final Sort categorySort = new Sort(
            new Sort.Order(Sort.Direction.ASC, "orderIndex"),
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    @Autowired private ConsultationCategoryRepository categoryRep;
    @Autowired private ConsultationCategoryBeanConverter categoryBeanConverter;
    @Autowired private UserGo2NurseFileStorageService userFileStorage;

    //=====================================================================
    //                   getting
    //=====================================================================
    public boolean existCategory(long categoryId) {
        return categoryRep.exists(categoryId);
    }

    public long countCategoryByStatus(List<CommonStatus> statuses) {
        long count = 0;
        if (!VerifyUtil.isListEmpty(statuses)) {
            count = categoryRep.countByStatusIn(statuses);
        }
        logger.info("count consultation category by status={}, size is {}", statuses, count);
        return count;
    }

    public List<ConsultationCategoryBean> getCategoryByStatus(List<CommonStatus> statuses) {
        logger.info("get consultation category by status={}", statuses);
        List<ConsultationCategoryBean> beans;
        if (!VerifyUtil.isListEmpty(statuses)) {
            List<ConsultationCategoryEntity> entities = categoryRep.findByStatusIn(statuses, categorySort);
            beans = entitiesToBeans(entities);
            fillOtherProperties(beans);
        }
        else {
            logger.warn("statuses is empty");
            beans = new ArrayList<>();
        }
        logger.info("count is ={}", beans.size());
        return beans;
    }

    public List<ConsultationCategoryBean> getCategoryByStatus(List<CommonStatus> statuses, int pageIndex, int sizePerPage) {
        logger.info("get consultation category by status={}, at page={} size={}", statuses, pageIndex, sizePerPage);
        List<ConsultationCategoryBean> beans;
        if (!VerifyUtil.isListEmpty(statuses)) {
            PageRequest pageRequest = new PageRequest(pageIndex, sizePerPage, categorySort);
            Page<ConsultationCategoryEntity> entities = categoryRep.findByStatusIn(statuses, pageRequest);
            beans = entitiesToBeans(entities);
            fillOtherProperties(beans);
        }
        else {
            logger.warn("statuses is empty");
            beans = new ArrayList<>();
        }
        logger.info("count is ={}", beans.size());
        return beans;
    }

    public List<ConsultationCategoryBean> getCategoryAndParentById(long categoryId) {
        logger.info("get consultation category by categoryId={}", categoryId);
        List<ConsultationCategoryEntity> entities = new ArrayList<>();
        ConsultationCategoryEntity entity = categoryRep.findOne(categoryId);
        if(null==entity) {
            return new ArrayList<>();
        }
        entities.add(entity);

        List<ConsultationCategoryBean> beans = entitiesToBeans(entities);
        fillOtherProperties(beans);
        return beans;
    }

    public Map<Long, ConsultationCategoryBean> getCategoryIdToBean(List<Long> userIds) {
        logger.info("get user by userIds={}", userIds);
        if (VerifyUtil.isListEmpty(userIds)) {
            return new HashMap<>();
        }
        List<ConsultationCategoryEntity> resultSet = categoryRep.findAll(userIds);
        List<ConsultationCategoryBean> beans = entitiesToBeans(resultSet);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        Map<Long, ConsultationCategoryBean> map = new HashMap<>();
        for (ConsultationCategoryBean tmp : beans) {
            map.put(tmp.getId(), tmp);
        }
        return map;
    }

    private List<ConsultationCategoryBean> entitiesToBeans(Iterable<ConsultationCategoryEntity> entities) {
        List<ConsultationCategoryBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }
        for (ConsultationCategoryEntity entity : entities) {
            ConsultationCategoryBean bean = categoryBeanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    private void fillOtherProperties(List<ConsultationCategoryBean> items) {
        if (VerifyUtil.isListEmpty(items)) {
            return;
        }

        List<Long> imagesId = new ArrayList<>();
        for (ConsultationCategoryBean item : items) {
            if (imagesId.contains(item.getImageId())) {
                continue;
            }
            imagesId.add(item.getImageId());
        }

        Map<Long, String> imageIdToUrl = userFileStorage.getFileUrl(imagesId);
        for (ConsultationCategoryBean item : items) {
            String imageUrl = imageIdToUrl.get(item.getImageId());
            if (VerifyUtil.isStringEmpty(imageUrl)) {
                continue;
            }
            item.setImageUrl(imageUrl);
        }
    }

    //=====================================================================
    //                   deleting
    //=====================================================================
    @Transactional
    public List<Long> deleteCategoryByIds(List<Long> categoryIds) {
        logger.info("delete consultation category by categoryIds={}", categoryIds);
        if (VerifyUtil.isListEmpty(categoryIds)) {
            return categoryIds;
        }
        List<ConsultationCategoryEntity> entities = categoryRep.findByIdIn(categoryIds);
        if (VerifyUtil.isListEmpty(entities)) {
            return categoryIds;
        }

        List<Long> imagesId = new ArrayList<>();
        for (ConsultationCategoryEntity entity : entities) {
            if (imagesId.contains(entity.getImageId())) {
                continue;
            }
            imagesId.add(entity.getImageId());
        }
        userFileStorage.deleteFiles(imagesId);
        categoryRep.delete(entities);

        logger.info("delete consultation category={}", entities);
        return categoryIds;
    }

    //=====================================================================
    //                   updating
    //=====================================================================
    @Transactional
    public void changeTwoCategoryOrder(long firstAdId, long firstAdOrder,
                                       long secondAdId, long secondAdOrder
    ) {
        logger.info("change two consultation category order 1stId={}, 1stOrder={}, 2ndId={}, 2ndOrder={}",
                firstAdId, firstAdOrder, secondAdId, secondAdOrder);
        ConsultationCategoryEntity _1st = categoryRep.findOne(firstAdId);
        ConsultationCategoryEntity _2nd = categoryRep.findOne(secondAdId);
        if (null==_1st || null==_2nd) {
            logger.error("the consultation category is not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        _1st.setOrderIndex(secondAdOrder);
        _2nd.setOrderIndex(firstAdOrder);
        categoryRep.save(_1st);
        categoryRep.save(_2nd);
        return;
    }

    @Transactional
    public ConsultationCategoryBean updateCategory(long categoryId, String name, String description, String strStatus) {
        logger.info("update consultation category={} by name={} description={} status={}",
                categoryId, name, description, strStatus);

        ConsultationCategoryEntity entity = categoryRep.findOne(categoryId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        boolean changed = false;
        if (!VerifyUtil.isStringEmpty(name)) {
            entity.setName(name.trim());
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(description)) {
            entity.setDescription(description);
            changed = true;
        }
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null!=status) {
            entity.setStatus(status);
            changed = true;
        }

        if (changed) {
            entity = categoryRep.save(entity);
        }

        ConsultationCategoryBean bean = categoryBeanConverter.convert(entity);
        bean.setImageUrl(userFileStorage.getFileURL(bean.getImageId()));
        logger.info("consultation category updated is {}", bean);
        return bean;
    }

    @Transactional
    public ConsultationCategoryBean updateCategoryImage(long categoryId, String imageName, InputStream image) {
        logger.info("update consultation category={} by imageName={} image={}",
                categoryId, imageName, null!=image);

        ConsultationCategoryEntity entity = categoryRep.findOne(categoryId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        String imageUrl = "";
        if (null!=image) {
            if (VerifyUtil.isStringEmpty(imageName)) {
                imageName = "consultation_category_image_" + System.nanoTime();
            }
            long imageId = userFileStorage.addFile(entity.getImageId(), imageName, image);
            imageUrl = userFileStorage.getFileURL(imageId);
            entity.setImageId(imageId);
        }

        ConsultationCategoryBean bean = categoryBeanConverter.convert(entity);
        bean.setImageUrl(imageUrl);
        logger.info("consultation category updated is {}", bean);
        return bean;
    }

    //=====================================================================
    //                   adding
    //=====================================================================

    @Transactional
    public ConsultationCategoryBean addCategory(String name, String description) {
        logger.info("add consultation category by name={} description={}", name, description);
        if (VerifyUtil.isStringEmpty(name)) {
            logger.error("name is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        name = name.trim();
        ConsultationCategoryEntity entity = new ConsultationCategoryEntity();
        if (!VerifyUtil.isStringEmpty(description)) {
            entity.setDescription(description);
        }
        entity.setName(name);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());
        entity = categoryRep.save(entity);
        entity.setOrderIndex(entity.getId());
        entity = categoryRep.save(entity);
        ConsultationCategoryBean bean = categoryBeanConverter.convert(entity);
        logger.info("consultation category added is {}", bean);
        return bean;
    }
}

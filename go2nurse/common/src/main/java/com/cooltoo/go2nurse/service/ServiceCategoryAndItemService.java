package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.ServiceCategoryBean;
import com.cooltoo.go2nurse.beans.ServiceItemBean;
import com.cooltoo.go2nurse.constants.ServiceClass;
import com.cooltoo.go2nurse.constants.TimeUnit;
import com.cooltoo.go2nurse.converter.ServiceCategoryBeanConverter;
import com.cooltoo.go2nurse.converter.ServiceItemBeanConverter;
import com.cooltoo.go2nurse.entities.ServiceCategoryEntity;
import com.cooltoo.go2nurse.entities.ServiceItemEntity;
import com.cooltoo.go2nurse.repository.ServiceCategoryRepository;
import com.cooltoo.go2nurse.repository.ServiceItemRepository;
import com.cooltoo.go2nurse.service.file.UserGo2NurseFileStorageService;
import com.cooltoo.util.NumberUtil;
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
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by hp on 2016/7/13.
 */
@Service("ServiceCategoryAndItemService")
public class ServiceCategoryAndItemService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceCategoryAndItemService.class);

    private static final Sort categorySort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "grade"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    private static final Sort itemSort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "grade"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private ServiceCategoryRepository categoryRep;
    @Autowired private ServiceCategoryBeanConverter categoryBeanConverter;
    @Autowired private ServiceItemRepository itemRep;
    @Autowired private ServiceItemBeanConverter itemBeanConverter;
    @Autowired private UserGo2NurseFileStorageService userFileStorage;

    //=====================================================================
    //                   getting
    //=====================================================================

    public long countTopCategory() {
        return countCategoryByParentId(0L);
    }

    public List<ServiceCategoryBean> getTopCategory(int pageIndex, int sizePerPage) {
        return getCategoryByParentId(0L, pageIndex, sizePerPage);
    }

    public long countCategoryByParentId(long categoryParentId) {
        long count = categoryRep.countByParentId(categoryParentId);
        logger.info("count service category by parentId={}, size is {}", categoryParentId, count);
        return count;
    }

    public List<ServiceCategoryBean> getCategoryByParentId(long categoryParentId) {
        logger.info("get service category by parentId={}", categoryParentId);
        List<ServiceCategoryEntity> entities = categoryRep.findByParentId(categoryParentId, categorySort);
        List<ServiceCategoryBean> beans = serviceCategoryEntitiesToBeans(entities);
        fillCategoryOtherProperties(beans);
        logger.info("count is ={}");
        return beans;
    }

    public List<ServiceCategoryBean> getCategoryByParentId(long categoryParentId, int pageIndex, int sizePerPage) {
        logger.info("get service category by parentId={}, at page={} size={}", categoryParentId, pageIndex, sizePerPage);
        PageRequest pageRequest = new PageRequest(pageIndex, sizePerPage, categorySort);
        Page<ServiceCategoryEntity> entities = categoryRep.findByParentId(categoryParentId, pageRequest);
        List<ServiceCategoryBean> beans = serviceCategoryEntitiesToBeans(entities);
        fillCategoryOtherProperties(beans);
        logger.info("count is ={}");
        return beans;
    }

    public boolean existItem(long itemId) {
        return itemRep.exists(itemId);
    }

    public long countItemByCategoryId(long categoryId) {
        long count = itemRep.countByCategoryId(categoryId);
        logger.info("count service item by categoryId={}, size is {}", categoryId, count);
        return count;
    }

    public List<ServiceItemBean> getItemByCategoryId(long categoryId) {
        logger.info("get service item by categoryId={}", categoryId);
        List<ServiceItemEntity> entities = itemRep.findByCategoryId(categoryId, itemSort);
        List<ServiceItemBean> beans = serviceItemEntitiesToBeans(entities);
        fillItemOtherProperties(beans);
        logger.info("count is ={}");
        return beans;
    }

    public List<ServiceItemBean> getItemByCategoryId(long categoryId, int pageIndex, int sizePerPage) {
        logger.info("get service item by categoryId={}, at page={} size={}", categoryId, pageIndex, sizePerPage);
        PageRequest pageRequest = new PageRequest(pageIndex, sizePerPage, itemSort);
        Page<ServiceItemEntity> entities = itemRep.findByCategoryId(categoryId, pageRequest);
        List<ServiceItemBean> beans = serviceItemEntitiesToBeans(entities);
        fillItemOtherProperties(beans);
        logger.info("count is ={}");
        return beans;
    }

    public ServiceItemBean getItemById(Long itemId) {
        logger.info("get service item by id={}", itemId);
        ServiceItemEntity entity = itemRep.findOne(itemId);
        if (null==entity) {
            return null;
        }
        List<ServiceItemEntity> entities = new ArrayList<>();
        entities.add(entity);
        List<ServiceItemBean> beans = serviceItemEntitiesToBeans(entities);
        fillItemOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans.get(0);
    }

    public List<ServiceItemBean> getItemByIdIn(List<Long> itemIds) {
        logger.info("get service item by ids");
        List<ServiceItemEntity> entities = itemRep.findByIdIn(itemIds);
        List<ServiceItemBean> beans = serviceItemEntitiesToBeans(entities);
        fillItemOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public Map<Long, ServiceItemBean> getItemIdToBeanMap(List<Long> itemIds) {
        List<ServiceItemBean> beans = getItemByIdIn(itemIds);
        Map<Long, ServiceItemBean> itemIdToBean = new HashMap<>();
        for (ServiceItemBean bean : beans) {
            itemIdToBean.put(bean.getId(), bean);
        }
        return itemIdToBean;
    }

    private List<ServiceItemBean> serviceItemEntitiesToBeans(Iterable<ServiceItemEntity> entities) {
        List<ServiceItemBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }
        for (ServiceItemEntity entity : entities) {
            ServiceItemBean bean = itemBeanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    private List<ServiceCategoryBean> serviceCategoryEntitiesToBeans(Iterable<ServiceCategoryEntity> entities) {
        List<ServiceCategoryBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }
        for (ServiceCategoryEntity entity : entities) {
            ServiceCategoryBean bean = categoryBeanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    private void fillItemOtherProperties(List<ServiceItemBean> items) {
        if (VerifyUtil.isListEmpty(items)) {
            return;
        }

        List<Long> imagesId = new ArrayList<>();
        for (ServiceItemBean item : items) {
            if (imagesId.contains(item.getImageId())) {
                continue;
            }
            imagesId.add(item.getImageId());
        }

        Map<Long, String> imageIdToUrl = userFileStorage.getFileUrl(imagesId);
        for (ServiceItemBean item : items) {
            String imageUrl = imageIdToUrl.get(item.getImageId());
            if (VerifyUtil.isStringEmpty(imageUrl)) {
                continue;
            }
            item.setImageUrl(imageUrl);
        }
    }

    private void fillCategoryOtherProperties(List<ServiceCategoryBean> items) {
        if (VerifyUtil.isListEmpty(items)) {
            return;
        }

        List<Long> imagesId = new ArrayList<>();
        for (ServiceCategoryBean item : items) {
            if (imagesId.contains(item.getImageId())) {
                continue;
            }
            imagesId.add(item.getImageId());
        }

        Map<Long, String> imageIdToUrl = userFileStorage.getFileUrl(imagesId);
        for (ServiceCategoryBean item : items) {
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
        logger.info("delete service category by categoryIds={}", categoryIds);
        if (VerifyUtil.isListEmpty(categoryIds)) {
            return categoryIds;
        }
        List<ServiceCategoryEntity> entities = categoryRep.findByIdIn(categoryIds);
        if (VerifyUtil.isListEmpty(entities)) {
            return categoryIds;
        }

        List<Long> imagesId = new ArrayList<>();
        for (ServiceCategoryEntity entity : entities) {
            if (imagesId.contains(entity.getImageId())) {
                continue;
            }
            imagesId.add(entity.getImageId());
        }
        userFileStorage.deleteFiles(imagesId);
        categoryRep.delete(entities);
        categoryRep.setPatentIdToNone(categoryIds);

        logger.info("delete service category={}", entities);
        return categoryIds;
    }

    @Transactional
    public Long deleteItemByCategoryId(long categoryId) {
        logger.info("delete service item by categoryId={}", categoryId);
        List<ServiceItemEntity> entities = itemRep.findByCategoryId(categoryId, itemSort);
        if (VerifyUtil.isListEmpty(entities)) {
            return categoryId;
        }

        List<Long> imagesId = new ArrayList<>();
        for (ServiceItemEntity entity : entities) {
            if (imagesId.contains(entity.getImageId())) {
                continue;
            }
            imagesId.add(entity.getImageId());
        }
        userFileStorage.deleteFiles(imagesId);
        itemRep.delete(entities);

        logger.info("delete service item={}", entities);
        return categoryId;
    }

    @Transactional
    public List<Long> deleteItemByIds(List<Long> itemIds) {
        logger.info("delete service item by itemIds={}", itemIds);
        if (VerifyUtil.isListEmpty(itemIds)) {
            return itemIds;
        }
        List<ServiceItemEntity> entities = itemRep.findByIdIn(itemIds);
        if (VerifyUtil.isListEmpty(entities)) {
            return itemIds;
        }

        List<Long> imagesId = new ArrayList<>();
        for (ServiceItemEntity entity : entities) {
            if (imagesId.contains(entity.getImageId())) {
                continue;
            }
            imagesId.add(entity.getImageId());
        }
        userFileStorage.deleteFiles(imagesId);
        itemRep.delete(entities);

        logger.info("delete service item={}", entities);
        return itemIds;
    }

    //=====================================================================
    //                   updating
    //=====================================================================
    @Transactional
    public ServiceCategoryBean updateCategory(long categoryId, String name, String description, int grade, long parentId, String strStatus) {
        logger.info("update service category={} by name={} description={} grade={} parentId={} status={}",
                categoryId, name, description, grade, parentId, strStatus);

        ServiceCategoryEntity entity = categoryRep.findOne(categoryId);
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
        if (grade>=0 && grade!=entity.getGrade()) {
            entity.setGrade(grade);
            changed = true;
        }
        if (parentId>=0 && parentId!=entity.getParentId()) {
            entity.setParentId(parentId);
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

        ServiceCategoryBean bean = categoryBeanConverter.convert(entity);
        logger.info("service category updated is {}", bean);
        return bean;
    }

    @Transactional
    public ServiceCategoryBean updateCategoryImage(long categoryId, String imageName, InputStream image) {
        logger.info("update service category={} by imageName={} image={}",
                categoryId, imageName, null!=image);

        ServiceCategoryEntity entity = categoryRep.findOne(categoryId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        String imageUrl = "";
        if (null!=image) {
            if (VerifyUtil.isStringEmpty(imageName)) {
                imageName = "service_category_image_" + System.nanoTime();
            }
            long imageId = userFileStorage.addFile(entity.getImageId(), imageName, image);
            imageUrl = userFileStorage.getFileURL(imageId);
            entity.setImageId(imageId);
        }

        ServiceCategoryBean bean = categoryBeanConverter.convert(entity);
        bean.setImageUrl(imageUrl);
        logger.info("service category updated is {}", bean);
        return bean;
    }

    @Transactional
    public ServiceItemBean updateItem(long itemId, String name, String clazz, String description,
                                      String price, int timeDuration, String timeUnit,
                                      int grade, long categoryId, String strStatus) {
        logger.info("update service item={} by name={} clazz={} description={} price={} timeDuration={} timeUnit={} grade={} categoryId={} status={}",
                itemId, name, clazz, description, price, timeDuration, timeUnit, grade, categoryId, strStatus);

        ServiceItemEntity entity = itemRep.findOne(itemId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        boolean changed = false;
        if (!VerifyUtil.isStringEmpty(name)) {
            entity.setName(name.trim());
            changed = true;
        }
        ServiceClass serviceClass = ServiceClass.parseString(clazz);
        if (null!=serviceClass) {
            entity.setClazz(serviceClass);
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(description)) {
            entity.setDescription(description);
            changed = true;
        }
        BigDecimal servicePrice = NumberUtil.getDecimal(price, 2);
        if (null!=servicePrice) {
            entity.setServicePrice(servicePrice);
            changed = true;
        }
        if (timeDuration>=0 && timeDuration!=entity.getServiceTimeDuration()) {
            entity.setServiceTimeDuration(timeDuration);
            changed = true;
        }
        TimeUnit serviceTimeUnit = TimeUnit.parseString(timeUnit);
        if (null!=serviceTimeUnit) {
            entity.setServiceTimeUnit(serviceTimeUnit);
            changed = true;
        }
        if (grade>=0 && grade!=entity.getGrade()) {
            entity.setGrade(grade);
            changed = true;
        }
        if (categoryId>=0 && categoryId!=entity.getCategoryId()) {
            entity.setCategoryId(categoryId);
            changed = true;
        }
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null!=status) {
            entity.setStatus(status);
            changed = true;
        }

        if (changed) {
            entity = itemRep.save(entity);
        }

        ServiceItemBean bean = itemBeanConverter.convert(entity);
        logger.info("service item updated is {}", bean);
        return bean;
    }

    @Transactional
    public ServiceItemBean updateItemImage(long itemId, String imageName, InputStream image) {
        logger.info("update service item={} by imageName={} image={}",
                itemId, imageName, null!=image);

        ServiceItemEntity entity = itemRep.findOne(itemId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        String imageUrl = "";
        if (null!=image) {
            if (VerifyUtil.isStringEmpty(imageName)) {
                imageName = "service_item_image_" + System.nanoTime();
            }
            long imageId = userFileStorage.addFile(
                    0/*entity.getImageId();  can not delete the image, because the service order service_item json use it*/
                    , imageName, image);
            imageUrl = userFileStorage.getFileURL(imageId);
            entity.setImageId(imageId);
        }

        ServiceItemBean bean = itemBeanConverter.convert(entity);
        bean.setImageUrl(imageUrl);
        logger.info("service item updated is {}", bean);
        return bean;
    }

    //=====================================================================
    //                   adding
    //=====================================================================

    @Transactional
    public ServiceCategoryBean addCategory(String name, String description, int grade, long parentId) {
        logger.info("add service category by name={} description={} grade={} parentId={}",
                name, description, grade, parentId);
        if (VerifyUtil.isStringEmpty(name)) {
            logger.error("name is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        name = name.trim();
        ServiceCategoryEntity entity = new ServiceCategoryEntity();
        if (!VerifyUtil.isStringEmpty(description)) {
            entity.setDescription(description);
        }
        entity.setName(name);
        entity.setGrade(grade<0 ? 0 : grade);
        entity.setParentId(parentId<0 ? 0 : parentId);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());
        entity = categoryRep.save(entity);
        ServiceCategoryBean bean = categoryBeanConverter.convert(entity);
        logger.info("service category added is {}", bean);
        return bean;
    }

    @Transactional
    public ServiceItemBean addItem(String name, String clazz, String description, String price, int timeDuration, String timeUnit, int grade, long categoryId) {
        logger.info("add service item by name={} clazz={} description={} price={} timeDuration={} timeUnit={} grade={} categoryId={}",
                name, clazz, description, price, timeDuration, timeUnit, grade, categoryId);
        if (VerifyUtil.isStringEmpty(name)) {
            logger.error("name is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        name = name.trim();
        ServiceClass serviceClass = ServiceClass.parseString(clazz);
        BigDecimal servicePrice = NumberUtil.getDecimal(price, 2);
        TimeUnit serviceTimeUnit = TimeUnit.parseString(timeUnit);

        ServiceItemEntity entity = new ServiceItemEntity();
        if (!VerifyUtil.isStringEmpty(description)) {
            entity.setDescription(description);
        }
        entity.setName(name);
        entity.setClazz(serviceClass);
        entity.setServicePrice(servicePrice);
        entity.setServiceTimeDuration(timeDuration);
        entity.setServiceTimeUnit(serviceTimeUnit);
        entity.setGrade(grade<0 ? 0 : grade);
        entity.setCategoryId(categoryId<0 ? 0 : categoryId);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());
        entity = itemRep.save(entity);
        ServiceItemBean bean = itemBeanConverter.convert(entity);
        logger.info("service item added is {}", bean);
        return bean;
    }
}

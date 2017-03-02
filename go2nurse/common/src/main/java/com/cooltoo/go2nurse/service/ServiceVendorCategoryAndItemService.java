package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ManagedBy;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.ServiceCategoryBean;
import com.cooltoo.go2nurse.beans.ServiceItemBean;
import com.cooltoo.go2nurse.beans.ServiceVendorBean;
import com.cooltoo.go2nurse.constants.ServiceClass;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.constants.TimeUnit;
import com.cooltoo.go2nurse.converter.ServiceCategoryBeanConverter;
import com.cooltoo.go2nurse.converter.ServiceItemBeanConverter;
import com.cooltoo.go2nurse.converter.ServiceVendorBeanConverter;
import com.cooltoo.go2nurse.entities.ServiceCategoryEntity;
import com.cooltoo.go2nurse.entities.ServiceItemEntity;
import com.cooltoo.go2nurse.entities.ServiceVendorEntity;
import com.cooltoo.go2nurse.repository.ServiceCategoryRepository;
import com.cooltoo.go2nurse.repository.ServiceItemRepository;
import com.cooltoo.go2nurse.repository.ServiceVendorRepository;
import com.cooltoo.go2nurse.service.file.UserGo2NurseFileStorageService;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.cooltoo.services.CommonDepartmentService;
import com.cooltoo.services.CommonHospitalService;
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
import java.util.*;

/**
 * Created by hp on 2016/7/13.
 */
@Service("ServiceCategoryAndItemService")
public class ServiceVendorCategoryAndItemService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceVendorCategoryAndItemService.class);

    private static final Sort vendorSort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "id")
    );

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
    @Autowired private ServiceVendorRepository vendorRep;
    @Autowired private ServiceVendorBeanConverter vendorBeanConverter;
    @Autowired private CommonHospitalService hospitalService;
    @Autowired private CommonDepartmentService departmentService;

    @Autowired private Go2NurseUtility utility;

    //================================================================================
    //                                   Getting
    //================================================================================

    //=========================================
    //             Vendor Service
    //=========================================
    public boolean existVendor(long vendorId) {
        return vendorRep.exists(vendorId);
    }

    public ServiceVendorBean getVendorById(long vendorId) {
        logger.info("get service vendor by vendorId={}", vendorId);
        ServiceVendorEntity vendor = vendorRep.findOne(vendorId);
        if (null==vendor) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        List<ServiceVendorBean> beans = serviceVendorEntitiesToBeans(Arrays.asList(new ServiceVendorEntity[]{vendor}));
        fillVendorOtherProperties(beans);
        logger.info("count is ={}", beans.size());
        return beans.get(0);
    }

    public long countVendor(List<CommonStatus> statuses) {
        long count = 0;
        if (!VerifyUtil.isListEmpty(statuses)) {
            count = vendorRep.countByStatusIn(statuses);
        }
        logger.info("count service vendor by status={}, size is {}", statuses, count);
        return count;
    }

    public List<ServiceVendorBean> getVendor(List<CommonStatus> statuses, int pageIndex, int sizePerPage) {
        logger.info("get service vendor by status={} at page={} size={}", statuses, pageIndex, sizePerPage);
        List<ServiceVendorBean> beans;
        if (!VerifyUtil.isListEmpty(statuses)) {
            PageRequest pageRequest = new PageRequest(pageIndex, sizePerPage, vendorSort);
            Page<ServiceVendorEntity> entities = vendorRep.findByStatusIn(statuses, pageRequest);
            beans = serviceVendorEntitiesToBeans(entities);
            fillVendorOtherProperties(beans);
        }
        else {
            logger.warn("statuses is empty");
            beans = new ArrayList<>();
        }
        logger.info("count is ={}", beans.size());
        return beans;
    }

    private List<ServiceVendorBean> getVendorByIds(List<Long> vendorIds) {
        logger.info("get vendor information by vendorIds, vendors id size={}", VerifyUtil.isListEmpty(vendorIds) ? 0 : vendorIds.size());
        List<ServiceVendorEntity> resultSet = vendorRep.findAll(vendorIds);
        List<ServiceVendorBean> beans = serviceVendorEntitiesToBeans(resultSet);
        logger.info("get vendor information size={}", beans.size());
        return beans;
    }

    private Map<Long, ServiceVendorBean> getVendorIdToBeanMapByIds(List<Long> vendorIds) {
        List<ServiceVendorBean> beans  = getVendorByIds(vendorIds);
        Map<Long, ServiceVendorBean> map = new HashMap<>();
        for (ServiceVendorBean bean : beans) {
            map.put(bean.getId(), bean);
        }
        return map;
    }


    //=========================================
    //             Category Service
    //=========================================
    public boolean existCategory(long categoryId) {
        return categoryRep.exists(categoryId);
    }

    public long countTopCategory(List<CommonStatus> statuses) {
        return countCategoryByParentId(0L, statuses);
    }

    public List<ServiceCategoryBean> getTopCategory(List<CommonStatus> statuses, int pageIndex, int sizePerPage) {
        return getCategoryByParentId(0L, statuses, pageIndex, sizePerPage);
    }

    public long countCategoryByParentId(long categoryParentId, List<CommonStatus> statuses) {
        long count = 0;
        if (!VerifyUtil.isListEmpty(statuses)) {
            count = categoryRep.countByParentIdAndStatusIn(categoryParentId, statuses);
        }
        logger.info("count service category by parentId={} and status={}, size is {}", categoryParentId, statuses, count);
        return count;
    }

    public List<ServiceCategoryBean> getCategoryByParentId(long categoryParentId, List<CommonStatus> statuses) {
        logger.info("get service category by parentId={} status={}", categoryParentId, statuses);
        List<ServiceCategoryBean> beans;
        if (!VerifyUtil.isListEmpty(statuses)) {
            List<ServiceCategoryEntity> entities = categoryRep.findByParentIdAndStatusIn(categoryParentId, statuses, categorySort);
            beans = serviceCategoryEntitiesToBeans(entities);
            fillCategoryOtherProperties(beans);
        }
        else {
            logger.warn("statuses is empty");
            beans = new ArrayList<>();
        }
        logger.info("count is ={}", beans.size());
        return beans;
    }

    public List<ServiceCategoryBean> getCategoryByParentId(long categoryParentId, List<CommonStatus> statuses, int pageIndex, int sizePerPage) {
        logger.info("get service category by parentId={} status={}, at page={} size={}", categoryParentId, statuses, pageIndex, sizePerPage);
        List<ServiceCategoryBean> beans;
        if (!VerifyUtil.isListEmpty(statuses)) {
            PageRequest pageRequest = new PageRequest(pageIndex, sizePerPage, categorySort);
            Page<ServiceCategoryEntity> entities = categoryRep.findByParentIdAndStatusIn(categoryParentId, statuses, pageRequest);
            beans = serviceCategoryEntitiesToBeans(entities);
            fillCategoryOtherProperties(beans);
        }
        else {
            logger.warn("statuses is empty");
            beans = new ArrayList<>();
        }
        logger.info("count is ={}", beans.size());
        return beans;
    }

    private List<Long> getCategoryIdByParentId(long categoryParentId, List<CommonStatus> statuses) {
        logger.info("get service category Id by parentId={} status={}, at page={} size={}", categoryParentId, statuses);
        List<ServiceCategoryBean> categories = getCategoryByParentId(categoryParentId, statuses);
        List<Long> ids = new ArrayList<>();
        for (ServiceCategoryBean tmp : categories) {
            if (!ids.contains(tmp.getId())) {
                ids.add(tmp.getId());
            }
        }
        logger.info("count is ={}", ids.size());
        return ids;
    }

    public List<ServiceCategoryBean> getCategoryAndParentById(long categoryId) {
        logger.info("get service category by categoryId={}", categoryId);
        List<ServiceCategoryEntity> entities = new ArrayList<>();
        ServiceCategoryEntity entity = categoryRep.findOne(categoryId);
        if(null==entity) {
            return new ArrayList<>();
        }
        entities.add(entity);

        ServiceCategoryEntity parentEntity = categoryRep.findOne(entity.getParentId());
        if(null!=parentEntity) {
            entities.add(parentEntity);
        }

        List<ServiceCategoryBean> beans = serviceCategoryEntitiesToBeans(entities);
        fillCategoryOtherProperties(beans);
        if (beans.size()>1) {
            beans.get(0).setParent(beans.get(1));
        }
        return beans;
    }

    private Map<Long, Long> getSubCategoryIdToTopCategoryId(List<Long> subCategoryId) {
        Map<Long, Long> subToTop = new HashMap<>();
        if (VerifyUtil.isListEmpty(subCategoryId)) {
        }
        else {
            List<Object[]> subAndTop = categoryRep.findIdAndParentIdByIdIn(subCategoryId);
            for (Object[] tmp : subAndTop) {
                if (null != tmp[1]) {
                    subToTop.put((Long)tmp[0], (Long)tmp[1]);
                }
            }
        }
        logger.info("get sub category id --> top category id, count is {}", subToTop.size());
        return subToTop;
    }


    //=========================================
    //               Item Service
    //=========================================
    public boolean existItem(long itemId) {
        return itemRep.exists(itemId);
    }

    public long countItemByCategoryId(ServiceVendorType vendorType, Long vendorId, Long vendorDepartId,
                                      Long categoryId,
                                      YesNoEnum needVisitPatientRecord, YesNoEnum managerApproved,
                                      List<CommonStatus> statuses) {
        long count = 0;
        if (!VerifyUtil.isListEmpty(statuses)) {
            count = itemRep.countByConditions(vendorType, vendorId, vendorDepartId, categoryId, needVisitPatientRecord, managerApproved, statuses);
        }

        logger.info("count service item by vendorType={} vendorId={} vendorDepartId={} categoryId={} needVisitPatientRecord={} managerApproved={} statuses={}, size is {}",
                vendorType, vendorId, vendorDepartId, categoryId, needVisitPatientRecord, managerApproved, statuses, count);
        return count;
    }

    public List<ServiceItemBean> getItemByCategoryId(ServiceVendorType vendorType, Long vendorId, Long vendorDepartId,
                                                     Long categoryId,
                                                     YesNoEnum needVisitPatientRecord, YesNoEnum managerApproved,
                                                     List<CommonStatus> statuses) {
        logger.info("get service item by categoryId={} status={}", categoryId, statuses);
        List<ServiceItemBean> beans;
        if (!VerifyUtil.isListEmpty(statuses)) {
            List<Long> categoryIds = getSubCategoryId(categoryId, statuses);
            List<ServiceItemEntity> entities;
            if (null==categoryIds || categoryIds.size()<=1) {
                entities = itemRep.findByConditions(vendorType, vendorId, vendorDepartId, categoryId, needVisitPatientRecord, managerApproved, statuses, itemSort);
            }
            else {
                entities = itemRep.findByConditions(vendorType, vendorId, vendorDepartId, categoryIds, needVisitPatientRecord, managerApproved, statuses, itemSort);
            }
            beans = serviceItemEntitiesToBeans(entities);
            fillItemOtherProperties(beans);
        }
        else {
            logger.warn("statuses is empty");
            beans = new ArrayList<>();
        }

        logger.info("count is ={}", beans.size());
        return beans;
    }

    public List<ServiceItemBean> getItemByCategoryId(ServiceVendorType vendorType, Long vendorId, Long vendorDepartId,
                                                     Long categoryId,
                                                     YesNoEnum needVisitPatientRecord, YesNoEnum managerApproved,
                                                     List<CommonStatus> statuses, int pageIndex, int sizePerPage) {
        logger.info("get service item by categoryId={} status={}", categoryId, statuses);
        List<ServiceItemBean> beans;
        if (!VerifyUtil.isListEmpty(statuses)) {
            PageRequest pageRequest = new PageRequest(pageIndex, sizePerPage, itemSort);
            List<Long> categoryIds = getSubCategoryId(categoryId, statuses);
            Page<ServiceItemEntity> entities;
            if (null==categoryIds || categoryIds.size()<=1) {
                entities = itemRep.findByConditions(vendorType, vendorId, vendorDepartId, categoryId, needVisitPatientRecord, managerApproved, statuses, pageRequest);
            }
            else {
                entities = itemRep.findByConditions(vendorType, vendorId, vendorDepartId, categoryIds, needVisitPatientRecord, managerApproved, statuses, pageRequest);
            }
            beans = serviceItemEntitiesToBeans(entities);
            fillItemOtherProperties(beans);
        }
        else {
            logger.warn("statuses is empty");
            beans = new ArrayList<>();
        }

        logger.info("count is ={}", beans.size());
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
        List<ServiceItemEntity> entities = itemRep.findByIdIn(itemIds, itemSort);
        List<ServiceItemBean> beans = serviceItemEntitiesToBeans(entities);
        fillItemOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
    }


    //=========================================
    //             Common Service
    //=========================================
    private List<Long> getSubCategoryId(Long categoryId, List<CommonStatus> statuses) {
        List<Long> categoryIds = null;
        if (null!=categoryId) {
            categoryIds = getCategoryIdByParentId(categoryId, statuses);
            categoryIds.add(categoryId);
        }
        if (VerifyUtil.isListEmpty(categoryIds)) {
            categoryIds = null;
        }
        if (null!=categoryIds) {
            for (int i = 0; i < categoryIds.size(); i++) {
                if (null == categoryIds.get(i)) {
                    categoryIds.remove(i);
                    i--;
                }
            }
        }
        logger.info("get sub category id by categoryId={} status={}, count is {}",
                categoryId, statuses, VerifyUtil.isListEmpty(categoryIds) ? 0 : categoryIds.size());
        return categoryIds;
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

    private List<ServiceVendorBean> serviceVendorEntitiesToBeans(Iterable<ServiceVendorEntity> entities) {
        List<ServiceVendorBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }
        for (ServiceVendorEntity entity : entities) {
            ServiceVendorBean bean = vendorBeanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    private void fillItemOtherProperties(List<ServiceItemBean> items) {
        if (VerifyUtil.isListEmpty(items)) {
            return;
        }

        List<Long> imagesId = new ArrayList<>();
        List<Long> companyId = new ArrayList<>();
        List<Integer> hospitalsId = new ArrayList<>();
        List<Integer> hospitalDepartmentsId = new ArrayList<>();
        List<Long> categoryIds = new ArrayList<>();
        for (ServiceItemBean item : items) {
            if (!categoryIds.contains(item.getCategoryId())) {
                categoryIds.add(item.getCategoryId());
            }
            if (!imagesId.contains(item.getImageId())) {
                imagesId.add(item.getImageId());
            }
            if (!imagesId.contains(item.getDetailImageId())) {
                imagesId.add(item.getDetailImageId());
            }
            if (ServiceVendorType.COMPANY.equals(item.getVendorType()) && !companyId.contains(item.getVendorId())) {
                companyId.add(item.getVendorId());
            }
            if (ServiceVendorType.HOSPITAL.equals(item.getVendorType())) {
                if (!hospitalsId.contains(item.getVendorId())) {
                    hospitalsId.add((int) item.getVendorId());
                }
                if (!hospitalDepartmentsId.contains(item.getVendorDepartId())) {
                    hospitalDepartmentsId.add((int) item.getVendorDepartId());
                }
            }

        }

        Map<Long, String> imageIdToUrl = userFileStorage.getFileUrl(imagesId);
        Map<Long, ServiceVendorBean> vendorIdToBean = getVendorIdToBeanMapByIds(companyId);
        Map<Integer, HospitalBean> hospitalIdToBean = hospitalService.getHospitalIdToBeanMapByIds(hospitalsId);
        Map<Integer, HospitalDepartmentBean> departmentIdToBean = departmentService.getDepartmentIdToBean(hospitalDepartmentsId, utility.getHttpPrefixForNurseGo());
        Map<Long, Long> subCategoryIdToTopId = getSubCategoryIdToTopCategoryId(categoryIds);
        for (ServiceItemBean item : items) {
            String imageUrl = imageIdToUrl.get(item.getImageId());
            if (!VerifyUtil.isStringEmpty(imageUrl)) {
                item.setImageUrl(imageUrl);
            }
            imageUrl = imageIdToUrl.get(item.getDetailImageId());
            if (!VerifyUtil.isStringEmpty(imageUrl)) {
                item.setDetailImageUrl(imageUrl);
            }
            Long topCategoryId = subCategoryIdToTopId.get(item.getCategoryId());
            if (null!= topCategoryId) {
                item.setTopCategoryId(topCategoryId);
            }
            if (ServiceVendorType.COMPANY.equals(item.getVendorType())) {
                ServiceVendorBean vendor = vendorIdToBean.get(item.getVendorId());
                if (null!=vendor) {
                    item.setVendor(vendor);
                }
            }
            if (ServiceVendorType.HOSPITAL.equals(item.getVendorType())) {
                HospitalBean hospital = hospitalIdToBean.get((int)item.getVendorId());
                if (null!=hospital) {
                    item.setHospital(hospital);
                }
                HospitalDepartmentBean department = departmentIdToBean.get((int)item.getVendorDepartId());
                if (null!=department) {
                    item.setHospitalDepartment(department);
                }
            }
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

    private void fillVendorOtherProperties(List<ServiceVendorBean> items) {
        if (VerifyUtil.isListEmpty(items)) {
            return;
        }

        List<Long> imagesId = new ArrayList<>();
        for (ServiceVendorBean item : items) {
            if (imagesId.contains(item.getLogoId())) {
                continue;
            }
            imagesId.add(item.getLogoId());
        }

        Map<Long, String> imageIdToUrl = userFileStorage.getFileUrl(imagesId);
        for (ServiceVendorBean item : items) {
            String imageUrl = imageIdToUrl.get(item.getLogoId());
            if (VerifyUtil.isStringEmpty(imageUrl)) {
                continue;
            }
            item.setLogoUrl(imageUrl);
        }
    }


    //==========================================================================================
    //                                Deleting
    //==========================================================================================

    //=========================================
    //             Vendor Service
    //=========================================
    @Transactional
    public List<Long> deleteVendorByIds(List<Long> vendorIds) {
        logger.info("delete service vendor by vendorIds={}", vendorIds);
        if (VerifyUtil.isListEmpty(vendorIds)) {
            return vendorIds;
        }
        List<ServiceVendorEntity> entities = vendorRep.findAll(vendorIds);
        if (VerifyUtil.isListEmpty(entities)) {
            return vendorIds;
        }

        List<Long> imagesId = new ArrayList<>();
        for (ServiceVendorEntity entity : entities) {
            if (imagesId.contains(entity.getLogoId())) {
                continue;
            }
            imagesId.add(entity.getLogoId());
        }
        userFileStorage.deleteFiles(imagesId);
        vendorRep.delete(entities);
        itemRep.setVendorIdToNone(vendorIds);

        logger.info("delete service vendor={}", entities);
        return vendorIds;
    }

    //=========================================
    //             Category Service
    //=========================================
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
        itemRep.setCategoryIdToNone(categoryIds);

        logger.info("delete service category={}", entities);
        return categoryIds;
    }

    //=========================================
    //             Item Service
    //=========================================
    @Transactional
    public Long deleteItemByCategoryId(long categoryId) {
        logger.info("delete service item by categoryId={}", categoryId);
        List<ServiceItemEntity> entities = itemRep.findByCategoryId(categoryId);
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
        List<ServiceItemEntity> entities = itemRep.findByIdIn(itemIds, itemSort);
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

    //===================================================================================
    //                                   Updating
    //===================================================================================

    //=========================================
    //             Vendor Service
    //=========================================
    @Transactional
    public ServiceVendorBean updateVendor(long vendorId, String name, String description, String strStatus) {
        logger.info("update service vendor={} by name={} description={} status={}",
                vendorId, name, description, strStatus);

        ServiceVendorEntity entity = vendorRep.findOne(vendorId);
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
            entity = vendorRep.save(entity);
        }

        ServiceVendorBean bean = vendorBeanConverter.convert(entity);
        logger.info("service vendor updated is {}", bean);
        return bean;
    }

    @Transactional
    public ServiceVendorBean updateVendorLogoImage(long vendorId, String imageName, InputStream image) {
        logger.info("update service vendor={} by imageName={} image={}",
                vendorId, imageName, null!=image);

        ServiceVendorEntity entity = vendorRep.findOne(vendorId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        String imageUrl = "";
        if (null!=image) {
            if (VerifyUtil.isStringEmpty(imageName)) {
                imageName = "service_vendor_logo_" + System.nanoTime();
            }
            long imageId = userFileStorage.addFile(entity.getLogoId(), imageName, image);
            imageUrl = userFileStorage.getFileURL(imageId);
            entity.setLogoId(imageId);
        }

        ServiceVendorBean bean = vendorBeanConverter.convert(entity);
        bean.setLogoUrl(imageUrl);
        logger.info("service vendor updated is {}", bean);
        return bean;
    }

    //=========================================
    //             Category Service
    //=========================================
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

    //=========================================
    //             Item Service
    //=========================================
    @Transactional
    public ServiceItemBean updateItem(long itemId, String name, String clazz, String description,
                                      String price, String discount, String serverIncome, YesNoEnum needVisitPatientRecord,
                                      Integer timeDuration, String timeUnit, Integer grade,
                                      Long categoryId, String strVendorType, Long vendorId, Long vendorDepartId, ManagedBy managedBy,
                                      String strStatus,
                                      YesNoEnum needSymptoms, String symptomsItems, Long questionnaireId) {
        StringBuilder log = new StringBuilder();
        log.append("updateItem service item={} by name={} clazz={} description={} ");
        log.append("price={} discount={} serverIncome={} needVisitPatientRecord={} ");
        log.append("timeDuration={} timeUnit={} grade={} ");
        log.append("categoryId={} vendorId={} vendorType={} vendorDepartId={} ");
        log.append("strStatus={} needSymptoms={} symptomsItems={} questionnaireId={}");
        logger.info(log.toString(), itemId, name, clazz, description,
                price, discount, serverIncome, needVisitPatientRecord,
                timeDuration, timeUnit, grade,
                categoryId, vendorId, strVendorType, vendorDepartId, strStatus,
                needSymptoms, symptomsItems, questionnaireId);

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
        Integer servicePriceCent = NumberUtil.getCent(price);
        if (null!=servicePriceCent) {
            entity.setServicePriceCent(servicePriceCent);
            changed = true;
        }
        Integer serviceDiscountCent = NumberUtil.getCent(discount);
        if (null!=serviceDiscountCent) {
            entity.setServiceDiscountCent(serviceDiscountCent);
            changed = true;
        }
        Integer serverIncomeCent = NumberUtil.getCent(serverIncome);
        if (null!=serverIncomeCent) {
            entity.setServerIncomeCent(serverIncomeCent);
            changed = true;
        }
        if (null!=needVisitPatientRecord) {
            entity.setNeedVisitPatientRecord(needVisitPatientRecord);
            changed = true;
        }
        if (null!=entity) {
            int priceCent = entity.getServicePriceCent();
            int discountCent = entity.getServiceDiscountCent();
            int incomeCent = entity.getServerIncomeCent();
            YesNoEnum managerApproved = (priceCent-discountCent-incomeCent)>=0 ? YesNoEnum.YES : YesNoEnum.NO;
            entity.setManagerApproved(managerApproved);
            changed = true;
        }
        if (null!= managedBy && !managedBy.equals(entity.getManagedBy())) {
            entity.setManagedBy(managedBy);
            changed = true;
        }
        if (null!=timeDuration && timeDuration!=entity.getServiceTimeDuration()) {
            entity.setServiceTimeDuration(timeDuration);
            changed = true;
        }
        TimeUnit serviceTimeUnit = TimeUnit.parseString(timeUnit);
        if (null!=serviceTimeUnit) {
            entity.setServiceTimeUnit(serviceTimeUnit);
            changed = true;
        }
        if (null!=grade && grade!=entity.getGrade()) {
            entity.setGrade(grade);
            changed = true;
        }
        if (null!=categoryId && categoryId!=entity.getCategoryId()) {
            entity.setCategoryId(categoryId);
            changed = true;
        }
        if (null!=vendorId && vendorId!=entity.getVendorId()) {
            entity.setVendorId(vendorId);
            changed = true;
        }
        if (null!=vendorDepartId && vendorDepartId!=entity.getVendorDepartId()) {
            entity.setVendorDepartId(vendorDepartId);
            changed = true;
        }
        ServiceVendorType vendorType = ServiceVendorType.parseString(strVendorType);
        if (null!=vendorType) {
            entity.setVendorType(vendorType);
            changed = true;
        }
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null!=status) {
            entity.setStatus(status);
            changed = true;
        }
        if (null!=needSymptoms && !needSymptoms.equals(entity.getNeedSymptoms())) {
            entity.setNeedSymptoms(needSymptoms);
            changed = true;
        }
        if (null!=symptomsItems && !symptomsItems.equals(entity.getSymptomsItems())) {
            entity.setSymptomsItems(symptomsItems.trim());
            changed = true;
        }
        if (null!=questionnaireId && !questionnaireId.equals(entity.getQuestionnaireId())) {
            entity.setQuestionnaireId(questionnaireId);
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
    public ServiceItemBean updateItemManagerApproved(long itemId, YesNoEnum managerApproved) {
        logger.debug("update service item={} 's managerApproved to {}", itemId, managerApproved);
        ServiceItemEntity entity = itemRep.findOne(itemId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (null==managerApproved) {
            logger.error("managerApproved is null");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        entity.setManagerApproved(managerApproved);
        entity = itemRep.save(entity);
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

    @Transactional
    public ServiceItemBean updateItemDetailImage(long itemId, String detailImageName, InputStream detailImage) {
        logger.info("update service item={} by detailImageName={} detailImage={}",
                itemId, detailImageName, null!=detailImage);

        ServiceItemEntity entity = itemRep.findOne(itemId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        String imageUrl = "";
        if (null!=detailImage) {
            if (VerifyUtil.isStringEmpty(detailImageName)) {
                detailImageName = "item_detail_image_" + System.nanoTime();
            }
            long imageId = userFileStorage.addFile(
                    0/*entity.getImageId();  can not delete the image, because the service order service_item json use it*/
                    , detailImageName, detailImage);
            imageUrl = userFileStorage.getFileURL(imageId);
            entity.setDetailImageId(imageId);
        }

        ServiceItemBean bean = itemBeanConverter.convert(entity);
        bean.setDetailImageUrl(imageUrl);
        logger.info("service item updated is {}", bean);
        return bean;
    }

    //=====================================================================================
    //                                    Adding
    //=====================================================================================

    //=========================================
    //             Vendor Service
    //=========================================
    @Transactional
    public ServiceVendorBean addVendor(String name, String description) {
        logger.info("add service vendor by name={} description={}",
                name, description);
        if (VerifyUtil.isStringEmpty(name)) {
            logger.error("name is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        name = name.trim();
        ServiceVendorEntity entity = new ServiceVendorEntity();
        if (!VerifyUtil.isStringEmpty(description)) {
            entity.setDescription(description);
        }
        entity.setName(name);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());
        entity = vendorRep.save(entity);
        ServiceVendorBean bean = vendorBeanConverter.convert(entity);
        logger.info("service vendor added is {}", bean);
        return bean;
    }

    //=========================================
    //             Category Service
    //=========================================
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

    //=========================================
    //             Item Service
    //=========================================
    @Transactional
    public ServiceItemBean addItem(String name, String clazz, String description,
                                   String price, String discount, String serverIncome, YesNoEnum needVisitPatientRecord,
                                   int timeDuration, String timeUnit, int grade,
                                   long categoryId, String strVendorType, long vendorId, long vendorDepartId, ManagedBy managedBy,
                                   YesNoEnum needSymptoms, String symptomsItems, Long questionnaireId
    ) {
        StringBuilder log = new StringBuilder();
        log.append("add service item by name={} clazz={} description={} ");
        log.append("price={} discount={} serverIncome={} needVisitPatientRecord={} ");
        log.append("timeDuration={} timeUnit={} grade={} ");
        log.append("categoryId={} vendorId={} vendorType={} vendorDepartId={}");
        log.append("needSymptoms={} symptomsItems={} questionnaireId={}");

        logger.info(log.toString(), name, clazz, description,
                price, discount, serverIncome, needVisitPatientRecord,
                timeDuration, timeUnit, grade,
                categoryId, vendorId, strVendorType, vendorDepartId,
                needSymptoms, symptomsItems, questionnaireId);
        if (VerifyUtil.isStringEmpty(name)) {
            logger.error("name is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        name = name.trim();
        ServiceClass serviceClass = ServiceClass.parseString(clazz);
        ServiceVendorType vendorType = ServiceVendorType.parseString(strVendorType);
        Integer servicePriceCent = NumberUtil.getCent(price);
        Integer serviceDiscountCent = NumberUtil.getCent(discount);
        Integer serverIncomeCent = NumberUtil.getCent(serverIncome);
        TimeUnit serviceTimeUnit = TimeUnit.parseString(timeUnit);

        ServiceItemEntity entity = new ServiceItemEntity();
        if (!VerifyUtil.isStringEmpty(description)) {
            entity.setDescription(description);
        }
        entity.setName(name);
        entity.setClazz(serviceClass);
        entity.setServicePriceCent(null==servicePriceCent ? 0 : servicePriceCent);
        entity.setServiceDiscountCent(null==serviceDiscountCent ? 0 : serviceDiscountCent);
        entity.setServerIncomeCent(null==serverIncomeCent ? 0 : serverIncomeCent);
        entity.setNeedVisitPatientRecord(null==needVisitPatientRecord ? YesNoEnum.NO : needVisitPatientRecord);

        servicePriceCent    = null==servicePriceCent    ? 0 : servicePriceCent;
        serviceDiscountCent = null==serviceDiscountCent ? 0 : serviceDiscountCent;
        serverIncomeCent    = null==serverIncomeCent    ? 0 : serverIncomeCent;
        needSymptoms        = null==needSymptoms        ? YesNoEnum.NO : needSymptoms;
        questionnaireId     = null==questionnaireId     ? 0 : questionnaireId;

        YesNoEnum managerApproved = (servicePriceCent-serviceDiscountCent-serverIncomeCent)>=0 ? YesNoEnum.YES : YesNoEnum.NO;
        entity.setManagerApproved(managerApproved);
        entity.setManagedBy(null== managedBy ? ManagedBy.SELF : managedBy);
        entity.setServiceTimeDuration(timeDuration);
        entity.setServiceTimeUnit(serviceTimeUnit);
        entity.setGrade(grade<0 ? 0 : grade);
        entity.setCategoryId(categoryId<0 ? 0 : categoryId);
        entity.setVendorId(vendorId<0 ? 0 : vendorId);
        entity.setVendorDepartId(vendorDepartId<0 ? 0 : vendorDepartId);
        entity.setVendorType(null==vendorType ? ServiceVendorType.NONE : vendorType);
        entity.setNeedSymptoms(needSymptoms);
        entity.setSymptomsItems(symptomsItems);
        entity.setQuestionnaireId(questionnaireId);
        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());
        entity = itemRep.save(entity);
        ServiceItemBean bean = itemBeanConverter.convert(entity);
        logger.info("service item added is {}", bean);
        return bean;
    }
}

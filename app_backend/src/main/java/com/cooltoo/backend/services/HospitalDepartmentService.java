package com.cooltoo.backend.services;

import com.cooltoo.converter.HospitalDepartmentBeanConverter;
import com.cooltoo.entities.HospitalDepartmentEntity;
import com.cooltoo.repository.HospitalDepartmentRepository;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.HospitalRepository;
import com.cooltoo.services.CommonDepartmentService;
import com.cooltoo.services.file.OfficialFileStorageService;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Service("HospitalDepartmentService")
public class HospitalDepartmentService {

    private static final Logger logger = LoggerFactory.getLogger(HospitalDepartmentService.class.getName());

    @Autowired private CommonDepartmentService commonDepartmentService;
    @Autowired private HospitalRepository hospitalRepository;
    @Autowired private HospitalDepartmentRepository repository;
    @Autowired private NurseHospitalRelationService nurseRelationService;
    @Autowired
    @Qualifier("OfficialFileStorageService")
    private OfficialFileStorageService officialStorage;
    @Autowired private HospitalDepartmentBeanConverter beanConverter;

    //=======================================================
    //        get department
    //=======================================================
    public List<HospitalDepartmentBean> getDepartmentByUniqueId(String uniqueId) {
        List<HospitalDepartmentBean> departments = commonDepartmentService.getDepartmentByUniqueId(uniqueId, "");
        return departments;
    }

    public HospitalDepartmentBean getOneById(Integer id) {
        HospitalDepartmentBean bean = commonDepartmentService.getById(id, "");
        return bean;
    }

    public List<HospitalDepartmentBean> getByHospitalId(Integer hospitalId) {
        List<HospitalDepartmentBean> beans = commonDepartmentService.getByHospitalId(hospitalId, "");
        return beans;
    }

    public List<HospitalDepartmentBean> getDepartmentsByIds(List<Integer> ids) {
        List<HospitalDepartmentBean> departments = commonDepartmentService.getByIds(ids, "");
        return departments;
    }

    public List<HospitalDepartmentBean> getAllTopLevelDepartment(int hospitalId) {
        List<HospitalDepartmentBean> allTopLevels = commonDepartmentService.getTopLevel(hospitalId, false, 0, "");
        return allTopLevels;
    }

    public List<HospitalDepartmentBean> getSecondLevelDepartment(int hospitalId, int parentId) {
        List<HospitalDepartmentBean> beans = commonDepartmentService.getByParentId(hospitalId, parentId, false, 0, "");
        return beans;
    }

    //==================================================
    //                    for nurse
    //==================================================

    public List<HospitalDepartmentBean> getAllTopLevelDepartmentEnable(int hospitalId) {
        List<HospitalDepartmentBean> topLevelDepartments = getAllTopLevelDepartment(hospitalId);
        List<HospitalDepartmentBean> allTopLevelEnable   = new ArrayList<>();
        for(HospitalDepartmentBean department : topLevelDepartments) {
            if (department.getEnable() > 0) {
                allTopLevelEnable.add(department);
            }
        }
        return allTopLevelEnable;
    }

    public List<HospitalDepartmentBean> getSecondLevelDepartmentEnable(int hospitalId, int parentId) {
        List<HospitalDepartmentBean> allSecondLevelDepartment = getSecondLevelDepartment(hospitalId, parentId);
        List<HospitalDepartmentBean> allSecondLevenEnable     = new ArrayList<>();
        for(HospitalDepartmentBean department : allSecondLevelDepartment) {
            if (department.getEnable() > 0) {
                allSecondLevenEnable.add(department);
            }
        }
        return allSecondLevenEnable;
    }


    private HospitalDepartmentBean addImageUrl(HospitalDepartmentBean department) {
        if (null==department) {
            return department;
        }
        List<HospitalDepartmentBean> departmentsB = new ArrayList<>();
        departmentsB.add(department);
        addImageUrl(departmentsB);
        return departmentsB.get(0);
    }

    private List<HospitalDepartmentBean> addImageUrl(List<HospitalDepartmentBean> departments) {
        if(null==departments||departments.isEmpty()) {
            return departments;
        }

        List<Long> imageIds = new ArrayList<>();
        for (HospitalDepartmentBean department : departments) {
            imageIds.add(department.getImageId());
            imageIds.add(department.getDisableImageId());
        }
        Map<Long,String> idToPathMap = officialStorage.getFilePath(imageIds);

        String path = null;
        for (HospitalDepartmentBean department : departments) {
            path = idToPathMap.get(department.getImageId());
            if (!VerifyUtil.isStringEmpty(path)) {
                department.setImageUrl(path);
            }
            path = idToPathMap.get(department.getDisableImageId());
            if (!VerifyUtil.isStringEmpty(path)) {
                department.setDisableImageUrl(path);
            }
        }

        return departments;
    }

    //=======================================================
    //        delete department
    //=======================================================
    @Transactional
    public HospitalDepartmentBean deleteById(Integer departmentId) {
        HospitalDepartmentEntity entity = repository.findOne(departmentId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.HOSPITAL_DEPARTMENT_NOT_EXIST);
        }
        repository.delete(departmentId);

        List<Integer> ids = new ArrayList<>();
        ids.add(departmentId);
        nurseRelationService.deleteByDepartmentIds(ids);

        return beanConverter.convert(entity);
    }


    @Transactional
    public List<HospitalDepartmentBean> deleteByIds(String strDepartmentIds) {
        logger.info("delete department by department ids {}", strDepartmentIds);
        if (!VerifyUtil.isIds(strDepartmentIds)) {
            logger.warn("department ids are invalid");
            return new ArrayList<>();
        }

        List<Integer> ids = VerifyUtil.parseIntIds(strDepartmentIds);
        return deleteByIds(ids);
    }

    @Transactional
    public List<HospitalDepartmentBean> deleteByIds(List<Integer> departmentIds) {
        logger.info("delete department by department ids {}", departmentIds);
        if (null==departmentIds || departmentIds.isEmpty()) {
            return new ArrayList<>();
        }
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        List<HospitalDepartmentEntity> departments = repository.findByIdIn(departmentIds, sort);
        if (null==departments || departments.isEmpty()) {
            logger.info("delete nothing");
            return new ArrayList<>();
        }

        repository.delete(departments);
        nurseRelationService.deleteByDepartmentIds(departmentIds);

        List<HospitalDepartmentBean> retValue = new ArrayList<>();
        for (HospitalDepartmentEntity tmp : departments) {
            HospitalDepartmentBean hospital = beanConverter.convert(tmp);
            retValue.add(hospital);
        }
        return retValue;
    }

    //=======================================================
    //        update department
    //=======================================================
    @Transactional
    public HospitalDepartmentBean update(HospitalDepartmentBean bean, InputStream image, InputStream disableImage) {
        if (!repository.exists(bean.getId())) {
            throw new BadRequestException(ErrorCode.HOSPITAL_DEPARTMENT_NOT_EXIST);
        }
        HospitalDepartmentEntity entity = repository.findOne(bean.getId());

        // name
        String value = bean.getName();
        if (!VerifyUtil.isStringEmpty(value)) {
            if (!entity.getName().equals(bean.getName())) {
                List<HospitalDepartmentEntity> sameName = repository.findByName(bean.getName());
                if (!sameName.isEmpty()) {
                    throw new BadRequestException(ErrorCode.DATA_ERROR);
                }
                entity.setName(bean.getName());
            }
        }
        // parentId
        if (bean.getParentId()>0 && bean.getParentId()!=bean.getId()) {
            HospitalDepartmentEntity parent = repository.findOne(bean.getParentId());
            if (null!=parent) {
                entity.setParentId(bean.getParentId());
            }
            else {
                logger.error("department parent id=" + bean.getParentId() + " is not exist");
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
        }

        // image
        String imageUrl = null;
        if(null!=image) {
            long fileId = officialStorage.addFile(entity.getImageId(),entity.getName(), image);
            if (fileId>0) {
                entity.setImageId(fileId);
                imageUrl = officialStorage.getFilePath(fileId);
            }
        }

        // disableImage
        String disableImageUrl = null;
        if (null!=disableImage) {
            long fileId = officialStorage.addFile(entity.getDisableImageId(), entity.getName()+"_disable", disableImage);
            if (fileId>0) {
                entity.setDisableImageId(fileId);
                disableImageUrl = officialStorage.getFilePath(fileId);
            }
        }

        // enable
        int enable = bean.getEnable();
        enable = enable<0 ? enable : (enable>1 ? 1 : enable);
        if (enable>=0) {
            entity.setEnable(enable);
        }

        // description
        if (!VerifyUtil.isStringEmpty(bean.getDescription())) {
            entity.setDescription(bean.getDescription());
        }
        logger.info("update department is == " + entity);
        entity = repository.save(entity);
        bean = beanConverter.convert(entity);
        bean.setParentValid(null!=repository.findOne(bean.getParentId()));
        bean.setImageUrl(imageUrl);
        bean.setDisableImageUrl(disableImageUrl);
        return addImageUrl(bean);
    }

    @Transactional
    public HospitalDepartmentBean update(int id, String name, String description, int enable, int parentId, InputStream image, InputStream disableImage) {
        HospitalDepartmentBean bean = new HospitalDepartmentBean();
        bean.setId(id);
        bean.setName(name);
        bean.setDescription(description);
        bean.setEnable(enable);
        bean.setParentId(parentId);
        return update(bean, image, disableImage);
    }


    //=======================================================
    //        create department
    //=======================================================
    @Transactional
    public Integer createHospitalDepartment(int hospitalId, String name, String description, int enable, int parentId, InputStream image, InputStream disableImage) {
        if (!hospitalRepository.exists(hospitalId)) {
            logger.error("hospital is not exist");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (VerifyUtil.isStringEmpty(name)) {
            logger.error("new department name is empty!");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        List<HospitalDepartmentEntity> sameName = repository.findByName(name);
        if (!sameName.isEmpty()) {
            logger.error("new department name is already exist!");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        HospitalDepartmentEntity entity = new HospitalDepartmentEntity();
        entity.setHospitalId(hospitalId);
        entity.setName(name);

        String uniqueId = null;
        for (int i = 10; i>0; i--) {
            uniqueId = NumberUtil.randomIdentity();
            if (repository.countByUniqueId(uniqueId)<=0) {
                break;
            }
            else {
                uniqueId = null;
            }
        }
        if (VerifyUtil.isStringEmpty(uniqueId)) {
            logger.info("unique id generated failed");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        entity.setUniqueId(uniqueId);

        if (!VerifyUtil.isStringEmpty(description)) {
            entity.setDescription(description);
        }

        enable = enable<0 ? enable : (enable>1 ? 1 : enable);
        if (enable>=0) {
            entity.setEnable(enable);
        }
        if (parentId>0) {
            entity.setParentId(parentId);
        }
        if(null!=image) {
            try {
                long fileId = officialStorage.addFile(entity.getImageId(),entity.getName(), image);
                entity.setImageId(fileId);
            }
            catch(Exception ex) {
                //do nothing
            }
        }
        if (null!=disableImage) {
            try {
                long fileId = officialStorage.addFile(entity.getImageId(), entity.getName()+"_disable", disableImage);
                entity.setDisableImageId(fileId);
            }
            catch (Exception ex) {
                // do nothing
            }
        }

        entity = repository.save(entity);
        return entity.getId();
    }
}

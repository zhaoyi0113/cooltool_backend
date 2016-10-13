package com.cooltoo.services;

import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.converter.HospitalDepartmentBeanConverter;
import com.cooltoo.entities.HospitalDepartmentEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.HospitalDepartmentRepository;
import com.cooltoo.repository.HospitalRepository;
import com.cooltoo.services.file.OfficialFileStorageService;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Service("CommonDepartmentService")
public class CommonDepartmentService {

    private static final Logger logger = LoggerFactory.getLogger(CommonDepartmentService.class.getName());

    private static final Sort sort = new Sort(Sort.Direction.ASC, "id");

    @Autowired private HospitalDepartmentRepository repository;
    @Autowired private HospitalDepartmentBeanConverter beanConverter;
    @Autowired private HospitalRepository hospitalRepository;
    @Autowired private CommonNurseHospitalRelationService nurseRelationService;
    @Autowired
    @Qualifier("OfficialFileStorageService")
    private OfficialFileStorageService officialStorage;

    //=======================================================
    //        get
    //=======================================================
    public boolean existsDepartment(Integer departmentId) {
        return repository.exists(departmentId);
    }

    public List<HospitalDepartmentBean> getDepartmentByUniqueId(String uniqueId, String nginxPrefix) {
        List<HospitalDepartmentEntity> entities = repository.findByUniqueId(uniqueId);
        List<HospitalDepartmentBean> departments = entitiesToBeans(entities);
        fillOtherProperties(departments, nginxPrefix);
       return departments;
    }

    public HospitalDepartmentBean getById(Integer id, String nginxPrefix) {
        HospitalDepartmentBean   bean       = null;
        HospitalDepartmentEntity department = repository.findOne(id);
        if (null == department) {
            throw new BadRequestException(ErrorCode.HOSPITAL_DEPARTMENT_NOT_EXIST);
        }
        bean = beanConverter.convert(department);
        bean.setParentValid(repository.exists(bean.getParentId()));
        List<HospitalDepartmentBean> beans = Arrays.asList(new HospitalDepartmentBean[]{bean});
        fillOtherProperties(beans, nginxPrefix);
        return bean;
    }

    public List<HospitalDepartmentBean> getByIds(List<Integer> ids, String nginxPrefix) {
        if (VerifyUtil.isListEmpty(ids)) {
            return new ArrayList<>();
        }
        List<HospitalDepartmentEntity> entities = repository.findByIdIn(ids, sort);
        List<HospitalDepartmentBean> beans = entitiesToBeans(entities);
        fillOtherProperties(beans, nginxPrefix);
        return beans;
    }

    public List<HospitalDepartmentBean> getByHospitalId(Integer hospitalId, String nginxPrefix) {
        List<HospitalDepartmentEntity> entities = repository.findByHospitalId(hospitalId, sort);
        List<HospitalDepartmentBean> beans = entitiesToBeans(entities);
        fillOtherProperties(beans, nginxPrefix);
        return beans;
    }

    public List<HospitalDepartmentBean> getTopLevel(int hospitalId, boolean checkEnable, int enable, String nginxPrefix) {
        List<HospitalDepartmentEntity> entities = repository.findByHospitalId(hospitalId, sort);
        List<HospitalDepartmentBean> allDepartments = entitiesToBeans(entities);
        fillOtherProperties(allDepartments, nginxPrefix);

        List<HospitalDepartmentBean> allTopLevels = new ArrayList<>();
        List<HospitalDepartmentBean> subDepartment;
        Map<Integer, List<HospitalDepartmentBean>> id2SubDepart = new Hashtable<>();

        for(HospitalDepartmentBean department : allDepartments) {
            if (checkEnable && department.getEnable()!=enable) {
                continue;
            }
            if (department.getParentId() <= 0) {
                subDepartment = new ArrayList<>();
                allTopLevels.add(department);
                department.setSubDepartment(subDepartment);
                id2SubDepart.put(department.getId(), subDepartment);
            }
        }
        for(HospitalDepartmentBean department : allDepartments) {
            if (department.getParentId()<=0) {
                continue;
            }
            int parentId = department.getParentId();
            subDepartment = id2SubDepart.get(parentId);
            if (null==subDepartment) {
                continue;
            }
            subDepartment.add(department);
        }

        return allTopLevels;
    }

    public List<HospitalDepartmentBean> getByParentId(int hospitalId, int parentId, boolean checkEnable, int enable, String nginxPrefix) {
        List<HospitalDepartmentEntity> entities = repository.findByHospitalIdAndParentId(hospitalId, parentId, sort);
        List<HospitalDepartmentBean> allDepartments = entitiesToBeans(entities);
        fillOtherProperties(allDepartments, nginxPrefix);

        List<HospitalDepartmentBean> secondLevels = new ArrayList<>();
        for(HospitalDepartmentBean department : allDepartments) {
            if (checkEnable && department.getEnable()!=enable) {
                continue;
            }
            secondLevels.add(department);
        }
        return secondLevels;
    }

    private List<HospitalDepartmentBean> entitiesToBeans(Iterable<HospitalDepartmentEntity> entities) {
        List<HospitalDepartmentBean> beans = new ArrayList<>();
        if (null==entities) {
            return beans;
        }
        HospitalDepartmentBean bean;
        for (HospitalDepartmentEntity entity : entities) {
            bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    private void fillOtherProperties(List<HospitalDepartmentBean> beans, String nginxPrefix) {
        if(VerifyUtil.isListEmpty(beans)) {
            return;
        }
        if (VerifyUtil.isStringEmpty(nginxPrefix)) {
            nginxPrefix = "";
        }

        List<Long> imageIds = new ArrayList<>();
        for (HospitalDepartmentBean bean : beans) {
            imageIds.add(bean.getImageId());
            imageIds.add(bean.getDisableImageId());
        }
        Map<Long,String> idToPathMap = officialStorage.getFilePath(imageIds);

        String path1;
        String path2;
        for (HospitalDepartmentBean bean : beans) {
            path1 = idToPathMap.get(bean.getImageId());
            path2 = idToPathMap.get(bean.getDisableImageId());
            if (!VerifyUtil.isStringEmpty(path1)) {
                bean.setImageUrl(nginxPrefix+path1);
            }
            if (!VerifyUtil.isStringEmpty(path2)) {
                bean.setDisableImageUrl(nginxPrefix+path2);
            }
        }
    }


    //==================================================
    //                    for nurse
    //==================================================
    public List<HospitalDepartmentBean> getAllTopLevelDepartmentEnable(int hospitalId, String nginxPrefix) {
        List<HospitalDepartmentBean> topLevelDepartments = getTopLevel(hospitalId, false, 0, nginxPrefix);
        List<HospitalDepartmentBean> allTopLevelEnable   = new ArrayList<>();
        for(HospitalDepartmentBean department : topLevelDepartments) {
            if (department.getEnable() > 0) {
                allTopLevelEnable.add(department);
            }
        }
        return allTopLevelEnable;
    }

    public List<HospitalDepartmentBean> getSecondLevelDepartmentEnable(int hospitalId, int parentId, String nginxPrefix) {
        List<HospitalDepartmentBean> allSecondLevelDepartment = getByParentId(hospitalId, parentId, false, 0, nginxPrefix);
        List<HospitalDepartmentBean> allSecondLevelEnable     = new ArrayList<>();
        for(HospitalDepartmentBean department : allSecondLevelDepartment) {
            if (department.getEnable() > 0) {
                allSecondLevelEnable.add(department);
            }
        }
        return allSecondLevelEnable;
    }


    //=======================================================
    //        delete
    //=======================================================
    @Transactional
    public HospitalDepartmentBean deleteById(Integer departmentId) {
        logger.info("delete department by department id {}", departmentId);
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
    //        update
    //=======================================================
    @Transactional
    public HospitalDepartmentBean update(HospitalDepartmentBean bean, InputStream image, InputStream disableImage, String nginxPrefix) {
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
            long fileId = officialStorage.addFile(/*entity.getImageId()*/ 0, entity.getName(), image);
            if (fileId>0) {
                entity.setImageId(fileId);
                imageUrl = officialStorage.getFilePath(fileId);
            }
        }

        // disableImage
        String disableImageUrl = null;
        if (null!=disableImage) {
            long fileId = officialStorage.addFile(/*entity.getDisableImageId()*/ 0, entity.getName()+"_disable", disableImage);
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
        bean.setImageUrl(nginxPrefix+imageUrl);
        bean.setDisableImageUrl(nginxPrefix+disableImageUrl);
        return bean;
    }

    @Transactional
    public HospitalDepartmentBean update(int id, String name, String description, int enable, int parentId, InputStream image, InputStream disableImage, String nginxPrefix) {
        HospitalDepartmentBean bean = new HospitalDepartmentBean();
        bean.setId(id);
        bean.setName(name);
        bean.setDescription(description);
        bean.setEnable(enable);
        bean.setParentId(parentId);
        return update(bean, image, disableImage, nginxPrefix);
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

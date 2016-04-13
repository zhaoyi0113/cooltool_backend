package com.cooltoo.backend.services;

import com.cooltoo.backend.converter.HospitalDepartmentBeanConverter;
import com.cooltoo.backend.entities.HospitalDepartmentEntity;
import com.cooltoo.backend.repository.HospitalDepartmentRepository;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.StorageService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
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

    @Autowired
    private HospitalDepartmentRepository repository;
    @Autowired
    private HospitalDepartmentRelationService relationService;
    @Autowired
    @Qualifier("StorageService")
    private StorageService storageService;
    @Autowired
    private HospitalDepartmentBeanConverter beanConverter;

    //=======================================================
    //        get department
    //=======================================================
    public List<HospitalDepartmentBean> getAll() {
        Iterable<HospitalDepartmentEntity> departments  = repository.findAll();
        List<HospitalDepartmentBean>       departmentsB = new ArrayList<HospitalDepartmentBean>();
        HospitalDepartmentBean             bean         = null;
        List<Integer>                      topLevelIds  = new ArrayList<Integer>();
        for (HospitalDepartmentEntity department : departments) {
            bean = beanConverter.convert(department);
            departmentsB.add(bean);
            if (bean.getParentId()<=0) {
                topLevelIds.add(bean.getId());
            }
        }
        for (HospitalDepartmentBean department : departmentsB) {
            department.setParentValid(topLevelIds.contains(department.getParentId()));
        }
        addImageUrl(departmentsB);
        return departmentsB;
    }

    public HospitalDepartmentBean getOneById(Integer id) {
        HospitalDepartmentBean   bean       = null;
        HospitalDepartmentEntity department = repository.findOne(id);
        if (null == department) {
            throw new BadRequestException(ErrorCode.HOSPITAL_DEPARTMENT_NOT_EXIST);
        }
        bean = beanConverter.convert(department);
        department = repository.findOne(bean.getParentId());
        bean.setParentValid(null!=department);
        return addImageUrl(bean);
    }

    public List<HospitalDepartmentBean> getDepartmentsByIds(List<Integer> ids) {
        List<HospitalDepartmentBean> all = getAll();
        for (int i=0, count=all.size(); i<count; i++) {
            HospitalDepartmentBean bean = all.get(i);
            if (!ids.contains(bean.getId())) {
                all.remove(i);
                i --;
                count --;
            }
        }
        return all;
    }

    public List<HospitalDepartmentBean> getAllTopLevelDepartment() {
        List<HospitalDepartmentBean>               allDepartments = getAll();
        List<HospitalDepartmentBean>               allTopLevels   = new ArrayList<HospitalDepartmentBean>();
        List<HospitalDepartmentBean>               subDepartment  = null;
        Map<Integer, List<HospitalDepartmentBean>> id2SubDepart   = new Hashtable<Integer, List<HospitalDepartmentBean>>();

        for(HospitalDepartmentBean department : allDepartments) {
            if (department.getParentId() <= 0) {
                allTopLevels.add(department);
                subDepartment = new ArrayList<HospitalDepartmentBean>();
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

        for (HospitalDepartmentBean department : allTopLevels) {
            int topLevelId = department.getId();
            subDepartment  = id2SubDepart.get(topLevelId);
            department.setSubDepartment(subDepartment);
        }

        return allTopLevels;
    }

    public List<HospitalDepartmentBean> getSecondLevelDepartment(int parentId) {
        List<HospitalDepartmentBean> allDepartments = getAll();
        List<HospitalDepartmentBean> secondLevels   = new ArrayList<HospitalDepartmentBean>();
        for(HospitalDepartmentBean department : allDepartments) {
            if (department.getParentId() == parentId) {
                secondLevels.add(department);
            }
        }
        return secondLevels;
    }

    public List<HospitalDepartmentBean> getAllTopLevelDepartmentEnable() {
        List<HospitalDepartmentBean> topLevelDepartments = getAllTopLevelDepartment();
        List<HospitalDepartmentBean> allTopLevelEnable   = new ArrayList<HospitalDepartmentBean>();
        for(HospitalDepartmentBean department : topLevelDepartments) {
            if (department.getEnable() > 0) {
                allTopLevelEnable.add(department);
            }
        }
        return allTopLevelEnable;
    }

    public List<HospitalDepartmentBean> getSecondLevelDepartmentEnable(int parentId) {
        List<HospitalDepartmentBean> allSecondLevelDepartment = getSecondLevelDepartment(parentId);
        List<HospitalDepartmentBean> allSecondLevenEnable     = new ArrayList<HospitalDepartmentBean>();
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
        List<HospitalDepartmentBean> departmentsB = new ArrayList<HospitalDepartmentBean>();
        departmentsB.add(department);
        addImageUrl(departmentsB);
        return departmentsB.get(0);
    }

    private List<HospitalDepartmentBean> addImageUrl(List<HospitalDepartmentBean> departments) {
        if(null==departments||departments.isEmpty()) {
            return departments;
        }

        List<Long> imageIds = new ArrayList<Long>();
        for (HospitalDepartmentBean department : departments) {
            imageIds.add(department.getImageId());
            imageIds.add(department.getDisableImageId());
        }
        Map<Long,String> idToPathMap = storageService.getFilePath(imageIds);

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
    public HospitalDepartmentBean deleteById(Integer id) {
        HospitalDepartmentEntity entity = repository.findOne(id);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.HOSPITAL_DEPARTMENT_NOT_EXIST);
        }
        repository.delete(id);
        return beanConverter.convert(entity);
    }


    @Transactional
    public List<HospitalDepartmentBean> deleteByIds(String strDepartmentIds) {
        logger.info("delete department by department ids {}", strDepartmentIds);
        if (!VerifyUtil.isIds(strDepartmentIds)) {
            logger.warn("department ids are invalid");
            return new ArrayList<>();
        }

        List<Integer> ids       = new ArrayList<>();
        String[]      strArrIds = strDepartmentIds.split(",");
        for (String tmp : strArrIds) {
            Integer id = Integer.parseInt(tmp);
            ids.add(id);
        }

        return deleteByIds(ids);
    }

    @Transactional
    public List<HospitalDepartmentBean> deleteByIds(List<Integer> departmentIds) {
        logger.info("delete department by department ids {}", departmentIds);
        if (null==departmentIds || departmentIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<HospitalDepartmentEntity> departments = repository.findByIdIn(departmentIds);
        if (null==departments || departments.isEmpty()) {
            logger.info("delete nothing");
            return new ArrayList<>();
        }

        repository.delete(departments);
        relationService.deleteByDepartmentIds(departmentIds);

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

        if(null!=image) {
            try {
                long fileId = storageService.saveFile(entity.getImageId(),entity.getName(), image);
                entity.setImageId(fileId);
            }
            catch(Exception ex) {
                //do nothing
            }
        }
        if (null!=disableImage) {
            try {
                long fileId = storageService.saveFile(entity.getDisableImageId(), entity.getName()+"_disable", disableImage);
                entity.setDisableImageId(fileId);
            }
            catch (Exception ex) {
                // do nothing
            }
        }
        int enable = bean.getEnable();
        enable = enable<0 ? enable : (enable>1 ? 1 : enable);
        if (enable>=0) {
            entity.setEnable(enable);
        }
        if (!VerifyUtil.isStringEmpty(bean.getDescription())) {
            entity.setDescription(bean.getDescription());
        }
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
        logger.info("update department is == " + entity);
        entity = repository.save(entity);
        bean = beanConverter.convert(entity);
        bean.setParentValid(null!=repository.findOne(bean.getParentId()));
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
    public Integer createHospitalDepartment(String name, String description, int enable, int parentId, InputStream image, InputStream disableImage) {
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
        entity.setName(name);

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
                long fileId = storageService.saveFile(entity.getImageId(),entity.getName(), image);
                entity.setImageId(fileId);
            }
            catch(Exception ex) {
                //do nothing
            }
        }
        if (null!=disableImage) {
            try {
                long fileId = storageService.saveFile(entity.getImageId(), entity.getName()+"_disable", disableImage);
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

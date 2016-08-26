package com.cooltoo.services;

import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.converter.HospitalDepartmentBeanConverter;
import com.cooltoo.entities.HospitalDepartmentEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.HospitalDepartmentRepository;
import com.cooltoo.services.file.OfficialFileStorageService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Service("CommonDepartmentService")
public class CommonDepartmentService {

    private static final Sort sort = new Sort(Sort.Direction.ASC, "id");

    @Autowired private HospitalDepartmentRepository repository;
    @Autowired private HospitalDepartmentBeanConverter beanConverter;
    @Autowired
    @Qualifier("OfficialFileStorageService")
    private OfficialFileStorageService officialStorage;

    //=======================================================
    //        get department
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
}

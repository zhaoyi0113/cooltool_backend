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

    private static final Logger logger = LoggerFactory.getLogger(CommonDepartmentService.class.getName());

    private static final Sort sort = new Sort(Sort.Direction.ASC, "id");

    @Autowired private HospitalDepartmentRepository repository;
    @Autowired private HospitalDepartmentBeanConverter beanConverter;
    @Autowired
    @Qualifier("OfficialFileStorageService")
    private OfficialFileStorageService officialStorage;

    //=======================================================
    //        get department
    //=======================================================
    public List<HospitalDepartmentBean> getAll(String nginxPrefix) {
        Sort sort = new Sort(Sort.Direction.ASC, "id");
        Iterable<HospitalDepartmentEntity> resultSet  = repository.findAll(sort);
        List<HospitalDepartmentBean> beans = entitiesToBeans(resultSet);
        List<Integer> topLevelIds  = new ArrayList<>();
        for (HospitalDepartmentBean bean : beans) {
            if (bean.getParentId()<=0) {
                topLevelIds.add(bean.getId());
            }
        }
        for (HospitalDepartmentBean bean : beans) {
            bean.setParentValid(topLevelIds.contains(bean.getParentId()));
        }
        fillOtherProperties(beans, nginxPrefix);
        return beans;
    }

    public List<HospitalDepartmentBean> getDepartmentByUniqueId(String uniqueId, String nginxPrefix) {
        List<HospitalDepartmentBean> all = getAll(nginxPrefix);
        List<HospitalDepartmentBean> departments = new ArrayList<>();
        for (int i=0, count=all.size(); i<count; i++) {
            HospitalDepartmentBean bean = all.get(i);
            if (bean.getUniqueId().equals(uniqueId)) {
                departments.add(bean);
            }
        }
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
        List<HospitalDepartmentBean> retVal = new ArrayList<>();
        if (VerifyUtil.isListEmpty(ids)) {
            return retVal;
        }
        List<HospitalDepartmentBean> beans = getAll(nginxPrefix);
        for (HospitalDepartmentBean bean : beans) {
            if (ids.contains(bean.getId())) {
                retVal.add(bean);
            }
        }
        return retVal;
    }

    public List<HospitalDepartmentBean> getTopLevel(boolean checkEnable, int enable, String nginxPrefix) {
        List<HospitalDepartmentBean> allDepartments = getAll(nginxPrefix);
        List<HospitalDepartmentBean> allTopLevels = new ArrayList<>();
        List<HospitalDepartmentBean> subDepartment;
        Map<Integer, List<HospitalDepartmentBean>> id2SubDepart = new Hashtable<>();

        for(HospitalDepartmentBean department : allDepartments) {
            if (checkEnable && department.getEnable()!=enable) {
                continue;
            }
            if (department.getParentId() <= 0) {
                subDepartment = new ArrayList<HospitalDepartmentBean>();
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

    public List<HospitalDepartmentBean> getByParentId(int parentId, boolean checkEnable, int enable, String nginxPrefix) {
        List<HospitalDepartmentBean> allDepartments = getAll(nginxPrefix);
        List<HospitalDepartmentBean> secondLevels = new ArrayList<>();
        for(HospitalDepartmentBean department : allDepartments) {
            if (checkEnable && department.getEnable()!=enable) {
                continue;
            }
            if (department.getParentId() == parentId) {
                secondLevels.add(department);
            }
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

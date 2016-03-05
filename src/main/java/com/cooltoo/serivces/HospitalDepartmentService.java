package com.cooltoo.serivces;

import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.converter.HospitalDepartmentBeanConverter;
import com.cooltoo.converter.HospitalDepartmentEntityConverter;
import com.cooltoo.entities.HospitalDepartmentEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.HospitalDepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Service("HospitalDepartmentService")
public class HospitalDepartmentService {

    @Autowired
    private HospitalDepartmentRepository repository;
    @Autowired
    private HospitalDepartmentBeanConverter beanConverter;
    @Autowired
    private HospitalDepartmentEntityConverter entityConverter;

    public List<HospitalDepartmentBean> getAll() {
        Iterable<HospitalDepartmentEntity> iterable = repository.findAll();
        List<HospitalDepartmentBean> all = new ArrayList<HospitalDepartmentBean>();
        for (HospitalDepartmentEntity entity : iterable) {
            HospitalDepartmentBean bean = beanConverter.convert(entity);
            all.add(bean);
        }
        return all;
    }

    public HospitalDepartmentBean getOneById(Integer id) {
        HospitalDepartmentEntity entity = repository.findOne(id);
        if (null == entity) {
            return null;
        }
        return beanConverter.convert(entity);
    }

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
    public HospitalDepartmentBean update(HospitalDepartmentBean bean) {
        if (!repository.exists(bean.getId())) {
            throw new BadRequestException(ErrorCode.HOSPITAL_DEPARTMENT_NOT_EXIST);
        }
        String value = bean.getName();
        if (null==value || "".equals(value)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        HospitalDepartmentEntity entity = repository.findOne(bean.getId());
        if (!value.equals(entity.getName())) {
            entity.setName(value);
        }
        entity = repository.save(entity);
        return beanConverter.convert(entity);
    }

    @Transactional
    public HospitalDepartmentBean update(int id, String name) {
        HospitalDepartmentBean bean = new HospitalDepartmentBean();
        bean.setId(id);
        bean.setName(name);
        return update(bean);
    }

    @Transactional
    public Integer newOne(HospitalDepartmentBean bean) {
        String value = bean.getName();
        if (null==value || "".equals(value)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        HospitalDepartmentEntity entity = entityConverter.convert(bean);
        entity = repository.save(entity);
        return entity.getId();
    }

    @Transactional
    public Integer newOne(String name) {
        HospitalDepartmentBean bean = new HospitalDepartmentBean();
        bean.setName(name);
        return newOne(bean);
    }
}

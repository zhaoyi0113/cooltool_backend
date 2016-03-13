package com.cooltoo.serivces;

import com.cooltoo.beans.HospitalDepartmentRelationBean;
import com.cooltoo.converter.HospitalDepartmentRelationBeanConverter;
import com.cooltoo.converter.HospitalDepartmentRelationEntityConverter;
import com.cooltoo.entities.HospitalDepartmentRelationEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.HospitalDepartmentRelationRepository;
import com.cooltoo.repository.HospitalDepartmentRepository;
import com.cooltoo.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Service("HospitalDepartmentRelationService")
public class HospitalDepartmentRelationService {

    @Autowired
    private HospitalDepartmentRelationRepository repository;
    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    private HospitalDepartmentRepository departmentRepository;
    @Autowired
    private HospitalDepartmentRelationBeanConverter beanConverter;
    @Autowired
    private HospitalDepartmentRelationEntityConverter entityConverter;

    public List<HospitalDepartmentRelationBean> getAll() {
        Iterable<HospitalDepartmentRelationEntity> iterable = repository.findAll();
        List<HospitalDepartmentRelationBean> all = new ArrayList<HospitalDepartmentRelationBean>();
        for (HospitalDepartmentRelationEntity entity : iterable) {
            HospitalDepartmentRelationBean bean = beanConverter.convert(entity);
            all.add(bean);
        }
        return all;
    }

    public HospitalDepartmentRelationBean getOneById(Integer id) {
        HospitalDepartmentRelationEntity entity = repository.findOne(id);
        if (null == entity) {
            return null;
        }
        return beanConverter.convert(entity);
    }

    @Transactional
    public HospitalDepartmentRelationBean deleteById(Integer id) {
        HospitalDepartmentRelationEntity entity = repository.findOne(id);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        repository.delete(id);
        return beanConverter.convert(entity);
    }

    @Transactional
    public HospitalDepartmentRelationBean update(HospitalDepartmentRelationBean bean) {
        if (!repository.exists(bean.getId())) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!hospitalRepository.exists(bean.getHospitalId())) {
            throw new BadRequestException(ErrorCode.HOSPITAL_NOT_EXIST);
        }
        if (!departmentRepository.exists(bean.getDepartmentId())) {
            throw new BadRequestException(ErrorCode.HOSPITAL_DEPARTMENT_NOT_EXIST);
        }
        HospitalDepartmentRelationEntity entity = entityConverter.convert(bean);
        entity = repository.save(entity);
        return beanConverter.convert(entity);
    }

    @Transactional
    public HospitalDepartmentRelationBean update(int id, int hospitalId, int departmentId) {
        HospitalDepartmentRelationBean bean = new HospitalDepartmentRelationBean();
        bean.setId(id);
        bean.setHospitalId(hospitalId);
        bean.setDepartmentId(departmentId);
        return update(bean);
    }

    @Transactional
    public Integer newOne(HospitalDepartmentRelationBean bean) {
        if (!hospitalRepository.exists(bean.getHospitalId())) {
            throw new BadRequestException(ErrorCode.HOSPITAL_NOT_EXIST);
        }
        if (!departmentRepository.exists(bean.getDepartmentId())) {
            throw new BadRequestException(ErrorCode.HOSPITAL_DEPARTMENT_NOT_EXIST);
        }
        HospitalDepartmentRelationEntity entity = entityConverter.convert(bean);
        entity = repository.save(entity);
        return entity.getId();
    }

    @Transactional
    public Integer newOne(int hospitalId, int departmentId) {
        HospitalDepartmentRelationBean bean = new HospitalDepartmentRelationBean();
        bean.setHospitalId(hospitalId);
        bean.setDepartmentId(departmentId);
        return newOne(bean);
    }
}

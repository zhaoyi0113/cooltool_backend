package com.cooltoo.serivces;

import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.converter.NurseHospitalRelationBeanConverter;
import com.cooltoo.converter.NurseHospitalRelationEntityConverter;
import com.cooltoo.entities.NurseHospitalRelationEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.HospitalDepartmentRepository;
import com.cooltoo.repository.HospitalRepository;
import com.cooltoo.repository.NurseHospitalRelationRepository;
import com.cooltoo.repository.NurseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Service("NurseHospitalRelationService")
public class NurseHospitalRelationService {

    @Autowired
    private NurseHospitalRelationRepository repository;
    @Autowired
    private NurseRepository nurseRepository;
    @Autowired
    private HospitalRepository hospitalRepository;
    @Autowired
    private HospitalDepartmentRepository departmentRepository;
    @Autowired
    private NurseHospitalRelationBeanConverter beanConverter;
    @Autowired
    private NurseHospitalRelationEntityConverter entityConverter;

    public List<NurseHospitalRelationBean> getAll() {
        Iterable<NurseHospitalRelationEntity> iterable = repository.findAll();
        List<NurseHospitalRelationBean> all = new ArrayList<NurseHospitalRelationBean>();
        for (NurseHospitalRelationEntity entity : iterable) {
            NurseHospitalRelationBean bean = beanConverter.convert(entity);
            all.add(bean);
        }
        return all;
    }

    public NurseHospitalRelationBean getOneById(Long id) {
        NurseHospitalRelationEntity entity = repository.findOne(id);
        if (null == entity) {
            return null;
        }
        return beanConverter.convert(entity);
    }

    @Transactional
    public NurseHospitalRelationBean deleteById(Long id) {
        NurseHospitalRelationEntity entity = repository.findOne(id);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        repository.delete(id);
        return beanConverter.convert(entity);
    }

    @Transactional
    public NurseHospitalRelationBean update(NurseHospitalRelationBean bean) {
        if (!repository.exists(bean.getId())) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!nurseRepository.exists(bean.getNurseId())) {
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
        }
        if (!hospitalRepository.exists(bean.getHospitalId())) {
            throw new BadRequestException(ErrorCode.HOSPITAL_NOT_EXIST);
        }
        if (!departmentRepository.exists(bean.getDepartmentId())) {
            throw new BadRequestException(ErrorCode.HOSPITAL_DEPARTMENT_NOT_EXIST);
        }
        NurseHospitalRelationEntity entity = entityConverter.convert(bean);
        entity = repository.save(entity);
        return beanConverter.convert(entity);
    }

    @Transactional
    public NurseHospitalRelationBean update(long id, long nurseId, int hospitalId, int departmentId) {
        NurseHospitalRelationBean bean = new NurseHospitalRelationBean();
        bean.setId(id);
        bean.setNurseId(nurseId);
        bean.setHospitalId(hospitalId);
        bean.setDepartmentId(departmentId);
        return update(bean);
    }

    @Transactional
    public Long newOne(NurseHospitalRelationBean bean) {
        if (!nurseRepository.exists(bean.getNurseId())) {
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
        }
        if (!hospitalRepository.exists(bean.getHospitalId())) {
            throw new BadRequestException(ErrorCode.HOSPITAL_NOT_EXIST);
        }
        if (!departmentRepository.exists(bean.getDepartmentId())) {
            throw new BadRequestException(ErrorCode.NURSE_NOT_EXIST);
        }
        NurseHospitalRelationEntity entity = entityConverter.convert(bean);
        entity = repository.save(entity);
        return entity.getId();
    }

    @Transactional
    public Long newOne(long nurseId, int hospitalId, int departmentId) {
        NurseHospitalRelationBean bean = new NurseHospitalRelationBean();
        bean.setNurseId(nurseId);
        bean.setHospitalId(hospitalId);
        bean.setDepartmentId(departmentId);
        return newOne(bean);
    }
}

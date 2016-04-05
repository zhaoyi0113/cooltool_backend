package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseBean;
import com.cooltoo.backend.converter.NurseHospitalRelationBeanConverter;
import com.cooltoo.backend.converter.NurseHospitalRelationEntityConverter;
import com.cooltoo.backend.entities.NurseHospitalRelationEntity;
import com.cooltoo.backend.repository.HospitalDepartmentRepository;
import com.cooltoo.backend.repository.HospitalRepository;
import com.cooltoo.backend.repository.NurseHospitalRelationRepository;
import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.backend.repository.NurseRepository;
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
    private NurseService nurseService;
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private HospitalDepartmentService departmentService;
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

    public NurseHospitalRelationBean getRelationByNurseId(Long nurseId) {
        NurseHospitalRelationBean relation = null;
        HospitalBean              hospital     = null;
        HospitalDepartmentBean    department   = null;
        HospitalDepartmentBean    parentDepart = null;

        List<NurseHospitalRelationEntity> relations = repository.findByNurseId(nurseId);
        if (null==relations || relations.isEmpty()) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (relations.size()>1) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        relation = beanConverter.convert(relations.get(0));
        if (relation.getHospitalId()>0) {
            hospital = hospitalService.getOneById(relation.getHospitalId());
            relation.setHospital(hospital);
        }
        if (relation.getDepartmentId()>0) {
            department = departmentService.getOneById(relation.getDepartmentId());
            relation.setDepartment(department);
            if (department.getParentValid()) {
                parentDepart = departmentService.getOneById(relation.getDepartmentId());
                relation.setParentDepart(parentDepart);
            }
        }

        return relation;
    }

    @Transactional
    public Long newOne(NurseHospitalRelationBean bean) {
        HospitalBean                hospital     = null;
        HospitalDepartmentBean      department   = null;
        HospitalDepartmentBean      parentDepart = null;
        NurseHospitalRelationEntity relation     = null;
        List<NurseHospitalRelationEntity> relations = repository.findByNurseId(bean.getNurseId());
        if (!relations.isEmpty()) {
            relation = relations.get(0);
        }

        nurseService.getNurse(bean.getNurseId());
        if (bean.getHospitalId()>0) {
            hospitalService.getOneById(bean.getHospitalId());
        }
        if (bean.getDepartmentId()>0) {
            department = departmentService.getOneById(bean.getDepartmentId());
            if (department.getParentValid()) {
                parentDepart = departmentService.getOneById(bean.getDepartmentId());
            }
        }

        if (null!=relation) {
            if (relation.getDepartmentId()>0 && bean.getDepartmentId()<=0) {
                bean.setDepartmentId(relation.getDepartmentId());
            }
            if (relation.getHospitalId()>0 && bean.getHospitalId()<=0) {
                bean.setHospitalId(relation.getHospitalId());
            }
        }

        if (!relations.isEmpty()) {
            repository.delete(relations);
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

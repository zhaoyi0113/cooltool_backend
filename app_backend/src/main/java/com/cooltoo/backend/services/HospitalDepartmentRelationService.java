package com.cooltoo.backend.services;

import com.cooltoo.backend.converter.HospitalDepartmentRelationBeanConverter;
import com.cooltoo.backend.converter.HospitalDepartmentRelationEntityConverter;
import com.cooltoo.backend.entities.HospitalDepartmentRelationEntity;
import com.cooltoo.backend.repository.HospitalDepartmentRelationRepository;
import com.cooltoo.backend.repository.HospitalDepartmentRepository;
import com.cooltoo.backend.repository.HospitalRepository;
import com.cooltoo.beans.HospitalDepartmentRelationBean;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(HospitalDepartmentRelationService.class.getName());

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

    public List<HospitalDepartmentRelationBean> getRelationByHospitalId(int hospitalId) {
        Iterable<HospitalDepartmentRelationEntity> iterable = repository.findRelationByHospitalId(hospitalId);
        List<HospitalDepartmentRelationBean> all = new ArrayList<HospitalDepartmentRelationBean>();
        for (HospitalDepartmentRelationEntity entity : iterable) {
            HospitalDepartmentRelationBean bean = beanConverter.convert(entity);
            all.add(bean);
        }
        return all;
    }

    public List<HospitalDepartmentRelationBean> getRelationByDepartmentId(int departmentId) {
        Iterable<HospitalDepartmentRelationEntity> iterable = repository.findRelationByDepartmentId(departmentId);
        List<HospitalDepartmentRelationBean> all = new ArrayList<HospitalDepartmentRelationBean>();
        for (HospitalDepartmentRelationEntity entity : iterable) {
            HospitalDepartmentRelationBean bean = beanConverter.convert(entity);
            all.add(bean);
        }
        return all;
    }

    //================================================================
    //            add
    //================================================================

    @Transactional
    public HospitalDepartmentRelationBean deleteById(Integer id) {
        logger.info("delete nurse hospital department relation ship by relation id {}", id);
        HospitalDepartmentRelationEntity entity = repository.findOne(id);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        repository.delete(id);
        return beanConverter.convert(entity);
    }

    @Transactional
    public String deleteByHospitalIds(String strHospitalIds) {
        logger.info("delete nurse hospital department relation ship by hospital ids {}", strHospitalIds);
        if (!VerifyUtil.isIds(strHospitalIds)) {
            logger.warn("hospital ids are invalid");
            return strHospitalIds;
        }
        List<Integer> hospitalIds = new ArrayList<>();
        String[]      strArrIds   = strHospitalIds.split(",");
        for (String tmp : strArrIds) {
            Integer id = Integer.parseInt(tmp);
            hospitalIds.add(id);
        }

        deleteByHospitalIds(hospitalIds);
        return strHospitalIds;
    }

    @Transactional
    public List<Integer> deleteByHospitalIds(List<Integer> hospitalIds) {
        logger.info("delete nurse hospital department relation ship by hospital ids {}", hospitalIds);
        if (null==hospitalIds || hospitalIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<HospitalDepartmentRelationEntity> relations = repository.findRelationByHospitalIdIn(hospitalIds);
        if (null!=relations && !relations.isEmpty()) {
            repository.delete(relations);
            return hospitalIds;
        }
        else {
            logger.info("delete nothing");
            return new ArrayList<>();
        }
    }

    @Transactional
    public String deleteByDepartmentIds(String strDepartmentIds) {
        logger.info("delete nurse hospital department relation ship by department ids {}", strDepartmentIds);
        if (!VerifyUtil.isIds(strDepartmentIds)) {
            logger.warn("hospital ids are invalid");
            return strDepartmentIds;
        }
        List<Integer> departmentIds = new ArrayList<>();
        String[]      strArrIds     = strDepartmentIds.split(",");
        for (String tmp : strArrIds) {
            Integer id = Integer.parseInt(tmp);
            departmentIds.add(id);
        }

        deleteByDepartmentIds(departmentIds);
        return strDepartmentIds;
    }

    @Transactional
    public List<Integer> deleteByDepartmentIds(List<Integer> departmentIds) {
        logger.info("delete nurse hospital department relation ship by department ids {}", departmentIds);
        if (null==departmentIds || departmentIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<HospitalDepartmentRelationEntity> relations = repository.findRelationByDepartmentIdIn(departmentIds);
        if (null!=relations && !relations.isEmpty()) {
            repository.delete(relations);
            return departmentIds;
        }
        else {
            logger.info("delete nothing");
            return new ArrayList<>();
        }
    }

    //================================================================
    //            update
    //================================================================

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

    //================================================================
    //            add
    //================================================================

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

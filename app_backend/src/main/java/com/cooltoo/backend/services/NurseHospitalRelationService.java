package com.cooltoo.backend.services;

import com.cooltoo.backend.converter.NurseHospitalRelationBeanConverter;
import com.cooltoo.backend.converter.NurseHospitalRelationEntityConverter;
import com.cooltoo.backend.entities.NurseHospitalRelationEntity;
import com.cooltoo.backend.repository.NurseHospitalRelationRepository;
import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Service("NurseHospitalRelationService")
public class NurseHospitalRelationService {

    private static final Logger logger = LoggerFactory.getLogger(NurseHospitalRelationService.class.getName());

    @Autowired private NurseHospitalRelationRepository    repository;
    @Autowired private NurseService                       nurseService;
    @Autowired private HospitalService                    hospitalService;
    @Autowired private HospitalDepartmentService          departmentService;
    @Autowired private NurseHospitalRelationBeanConverter beanConverter;
    @Autowired private NurseHospitalRelationEntityConverter entityConverter;

    //====================================================================
    //             get
    //====================================================================
    public List<NurseHospitalRelationBean> getAll() {
        logger.info("get all nurse-hospital-department relationship information");
        Iterable<NurseHospitalRelationEntity> iterable = repository.findAll();
        List<NurseHospitalRelationBean> all = new ArrayList<NurseHospitalRelationBean>();
        for (NurseHospitalRelationEntity entity : iterable) {
            NurseHospitalRelationBean bean = beanConverter.convert(entity);
            all.add(bean);
        }
        return all;
    }

    public NurseHospitalRelationBean getOneById(Long id) {
        logger.info("get one nurse-hospital-department relationship information by id={}", id);
        NurseHospitalRelationEntity entity = repository.findOne(id);
        if (null == entity) {
            return null;
        }
        return beanConverter.convert(entity);
    }

    public NurseHospitalRelationBean getRelationByNurseId(Long nurseId) {
        logger.info("get one nurse-hospital-department relationship information by nurseId={}", nurseId);

        List<NurseHospitalRelationEntity> resultSert = repository.findByNurseId(nurseId);
        if (null==resultSert || resultSert.isEmpty()) {
            return null;
        }
        if (resultSert.size()>1) {
            return null;
        }

        List<NurseHospitalRelationBean> relations = new ArrayList<>();
        for (NurseHospitalRelationEntity result : resultSert) {
            NurseHospitalRelationBean bean = beanConverter.convert(result);
            relations.add(bean);
        }

        fillOtherProperties(relations);
        return relations.get(0);
    }

    public List<NurseHospitalRelationBean> getRelationByNurseIds(List<Long> nurseIds) {
        logger.info("get one nurse-hospital-department relationship information by nurseIds={}", nurseIds);

        List<NurseHospitalRelationEntity> resultSert = repository.findByNurseIdIn(nurseIds);
        if (null==resultSert || resultSert.isEmpty()) {
            return new ArrayList<>();
        }

        List<NurseHospitalRelationBean> relations = new ArrayList<>();
        for (NurseHospitalRelationEntity result : resultSert) {
            NurseHospitalRelationBean bean = beanConverter.convert(result);
            relations.add(bean);
        }

        fillOtherProperties(relations);
        return relations;
    }

    public Map<Long, NurseHospitalRelationBean> getRelationMapByNurseIds(List<Long> nurseIds) {
        logger.info("get one nurse-hospital-department relationship information by nurseIds={}", nurseIds);

        Map<Long, NurseHospitalRelationBean> userId2Hospital = new HashMap<>();
        List<NurseHospitalRelationBean>      relations       = getRelationByNurseIds(nurseIds);
        for (NurseHospitalRelationBean result : relations) {
            userId2Hospital.put(result.getNurseId(), result);
        }
        return userId2Hospital;
    }

    private void fillOtherProperties(List<NurseHospitalRelationBean> relationBeans) {
        if (null==relationBeans || relationBeans.isEmpty()) {
            return;
        }
        List<Integer> hospitalIds   = new ArrayList<>();
        List<Integer> departmentIds = new ArrayList<>();
        for (NurseHospitalRelationBean bean : relationBeans) {
            if (bean.getHospitalId()>0) {
                hospitalIds.add(bean.getHospitalId());
            }
            if (bean.getDepartmentId()>0) {
                departmentIds.add(bean.getDepartmentId());
            }
        }

        // get hospital
        List<HospitalBean>                  hospitals    = hospitalService.getHospitalByIds(hospitalIds);
        Map<Integer, HospitalBean>          hospId2Bean  = new HashMap<>();
        for (HospitalBean bean : hospitals) {
            hospId2Bean.put(bean.getId(), bean);
        }
        // get department
        List<HospitalDepartmentBean>         departmentes = departmentService.getDepartmentsByIds(departmentIds);
        Map<Integer, HospitalDepartmentBean> deptId2Bean  = new HashMap<>();
        for (HospitalDepartmentBean bean : departmentes) {
            deptId2Bean.put(bean.getId(), bean);
        }

        // cache parent deparment ids
        departmentIds.clear();
        // set hospital and department
        for (NurseHospitalRelationBean bean : relationBeans) {
            HospitalBean           tmpHos = hospId2Bean.get(bean.getHospitalId());
            HospitalDepartmentBean tmpDep = deptId2Bean.get(bean.getDepartmentId());
            bean.setHospital(tmpHos);
            bean.setDepartment(tmpDep);
            if (null!=tmpDep && tmpDep.getParentValid()) {
                departmentIds.add(tmpDep.getParentId());
            }
        }

        // get parent department
        departmentes.clear();
        deptId2Bean.clear();
        departmentes = departmentService.getDepartmentsByIds(departmentIds);
        for (HospitalDepartmentBean bean : departmentes) {
            deptId2Bean.put(bean.getId(), bean);
        }
        for (NurseHospitalRelationBean bean : relationBeans) {
            HospitalDepartmentBean tmpDep = bean.getDepartment();
            if (null!=tmpDep && tmpDep.getParentValid()) {
                tmpDep = deptId2Bean.get(tmpDep.getParentId());
                bean.setParentDepart(tmpDep);
            }
        }
    }

    //====================================================================
    //     toggle set nurse hospital and department
    //====================================================================

    @Transactional
    public Long newOne(NurseHospitalRelationBean bean) {
        logger.info("set nurse-hospital-department relationship information by bean={}", bean);
        HospitalBean                hospital     = null;
        HospitalDepartmentBean      department   = null;
        HospitalDepartmentBean      parentDepart = null;
        NurseHospitalRelationEntity relation     = null;
        List<NurseHospitalRelationEntity> relations = repository.findByNurseId(bean.getNurseId());
        if (!relations.isEmpty()) {
            // exist relationship
            relation = relations.get(0);
        }
        else {
            // new relationship
            relation = new NurseHospitalRelationEntity();
            relation.setTime(new Date());
        }
        // nurse exist
        nurseService.getNurseWithoutOtherInfo(bean.getNurseId());
        relation.setNurseId(bean.getNurseId());
        // hospital exist
        if (bean.getHospitalId()>0) {
            hospitalService.getOneById(bean.getHospitalId());
            if (relation.getHospitalId()!=bean.getHospitalId()) {
                relation.setHospitalId(bean.getHospitalId());
            }
        }
        // department exist
        if (bean.getDepartmentId()>0) {
            departmentService.getOneById(bean.getDepartmentId());
            if (relation.getDepartmentId()!=bean.getDepartmentId()) {
                relation.setDepartmentId(bean.getDepartmentId());
                bean.setTime(new Date());
            }
        }

        relation.setStatus(CommonStatus.ENABLED);
        relation = repository.save(relation);
        return relation.getId();
    }

    @Transactional
    public Long newOne(long nurseId, int hospitalId, int departmentId) {
        logger.info("set nurse={}--hospital={}--department={} relationship", nurseId, hospitalId, departmentId);
        NurseHospitalRelationBean bean = new NurseHospitalRelationBean();
        bean.setNurseId(nurseId);
        bean.setHospitalId(hospitalId);
        bean.setDepartmentId(departmentId);
        return newOne(bean);
    }

    @Transactional
    public Long newOne(long nurseId, String hospitalName) {
        logger.info("set nurse={}--HospitalName={} relationship", nurseId, hospitalName);
        if (VerifyUtil.isStringEmpty(hospitalName)) {
            logger.error("hospital name is invalid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        hospitalName = hospitalName.trim();
        List<HospitalBean> hospitals = hospitalService.getHospital(hospitalName);
        if (!VerifyUtil.isListEmpty(hospitals)) {
            logger.error("hospital is exist");
            throw new BadRequestException(ErrorCode.RECORD_ALREADY_EXIST);
        }
        Integer hospitalId = hospitalService.newOne(hospitalName, "", -1, -1, -1, "", 0);
        return newOne(nurseId, hospitalId, -1);
    }

    //========================================================
    //              delete
    //========================================================

    @Transactional
    public void deleteByHospitalIds(List<Integer> hospitalIds) {
        logger.info("delete nurse hospital department relation ship by hospital ids {}", hospitalIds);
        if (null==hospitalIds || hospitalIds.isEmpty()) {
            return;
        }

        List<NurseHospitalRelationEntity> relations = repository.findByHospitalIdIn(hospitalIds);
        if(null==relations || relations.isEmpty()) {
            logger.info("delete nothing");
            return;
        }

        List<NurseHospitalRelationEntity> needDelete = new ArrayList<>();
        for (NurseHospitalRelationEntity tmp : relations) {
            if (tmp.getDepartmentId()<0) {
                needDelete.add(tmp);
            }
            tmp.setHospitalId(-1);
        }
        repository.save(relations);

        if (!needDelete.isEmpty()) {
            repository.delete(needDelete);
        }
    }

    @Transactional
    public void deleteByDepartmentIds(List<Integer> departmentIds) {
        logger.info("delete nurse hospital department relation ship by department ids {}", departmentIds);
        if (null==departmentIds || departmentIds.isEmpty()) {
            return;
        }

        List<NurseHospitalRelationEntity> relations = repository.findByDepartmentIdIn(departmentIds);
        if(null==relations || relations.isEmpty()) {
            logger.info("delete nothing");
            return;
        }

        List<NurseHospitalRelationEntity> needDelete = new ArrayList<>();
        for (NurseHospitalRelationEntity tmp : relations) {
            if (tmp.getHospitalId()<0) {
                needDelete.add(tmp);
            }
            tmp.setDepartmentId(-1);
        }
        repository.save(relations);

        if (!needDelete.isEmpty()) {
            repository.delete(needDelete);
        }
    }
}

package com.cooltoo.services;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.beans.NurseHospitalRelationBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.converter.NurseHospitalRelationBeanConverter;
import com.cooltoo.entities.NurseHospitalRelationEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.NurseHospitalRelationRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Service("CommonNurseHospitalRelationService")
public class CommonNurseHospitalRelationService {

    private static final Logger logger = LoggerFactory.getLogger(CommonNurseHospitalRelationService.class.getName());

    private static final Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "id"));

    @Autowired private NurseHospitalRelationRepository repository;
    @Autowired private CommonHospitalService hospitalService;
    @Autowired private CommonDepartmentService departmentService;
    @Autowired private CommonNurseService nurseService;
    @Autowired private NurseHospitalRelationBeanConverter beanConverter;

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

    public NurseHospitalRelationBean getRelationWithoutOtherInfoByNurseId(Long nurseId) {
        logger.info("get one nurse hospital department information without other information by nurseId={}", nurseId);

        List<NurseHospitalRelationEntity> resultSet = repository.findByNurseId(nurseId, sort);
        if (null==resultSet || resultSet.isEmpty()) {
            return null;
        }
        if (resultSet.size()>1) {
            return null;
        }
        return beanConverter.convert(resultSet.get(0));
    }

    public NurseHospitalRelationBean getRelationByNurseId(Long nurseId, String nginxPrefix) {
        logger.info("get one nurse hospital department information by nurseId={}", nurseId);

        List<NurseHospitalRelationEntity> resultSet = repository.findByNurseId(nurseId, sort);
        if (null==resultSet || resultSet.isEmpty()) {
            return null;
        }
        if (resultSet.size()>1) {
            return null;
        }

        List<NurseHospitalRelationBean> relations = new ArrayList<>();
        for (NurseHospitalRelationEntity result : resultSet) {
            NurseHospitalRelationBean bean = beanConverter.convert(result);
            relations.add(bean);
        }

        fillOtherProperties(relations, nginxPrefix);
        return relations.get(0);
    }

    public Map<Long, NurseHospitalRelationBean> getRelationMapByNurseIds(List<Long> nurseIds, String nginxPrefix) {
        logger.info("get nurse<--->hospital-department relationship information map by nurseIds={}", nurseIds);

        Map<Long, NurseHospitalRelationBean> userId2Hospital = new HashMap<>();
        List<NurseHospitalRelationBean>      relations       = getRelationByNurseIds(nurseIds, nginxPrefix);
        for (NurseHospitalRelationBean result : relations) {
            userId2Hospital.put(result.getNurseId(), result);
        }
        return userId2Hospital;
    }

    private List<NurseHospitalRelationBean> getRelationByNurseIds(List<Long> nurseIds, String nginxPrefix) {
        logger.info("get nurses hospital department information by nurseIds={}", nurseIds);
        if (null==nurseIds || nurseIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<NurseHospitalRelationEntity> resultSet = repository.findByNurseIdIn(nurseIds);
        if (null==resultSet || resultSet.isEmpty()) {
            return new ArrayList<>();
        }

        List<NurseHospitalRelationBean> relations = new ArrayList<>();
        for (NurseHospitalRelationEntity result : resultSet) {
            NurseHospitalRelationBean bean = beanConverter.convert(result);
            relations.add(bean);
        }

        fillOtherProperties(relations, nginxPrefix);
        return relations;
    }

    private void fillOtherProperties(List<NurseHospitalRelationBean> relationBeans, String nginxPrefix) {
        if (VerifyUtil.isListEmpty(relationBeans)) {
            return;
        }
        List<Integer> hospitalIds   = new ArrayList<>();
        List<Integer> departmentIds = new ArrayList<>();
        for (NurseHospitalRelationBean bean : relationBeans) {
            if (!hospitalIds.contains(bean.getHospitalId())) {
                hospitalIds.add(bean.getHospitalId());
            }
            if (!departmentIds.contains(bean.getDepartmentId())) {
                departmentIds.add(bean.getDepartmentId());
            }
        }

        // get hospital
        Map<Integer, HospitalBean> hospId2Bean  = hospitalService.getHospitalIdToBeanMapByIds(hospitalIds);

        // get department
        List<HospitalDepartmentBean> departments = departmentService.getByIds(departmentIds, nginxPrefix);
        Map<Integer, HospitalDepartmentBean> deptId2Bean  = new HashMap<>();
        for (HospitalDepartmentBean bean : departments) {
            deptId2Bean.put(bean.getId(), bean);
        }

        // cache parent deparment ids
        departmentIds.clear();
        // set hospital and department
        for (NurseHospitalRelationBean bean : relationBeans) {
            HospitalBean tmpHos = hospId2Bean.get(bean.getHospitalId());
            HospitalDepartmentBean tmpDep = deptId2Bean.get(bean.getDepartmentId());
            bean.setHospital(tmpHos);
            bean.setDepartment(tmpDep);
            if (null!=tmpDep && tmpDep.getParentValid()) {
                departmentIds.add(tmpDep.getParentId());
            }
        }

        // get parent department
        departments.clear();
        deptId2Bean.clear();
        departments = departmentService.getByIds(departmentIds, nginxPrefix);
        for (HospitalDepartmentBean bean : departments) {
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

    //==============================================================
    //                       adding
    //==============================================================

    @Transactional
    public long setRelation(long nurseId, int hospitalId, int departmentId) {
        logger.info("nurse={} set hospital={} department={}", nurseId, hospitalId, departmentId);

        if (!nurseService.existNurse(nurseId)) {
            return 0L;
        }

        NurseHospitalRelationEntity relation;
        List<NurseHospitalRelationEntity> relations = repository.findByNurseId(nurseId, sort);
        if (!relations.isEmpty()) {
            // exist relationship
            relation = relations.get(0);
        }
        else {
            // new relationship
            relation = new NurseHospitalRelationEntity();
            relation.setNurseId(nurseId);
        }
        // hospital exist
        if (hospitalService.existHospital(hospitalId)) {
            if (relation.getHospitalId()!=hospitalId) {
                relation.setHospitalId(hospitalId);
            }
        }
        // department exist
        if (departmentService.existsDepartment(departmentId)) {
            if (relation.getDepartmentId()!=departmentId) {
                relation.setDepartmentId(departmentId);
            }
        }
        relation.setTime(new Date());
        relation.setStatus(CommonStatus.ENABLED);
        relation = repository.save(relation);

        // delete others
        relations = repository.findByNurseId(nurseId, sort);
        for (int i = 0; i < relations.size(); i ++) {
            NurseHospitalRelationEntity temp = relations.get(i);
            if (temp.getId() == relation.getId()) {
                relations.remove(i);
                break;
            }
        }
        repository.delete(relations);

        return relation.getId();
    }

    @Transactional
    public Long setRelation(long nurseId, String hospitalName) {
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
        Integer hospitalId = hospitalService.newOne(hospitalName, "", -1, -1, -1, "", 0, 0, null, null);
        return setRelation(nurseId, hospitalId, -1);
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

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
    @Autowired private NurseHospitalRelationBeanConverter beanConverter;

    //====================================================================
    //             get
    //====================================================================
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
        logger.info("get one nurse hospital-department relationship information by nurseIds={}", nurseIds);

        Map<Long, NurseHospitalRelationBean> userId2Hospital = new HashMap<>();
        List<NurseHospitalRelationBean>      relations       = getRelationByNurseIds(nurseIds, nginxPrefix);
        for (NurseHospitalRelationBean result : relations) {
            userId2Hospital.put(result.getNurseId(), result);
        }
        return userId2Hospital;
    }

    private List<NurseHospitalRelationBean> getRelationByNurseIds(List<Long> nurseIds, String nginxPrefix) {
        logger.info("get nurses hospital department information by nurseIds={}", nurseIds);

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

        if (nurseId<=0) {
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
}

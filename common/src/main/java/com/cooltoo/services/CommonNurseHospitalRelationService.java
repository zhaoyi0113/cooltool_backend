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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Service("CommonNurseHospitalRelationService")
public class CommonNurseHospitalRelationService {

    private static final Logger logger = LoggerFactory.getLogger(CommonNurseHospitalRelationService.class.getName());

    @Autowired private NurseHospitalRelationRepository repository;
    @Autowired private CommonHospitalService hospitalService;
    @Autowired private CommonDepartmentService departmentService;
    @Autowired private NurseHospitalRelationBeanConverter beanConverter;

    //====================================================================
    //             get
    //====================================================================
    public NurseHospitalRelationBean getRelationByNurseId(Long nurseId, String nginxPrefix) {
        logger.info("get one nurse hospital department information by nurseId={}", nurseId);

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
}

package com.cooltoo.backend.services;

import com.cooltoo.backend.converter.HospitalBeanConverter;
import com.cooltoo.backend.converter.HospitalEntityConverter;
import com.cooltoo.backend.entities.HospitalEntity;
import com.cooltoo.backend.repository.HospitalRepository;
import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.beans.HospitalDepartmentRelationBean;
import com.cooltoo.beans.RegionBean;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Service("HospitalService")
public class HospitalService {

    private static final Logger logger = Logger.getLogger(HospitalService.class.getName());

    @Autowired
    private HospitalRepository repository;
    @Autowired
    private HospitalDepartmentService departmentService;
    @Autowired
    private HospitalDepartmentRelationService relationService;
    @Autowired
    private HospitalBeanConverter beanConverter;
    @Autowired
    private HospitalEntityConverter entityConverter;
    @Autowired
    private RegionService regionService;

    public List<HospitalBean> getAll() {
        Iterable<HospitalEntity> iterable = repository.findAll();
        List<HospitalBean> all = new ArrayList<HospitalBean>();
        for (HospitalEntity entity : iterable) {
            HospitalBean bean = beanConverter.convert(entity);
            all.add(bean);
        }
        addRegion(all);
        return all;
    }

    public HospitalBean getOneById(Integer id) {
        HospitalEntity entity = repository.findOne(id);
        if (null == entity) {
            return null;
        }
        HospitalBean bean = beanConverter.convert(entity);
        List<HospitalBean> one = new ArrayList<HospitalBean>();
        one.add(bean);
        addRegion(one);
        return one.get(0);
    }

    @Transactional
    public HospitalBean deleteById(Integer id) {
        HospitalEntity entity = repository.findOne(id);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.HOSPITAL_NOT_EXIST);
        }
        repository.delete(entity.getId());
        return beanConverter.convert(entity);
    }

    @Transactional
    public HospitalBean update(HospitalBean bean) {
        if (!repository.exists(bean.getId())) {
            throw new BadRequestException(ErrorCode.HOSPITAL_NOT_EXIST);
        }
        String value = bean.getName();
        if (null==value || "".equals(value)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        HospitalEntity entity = repository.findOne(bean.getId());
        if (!value.equals(entity.getName())) {
            entity.setName(value);
        }
        int iValue = bean.getProvince();
        if (iValue>0 && iValue != entity.getProvince()) {
            entity.setProvince(iValue);
        }
        iValue = bean.getCity();
        if (iValue>0 && iValue != entity.getCity()) {
            entity.setCity(iValue);
        }
        iValue = bean.getDistrict();
        if (iValue>0 && iValue != entity.getDistrict()) {
            entity.setDistrict(iValue);
        }
        logger.info("update hospital == " + bean);
        entity = repository.save(entity);
        return beanConverter.convert(entity);
    }

    @Transactional
    public HospitalBean update(int id, String name, int province, int city, int district, String address, int enable) {
        HospitalBean bean = new HospitalBean();
        bean.setId(id);
        bean.setName(name);
        bean.setProvince(province);
        bean.setCity(city);
        bean.setDistrict(district);
        bean.setAddress(address);
        bean.setEnable(enable);
        return update(bean);
    }

    @Transactional
    public Integer newOne(HospitalBean bean) {
        String value = bean.getName();
        if (null==value || "".equals(value)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        HospitalEntity entity = entityConverter.convert(bean);
        logger.info("Add hospital == " + bean);
        entity = repository.save(entity);
        return entity.getId();
    }

    @Transactional
    public Integer newOne(String name, int province, int city, int district, String address, int enable) {
        HospitalBean bean = new HospitalBean();
        bean.setName(name);
        bean.setProvince(province);
        bean.setCity(city);
        bean.setDistrict(district);
        bean.setAddress(address);
        bean.setEnable(enable);
        if (!checkRegion(bean)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        return newOne(bean);
    }

    public List<HospitalDepartmentBean> getAllDepartments(int hospitalId) {
        List<HospitalDepartmentRelationBean> relationBeans = relationService.getRelationByHospitalId(hospitalId);
        List<Integer> departmentIds = new ArrayList<Integer>();
        for (HospitalDepartmentRelationBean relation : relationBeans) {
            departmentIds.add(relation.getDepartmentId());
        }
        return departmentService.getDepartmentsByIds(departmentIds);
    }

    @Transactional
    public Integer setHospitalAndDepartmentRelation(int hospitalId, int departId) {
        HospitalBean hospital = getOneById(hospitalId);
        if (null==hospital) {
            throw new BadRequestException(ErrorCode.HOSPITAL_NOT_EXIST);
        }
        HospitalDepartmentBean department = departmentService.getOneById(departId);
        if (null==department) {
            throw new BadRequestException(ErrorCode.HOSPITAL_DEPARTMENT_NOT_EXIST);
        }
        List<HospitalDepartmentRelationBean> relations = relationService.getRelationByHospitalId(hospitalId);
        // if exist delete
        HospitalDepartmentRelationBean relationExist = null;
        for (HospitalDepartmentRelationBean relation : relations) {
            if (relation.getHospitalId()==hospitalId && relation.getDepartmentId()==departId) {
                relationExist = relation;
            }
        }
        if (null==relationExist) {
            return relationService.newOne(hospitalId, departId);
        }
        else {
            relationService.deleteById(relationExist.getId());
            return relationExist.getId();
        }
    }

    private boolean checkRegion(HospitalBean hospital) {
        if (null==hospital) {
            return false;
        }
        List<HospitalBean> hospitals = new ArrayList<HospitalBean>();
        hospitals.add(hospital);
        hospitals = addRegion(hospitals);
        RegionBean province = hospital.getProvinceBean();
        RegionBean city     = hospital.getCityBean();
        RegionBean district = hospital.getDistrictBean();
        logger.info("provice="+province+" city="+city+" district="+district);
        // do not check when one of they is null
        if (null==province || null==city || null==district) {
            return true;
        }
        if(city.getParentId()==province.getId() && district.getParentId()==city.getId()) {
            return true;
        }
        return false;
    }

    private List<HospitalBean> addRegion(List<HospitalBean> hospitals) {
        if (null==hospitals || hospitals.isEmpty()) {
            return hospitals;
        }

        List<Integer> regionIds = new ArrayList<Integer>();
        for (HospitalBean hospital : hospitals) {
            regionIds.add(hospital.getProvince());
            regionIds.add(hospital.getCity());
            regionIds.add(hospital.getDistrict());
        }

        List<RegionBean> regions = regionService.getRegion(regionIds);
        for (RegionBean region : regions) {
            for (HospitalBean hospital : hospitals) {
                if (hospital.getProvince()==region.getId()) {
                    hospital.setProvinceBean(region);
                }
                if (hospital.getCity()==region.getId()) {
                    hospital.setCityBean(region);
                }
                if (hospital.getDistrict()==region.getId()) {
                    hospital.setDistrictBean(region);
                }
            }
        }
        return hospitals;
    }
}

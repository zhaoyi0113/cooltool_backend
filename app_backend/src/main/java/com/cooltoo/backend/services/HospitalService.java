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
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Service("HospitalService")
public class HospitalService {

    private static final Logger logger = LoggerFactory.getLogger(HospitalService.class.getName());

    @Autowired
    private HospitalRepository repository;
    @Autowired
    private HospitalDepartmentService departmentService;
    @Autowired
    private HospitalDepartmentRelationService relationService;
    @Autowired
    private NurseHospitalRelationService nurseRelationService;
    @Autowired
    private HospitalBeanConverter beanConverter;
    @Autowired
    private HospitalEntityConverter entityConverter;
    @Autowired
    private RegionService regionService;

    //=======================================================
    //        get
    //=======================================================
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

    public long getHospitalSize() {
        long count = repository.count();
        logger.info("get the number of hospital is {}", count);
        return count;
    }

    public List<HospitalBean> getAllByPage(int index, int number) {
        Sort        sort = new Sort(new Sort.Order(Sort.Direction.ASC, "name"));
        PageRequest page = new PageRequest(index, number, sort);

        Page<HospitalEntity> pageResult = repository.findAll(page);
        List<HospitalBean>   hospital = new ArrayList<HospitalBean>();
        for (HospitalEntity entity : pageResult) {
            HospitalBean bean = beanConverter.convert(entity);
            hospital.add(bean);
        }
        addRegion(hospital);
        return hospital;
    }

    public List<HospitalBean> getHospitalByIds(List<Integer> hospitalIds) {
        if (null==hospitalIds || hospitalIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<HospitalEntity> resultSet = repository.findByIdIn(hospitalIds);
        List<HospitalBean>   hospitals = new ArrayList<>();
        for (HospitalEntity result : resultSet) {
            HospitalBean bean = beanConverter.convert(result);
            hospitals.add(bean);
        }
        addRegion(hospitals);
        return hospitals;
    }

    public List<HospitalBean> searchHospital(boolean isCount, boolean andOrOr, String name,
                                             int province, int city, int district, String address,
                                             int index, int number) {
        List<HospitalBean> allHospitals     = getAll();
        List<HospitalBean> allHospitalMatch = new ArrayList<HospitalBean>();

        boolean matchName     = !VerifyUtil.isStringEmpty(name);
        boolean matchAddress  = !VerifyUtil.isStringEmpty(address);
        boolean matchProvince = (province>0);
        boolean matchCity     = (city    >0);
        boolean matchDistrict = (district>0);
        boolean matchRegion   = (matchProvince || matchCity || matchDistrict);
        boolean condition1    = true;
        boolean condition2    = true;
        boolean condition3    = true;
        if (null==allHospitals || allHospitals.isEmpty()) {
            return allHospitals;
        }
        for (HospitalBean hospital : allHospitals) {
            condition1 = (!matchName)    || (matchName    && hospital.getName().contains(name));
            condition2 = (!matchAddress) || (matchAddress && hospital.getAddress().contains(address));
            if (matchRegion) {
                if (!matchProvince) { province = hospital.getProvince(); }
                if (!matchCity    ) { city     = hospital.getCity();     }
                if (!matchDistrict) { district = hospital.getDistrict(); }

                condition3 = ((hospital.getProvince() == province)
                           && (hospital.getCity()     == city)
                           && (hospital.getDistrict() == district));
            }
            else {
                condition3 = true;
            }

            if (( andOrOr && (condition1 && condition2 && condition3))
             || (!andOrOr && (condition1 || condition2 || condition3))) {
                allHospitalMatch.add(hospital);
            }
        }

        if (isCount) {
            return allHospitalMatch;
        }

        List<HospitalBean> hospitals  = new ArrayList<>();
        int iIndex   = Math.abs(index);
        int iNumber  = Math.abs(number);
        int startIdx = iIndex*iNumber;
        int endIdx   = startIdx + number;
        if (startIdx<0) {
            startIdx = 0;
        }
        else if (startIdx>=allHospitalMatch.size()) {
            return hospitals;
        }

        for (int i=startIdx, count=allHospitalMatch.size(); i<endIdx && i<count; i++) {
            HospitalBean tmp = allHospitalMatch.get(i);
            hospitals.add(tmp);
        }

        return hospitals;
    }

    public List<HospitalBean> getAllHospitalEnable() {
        List<HospitalBean> allHospitals      = getAll();
        List<HospitalBean> allHospitalEnable = new ArrayList<HospitalBean>();
        for (HospitalBean hospital : allHospitals) {
            if (hospital.getEnable()>0) {
                allHospitalEnable.add(hospital);
            }
        }
        return allHospitalEnable;
    }

    public List<HospitalDepartmentBean> getAllDepartments(int hospitalId) {
        List<HospitalDepartmentRelationBean> relationBeans = relationService.getRelationByHospitalId(hospitalId);
        List<Integer> departmentIds = new ArrayList<Integer>();
        for (HospitalDepartmentRelationBean relation : relationBeans) {
            departmentIds.add(relation.getDepartmentId());
        }
        return departmentService.getDepartmentsByIds(departmentIds);
    }

    public HospitalBean getOneById(Integer id) {
        HospitalEntity entity = repository.findOne(id);
        if (null == entity) {
            throw new BadRequestException(ErrorCode.HOSPITAL_NOT_EXIST);
        }
        HospitalBean bean = beanConverter.convert(entity);
        List<HospitalBean> one = new ArrayList<HospitalBean>();
        one.add(bean);
        addRegion(one);
        return one.get(0);
    }

    //=======================================================
    //        delete department
    //=======================================================
    @Transactional
    public HospitalBean deleteById(Integer hospitalId) {
        HospitalEntity entity = repository.findOne(hospitalId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.HOSPITAL_NOT_EXIST);
        }

        repository.delete(entity.getId());
        List<Integer> ids = new ArrayList<>();
        ids.add(hospitalId);
        relationService.deleteByHospitalIds(ids);
        nurseRelationService.deleteByHospitalIds(ids);

        return beanConverter.convert(entity);
    }

    @Transactional
    public List<HospitalBean> deleteByIds(String strHospitalIds) {
        logger.info("delete hospital by hospital ids {}", strHospitalIds);
        if (!VerifyUtil.isIds(strHospitalIds)) {
            logger.warn("hospital ids are invalid");
            return new ArrayList<>();
        }

        List<Integer> ids = VerifyUtil.parseIntIds(strHospitalIds);
        return deleteByIds(ids);
    }

    @Transactional
    public List<HospitalBean> deleteByIds(List<Integer> hospitalIds) {
        logger.info("delete hospital by hospital ids {}", hospitalIds);
        if (null==hospitalIds || hospitalIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<HospitalEntity> hospitals = repository.findByIdIn(hospitalIds);
        if (null==hospitals || hospitals.isEmpty()) {
            logger.info("delete nothing");
            return new ArrayList<>();
        }

        repository.delete(hospitals);
        relationService.deleteByHospitalIds(hospitalIds);
        nurseRelationService.deleteByHospitalIds(hospitalIds);

        List<HospitalBean> retValue = new ArrayList<>();
        for (HospitalEntity tmp : hospitals) {
            HospitalBean hospital = beanConverter.convert(tmp);
            retValue.add(hospital);
        }
        return retValue;
    }

    //=======================================================
    //        update department
    //=======================================================

    @Transactional
    public HospitalBean update(HospitalBean bean) {
        if (!repository.exists(bean.getId())) {
            throw new BadRequestException(ErrorCode.HOSPITAL_NOT_EXIST);
        }
        String value = bean.getName();
        if (VerifyUtil.isStringEmpty(bean.getName())) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        HospitalEntity entity = repository.findOne(bean.getId());
        if (!value.equals(entity.getName())) {
            List<HospitalEntity> hospitals = repository.findByName(value);
            if (!hospitals.isEmpty()) {
                logger.error("hospital name has been used!");
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
            entity.setName(value);
        }
        entity.setProvince(bean.getProvince());
        entity.setCity(bean.getCity());
        entity.setDistrict(bean.getDistrict());
        entity.setAddress(bean.getAddress());

        int enable = bean.getEnable();
        enable = enable<0 ? enable : (enable>1 ? 1 : enable);
        if (enable>=0) {
            entity.setEnable(bean.getEnable());
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
        if (!checkRegion(bean)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        return update(bean);
    }

    //=======================================================
    //        create department
    //=======================================================

    @Transactional
    public Integer newOne(HospitalBean bean) {
        String value = bean.getName();
        if (VerifyUtil.isStringEmpty(value)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        List<HospitalEntity> hospitals = repository.findByName(value);
        if (!hospitals.isEmpty()) {
            logger.error("hospital name has been used!");
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

    //=======================================================
    //        set relation of hospital and department
    //=======================================================

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
        if (null==province || null==city) {
            return true;
        }
        if (null==district) {
            if (city.getParentId() == province.getId()) {
                return true;
            }
        }
        else {
            if (city.getParentId() == province.getId() && district.getParentId() == city.getId()) {
                return true;
            }
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

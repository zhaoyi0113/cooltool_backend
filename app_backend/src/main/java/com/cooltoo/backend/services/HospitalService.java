package com.cooltoo.backend.services;

import com.cooltoo.converter.HospitalBeanConverter;
import com.cooltoo.converter.HospitalEntityConverter;
import com.cooltoo.entities.HospitalEntity;
import com.cooltoo.repository.HospitalRepository;
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
    public List<HospitalBean> getHospital(String hospitalName) {
        List<HospitalEntity> hospitals = repository.findByName(hospitalName);
        List<HospitalBean> hospitalBeans = entities2Beans(hospitals);
        fillOtherProperties(hospitalBeans);
        return hospitalBeans;
    }

    public List<HospitalBean> getAll() {
        Iterable<HospitalEntity> iterable = repository.findAll();
        List<HospitalBean> all = new ArrayList<>();
        for (HospitalEntity entity : iterable) {
            HospitalBean bean = beanConverter.convert(entity);
            all.add(bean);
        }
        fillOtherProperties(all);
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
        List<HospitalBean>   hospital = new ArrayList<>();
        for (HospitalEntity entity : pageResult) {
            HospitalBean bean = beanConverter.convert(entity);
            hospital.add(bean);
        }
        fillOtherProperties(hospital);
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
        fillOtherProperties(hospitals);
        return hospitals;
    }

    public List<HospitalBean> getHospitalByProvince(int provinceId, int enable) {
        List<HospitalEntity> resultSet = repository.findByProvinceAndEnable(provinceId, enable);
        List<HospitalBean>   hospitalsInProvince = entities2Beans(resultSet);
        fillOtherProperties(hospitalsInProvince);
        return hospitalsInProvince;
    }

    public List<HospitalBean> searchHospital(boolean isCount, boolean isAnd, String name,
                                             int province, int city, int district, String address,
                                             int status, int supportGo2nurse,
                                             int index, int number) {
        List<HospitalBean> allHospitals     = getAll();
        List<HospitalBean> allHospitalMatch = new ArrayList<HospitalBean>();

        boolean matchName     = !VerifyUtil.isStringEmpty(name);
        boolean matchAddress  = !VerifyUtil.isStringEmpty(address);
        boolean matchProvince = (province>0);
        boolean matchCity     = (city    >0);
        boolean matchDistrict = (district>0);
        boolean matchRegion   = (matchProvince || matchCity || matchDistrict);
        boolean matchStatus   = (1==status || 0==status);
        boolean matchSupport  = (1==supportGo2nurse || 0==supportGo2nurse);
        boolean condition1    = true;
        boolean condition2    = true;
        boolean condition3    = true;
        boolean condition4    = true;
        boolean condition5    = true;
        if (null==allHospitals || allHospitals.isEmpty()) {
            return allHospitals;
        }
        for (HospitalBean hospital : allHospitals) {
            condition1 = (!matchName)    || (matchName && !VerifyUtil.isStringEmpty(hospital.getName()) && hospital.getName().contains(name));
            condition1 = (condition1     || (!matchName) || (matchName && !VerifyUtil.isStringEmpty( hospital.getAliasName()) && hospital.getAliasName().contains(name)));
            condition2 = (!matchAddress) || (matchAddress && !VerifyUtil.isStringEmpty( hospital.getAddress()) && hospital.getAddress().contains(address));
            condition4 = (!matchStatus)  || (matchStatus && hospital.getEnable()==status);
            condition5 = (!matchSupport) || (matchSupport && hospital.getSupportGo2nurse()==supportGo2nurse);
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

            if (( isAnd && (condition1 && condition2 && condition3 && condition4 && condition5))
             || (!isAnd && (condition1 || condition2 || condition3 || condition4 || condition5))) {
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
        fillOtherProperties(one);
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
        value = bean.getAliasName();
        if (!VerifyUtil.isStringEmpty(value) && !value.equals(entity.getAliasName())) {
            entity.setAliasName(value);
        }
        entity.setProvince(bean.getProvince());
        entity.setCity(bean.getCity());
        entity.setDistrict(bean.getDistrict());
        entity.setAddress(bean.getAddress());


        int enable = bean.getEnable();
        enable = enable<0 ? 0 : (enable>1 ? 1 : enable);
        if (enable>=0) {
            entity.setEnable(enable);
        }
        int support = bean.getSupportGo2nurse();
        support = support<0 ? 0 : (support>1 ? 1 : support);
        if (support>=0) {
            entity.setSupportGo2nurse(support);
        }

        logger.info("update hospital == " + bean);
        entity = repository.save(entity);
        return beanConverter.convert(entity);
    }

    @Transactional
    public HospitalBean update(int id, String name, String aliasName, int province, int city, int district, String address, int enable, int supportGo2nurse) {
        HospitalBean bean = new HospitalBean();
        bean.setId(id);
        bean.setName(name);
        bean.setProvince(province);
        bean.setCity(city);
        bean.setDistrict(district);
        bean.setAddress(address);
        bean.setEnable(enable);
        bean.setAliasName(aliasName);
        bean.setSupportGo2nurse(supportGo2nurse);
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
            logger.error("hospital name is empty!");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        List<HospitalEntity> hospitals = repository.findByName(value);
        if (!hospitals.isEmpty()) {
            logger.error("hospital name has been used!");
            throw new BadRequestException(ErrorCode.RECORD_ALREADY_EXIST);
        }
        int enable = bean.getEnable();
        enable = enable<0 ? 0 : (enable>1 ? 1 : enable);
        bean.setEnable(enable);

        int support = bean.getSupportGo2nurse();
        support = support<0 ? 0 : (support>1 ? 1 : support);
        bean.setSupportGo2nurse(support);

        HospitalEntity entity = entityConverter.convert(bean);

        logger.info("Add hospital == " + bean);
        entity = repository.save(entity);
        return entity.getId();
    }

    @Transactional
    public Integer newOne(String name, String aliasName, int province, int city, int district, String address, int enable, int supportGo2nurse) {
        HospitalBean bean = new HospitalBean();
        bean.setName(name);
        bean.setProvince(province);
        bean.setCity(city);
        bean.setDistrict(district);
        bean.setAddress(address);
        bean.setEnable(enable);
        bean.setAliasName(aliasName);
        bean.setSupportGo2nurse(supportGo2nurse);
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
        hospitals = fillOtherProperties(hospitals);
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

    private List<HospitalBean> entities2Beans(Iterable<HospitalEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }
        List<HospitalBean> beans = new ArrayList<HospitalBean>();
        for (HospitalEntity entity : entities) {
            HospitalBean bean = beanConverter.convert(entity);
            beans.add(bean);
        }

        return beans;
    }

    private List<HospitalBean> fillOtherProperties(List<HospitalBean> hospitals) {
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

package com.cooltoo.services;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.beans.RegionBean;
import com.cooltoo.converter.HospitalBeanConverter;
import com.cooltoo.converter.HospitalEntityConverter;
import com.cooltoo.entities.HospitalEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.HospitalRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.target.HotSwappableTargetSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Service("CommonHospitalService")
public class CommonHospitalService {

    private static final Logger logger = LoggerFactory.getLogger(CommonHospitalService.class.getName());

    @Autowired private HospitalRepository repository;
    @Autowired private HospitalBeanConverter beanConverter;
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

    public List<HospitalBean> getHospitalByUniqueId(String uniqueId) {
        List<HospitalEntity> hospitals = repository.findByUniqueId(uniqueId);
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

    public Map<Integer, HospitalBean> getHospitalIdToBeanMapByIds(List<Integer> hospitalIds) {
        List<HospitalBean> hospitals = getHospitalByIds(hospitalIds);
        Map<Integer, HospitalBean> map = new HashMap<>();
        for (HospitalBean hospital : hospitals) {
            map.put(hospital.getId(), hospital);
        }
        return map;
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

    public List<HospitalBean> getAllHospitalEnableSupportGo2nurse() {
        List<HospitalBean> allHospitals      = getAll();
        List<HospitalBean> allHospitalEnable = new ArrayList<HospitalBean>();
        for (HospitalBean hospital : allHospitals) {
            if (hospital.getEnable()>0 && hospital.getSupportGo2nurse()>0) {
                allHospitalEnable.add(hospital);
            }
        }
        return allHospitalEnable;
    }

    public HospitalBean getOneById(Integer id) {
        HospitalEntity entity = repository.findOne(id);
        if (null == entity) {
            return null;
        }
        HospitalBean bean = beanConverter.convert(entity);
        List<HospitalBean> one = new ArrayList<HospitalBean>();
        one.add(bean);
        fillOtherProperties(one);
        return one.get(0);
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

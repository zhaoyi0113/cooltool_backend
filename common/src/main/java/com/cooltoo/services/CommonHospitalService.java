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
    @Autowired private RegionService regionService;

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
        List<HospitalBean> hospitals = entities2Beans(resultSet);
        fillOtherProperties(hospitals);
        return hospitals;
    }

    public List<HospitalBean> getHospitalByProvince(int provinceId, int enable) {
        List<HospitalEntity> resultSet = repository.findByProvinceAndEnable(provinceId, enable);
        List<HospitalBean>   hospitalsInProvince = entities2Beans(resultSet);
        fillOtherProperties(hospitalsInProvince);
        return hospitalsInProvince;
    }

    public long countHospitalByConditions(boolean isAnd, String name, Integer province, Integer city, Integer district, String address, Integer enable, Integer supportGo2nurse) {
        logger.info("count by isAnd={} name={} province={} city={} district={} address={} enable={} supportGo2nurse={}",
                isAnd, name, province, city, district, address, enable, supportGo2nurse);
        long count = 0;
        name = VerifyUtil.isStringEmpty(name) ? null : VerifyUtil.reconstructSQLContentLike(name.trim());
        address = VerifyUtil.isStringEmpty(address) ? null : VerifyUtil.reconstructSQLContentLike(address.trim());
        if (isAnd) {
            count = repository.countByConditionsAND(name, province, city, district, address, enable, supportGo2nurse);
        }
        else {
            count = repository.countByConditionsOR(name, province, city, district, address, enable, supportGo2nurse);
        }
        logger.info("count is {}", count);
        return count;
    }

    public List<HospitalBean> searchHospitalByConditions(boolean isAnd, String name,
                                                         Integer province, Integer city, Integer district, String address,
                                                         Integer enable, Integer supportGo2nurse,
                                                         Integer index, Integer number
    ) {
        logger.info("search by isAnd={} name={} province={} city={} district={} address={} enable={} supportGo2nurse={} index={} number={}",
                isAnd, name, province, city, district, address, enable, supportGo2nurse, index, number);
        if (number==0) {
            return new ArrayList<>();
        }
        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "name"));
        PageRequest page = new PageRequest(index, number, sort);

        name = VerifyUtil.isStringEmpty(name) ? null : VerifyUtil.reconstructSQLContentLike(name.trim());
        address = VerifyUtil.isStringEmpty(address) ? null : VerifyUtil.reconstructSQLContentLike(address.trim());

        Page<HospitalEntity> entities;
        if (isAnd) {
            entities = repository.findByConditionsAND(name, province, city, district, address, enable, supportGo2nurse, page);
        }
        else {
            entities = repository.findByConditionsOR(name, province, city, district, address, enable, supportGo2nurse, page);
        }
        List<HospitalBean> beans = entities2Beans(entities);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public List<HospitalBean> getAllHospitalEnable() {
        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "name"));
        List<HospitalEntity> entities = repository.findByEnable(1, sort);
        List<HospitalBean> allHospitalEnable = entities2Beans(entities);
        return allHospitalEnable;
    }

    public List<HospitalBean> getAllHospitalEnableSupportGo2nurse() {
        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "name"));
        List<HospitalEntity> entities = repository.findBySupportGo2nurse(1, sort);
        List<HospitalBean> allHospitals = entities2Beans(entities);
        List<HospitalBean> allHospitalEnable = new ArrayList<>();
        for (HospitalBean hospital : allHospitals) {
            if (hospital.getEnable()>0) {
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
        List<HospitalBean> one = new ArrayList<>();
        one.add(bean);
        fillOtherProperties(one);
        return one.get(0);
    }

    public boolean existHospital(Integer id) {
        return repository.exists(id);
    }

    public List<HospitalBean> entities2Beans(Iterable<HospitalEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }
        List<HospitalBean> beans = new ArrayList<>();
        for (HospitalEntity entity : entities) {
            HospitalBean bean = beanConverter.convert(entity);
            beans.add(bean);
        }

        return beans;
    }

    public List<HospitalBean> fillOtherProperties(List<HospitalBean> hospitals) {
        if (null==hospitals || hospitals.isEmpty()) {
            return hospitals;
        }

        List<Integer> regionIds = new ArrayList<>();
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

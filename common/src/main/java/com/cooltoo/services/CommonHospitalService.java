package com.cooltoo.services;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.RegionBean;
import com.cooltoo.converter.HospitalBeanConverter;
import com.cooltoo.converter.HospitalEntityConverter;
import com.cooltoo.entities.HospitalEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.HospitalRepository;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    private static final Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "id"));

    @Autowired private HospitalRepository repository;
    @Autowired private HospitalBeanConverter beanConverter;
    @Autowired private HospitalEntityConverter entityConverter;
    @Autowired private RegionService regionService;
    @Autowired private CommonNurseHospitalRelationService nurseHospitalRelationService;

    //=======================================================
    //        get
    //=======================================================
    public List<HospitalBean> getHospital(String hospitalName) {
        List<HospitalEntity> hospitals = repository.findByName(hospitalName);
        List<HospitalBean> hospitalBeans = entities2Beans(hospitals);
        fillOtherProperties(hospitalBeans);
        return hospitalBeans;
    }

    public HospitalBean getHospital(Integer hospitalId) {
        HospitalEntity hospital = repository.findOne(hospitalId);
        if (null==hospital) {
            return null;
        }
        List<HospitalEntity> hospitals = new ArrayList<>();
        hospitals.add(hospital);
        List<HospitalBean> hospitalBeans = entities2Beans(hospitals);
        fillOtherProperties(hospitalBeans);
        return hospitalBeans.get(0);
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
        PageRequest page = new PageRequest(index, number, sort);

        name = VerifyUtil.isStringEmpty(name) ? null : VerifyUtil.reconstructSQLContentLike(name.trim());
        address = VerifyUtil.isStringEmpty(address) ? null : VerifyUtil.reconstructSQLContentLike(address.trim());
        name = VerifyUtil.isStringEmpty(name) ? null : name.trim();
        address = VerifyUtil.isStringEmpty(address) ? null : address.trim();

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
        List<HospitalEntity> entities = repository.findByEnable(1, sort);
        List<HospitalBean> allHospitalEnable = entities2Beans(entities);
        return allHospitalEnable;
    }

    public List<HospitalBean> getAllHospitalEnableSupportGo2nurse() {
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

    //=======================================================
    //        delete
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
        nurseHospitalRelationService.deleteByHospitalIds(ids);

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
        nurseHospitalRelationService.deleteByHospitalIds(hospitalIds);

        List<HospitalBean> retValue = new ArrayList<>();
        for (HospitalEntity tmp : hospitals) {
            HospitalBean hospital = beanConverter.convert(tmp);
            retValue.add(hospital);
        }
        return retValue;
    }

    //=======================================================
    //        update
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
        if (!VerifyUtil.isStringEmpty(bean.getPhoneNumber()) && !bean.getPhoneNumber().trim().equals(entity.getPhoneNumber())) {
            entity.setPhoneNumber(bean.getPhoneNumber());
        }
        if (!VerifyUtil.isStringEmpty(bean.getZipCode()) && !bean.getZipCode().trim().equals(entity.getZipCode())) {
            entity.setZipCode(bean.getZipCode());
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
    public HospitalBean update(int id, String name, String aliasName, int province, int city, int district, String address, int enable, int supportGo2nurse, String phoneNumber, String zipCode) {
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
        bean.setPhoneNumber(phoneNumber);
        bean.setZipCode(zipCode);
        if (!checkRegion(bean)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        return update(bean);
    }

    //=======================================================
    //        create
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

        String uniqueId = null;
        for (int i = 10; i>0; i--) {
            uniqueId = NumberUtil.randomIdentity();
            if (repository.countByUniqueId(uniqueId)<=0) {
                break;
            }
            else {
                uniqueId = null;
            }
        }
        if (VerifyUtil.isStringEmpty(uniqueId)) {
            logger.info("unique id generated failed");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        bean.setUniqueId(uniqueId);

        HospitalEntity entity = entityConverter.convert(bean);

        logger.info("Add hospital == " + bean);
        entity = repository.save(entity);
        return entity.getId();
    }

    @Transactional
    public Integer newOne(String name, String aliasName, int province, int city, int district, String address, int enable, int supportGo2nurse, String phoneNumber, String zipCode) {
        HospitalBean bean = new HospitalBean();
        bean.setName(name);
        bean.setProvince(province);
        bean.setCity(city);
        bean.setDistrict(district);
        bean.setAddress(address);
        bean.setEnable(enable);
        bean.setAliasName(aliasName);
        bean.setSupportGo2nurse(supportGo2nurse);
        bean.setPhoneNumber(phoneNumber);
        bean.setZipCode(zipCode);
        if (!checkRegion(bean)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        return newOne(bean);
    }




    private boolean checkRegion(HospitalBean hospital) {
        if (null==hospital) {
            return false;
        }
        List<HospitalBean> hospitals = new ArrayList<>();
        hospitals.add(hospital);
        // fill region
        fillOtherProperties(hospitals);
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
}

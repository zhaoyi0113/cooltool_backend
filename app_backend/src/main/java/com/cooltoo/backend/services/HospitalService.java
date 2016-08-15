package com.cooltoo.backend.services;

import com.cooltoo.converter.HospitalBeanConverter;
import com.cooltoo.converter.HospitalEntityConverter;
import com.cooltoo.entities.HospitalEntity;
import com.cooltoo.repository.HospitalRepository;
import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.RegionBean;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.CommonHospitalService;
import com.cooltoo.services.RegionService;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired private CommonHospitalService commonHospitalService;
    @Autowired private HospitalRepository repository;
    @Autowired private NurseHospitalRelationService nurseRelationService;
    @Autowired private HospitalBeanConverter beanConverter;
    @Autowired private HospitalEntityConverter entityConverter;
    @Autowired private RegionService regionService;

    //=======================================================
    //        get
    //=======================================================
    public List<HospitalBean> getHospital(String hospitalName) {
        List<HospitalBean> hospitalBeans = commonHospitalService.getHospital(hospitalName);
        return hospitalBeans;
    }

    public List<HospitalBean> getHospitalByUniqueId(String uniqueId) {
        List<HospitalBean> hospitalBeans = commonHospitalService.getHospitalByUniqueId(uniqueId);
        return hospitalBeans;
    }

    public List<HospitalBean> getHospitalByIds(List<Integer> hospitalIds) {
        List<HospitalBean> hospitals = commonHospitalService.getHospitalByIds(hospitalIds);
        return hospitals;
    }

    public List<HospitalBean> getHospitalByProvince(int provinceId, int enable) {
        List<HospitalBean> hospitalsInProvince = commonHospitalService.getHospitalByProvince(provinceId, enable);
        return hospitalsInProvince;
    }

    public long countHospitalByConditions(boolean isAnd, String name, Integer province, Integer city, Integer district, String address, Integer enable, Integer supportGo2nurse) {
        long count = commonHospitalService.countHospitalByConditions(isAnd, name, province, city, district, address, enable, supportGo2nurse);
        return count;
    }

    public List<HospitalBean> searchHospitalByConditions(boolean isAnd, String name,
                                                         Integer province, Integer city, Integer district, String address,
                                                         Integer enable, Integer supportGo2nurse,
                                                         Integer index, Integer number
    ) {
        List<HospitalBean> beans = commonHospitalService.searchHospitalByConditions(isAnd, name, province, city, district, address, enable, supportGo2nurse, index, number);
        return beans;
    }

    public List<HospitalBean> getAllHospitalEnable() {
        List<HospitalBean> allHospitalEnable = commonHospitalService.getAllHospitalEnable();
        return allHospitalEnable;
    }

    public HospitalBean getOneById(Integer id) {
        return commonHospitalService.getOneById(id);
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

    private boolean checkRegion(HospitalBean hospital) {
        if (null==hospital) {
            return false;
        }
        List<HospitalBean> hospitals = new ArrayList<>();
        hospitals.add(hospital);
        // fill region
        commonHospitalService.fillOtherProperties(hospitals);
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

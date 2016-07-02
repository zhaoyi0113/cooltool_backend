package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.RegionBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.UserAddressBean;
import com.cooltoo.go2nurse.converter.UserAddressBeanConverter;
import com.cooltoo.go2nurse.entities.UserAddressEntity;
import com.cooltoo.go2nurse.repository.UserAddressRepository;
import com.cooltoo.services.RegionService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Service("UserAddressService")
public class UserAddressService {

    private static final Logger logger = LoggerFactory.getLogger(UserAddressService.class.getName());

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "grade"),
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private UserAddressRepository repository;
    @Autowired private UserAddressBeanConverter beanConverter;
    @Autowired private RegionService regionService;

    //=======================================================
    //        get
    //=======================================================
    public List<UserAddressBean> getUserAddress(long userId) {
        logger.info("get user={} 's addresses", userId);
        List<UserAddressEntity> userAddress = repository.findByUserId(userId, sort);
        List<UserAddressBean> beans = entities2Beans(userAddress);
        fillOtherProperties(beans);
        logger.info("count is {}", userAddress.size());
        return beans;
    }

    //=======================================================
    //        delete
    //=======================================================
    @Transactional
    public UserAddressBean deleteById(long addressId) {
        logger.info("delete user address by id={}", addressId);
        UserAddressEntity entity = repository.findOne(addressId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        repository.delete(entity);
        return beanConverter.convert(entity);
    }

    @Transactional
    public List<UserAddressBean> deleteByIds(String strAddressesId) {
        logger.info("delete user address by addressesId={}", strAddressesId);
        if (!VerifyUtil.isIds(strAddressesId)) {
            logger.warn("addressesId are invalid");
            return new ArrayList<>();
        }

        List<Long> ids = VerifyUtil.parseLongIds(strAddressesId);
        return deleteByIds(ids);
    }

    @Transactional
    public List<UserAddressBean> deleteByIds(List<Long> addressesId) {
        logger.info("delete user address by addressesId={}", addressesId);
        if (VerifyUtil.isListEmpty(addressesId)) {
            return new ArrayList<>();
        }
        List<UserAddressEntity> userAddress = repository.findByIdIn(addressesId, sort);
        if (VerifyUtil.isListEmpty(userAddress)) {
            logger.info("delete nothing");
            return new ArrayList<>();
        }
        repository.delete(userAddress);

        List<UserAddressBean> retValue = new ArrayList<>();
        for (UserAddressEntity tmp : userAddress) {
            UserAddressBean address = beanConverter.convert(tmp);
            retValue.add(address);
        }
        return retValue;
    }

    //=======================================================
    //        update department
    //=======================================================

    @Transactional
    public UserAddressBean update(long addressId, int provinceId, int cityId, int grade, String address, CommonStatus status) {
        logger.info("update user address by addressId={} provinceId={} cityId={} grade={} address={} status={}",
                addressId, provinceId, cityId, grade, address, status);

        UserAddressEntity entity = repository.findOne(addressId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        boolean changed = false;
        if (checkRegion(provinceId, cityId)) {
            if (provinceId>0 && cityId>0) {
                entity.setProvinceId(provinceId);
                entity.setCityId(cityId);
                changed = true;
            }
        }
        if (grade>=0) {
            entity.setGrade(grade);
            changed = true;
        }
        if (!VerifyUtil.isStringEmpty(address) && !address.trim().equals(entity.getAddress())) {
            entity.setAddress(address.trim());
            changed = true;
        }
        if (null!=status && !status.equals(entity.getStatus())) {
            entity.setStatus(status);
            changed = true;
        }

        if (changed) {
            entity = repository.save(entity);
        }
        UserAddressBean bean = beanConverter.convert(entity);
        fillOtherProperties(bean);
        return bean;
    }

    //=======================================================
    //        create department
    //=======================================================

    @Transactional
    public UserAddressBean createAddress(long userId, int provinceId, int cityId, int grade, String address) {
        logger.info("add address to user={} with provinceId={} cityId={} grade={] address={}",
                userId, provinceId, cityId, grade, address);
        if (!checkRegion(provinceId, cityId)) {
            logger.info("province and city must set together and be matched");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (VerifyUtil.isStringEmpty(address)) {
            logger.info("address must not empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        grade = grade<0 ? 0 : grade;

        UserAddressEntity entity = new UserAddressEntity();
        entity.setUserId(userId);
        entity.setProvinceId(provinceId);
        entity.setCityId(cityId);
        entity.setAddress(address.trim());
        entity.setGrade(grade);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);

        entity = repository.save(entity);
        UserAddressBean bean = beanConverter.convert(entity);
        fillOtherProperties(bean);
        return bean;
    }

    private boolean checkRegion(int provinceId, int cityId) {
        List<Integer> regionsId = Arrays.asList(new Integer[]{provinceId, cityId});
        List<RegionBean> regions = regionService.getRegion(regionsId);
        if (VerifyUtil.isListEmpty(regions)) {
            return true;
        }
        RegionBean province = null;
        RegionBean city     = null;
        for (RegionBean region : regions) {
            if (region.getId()==provinceId) {
                province = region;
            }
            if (region.getId()==cityId) {
                city = region;
            }
        }
        logger.info("province={}  city={}", province, city);
        // do not check when one of they is null
        if (null!=province && null!=city) {
            if (city.getParentId() == province.getId()) {
                return true;
            }
        }

        return false;
    }

    //==================================================================================
    //                    used by getting method
    //==================================================================================

    private List<UserAddressBean> entities2Beans(Iterable<UserAddressEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }
        List<UserAddressBean> beans = new ArrayList<>();
        for (UserAddressEntity entity : entities) {
            UserAddressBean bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    private void fillOtherProperties(List<UserAddressBean> userAddresses) {
        if (null==userAddresses || userAddresses.isEmpty()) {
            return;
        }

        List<Integer> regionIds = new ArrayList<>();
        for (UserAddressBean address : userAddresses) {
            regionIds.add(address.getProvinceId());
            regionIds.add(address.getCityId());
        }

        List<RegionBean> regions = regionService.getRegion(regionIds);
        for (RegionBean region : regions) {
            for (UserAddressBean address : userAddresses) {
                if (address.getProvinceId()==region.getId()) {
                    address.setProvince(region);
                }
                if (address.getCityId()==region.getId()) {
                    address.setCity(region);
                }
            }
        }
    }

    private void fillOtherProperties(UserAddressBean userAddress) {
        if (null==userAddress) {
            return;
        }

        List<Integer> regionIds = new ArrayList<>();
        regionIds.add(userAddress.getProvinceId());
        regionIds.add(userAddress.getCityId());

        List<RegionBean> regions = regionService.getRegion(regionIds);
        for (RegionBean region : regions) {
            if (userAddress.getProvinceId()==region.getId()) {
                userAddress.setProvince(region);
            }
            if (userAddress.getCityId()==region.getId()) {
                userAddress.setCity(region);
            }
        }
    }
}

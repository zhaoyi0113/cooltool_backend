package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.ServiceVendorAuthorizationBean;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.converter.ServiceVendorAuthorizationBeanConverter;
import com.cooltoo.go2nurse.entities.ServiceVendorAuthorizationEntity;
import com.cooltoo.go2nurse.repository.ServiceVendorAuthorizationRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhaolisong on 2016/12/1.
 */
@Service("ServiceVendorAuthorizationService")
public class ServiceVendorAuthorizationService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceVendorAuthorizationService.class);


    @Autowired private ServiceVendorAuthorizationRepository repository;
    @Autowired private ServiceVendorAuthorizationBeanConverter beanConverter;

    //============================================================================
    //                 get
    //============================================================================
    public List<Long> getForbiddenUserIds(ServiceVendorType vendorType, long vendorId, long departId) {
        logger.info("get forbidden userIds by vendorType={} vendorId={} departId={}",
                vendorType, vendorId, departId);
        List<ServiceVendorAuthorizationEntity> resultSet = repository.findByStatusAndVendorTypeAndVendorIdAndDepartId(
                CommonStatus.ENABLED /* ENABLED means been forbidden */, vendorType, vendorId, departId);
        List<Long> userIds = new ArrayList<>();
        if (!VerifyUtil.isListEmpty(resultSet)) {
            for (ServiceVendorAuthorizationEntity tmp : resultSet) {
                if (!userIds.contains(tmp.getUserId())) {
                    userIds.add(tmp.getUserId());
                }
            }
        }
        logger.info("count is {}", userIds.size());
        return userIds;
    }

    public boolean isUserForbidden(long userId, ServiceVendorType vendorType, long vendorId, long departId) {
        boolean forbidden = false;
        List<ServiceVendorAuthorizationEntity> relations = repository.findByUserIdAndVendorTypeAndVendorIdAndDepartId(userId, vendorType, vendorId, departId);
        if (!VerifyUtil.isListEmpty(relations)) {
            forbidden = beanConverter.convert(relations.get(0)).isForbidden();;
        }
        logger.info("is user={} forbidden by vendorType={} vendorId={} departId={}? {}",
                userId, vendorType, vendorId, departId, forbidden);
        return forbidden;
    }

    public void throwUserForbiddenException(long userId, ServiceVendorType vendorType, long vendorId, long departId) {
        if (isUserForbidden(userId, vendorType, vendorId, departId)) {
            throw new BadRequestException(ErrorCode.USER_FORBIDDEN_BY_VENDOR);
        }
    }


    //============================================================================
    //                 setter
    //============================================================================
    @Transactional
    public void forbidUser(long userId, ServiceVendorType vendorType, long vendorId, long departId, CommonStatus forbidden) {
        logger.info("set forbidden by userId={} vendorType={} vendorId={} departId={} forbidden={}",
                userId, vendorType, vendorId, departId, forbidden);
        ServiceVendorAuthorizationEntity entity = null;
        List<ServiceVendorAuthorizationEntity> relations = repository.findByUserIdAndVendorTypeAndVendorIdAndDepartId(userId, vendorType, vendorId, departId);
        if (CommonStatus.ENABLED.equals(forbidden)) {
            if (!VerifyUtil.isListEmpty(relations)) {
                entity = relations.get(0);
                relations.remove(entity);
            } else {
                entity = new ServiceVendorAuthorizationEntity();
                entity.setUserId(userId);
                entity.setVendorType(vendorType);
                entity.setVendorId(vendorId);
                entity.setDepartId(departId);
            }
            entity.setTime(new Date());
            entity.setStatus(CommonStatus.ENABLED);
            entity = repository.save(entity);
        }

        relations = repository.findByUserIdAndVendorTypeAndVendorIdAndDepartId(userId, vendorType, vendorId, departId);
        if (null!=entity && !VerifyUtil.isListEmpty(relations)) {
            for (int i=0; i<relations.size(); i++) {
                ServiceVendorAuthorizationEntity tmp = relations.get(i);
                if (tmp.getId()==entity.getId()) {
                    relations.remove(i);
                    break;
                }
            }
        }
        if (!VerifyUtil.isListEmpty(relations)) {
            repository.delete(relations);
        }

        return;
    }

}

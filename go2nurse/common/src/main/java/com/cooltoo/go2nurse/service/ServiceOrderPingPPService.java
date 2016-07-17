package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.ServiceOrderPingPPBean;
import com.cooltoo.go2nurse.constants.AppType;
import com.cooltoo.go2nurse.constants.PingPPType;
import com.cooltoo.go2nurse.converter.ServiceOrderPingPPBeanConverter;
import com.cooltoo.go2nurse.entities.ServiceOrderPingPPEntity;
import com.cooltoo.go2nurse.repository.ServiceOrderPingPPRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by hp on 2016/7/15.
 */
@Service("ServiceOrderPingPPService")
public class ServiceOrderPingPPService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceOrderPingPPService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private ServiceOrderPingPPRepository repository;
    @Autowired private ServiceOrderPingPPBeanConverter beanConverter;

    public List<ServiceOrderPingPPBean> getOrderPingPPResult(AppType appType, long orderId) {
        logger.info("get order ping++ information by appType={} orderId={}", appType, orderId);
        List<ServiceOrderPingPPEntity> entities = repository.findByAppTypeAndOrderId(appType, orderId, sort);
        List<ServiceOrderPingPPBean> beans = entitiesToBeans(entities);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public Map<Long, List<ServiceOrderPingPPBean>> getOrderPingPPResult(AppType appType, List<Long> orderIds) {
        logger.info("get order ping++ information by appType={} orderIds={}", appType, orderIds);
        if (VerifyUtil.isListEmpty(orderIds)) {
            return new HashMap<>();
        }
        List<ServiceOrderPingPPEntity> entities = repository.findByAppTypeAndOrderIdIn(appType, orderIds, sort);
        List<ServiceOrderPingPPBean> beans = entitiesToBeans(entities);
        logger.info("ping++ info count is {}", beans.size());

        List<Long> idSearched = new ArrayList<>();
        Map<Long, List<ServiceOrderPingPPBean>> returnMap = new HashMap<>();
        for (Long orderId : orderIds) {
            if (idSearched.contains(orderId)) {
                continue;
            }

            idSearched.add(orderId);
            List<ServiceOrderPingPPBean> pingPPInfo = new ArrayList<>();
            for (ServiceOrderPingPPBean bean : beans) {
                if (orderId==bean.getOrderId()) {
                    pingPPInfo.add(bean);
                }
            }
            returnMap.put(orderId, pingPPInfo);
        }
        return returnMap;
    }

    private List<ServiceOrderPingPPBean> entitiesToBeans(Iterable<ServiceOrderPingPPEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }
        List<ServiceOrderPingPPBean> beans = new ArrayList<>();
        for (ServiceOrderPingPPEntity entity : entities) {
            ServiceOrderPingPPBean bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    //==============================================================================
    //                      updating
    //==============================================================================
    @Transactional
    public ServiceOrderPingPPBean addOrderPingPPResult(AppType appType, PingPPType pingPPType, String pingPPId, String pingPPJson) {
        logger.info("update order ping++'s json by appType={} pingPPType={} pingPPId={} with pingPPJson={}",
                appType, pingPPType, pingPPId, pingPPJson);

        ServiceOrderPingPPEntity entity = repository.findByAppTypeAndPingPPTypeAndPingPPId(appType, pingPPType, pingPPId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        boolean changed = false;
        if (!VerifyUtil.isStringEmpty(pingPPJson)) {
            entity.setPingPPJson(pingPPJson);
            changed = true;
        }

        if (changed) {
            entity = repository.save(entity);
        }

        ServiceOrderPingPPBean bean = beanConverter.convert(entity);
        logger.info("update ping++ is {}", bean);
        return bean;
    }

    //==============================================================================
    //                      adding
    //==============================================================================
    @Transactional
    public ServiceOrderPingPPBean addOrderPingPPResult(AppType appType, long orderId, PingPPType pingPPType, String pingPPId, String pingPPJson) {
        logger.info("create order ping++ by appType={} orderId={} pingPPType={} pingPPId={} pingPPJson={}",
                appType, orderId, pingPPType, pingPPId, pingPPJson);
        if (null==appType) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (null==pingPPType) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (VerifyUtil.isStringEmpty(pingPPId)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (VerifyUtil.isStringEmpty(pingPPJson)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        ServiceOrderPingPPEntity entity = new ServiceOrderPingPPEntity();
        entity.setAppType(appType);
        entity.setOrderId(orderId);
        entity.setPingPPType(pingPPType);
        entity.setPingPPId(pingPPId);
        entity.setPingPPJson(pingPPJson);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity = repository.save(entity);

        ServiceOrderPingPPBean bean = beanConverter.convert(entity);
        logger.info("new ping++ is {}", bean);
        return bean;
    }
}

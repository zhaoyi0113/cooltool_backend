package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.ServiceOrderChargePingPPBean;
import com.cooltoo.go2nurse.constants.AppType;
import com.cooltoo.go2nurse.constants.ChargeStatus;
import com.cooltoo.go2nurse.constants.ChargeType;
import com.cooltoo.go2nurse.converter.ServiceOrderChargePingPPBeanConverter;
import com.cooltoo.go2nurse.entities.ServiceOrderChargePingPPEntity;
import com.cooltoo.go2nurse.repository.ServiceOrderChargePingPPRepository;
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
@Service("ServiceOrderChargePingPPService")
public class ServiceOrderChargePingPPService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceOrderChargePingPPService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.ASC, "time"),
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    @Autowired private ServiceOrderChargePingPPRepository repository;
    @Autowired private ServiceOrderChargePingPPBeanConverter beanConverter;

    public List<ServiceOrderChargePingPPBean> getOrderPingPPResult(AppType appType, long orderId) {
        logger.info("get order ping++ information by appType={} orderId={}", appType, orderId);
        List<ServiceOrderChargePingPPEntity> entities = repository.findByAppTypeAndOrderId(appType, orderId, sort);
        List<ServiceOrderChargePingPPBean> beans = entitiesToBeans(entities);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public Map<Long, List<ServiceOrderChargePingPPBean>> getOrderPingPPResult(AppType appType, List<Long> orderIds) {
        logger.info("get order ping++ information by appType={} orderIds={}", appType, orderIds);
        if (VerifyUtil.isListEmpty(orderIds)) {
            return new HashMap<>();
        }
        List<ServiceOrderChargePingPPEntity> entities = repository.findByAppTypeAndOrderIdIn(appType, orderIds, sort);
        List<ServiceOrderChargePingPPBean> beans = entitiesToBeans(entities);
        logger.info("ping++ info count is {}", beans.size());

        List<Long> idSearched = new ArrayList<>();
        Map<Long, List<ServiceOrderChargePingPPBean>> returnMap = new HashMap<>();
        for (Long orderId : orderIds) {
            if (idSearched.contains(orderId)) {
                continue;
            }

            idSearched.add(orderId);
            List<ServiceOrderChargePingPPBean> pingPPInfo = new ArrayList<>();
            for (ServiceOrderChargePingPPBean bean : beans) {
                if (orderId==bean.getOrderId()) {
                    pingPPInfo.add(bean);
                }
            }
            returnMap.put(orderId, pingPPInfo);
        }
        return returnMap;
    }

    private List<ServiceOrderChargePingPPBean> entitiesToBeans(Iterable<ServiceOrderChargePingPPEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }
        List<ServiceOrderChargePingPPBean> beans = new ArrayList<>();
        for (ServiceOrderChargePingPPEntity entity : entities) {
            ServiceOrderChargePingPPBean bean = beanConverter.convert(entity);
            beans.add(bean);
        }
        return beans;
    }

    //==============================================================================
    //                      updating
    //==============================================================================
    @Transactional
    public ServiceOrderChargePingPPBean orderChargePingPpWebhooks(String chargeId, String webhooksEventId, String webhooksEventJson) {
        logger.info("order charge webhooks event callback chargeId={} webhooksEventId={} webhooksEventJson={}",
                chargeId, webhooksEventId, webhooksEventJson);

        List<ServiceOrderChargePingPPEntity> entities = repository.findByChargeId(chargeId);
        if (VerifyUtil.isListEmpty(entities)) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (VerifyUtil.isStringEmpty(webhooksEventId)) {
            logger.error("webhooks event id is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (VerifyUtil.isStringEmpty(webhooksEventJson)) {
            logger.error("webhooks event json is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        ServiceOrderChargePingPPEntity entity = null;
        for (ServiceOrderChargePingPPEntity tmp : entities) {
            if (!CommonStatus.ENABLED.equals(tmp.getStatus())) {
                continue;
            }
            entity = tmp;
        }
        if (null==entity) {
            logger.error("no charge is valid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        entity.setWebhooksEventId(webhooksEventId);
        entity.setWebhooksEventJson(webhooksEventJson);
        entity.setChargeStatus(ChargeStatus.CHARGE_SUCCEED);
        entity = repository.save(entity);

        ServiceOrderChargePingPPBean bean = beanConverter.convert(entity);
        logger.info("order charge webhooks event callback is {}", bean);
        return bean;
    }

    //==============================================================================
    //                      adding
    //==============================================================================
    @Transactional
    public ServiceOrderChargePingPPBean addOrderCharge(long orderId, AppType appType, String orderNo, String channel,
                                                       ChargeType chargeType, String chargeId, String chargeJson) {
        logger.info("create order charge by orderId={} appType={} pingPPType={} pingPPJson={}",
                orderId, appType, chargeType, chargeJson);
        if (null==appType) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (VerifyUtil.isStringEmpty(orderNo)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (VerifyUtil.isStringEmpty(channel)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (null== chargeType) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (VerifyUtil.isStringEmpty(chargeId)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (VerifyUtil.isStringEmpty(chargeJson)) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        ServiceOrderChargePingPPEntity entity = null;
        List<ServiceOrderChargePingPPEntity> entities = repository.findByAppTypeAndOrderId(appType, orderId, sort);
        if (VerifyUtil.isListEmpty(entities)) {
            entity = new ServiceOrderChargePingPPEntity();
        }
        else {
            // user had pay for it
            for (ServiceOrderChargePingPPEntity tmp : entities) {
                if (ChargeStatus.CHARGE_SUCCEED.equals(tmp.getChargeStatus())) {
                    throw new BadRequestException(ErrorCode.HAS_PAID);
                }
            }

            // delete the invalid charges
            entity = entities.get(0);
            entities.remove(0);
            repository.delete(entities);
        }
        entity.setOrderId(orderId);
        entity.setOrderNo(orderNo);
        entity.setChannel(channel);
        entity.setAppType(appType);
        entity.setChargeType(chargeType);
        entity.setChargeId(chargeId);
        entity.setChargeJson(chargeJson);
        entity.setChargeStatus(ChargeStatus.CHARGE_CREATED);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity = repository.save(entity);

        ServiceOrderChargePingPPBean bean = beanConverter.convert(entity);
        logger.info("ping++ is {}", bean);
        return bean;
    }
}

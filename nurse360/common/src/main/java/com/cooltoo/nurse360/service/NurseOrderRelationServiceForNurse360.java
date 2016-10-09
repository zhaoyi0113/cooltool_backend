package com.cooltoo.nurse360.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.NurseOrderRelationBean;
import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.converter.NurseOrderRelationBeanConverter;
import com.cooltoo.go2nurse.entities.NurseOrderRelationEntity;
import com.cooltoo.go2nurse.repository.NurseOrderRelationRepository;
import com.cooltoo.go2nurse.service.ServiceOrderService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhaolisong on 16/9/28.
 */
@Service("NurseOrderRelationServiceForNurse360")
public class NurseOrderRelationServiceForNurse360 {

    private static final Logger logger = LoggerFactory.getLogger(NurseOrderRelationServiceForNurse360.class);

    public static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private NurseOrderRelationRepository repository;
    @Autowired private NurseOrderRelationBeanConverter beanConverter;
    @Autowired private ServiceOrderService orderService;

    //============================================================================
    //                 get
    //============================================================================
    public List<ServiceOrderBean> getOrderByOrderNo(String orderNo) {
        List<ServiceOrderBean> beans = orderService.getOrderByOrderNo(orderNo);
        return beans;
    }

    public List<ServiceOrderBean> getOrderByNurseId(long nurseId, String strStatus, int pageIndex, int sizePerPage) {
        logger.info("get orders by nurseId={} with status={}", nurseId, strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<NurseOrderRelationEntity> resultSet = repository.findByNurseIdAndStatus(nurseId, status, sort);
        List<Long> orderIds = new ArrayList<>();
        if (!VerifyUtil.isListEmpty(resultSet)) {
            for (NurseOrderRelationEntity tmp : resultSet) {
                if (orderIds.contains(tmp.getOrderId())) {
                    continue;
                }
                orderIds.add(tmp.getOrderId());
            }
        }
        List<ServiceOrderBean> orders = orderService.getOrderByIds(orderIds, pageIndex, sizePerPage);
        logger.info("count is {}", orders.size());
        return orders;
    }

    public List<ServiceOrderBean> getOrderByNurseIdAndOrderStatus(long nurseId, String strStatus, OrderStatus orderStatus) {
        logger.info("get orders by nurseId={} with status={} and orderStatus={}", nurseId, strStatus, orderStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<NurseOrderRelationEntity> resultSet = repository.findByNurseIdAndStatus(nurseId, status, sort);
        List<Long> orderIds = new ArrayList<>();
        if (!VerifyUtil.isListEmpty(resultSet)) {
            for (NurseOrderRelationEntity tmp : resultSet) {
                if (orderIds.contains(tmp.getOrderId())) {
                    continue;
                }
                orderIds.add(tmp.getOrderId());
            }
        }
        List<ServiceOrderBean> orders = orderService.getOrderByIdsAndOrderStatus(orderIds, orderStatus);
        logger.info("count is {}", orders.size());
        return orders;
    }


    //============================================================================
    //                 update
    //============================================================================
    @Transactional
    public NurseOrderRelationBean updateStatus(long nurseId, long orderId, String strStatus) {
        logger.info("update relation status to={} between nurse={} order={}",
                strStatus, nurseId, orderId);
        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null==status) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        List<NurseOrderRelationEntity> relations = repository.findByNurseIdAndOrderId(nurseId, orderId, sort);
        if (VerifyUtil.isListEmpty(relations)) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        NurseOrderRelationEntity entity = relations.get(0);
        relations.remove(entity);

        entity.setStatus(status);
        entity = repository.save(entity);

        if (!VerifyUtil.isListEmpty(relations)) {
            repository.delete(relations);
        }

        NurseOrderRelationBean bean = beanConverter.convert(entity);
        logger.info("update relation={}", bean);
        return bean;
    }

    //============================================================================
    //                 add
    //============================================================================
    @Transactional
    public long grabOrder(long nurseId, long orderId) {
        logger.info("add order={} to nurse={}", orderId, nurseId);
        if (!orderService.existOrder(orderId)) {
            logger.info("order not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        NurseOrderRelationEntity entity = null;
        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "id"));
        List<NurseOrderRelationEntity> relations = repository.findByOrderId(orderId, sort);
        if (!VerifyUtil.isListEmpty(relations)) {
            entity = relations.get(0);
            relations.remove(entity);
        }
        else {
            entity = new NurseOrderRelationEntity();
            entity.setNurseId(nurseId);
            entity.setOrderId(orderId);
            entity.setTime(new Date());
        }
        entity.setStatus(CommonStatus.ENABLED);
        repository.save(entity);

        relations = repository.findByOrderId(orderId, sort);
        entity = relations.get(0);
        relations.remove(0);
        if (!VerifyUtil.isListEmpty(relations)) {
            repository.delete(relations);
        }

        if (nurseId!=entity.getNurseId()) {
            logger.info("order has been grabbed");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        NurseOrderRelationBean bean = beanConverter.convert(entity);
        logger.info("add relation={}", bean);
        return bean.getNurseId();
    }

}

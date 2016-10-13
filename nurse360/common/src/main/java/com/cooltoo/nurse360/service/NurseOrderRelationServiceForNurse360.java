package com.cooltoo.nurse360.service;

import com.cooltoo.beans.NurseExtensionBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.entities.NurseHospitalRelationEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.NurseOrderRelationBean;
import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.converter.NurseOrderRelationBeanConverter;
import com.cooltoo.go2nurse.entities.NurseOrderRelationEntity;
import com.cooltoo.go2nurse.repository.NurseOrderRelationRepository;
import com.cooltoo.go2nurse.service.ServiceOrderService;
import com.cooltoo.repository.NurseHospitalRelationRepository;
import com.cooltoo.services.NurseExtensionService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by zhaolisong on 16/9/28.
 */
@Service("NurseOrderRelationServiceForNurse360")
public class NurseOrderRelationServiceForNurse360 {

    private static final Logger logger = LoggerFactory.getLogger(NurseOrderRelationServiceForNurse360.class);

    private static final Sort nurseHospitalRelationSort = new Sort(new Sort.Order(Sort.Direction.DESC, "id"));
    private static final Sort sort = new Sort(new Sort.Order(Sort.Direction.DESC, "id"));

    private static final List<OrderStatus> orderStatuses = Arrays.asList(new OrderStatus[]{OrderStatus.TO_DISPATCH, OrderStatus.TO_SERVICE});

    @Autowired private NurseExtensionService nurseExtensionService;
    @Autowired private NurseHospitalRelationRepository nurseHospitalRelationRepository;
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

    public List<ServiceOrderBean> getAllOrder(long nurseId, String strStatus, int pageIndex, int sizePerPage) {
        logger.info("get orders by nurseId={} with status={}", nurseId, strStatus);

        List<ServiceOrderBean> orders = new ArrayList<>();

        // get nurse extension information
        NurseExtensionBean nurseExtensionInfo = nurseExtensionService.getExtensionByNurseId(nurseId);
        boolean canSeeAllOrder = true;
        if (null==nurseExtensionInfo || null==nurseExtensionInfo.getCanSeeAllOrder() || !YesNoEnum.YES.equals(nurseExtensionInfo.getCanSeeAllOrder())) {
            canSeeAllOrder = false;
        }

        // get nurse hospital department relation
        List<NurseHospitalRelationEntity> nurseHospitalRelations = nurseHospitalRelationRepository.findByNurseId(nurseId, nurseHospitalRelationSort);
        if (null==nurseHospitalRelations || nurseHospitalRelations.isEmpty() || nurseHospitalRelations.size()!=1) {
            return orders;
        }
        NurseHospitalRelationEntity nurseHospitalRelation = nurseHospitalRelations.get(0);

        // get orders
        if (canSeeAllOrder) {
            orders = orderService.getOrderByConditions(null, null, orderStatuses, pageIndex, sizePerPage);
        }
        else {
            orders = orderService.getOrderByConditions(
                    new Long(nurseHospitalRelation.getHospitalId()), ServiceVendorType.HOSPITAL,
                    orderStatuses, pageIndex, sizePerPage);
        }

//        // order ids
//        List<Long> orderIds = new ArrayList<>();
//        for (ServiceOrderBean tmp : orders) {
//            if (!orderIds.contains(tmp.getId())) {
//                orderIds.add(tmp.getId());
//            }
//        }
//
//        // get order has been fetchbed or not
//        CommonStatus status = CommonStatus.parseString(strStatus);
//        List<NurseOrderRelationEntity> resultSet = repository.findByOrderIdInAndStatus(orderIds, status);
//        Map<Long, Long> orderIdToGrabberId = new HashMap<>();
//        if (!VerifyUtil.isListEmpty(resultSet)) {
//            for (NurseOrderRelationEntity tmp : resultSet) {
//                orderIdToGrabberId.put(tmp.getOrderId(), tmp.getNurseId());
//            }
//        }

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

    @Transactional
    public long completedOrder(long nurseId, long orderId) {
        logger.info("nurse={} complete order={}", nurseId, orderId);
        if (!orderService.existOrder(orderId)) {
            logger.info("order not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "id"));
        List<NurseOrderRelationEntity> relations = repository.findByOrderId(orderId, sort);
        boolean orderBelongToNurse = true;
        if (VerifyUtil.isListEmpty(relations)) {
            orderBelongToNurse = false;
        }
        else {
            for (NurseOrderRelationEntity tmp : relations) {
                if (tmp.getNurseId()!=nurseId) {
                    orderBelongToNurse = false;
                }
            }
        }
        if (!orderBelongToNurse) {
            logger.info("order not belong this nurse");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        // complete order
        ServiceOrderBean order = orderService.completedOrder(false, 0, orderId);

        logger.info("complete order={}", order);
        return orderId;
    }

    //============================================================================
    //                 add
    //============================================================================
    @Transactional
    public long fetchOrder(long nurseId, long orderId) {
        logger.info("nurse={} fetches order={}", orderId, nurseId);
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
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        repository.save(entity);

        relations = repository.findByOrderId(orderId, sort);
        entity = relations.get(0);
        relations.remove(0);
        if (!VerifyUtil.isListEmpty(relations)) {
            repository.delete(relations);
        }

        if (nurseId!=entity.getNurseId()) {
            logger.info("order has been fetched");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        // update order status
        orderService.nurseFetchOrder(orderId);

        NurseOrderRelationBean bean = beanConverter.convert(entity);
        logger.info("add relation={}", bean);
        return bean.getNurseId();
    }

    //============================================================================
    //                 delete
    //============================================================================
    @Transactional
    public long giveUpOrder(long nurseId, long orderId) {
        logger.info("nurse={} give up order={}", nurseId, orderId);
        if (!orderService.existOrder(orderId)) {
            logger.info("order not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "id"));
        List<NurseOrderRelationEntity> relations = repository.findByOrderId(orderId, sort);
        boolean orderBelongToNurse = true;
        if (VerifyUtil.isListEmpty(relations)) {
            orderBelongToNurse = false;
        }
        else {
            for (NurseOrderRelationEntity tmp : relations) {
                if (tmp.getNurseId()!=nurseId) {
                    orderBelongToNurse = false;
                }
            }
        }
        if (!orderBelongToNurse) {
            logger.info("order not belong this nurse");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        repository.delete(relations);

        // update order status
        orderService.nurseGiveUpOrder(orderId);

        return nurseId;
    }

}

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
import com.cooltoo.go2nurse.service.notification.NotifierServiceForGo2NurseAndNurse360;
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
    @Autowired private NotifierServiceForGo2NurseAndNurse360 orderNotifierService;

    //============================================================================
    //                 get
    //============================================================================
    public List<ServiceOrderBean> getOrderByOrderNo(long nurseId, String orderNo) {
        List<ServiceOrderBean> beans = orderService.getOrderByOrderNo(orderNo);
        if (!VerifyUtil.isListEmpty(beans)) {
            long orderId = beans.get(0).getId();
            List<NurseOrderRelationEntity> relations = repository.findByOrderId(orderId, sort);
            if (!VerifyUtil.isListEmpty(relations)) {
                beans.get(0).setIsNurseFetched(nurseId==relations.get(0).getNurseId() ? YesNoEnum.YES : YesNoEnum.NO);
            }
            else {
                beans.get(0).setIsNurseFetched(YesNoEnum.NO);
            }
        }
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

        List<Long> orderId = repository.findOrderIdByNurseId(nurseId);
        if (null==orderId) { orderId = new ArrayList<>(); }
        for (ServiceOrderBean tmp : orders) {
            tmp.setIsNurseFetched(orderId.contains(tmp.getId()) ? YesNoEnum.YES : YesNoEnum.NO);
        }

        logger.info("count is {}", orders.size());
        return orders;
    }

    public List<ServiceOrderBean> getOrderByNurseIdAndOrderStatus(long nurseId, String strStatus, OrderStatus orderStatus, int pageIndex, int sizePerPage) {
        logger.info("get orders by nurseId={} with status={} and orderStatus={} at index={} number={}", nurseId, strStatus, orderStatus, pageIndex, sizePerPage);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<Long> resultSet = repository.findByNurseIdAndStatus(nurseId, status, sort);
        List<Long> orderIdExisted = orderService.isOrderIdExisted(resultSet, orderStatus);
        List<Long> orderIdExistedSorted = new ArrayList<>();
        if (!VerifyUtil.isListEmpty(resultSet)) {
            for (Long tmp : resultSet) {
                if (orderIdExistedSorted.contains(tmp)) {
                    continue;
                }
                if (!orderIdExisted.contains(tmp)) {
                    continue;
                }
                orderIdExistedSorted.add(tmp);
            }
        }
        resultSet.clear();
        int startIndex = (pageIndex*sizePerPage)<0 ? 0 : (pageIndex*sizePerPage);
        for (int i=startIndex; i<orderIdExistedSorted.size(); i++) {
            if (i<(pageIndex*sizePerPage + sizePerPage)) {
                resultSet.add(orderIdExistedSorted.get(i));
                continue;
            }
        }

        List<ServiceOrderBean> beans = orderService.getOrderByIds(resultSet);
        List<ServiceOrderBean> beanSorted = new ArrayList<>();
        for (Long tmpId : resultSet) {
            for (ServiceOrderBean tmp : beans) {
                if (tmp.getId()==tmpId) {
                    beanSorted.add(tmp);
                    tmp.setIsNurseFetched(YesNoEnum.YES);
                    break;
                }
            }
        }

        logger.info("count is {}", beanSorted.size());
        return beanSorted;
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
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_NOT_EXPECTED);
        }
        List<NurseOrderRelationEntity> relations = repository.findByNurseIdAndOrderId(nurseId, orderId, sort);
        if (VerifyUtil.isListEmpty(relations)) {
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
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
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
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
            throw new BadRequestException(ErrorCode.NURSE360_PARAMETER_NOT_EXPECTED);
        }

        // complete order
        ServiceOrderBean order = orderService.completedOrder(false, 0, orderId);
        orderNotifierService.orderAlertToPatient(order.getUserId(), order.getId(), order.getOrderStatus(), "order completed!");


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
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
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
            throw new BadRequestException(ErrorCode.NURSE360_SERVICE_ORDER_BEEN_FETCHED);
        }

        // update order status
        ServiceOrderBean order = orderService.nurseFetchOrder(orderId);
        orderNotifierService.orderAlertToPatient(order.getUserId(), order.getId(), order.getOrderStatus(), "order fetched!");

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
            throw new BadRequestException(ErrorCode.NURSE360_RECORD_NOT_FOUND);
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
            throw new BadRequestException(ErrorCode.NURSE360_SERVICE_ORDER_NOT_YOURS);
        }

        repository.delete(relations);

        // update order status
        ServiceOrderBean order = orderService.nurseGiveUpOrder(orderId);
        orderNotifierService.orderAlertToPatient(order.getUserId(), order.getId(), order.getOrderStatus(), "order be given up!");

        return nurseId;
    }

}

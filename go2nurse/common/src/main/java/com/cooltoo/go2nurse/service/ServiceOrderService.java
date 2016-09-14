package com.cooltoo.go2nurse.service;

import com.cooltoo.go2nurse.constants.AppType;
import com.cooltoo.go2nurse.constants.ChargeType;
import com.cooltoo.go2nurse.openapp.WeChatService;
import com.cooltoo.go2nurse.service.notification.MessageBean;
import com.cooltoo.go2nurse.service.notification.MessageType;
import com.cooltoo.go2nurse.service.notification.Notifier;
import com.pingplusplus.model.Charge;
import com.cooltoo.beans.HospitalBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.converter.ServiceOrderBeanConverter;
import com.cooltoo.go2nurse.entities.ServiceOrderEntity;
import com.cooltoo.go2nurse.repository.ServiceOrderRepository;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
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

import java.util.*;

/**
 * Created by hp on 2016/7/13.
 */
@Service("ServiceOrderService")
public class ServiceOrderService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceOrderService.class);

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private ServiceOrderRepository repository;
    @Autowired private ServiceOrderBeanConverter beanConverter;

    @Autowired private ServiceVendorCategoryAndItemService serviceCategoryItemService;
    @Autowired private PatientService patientService;
    @Autowired private UserAddressService addressService;
    @Autowired private Go2NurseUtility go2NurseUtility;
    @Autowired private ServiceOrderChargePingPPService orderPingPPService;
    @Autowired private PingPPService pingPPService;
    @Autowired private WeChatService weChatService;

    @Autowired private Notifier notifier;

    //=====================================================================
    //                   getting
    //=====================================================================
    private String getOrderNo() {
        String orderNo = System.currentTimeMillis()+"";
        for (int i = 10; i>0; i--) {
            orderNo = NumberUtil.getUniqueString();
            if (repository.countByOrderNo(orderNo)<=0) {
                break;
            }
            else {
                orderNo = null;
            }
        }
        return orderNo;
    }

    public long countAllOrder() {
        long count = repository.count();
        logger.info("count all service order is {}", count);
        return count;
    }

    public List<ServiceOrderBean> getOrder(int pageIndex, int sizePerPage) {
        logger.info("get service order at page={} number={}", pageIndex, sizePerPage);
        PageRequest pageRequest = new PageRequest(pageIndex, sizePerPage, sort);
        Page<ServiceOrderEntity> resultSet = repository.findAll(pageRequest);
        List<ServiceOrderBean> beans = entitiesToBeans(resultSet);
        fillOtherProperties(beans);
        logger.info("service order count is {}", beans.size());
        return beans;
    }

    public long countOrderByConditions(Long serviceItemId, Long userId,
                                       Long categoryId, Long topCategoryId,
                                       Long vendorId, ServiceVendorType vendorType,
                                       OrderStatus orderStatus
    ) {
        logger.info("get order by userId={} itemId={} categoryId={} topCategoryId={} vendorType={} vendorId={} orderStatus={}",
                userId, serviceItemId, categoryId, topCategoryId, vendorType, vendorId, orderStatus);
        long count = repository.countByConditions(serviceItemId, userId, categoryId, topCategoryId, vendorType, vendorId, orderStatus);
        logger.info("count is {}", count);
        return count;
    }

    public List<ServiceOrderBean> getOrderByConditions(Long serviceItemId, Long userId,
                                                       Long categoryId, Long topCategoryId,
                                                       Long vendorId, ServiceVendorType vendorType,
                                                       OrderStatus orderStatus,
                                                       int pageIndex, int sizePerPage
    ) {
        logger.info("get order by userId={} itemId={} categoryId={} topCategoryId={} vendorType={} vendorId={} orderStatus={} pageIndex={} sizePerPage={}",
                userId, serviceItemId, categoryId, topCategoryId, vendorType, vendorId, orderStatus, pageIndex, sizePerPage);
        PageRequest pageRequest = new PageRequest(pageIndex, sizePerPage, sort);
        Page<ServiceOrderEntity> entities = repository.findByConditions(serviceItemId, userId, categoryId, topCategoryId, vendorType, vendorId, orderStatus, pageRequest);
        List<ServiceOrderBean> beans = entitiesToBeans(entities);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public List<ServiceOrderBean> getOrderByOrderId(long orderId) {
        logger.info("get service order by orderId={}", orderId);
        ServiceOrderEntity entity = repository.findOne(orderId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        List<ServiceOrderEntity> entities = new ArrayList<>();
        entities.add(entity);
        List<ServiceOrderBean> beans = entitiesToBeans(entities);
        fillOtherProperties(beans);
        logger.info("service order count is {}", beans.size());
        return beans;
    }

    public List<ServiceOrderBean> getOrderByUserId(long userId) {
        logger.info("get service order by userId={}", userId);
        List<ServiceOrderEntity> entities = repository.findByUserId(userId, sort);
        List<ServiceOrderBean> beans = entitiesToBeans(entities);
        fillOtherProperties(beans);
        logger.info("service order count is {}", beans.size());
        return beans;
    }

    private List<ServiceOrderBean> entitiesToBeans(Iterable<ServiceOrderEntity> entities) {
        if (null==entities) {
            return new ArrayList<>();
        }
        List<ServiceOrderBean> beans = new ArrayList<>();
        for (ServiceOrderEntity entity : entities) {
            ServiceOrderBean bean = beanConverter.convert(entity);
            bean.setProperty(ServiceOrderBean.FLAG, "order");
            beans.add(bean);
        }
        return beans;
    }

    private void fillOtherProperties(List<ServiceOrderBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }
        List<Long> orderIds = new ArrayList<>();
        for (ServiceOrderBean tmp : beans) {
            orderIds.add(tmp.getId());
        }
        Map<Long, List<ServiceOrderChargePingPPBean>> orderId2Charge = orderPingPPService.getOrderPingPPResult(AppType.GO_2_NURSE, orderIds);
        for (ServiceOrderBean tmp : beans) {
            List<ServiceOrderChargePingPPBean> charges = orderId2Charge.get(tmp.getId());
            if (null==charges) {
                tmp.setPingPP(new ArrayList<>());
            }
            else {
                tmp.setPingPP(charges);
            }
        }
    }

    //=====================================================================
    //                   deleting
    //=====================================================================

    //=====================================================================
    //                   updating
    //=====================================================================
    @Transactional
    public ServiceOrderBean updateOrder(long orderId, Long patientId, Long addressId,
                                        String strStartTime, Integer count, String leaveAMessage, int preferentialCent) {
        logger.info("update service order={} by patientId={} addressId={} strStartTime={} count={} leaveAMessage={} preferentialCent={}",
                orderId, patientId, addressId, strStartTime, count, leaveAMessage, preferentialCent);

        ServiceOrderEntity entity = repository.findOne(orderId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        if (!OrderStatus.TO_PAY.equals(entity.getOrderStatus())) {
            logger.info("the order is in status={}, can not be modified", entity.getOrderStatus());
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        boolean changed = false;
        if (patientId!=null && patientService.existPatient(patientId)) {
            PatientBean patient = patientService.getOneById(patientId);
            String patientJson = go2NurseUtility.toJsonString(patient);
            entity.setPatientId(patientId);
            entity.setPatient(patientJson);
            changed = true;
        }

        if (addressId!=null && addressService.existAddress(addressId)) {
            UserAddressBean address = addressService.getOneById(addressId);
            String addressJson = go2NurseUtility.toJsonString(address);
            entity.setAddressId(addressId);
            entity.setAddress(addressJson);
            changed = true;
        }

        long lStartTime = NumberUtil.getTime(strStartTime, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        if (lStartTime>0) {
            entity.setServiceStartTime(new Date(lStartTime));
            changed = true;
        }

        if (null!=count) {
            ServiceOrderBean bean = beanConverter.convert(entity);
            ServiceItemBean item = bean.getServiceItem();
            entity.setServiceTimeDuration(item.getServiceTimeDuration()*count);
            entity.setTotalConsumptionCent(item.getServicePriceCent()*count);
            changed = true;
        }

        if (!VerifyUtil.isStringEmpty(leaveAMessage)) {
            entity.setLeaveAMessage(leaveAMessage);
            changed = true;
        }

        if (preferentialCent>0 && preferentialCent!=entity.getPreferentialCent()) {
            entity.setPreferentialCent(preferentialCent);
            changed = true;
        }

        if (changed) {
            entity = repository.save(entity);
        }

        ServiceOrderBean bean = beanConverter.convert(entity);
        logger.info("service order added is {}", bean);
        return bean;
    }

    @Transactional
    public Charge payForService(Long userId, Long orderId, String channel, String clientIP) {
        logger.info("create charge object for order={} channel={} clientIp={}", orderId, channel, clientIP);
        ServiceOrderEntity entity = repository.findOne(orderId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (userId!=entity.getUserId()) {
            logger.error("this order does not belong to this user");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (VerifyUtil.isStringEmpty(channel)) {
            logger.warn("channel is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        ServiceOrderBean order = beanConverter.convert(entity);
        String orderNo = order.getOrderNo();
        Map extra = new HashMap<>();
        if(channel.equals("wx_pub")){
            String openId = weChatService.getOpenIdByUserId(userId);
            extra.put("open_id", openId);
        }
        Charge charge = pingPPService.createCharge(orderNo, channel, order.getTotalConsumptionCent(), clientIP,
                order.getServiceItem().getName(), order.getServiceItem().getDescription(), order.getLeaveAMessage(), extra);
        if (null==charge) {
            entity.setOrderStatus(OrderStatus.CREATE_CHARGE_FAILED);
            repository.save(entity);
            logger.info("create charge object failed ");

            MessageBean message = new MessageBean();
            message.setAlertBody("订单状态有更新");
            message.setType(MessageType.ORDER.name());
            message.setStatus(entity.getOrderStatus().name());
            message.setRelativeId(entity.getId());
            message.setDescription("Ping++ make charge failed!");
            notifier.notifyUserPatient(entity.getUserId(), message);

            return charge;
        }

        orderPingPPService.addOrderCharge(order.getId(), AppType.GO_2_NURSE, ChargeType.CHARGE, charge);

        return charge;
    }

    @Transactional
    public ServiceOrderBean orderChargeWebhooks(String chargeId, String webhooksEventId, String webhooksEventJson) {
        logger.info("order charge webhooks event callback chargeId={} webhooksEventId={} webhooksEventJson={}",
                chargeId, webhooksEventId, webhooksEventJson);
        ServiceOrderChargePingPPBean charge = orderPingPPService.orderChargePingPpWebhooks(chargeId, webhooksEventId, webhooksEventJson);
        long orderId = charge.getOrderId();
        ServiceOrderEntity order = repository.findOne(orderId);
        order.setOrderStatus(OrderStatus.TO_DISPATCH);

        order = repository.save(order);
        logger.info("order charged is {}", order);

        MessageBean message = new MessageBean();
        message.setAlertBody("订单状态有更新");
        message.setType(MessageType.ORDER.name());
        message.setStatus(order.getOrderStatus().name());
        message.setRelativeId(order.getId());
        message.setDescription("waiting for dispatch order!");
        notifier.notifyUserPatient(order.getUserId(), message);

        return beanConverter.convert(order);
    }

    @Transactional
    public ServiceOrderBean cancelOrder(boolean checkUser, long userId, long orderId) {
        logger.info("cancel order={} by user={} checkFlag={}", orderId, userId, checkUser);
        ServiceOrderEntity entity = repository.findOne(orderId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (checkUser) {
            if (entity.getUserId()!=userId) {
                logger.error("order not belong to user");
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
        }
        entity.setOrderStatus(OrderStatus.CANCELLED);
        entity = repository.save(entity);
        logger.info("order cancelled is {}", entity);

        MessageBean message = new MessageBean();
        message.setAlertBody("订单状态有更新");
        message.setType(MessageType.ORDER.name());
        message.setStatus(entity.getOrderStatus().name());
        message.setRelativeId(entity.getId());
        message.setDescription("order cancelled!");
        notifier.notifyUserPatient(entity.getUserId(), message);

        return beanConverter.convert(entity);
    }

    //=====================================================================
    //                   adding
    //=====================================================================
    @Transactional
    public ServiceOrderBean addOrder(long serviceItemId, long userId, long patientId, long addressId,
                                     String strStartTime, int count, String leaveAMessage, int preferentialCent) {
        logger.info("add service order by serviceItemId={} userId={} patientId={} addressId={} strStartTime={} count={} leaveAMessage={} preferentialCent={}",
                serviceItemId, userId, patientId, addressId, strStartTime, count, leaveAMessage, preferentialCent);
        if (!serviceCategoryItemService.existItem(serviceItemId)) {
            logger.error("service item not exists");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (patientId!=0 && !patientService.existPatient(patientId)) {
            logger.error("patient not exists");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!addressService.existAddress(addressId)) {
            logger.error("address not exists");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        long lStartTime = NumberUtil.getTime(strStartTime, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        if (lStartTime<0) {
            logger.error("start time not valid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (count<0) {
            logger.error("time duration not valid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        String orderNo = getOrderNo();
        if (VerifyUtil.isStringEmpty(orderNo)) {
            logger.error("create orderNo failed!");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        // get service item
        ServiceItemBean serviceItem = serviceCategoryItemService.getItemById(serviceItemId);
        String serviceItemJson = go2NurseUtility.toJsonString(serviceItem);

        // get vendor
        ServiceVendorType vendorType = serviceItem.getVendorType();
        ServiceVendorBean vendor = serviceItem.getVendor();
        HospitalBean vendorHospital = serviceItem.getHospital();
        String vendorJson = null;
        if (ServiceVendorType.HOSPITAL.equals(vendorType)) {
            vendorJson = go2NurseUtility.toJsonString(vendorHospital);
        }
        else if (ServiceVendorType.COMPANY.equals(vendorType)) {
            vendorJson = go2NurseUtility.toJsonString(vendor);
        }
        logger.info("hospital ========= {}", serviceItem.getHospital());
        logger.info("vendor ========= {}", serviceItem.getVendor());

        // get service category and parent category
        List<ServiceCategoryBean> serviceCategoryAndParent = serviceCategoryItemService.getCategoryAndParentById(serviceItem.getCategoryId());
        ServiceCategoryBean serviceCategory = null;
        ServiceCategoryBean serviceTopCategory = null;
        String serviceCategoryJson = null;
        String serviceTopCategoryJson = null;
        if (!VerifyUtil.isListEmpty(serviceCategoryAndParent)) {
            serviceCategory = serviceCategoryAndParent.get(0);
            if (serviceCategoryAndParent.size()==2) {
                serviceTopCategory = serviceCategoryAndParent.get(1);
                if (serviceTopCategory.getId()==serviceItem.getCategoryId()) {
                    ServiceCategoryBean tmp = serviceTopCategory;
                    serviceTopCategory = serviceCategory;
                    serviceCategory = tmp;
                }
            }
            if (null!=serviceCategory) {
                serviceCategoryJson = go2NurseUtility.toJsonString(serviceCategory);
            }
            if (null!=serviceTopCategory) {
                serviceTopCategoryJson = go2NurseUtility.toJsonString(serviceTopCategory);
            }
        }

        // get patient
        PatientBean patient = patientService.getOneById(patientId);
        String patientJson = go2NurseUtility.toJsonString(patient);

        // get address
        UserAddressBean address = addressService.getOneById(addressId);
        String addressJson = go2NurseUtility.toJsonString(address);


        //===========================================
        //               new order
        //===========================================
        ServiceOrderEntity entity = new ServiceOrderEntity();
        entity.setServiceItemId(serviceItemId);
        entity.setServiceItem(serviceItemJson);

        entity.setVendorId(serviceItem.getVendorId());
        entity.setVendorType(serviceItem.getVendorType());
        entity.setVendor(vendorJson);

        entity.setCategoryId(0L);
        if (null!=serviceCategory) {
            entity.setCategoryId(serviceCategory.getId());
            entity.setCategory(serviceCategoryJson);
        }

        entity.setTopCategoryId(0L);
        if (null!=serviceTopCategory) {
            entity.setTopCategoryId(serviceTopCategory.getId());
            entity.setTopCategory(serviceTopCategoryJson);
        }

        entity.setUserId(userId);

        entity.setPatientId(0L);
        if (null!=patient) {
            entity.setPatientId(patientId);
            entity.setPatient(patientJson);
        }

        entity.setAddressId(addressId);
        entity.setAddress(addressJson);

        entity.setServiceStartTime(new Date(lStartTime));
        entity.setServiceTimeDuration(serviceItem.getServiceTimeDuration()*count);
        entity.setServiceTimeUnit(serviceItem.getServiceTimeUnit());
        entity.setTotalConsumptionCent(serviceItem.getServicePriceCent()*count);
        entity.setPreferentialCent(preferentialCent);
        entity.setOrderNo(orderNo);
        entity.setLeaveAMessage(leaveAMessage);

        entity.setOrderStatus(OrderStatus.TO_PAY);
        entity.setPayTime(new Date(0));
        entity.setPaymentAmountCent(0);

        entity.setStatus(CommonStatus.ENABLED);
        entity.setTime(new Date());

        entity = repository.save(entity);
        ServiceOrderBean bean = beanConverter.convert(entity);

        logger.info("service order added is {}", bean);
        return bean;
    }
}

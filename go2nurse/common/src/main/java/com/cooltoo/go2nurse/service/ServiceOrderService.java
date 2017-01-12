package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.ManagedBy;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.constants.AppType;
import com.cooltoo.go2nurse.constants.ChargeType;
import com.cooltoo.go2nurse.entities.NurseOrderRelationEntity;
import com.cooltoo.go2nurse.openapp.WeChatPayService;
import com.cooltoo.go2nurse.openapp.WeChatService;
import com.cooltoo.go2nurse.repository.NurseOrderRelationRepository;
import com.cooltoo.util.JSONUtil;
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

    @Autowired private UserService userService;
    @Autowired private ServiceVendorCategoryAndItemService serviceCategoryItemService;
    @Autowired private PatientService patientService;
    @Autowired private ServiceOrderChargePingPPService orderPingPPService;
    @Autowired private PingPPService pingPPService;
    @Autowired private WeChatService weChatService;
    @Autowired private WeChatPayService weChatPayService;
    @Autowired private NurseVisitPatientService nurseVisitPatientService;

    @Autowired private NurseOrderRelationRepository nurseOrderRelationRepository;

    //=====================================================================
    //                   getting
    //=====================================================================

    private String getOrderNo() {
        String orderNo = System.currentTimeMillis() + "";
        for (int i = 10; i > 0; i--) {
            orderNo = NumberUtil.getUniqueString();
            if (repository.countByOrderNo(orderNo) <= 0) {
                break;
            } else {
                orderNo = null;
            }
        }
        return orderNo;
    }

    public boolean existOrder(long orderId) {
        return repository.exists(orderId);
    }

    public List<ServiceOrderBean> getOrderByOrderNo(String orderNo) {
        logger.info("get orders by orderNo={}", orderNo);
        List<ServiceOrderEntity> resultSet = repository.findByOrderNo(orderNo);
        List<ServiceOrderBean> beans = entitiesToBeans(resultSet);
        fillOtherProperties(beans);
        logger.info("service order count is {}", beans.size());
        return beans;
    }

    public long countAllOrder() {
        long count = repository.count();
        logger.info("count all service order is {}", count);
        return count;
    }

    public List<ServiceOrderBean> getOrderByIds(List<Long> orderIds) {
        if (VerifyUtil.isListEmpty(orderIds)) {
            return new ArrayList<>();
        }
        logger.info("get service order by orderId size={}", orderIds.size());
        List<ServiceOrderEntity> resultSet = repository.findByIdIn(orderIds);
        List<ServiceOrderBean> beans = entitiesToBeans(resultSet);
        fillOtherProperties(beans);
        logger.info("service order count is {}", beans.size());
        return beans;
    }

    public List<Long> isOrderIdExisted(List<Long> orderIds, OrderStatus orderStatus) {
        if (VerifyUtil.isListEmpty(orderIds)) {
            return new ArrayList<>();
        }
        logger.info("get service order id by orderId size={} and orderStatus={}", orderIds.size(), orderStatus);
        List<Long> resultSet = repository.findByIdInAndOrderStatus(orderIds, orderStatus);
        if (null == resultSet) {
            resultSet = new ArrayList<>();
        }
        logger.info("service order count is {}", resultSet.size());
        return resultSet;
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

    public long countOrderByConditions(Long serviceItemId, Long userId, Long patientId,
                                       Long categoryId, Long topCategoryId,
                                       ServiceVendorType vendorType, Long vendorId, Long vendorDepartId,
                                       OrderStatus orderStatus
    ) {
        logger.info("get order by userId={} patientId={} itemId={} categoryId={} topCategoryId={} vendorType={} vendorId={} vendorDepartId={} orderStatus={}",
                userId, patientId, serviceItemId, categoryId, topCategoryId, vendorType, vendorId, vendorDepartId, orderStatus);
        long count = repository.countByConditions(serviceItemId, userId, patientId, categoryId, topCategoryId, orderStatus, vendorType, vendorId, vendorDepartId);
        logger.info("count is {}", count);
        return count;
    }

    public List<ServiceOrderBean> getOrderByConditions(Long serviceItemId, Long userId, Long patientId,
                                                       Long categoryId, Long topCategoryId,
                                                       ServiceVendorType vendorType, Long vendorId, Long vendorDepartId,
                                                       OrderStatus orderStatus,
                                                       int pageIndex, int sizePerPage
    ) {
        logger.info("get order by userId={} patientId={} itemId={} categoryId={} topCategoryId={} vendorType={} vendorId={} vendorDepartId={} orderStatus={} pageIndex={} sizePerPage={}",
                userId, patientId, serviceItemId, categoryId, topCategoryId, vendorType, vendorId, vendorDepartId, orderStatus, pageIndex, sizePerPage);
        PageRequest pageRequest = new PageRequest(pageIndex, sizePerPage, sort);
        Page<ServiceOrderEntity> entities = repository.findByConditions(serviceItemId, userId, patientId, categoryId, topCategoryId, orderStatus, vendorType, vendorId, vendorDepartId, pageRequest);
        List<ServiceOrderBean> beans = entitiesToBeans(entities);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public List<ServiceOrderBean> getOrderByConditions(List<OrderStatus> orderStatus,
                                                       ServiceVendorType vendorType, Long vendorId, Long departId,
                                                       ManagedBy managedBy,
                                                       int pageIndex, int sizePerPage
    ) {
        logger.info("get order by orderStatus={} vendorType={} vendorId={} departId={} managedBy={} at pageIndex={} sizePerPage={}",
                orderStatus, vendorType, vendorId, managedBy, pageIndex, sizePerPage);
        PageRequest pageRequest = new PageRequest(pageIndex, sizePerPage, sort);
        Page<ServiceOrderEntity> entities = null;
        if (!VerifyUtil.isListEmpty(orderStatus)) {
            if (null == vendorType) {
                entities = repository.findByConditions(orderStatus, managedBy, pageRequest);
            } else {
                entities = repository.findByConditions(orderStatus, vendorType, vendorId, departId, managedBy, pageRequest);
            }
        }
        List<ServiceOrderBean> beans = entitiesToBeans(entities);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public List<ServiceOrderBean> getOrderByOrderId(long orderId) {
        logger.info("get service order by orderId={}", orderId);
        ServiceOrderEntity entity = repository.findOne(orderId);
        if (null == entity) {
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
        if (null == entities) {
            return new ArrayList<>();
        }
        // check order need completed
        List<Long> ordersCompleted = checkOrderNeedComplete();

        List<ServiceOrderBean> beans = new ArrayList<>();
        for (ServiceOrderEntity entity : entities) {
            if (null==entities) { continue; }
            ServiceOrderBean bean = beanConverter.convert(entity);
            if (null!=ordersCompleted && ordersCompleted.contains(bean.getId())) {
                bean.setOrderStatus(OrderStatus.COMPLETED.getName());
            }
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

        // set pingpp charge
        Map<Long, List<ServiceOrderChargePingPPBean>> orderId2Charge = orderPingPPService.getOrderPingPPResult(AppType.GO_2_NURSE, orderIds);
        for (ServiceOrderBean tmp : beans) {
            List<ServiceOrderChargePingPPBean> charges = orderId2Charge.get(tmp.getId());
            if (null == charges) {
                tmp.setPingPP(new ArrayList<>());
            } else {
                tmp.setPingPP(charges);
            }
        }

        // set fetch time
        final Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, "id"));
        List<NurseOrderRelationEntity> nurseFetchTime = nurseOrderRelationRepository.findByOrderIdInAndStatus(orderIds, CommonStatus.ENABLED, sort);
        Map<Long, Date> orderIdToFetchTime = new HashMap<>();
        for (NurseOrderRelationEntity tmp : nurseFetchTime) {
            if (orderIdToFetchTime.containsKey(tmp.getOrderId())) {
                continue;
            }
            orderIdToFetchTime.put(tmp.getOrderId(), tmp.getTime());
        }
        Date _1970 = new Date(0); // 1970-01-00 08:00:00
        for (ServiceOrderBean tmp : beans) {
            Date fetchTime = orderIdToFetchTime.get(tmp.getId());
            if (null == fetchTime) {
                tmp.setFetchTime(_1970);
            } else {
                tmp.setFetchTime(fetchTime);
            }
        }
    }

    //=====================================================================
    //                   deleting
    //=====================================================================

    //==================================================================================================
    //                                        updating
    //==================================================================================================
    @Transactional
    public ServiceOrderBean updateOrder(long orderId, Long patientId, String address,
                                        String strStartTime, Integer count, String leaveAMessage) {
        logger.info("update service order={} by patientId={} address={} strStartTime={} count={} leaveAMessage={}",
                orderId, patientId, address, strStartTime, count, leaveAMessage);

        ServiceOrderEntity entity = repository.findOne(orderId);
        if (null == entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        if (!OrderStatus.TO_PAY.equals(entity.getOrderStatus())) {
            logger.info("the order is in status={}, can not be modified", entity.getOrderStatus());
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        JSONUtil jsonUtil = JSONUtil.newInstance();

        boolean changed = false;
        if (patientId != null && patientService.existPatient(patientId)) {
            PatientBean patient = patientService.getOneById(patientId);
            String patientJson = jsonUtil.toJsonString(patient);
            entity.setPatientId(patientId);
            entity.setPatient(patientJson);
            changed = true;
        }

        if (!VerifyUtil.isStringEmpty(address) && !address.equals(entity.getAddress())) {
            entity.setAddress(address);
            changed = true;
        }

        long lStartTime = NumberUtil.getTime(strStartTime, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        if (lStartTime > 0) {
            entity.setServiceStartTime(new Date(lStartTime));
            changed = true;
        }

        if (null != count) {
            ServiceOrderBean bean = beanConverter.convert(entity);
            ServiceItemBean item = bean.getServiceItem();
            entity.setServiceTimeDuration(item.getServiceTimeDuration() * count);
            entity.setTotalPriceCent(item.getServicePriceCent() * count);
            entity.setTotalDiscountCent(item.getServiceDiscountCent() * count);
            entity.setTotalIncomeCent(item.getServerIncomeCent() * count);
            entity.setItemCount(count);
            changed = true;
        }

        if (!VerifyUtil.isStringEmpty(leaveAMessage)) {
            entity.setLeaveAMessage(leaveAMessage);
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
        if (null == entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (userId != entity.getUserId()) {
            logger.error("this order does not belong to this user");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (VerifyUtil.isStringEmpty(channel)) {
            logger.warn("channel is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (!OrderStatus.TO_PAY.equals(entity.getOrderStatus())) {
            logger.error("order status={}, not to_pay", entity.getOrderStatus());
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        ServiceOrderBean order = beanConverter.convert(entity);
        String orderNo = order.getOrderNo();
        Map extra = new HashMap<>();
        if (channel.equals("wx_pub")) {
            String openId = weChatService.getOpenIdByUserId(userId);
            extra.put("open_id", openId);
        }

        /* create PingPP charge for payment */
        Charge charge = pingPPService.createCharge(
                orderNo, channel,
                order.getTotalConsumptionCent()-order.getPreferentialCent(),
                clientIP,
                order.getServiceItem().getName(), order.getServiceItem().getDescription(), order.getLeaveAMessage(), extra);

        /* invoke PingPP SDK failed! */
        if (null == charge) {
            entity.setOrderStatus(OrderStatus.CREATE_CHARGE_FAILED);
            repository.save(entity);
            logger.info("create charge object failed ");
            return charge;
        }

        /* record PingPP charge instance, and wait for callback! */
        orderPingPPService.addOrderCharge(order.getId(), AppType.GO_2_NURSE, orderNo, channel, ChargeType.CHARGE, charge.getId(), charge.toString());

        return charge;
    }

    @Transactional
    public Map<String, String> payForServiceByWeChat(Long userId, Long orderId, String openId, String clientIP, WeChatAccountBean weChatAccount) {
        logger.debug("create charge object for order={} openId={} clientIp={} weChatAccount={}", orderId, openId, clientIP, weChatAccount);
        ServiceOrderEntity entity = repository.findOne(orderId);
        if (null == entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (userId != entity.getUserId()) {
            logger.error("this order does not belong to this user");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (VerifyUtil.isStringEmpty(openId)) {
            logger.warn("openId is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (null == weChatAccount) {
            logger.warn("weChatAccount is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (!OrderStatus.TO_PAY.equals(entity.getOrderStatus())) {
            logger.error("order status={}, not to_pay", entity.getOrderStatus());
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        /* record PingPP charge instance, and wait for callback! */
        ServiceOrderBean order = beanConverter.convert(entity);
        String orderNo = order.getOrderNo();
        String wechatNo = NumberUtil.createNoncestr(31);

        /* Invoke WeChat payment! */
        Map<String, String> weChatResponse = weChatPayService.payByWeChat(openId, "WEB", clientIP,
                weChatAccount.getAppId(), weChatAccount.getMchId(), weChatPayService.getApiKey(),
                wechatNo, "JSAPI", "订单=" + orderNo + " 描述=" + order.getServiceItem().getName(),
                "CNY", order.getTotalConsumptionCent(), weChatPayService.getNotifyUrl());
        logger.debug("get wei chat pay response "+weChatResponse);

        /* WeChat payment failed! */
        if (!"SUCCESS".equalsIgnoreCase((String) weChatResponse.get("return_code"))
         || !"SUCCESS".equalsIgnoreCase((String) weChatResponse.get("result_code"))) {
            entity.setOrderStatus(OrderStatus.CREATE_CHARGE_FAILED);
            repository.save(entity);
            logger.error("WeChat make order failed!");
            throw new BadRequestException(ErrorCode.PAY_FAILED);
        }

        /* WeChat payment success! then check the sign */
        String sign1 = (String) weChatResponse.get("sign");
        weChatResponse.remove("sign");
        String sign2 = weChatPayService.createSign(weChatPayService.getApiKey(), "UTF-8", new TreeMap<>(weChatResponse));

        /* The sign is not right */
        if (!sign2.equalsIgnoreCase(sign1)) {
            entity.setOrderStatus(OrderStatus.CREATE_CHARGE_FAILED);
            repository.save(entity);
            logger.error("WeChat sign not match!");
            throw new BadRequestException(ErrorCode.PAY_FAILED);
        }

        /* WeChat payment success!  record WeChat payment information, and wait for callback! */
        orderPingPPService.addOrderCharge(order.getId(), AppType.GO_2_NURSE, orderNo, "wx", ChargeType.CHARGE, wechatNo, weChatResponse.toString());
        weChatResponse.remove("mch_id");
        weChatResponse.remove("device_info");

        return signWeChatResponse(weChatAccount.getAppId(), weChatResponse.get("prepay_id"),  weChatResponse.get("nonce_str"));
    }

    private Map<String, String> signWeChatResponse(String appid, String prepayId, String noncestr) {
        StringBuffer buffer = new StringBuffer();
        String signType = "MD5";
        String timeStamp = System.currentTimeMillis()+"";
        buffer.append("appId=").append(appid).append("&nonceStr=").append(noncestr)
                .append("&package=prepay_id=").append(prepayId)
                .append("&signType="+signType).append("&timeStamp=").append(timeStamp)
                .append("&key=").append(weChatPayService.getApiKey());
        logger.debug("pay sign parameter:"+buffer.toString());
        String md5 = NumberUtil.signString(buffer.toString(), "MD5").toUpperCase();
        logger.debug("MD5 sign:"+md5);
        Map<String, String> sign = new HashMap<>();
        sign.put("timestamp", timeStamp);
        sign.put("nonceStr", noncestr);
        sign.put("package", prepayId);
        sign.put("signType", signType);
        sign.put("paySign", md5);
        logger.debug("create sign for payment "+sign);
        return sign;

    }

    @Transactional
    public ServiceOrderBean orderChargeWebhooks(String chargeId, String webhooksEventId, String webhooksEventJson) {
        logger.info("order charge webhooks event callback chargeId={} webhooksEventId={} webhooksEventJson={}",
                chargeId, webhooksEventId, webhooksEventJson);
        ServiceOrderChargePingPPBean charge = orderPingPPService.orderChargePingPpWebhooks(chargeId, webhooksEventId, webhooksEventJson);
        long orderId = charge.getOrderId();
        ServiceOrderEntity order = repository.findOne(orderId);
        if (null == order) {
            logger.error("order not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!OrderStatus.TO_PAY.equals(order.getOrderStatus())) {
            logger.error("order status={}, not to_pay", order.getOrderStatus());
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        order.setOrderStatus(OrderStatus.PAID);

        order = repository.save(order);
        logger.info("order charged is {}", order);


        return beanConverter.convert(order);
    }

    @Transactional
    public ServiceOrderBean alertNurseToFetchOrder(long orderId) {
        logger.info("nurse fetch order={}", orderId);
        ServiceOrderEntity entity = repository.findOne(orderId);
        if (null == entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        if (!OrderStatus.PAID.equals(entity.getOrderStatus())
         && !OrderStatus.WAIT_NURSE_FETCH.equals(entity.getOrderStatus())) {
            logger.info("the order is in status={}, can not be fetched", entity.getOrderStatus());
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        entity.setOrderStatus(OrderStatus.WAIT_NURSE_FETCH);
        entity = repository.save(entity);
        logger.info("order fetched is {}", entity);

        return beanConverter.convert(entity);
    }

    @Transactional
    public ServiceOrderBean nurseFetchOrder(long orderId, boolean isAdminDispatch) {
        logger.info("nurse fetch order={} isAdminDispatch={}", orderId, isAdminDispatch);
        ServiceOrderEntity entity = repository.findOne(orderId);
        if (null == entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (isAdminDispatch && !OrderStatus.PAID.equals(entity.getOrderStatus())) {
            logger.info("the order is in status={}, can not be dispatch by administrator", entity.getOrderStatus());
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (!isAdminDispatch && !OrderStatus.WAIT_NURSE_FETCH.equals(entity.getOrderStatus())) {
            logger.info("the order is in status={}, can not be fetched", entity.getOrderStatus());
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        entity.setOrderStatus(OrderStatus.IN_PROCESS);
        entity = repository.save(entity);
        logger.info("order fetched is {}", entity);

        return beanConverter.convert(entity);
    }

    @Transactional
    public ServiceOrderBean nurseGiveUpOrder(long orderId) {
        logger.info("nurse fetch order={}", orderId);
        ServiceOrderEntity entity = repository.findOne(orderId);
        if (null == entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!OrderStatus.IN_PROCESS.equals(entity.getOrderStatus())) {
            logger.info("the order is in status={}, can not be given_up", entity.getOrderStatus());
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        entity.setOrderStatus(OrderStatus.PAID);
        entity = repository.save(entity);
        logger.info("order fetched is {}", entity);

        return beanConverter.convert(entity);
    }

    @Transactional
    public ServiceOrderBean cancelOrder(boolean checkUser, long userId, long orderId) {
        logger.info("cancel order={} by user={} checkFlag={}", orderId, userId, checkUser);
        ServiceOrderEntity entity = repository.findOne(orderId);
        if (null == entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (checkUser) {
            if (entity.getUserId() != userId) {
                logger.error("order not belong to user");
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
        }

        // only to_pay can be canceled
        if (!OrderStatus.TO_PAY.equals(entity.getOrderStatus())) {
            logger.info("the order is in status={}, can not be cancelled", entity.getOrderStatus());
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        entity.setOrderStatus(OrderStatus.CANCELLED);
        entity = repository.save(entity);
        logger.info("order cancelled is {}", entity);

        return beanConverter.convert(entity);
    }

    @Transactional
    public ServiceOrderBean completedOrder(boolean checkUser, long userId, long orderId) {
        logger.info("complete order={} by user={} checkFlag={}", orderId, userId, checkUser);
        ServiceOrderEntity entity = repository.findOne(orderId);
        if (null == entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (checkUser) {
            if (entity.getUserId() != userId) {
                logger.error("order not belong to user");
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
        }
        if (!OrderStatus.PAID.equals(entity.getOrderStatus())
         && !OrderStatus.WAIT_NURSE_FETCH.equals(entity.getOrderStatus())
         && !OrderStatus.IN_PROCESS.equals(entity.getOrderStatus())
        ) {
            logger.info("the order is in status={}, can not be completed", entity.getOrderStatus());
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        // check needVisitPatientRecord
        if (YesNoEnum.YES.equals(entity.getNeedVisitPatientRecord())
         && !nurseVisitPatientService.isRecordForOrder(orderId)) {
            logger.error("order need record visit patient record and patient sign!");
            throw new BadRequestException(ErrorCode.NEED_VISIT_PATIENT_RECORD);
        }

        entity.setOrderStatus(OrderStatus.COMPLETED);
        entity.setCompletedTime(new Date());
        entity = repository.save(entity);
        logger.info("order completed is {}", entity);

        return beanConverter.convert(entity);
    }

    @Transactional
    public ServiceOrderBean deleteOrder(boolean checkUser, long userId, long orderId) {
        logger.info("delete order={} by user={} checkFlag={}", orderId, userId, checkUser);
        ServiceOrderEntity entity = repository.findOne(orderId);
        if (null == entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (checkUser) {
            if (entity.getUserId() != userId) {
                logger.error("order not belong to user");
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
        }

        // only REFUND_IN_PROCESS / IN_PROCESS / WAIT_NURSE_FETCH / PAID can not be deleted
        if (OrderStatus.REFUND_IN_PROCESS.equals(entity.getOrderStatus())
                && OrderStatus.IN_PROCESS.equals(entity.getOrderStatus())
                && OrderStatus.WAIT_NURSE_FETCH.equals(entity.getOrderStatus())
                && OrderStatus.PAID.equals(entity.getOrderStatus())) {
            logger.info("the order is in status={}, can not be deleted", entity.getOrderStatus());
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        entity.setStatus(CommonStatus.DELETED);
        entity = repository.save(entity);
        logger.info("order deleted is {}", entity);

        return beanConverter.convert(entity);
    }

    @Transactional
    public ServiceOrderBean refundFeeOfOrder(boolean checkUser, long userId, long orderId) {
        logger.info("refund fee of order={} by user={} checkFlag={}", orderId, userId, checkUser);
        ServiceOrderEntity entity = repository.findOne(orderId);
        if (null == entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (checkUser) {
            if (entity.getUserId() != userId) {
                logger.error("order not belong to user");
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
        }
        if (!OrderStatus.PAID.equals(entity.getOrderStatus())
                && !OrderStatus.WAIT_NURSE_FETCH.equals(entity.getOrderStatus())
                && !OrderStatus.IN_PROCESS.equals(entity.getOrderStatus())
        ) {
            logger.info("the order is in status={}, can not be refunded", entity.getOrderStatus());
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        entity.setOrderStatus(OrderStatus.REFUND_IN_PROCESS);
        entity.setCompletedTime(new Date());
        entity = repository.save(entity);
        logger.info("order completed is {}", entity);

        return beanConverter.convert(entity);
    }

    @Transactional
    public ServiceOrderBean completeRefundOfOrder(boolean checkUser, long userId, long orderId) {
        logger.info("complete refund of order={} by user={} checkFlag={}", orderId, userId, checkUser);
        ServiceOrderEntity entity = repository.findOne(orderId);
        if (null == entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (checkUser) {
            if (entity.getUserId() != userId) {
                logger.error("order not belong to user");
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
        }
        if (!OrderStatus.REFUND_IN_PROCESS.equals(entity.getOrderStatus())) {
            logger.info("the order is in status={}, refund can not be completed", entity.getOrderStatus());
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        entity.setOrderStatus(OrderStatus.REFUND_COMPLETED);
        entity.setCompletedTime(new Date());
        entity = repository.save(entity);
        logger.info("order's refund completed is {}", entity);

        return beanConverter.convert(entity);
    }

    @Transactional
    public ServiceOrderBean failRefundOrder(boolean checkUser, long userId, long orderId) {
        logger.info("failed to refund order={} by user={} checkFlag={}", orderId, userId, checkUser);
        ServiceOrderEntity entity = repository.findOne(orderId);
        if (null == entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (checkUser) {
            if (entity.getUserId() != userId) {
                logger.error("order not belong to user");
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
        }
        if (!OrderStatus.REFUND_IN_PROCESS.equals(entity.getOrderStatus())) {
            logger.info("the order is in status={}, refund can not be set to failed_refund", entity.getOrderStatus());
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        entity.setOrderStatus(OrderStatus.REFUND_FAILED);
        entity.setCompletedTime(new Date());
        entity = repository.save(entity);
        logger.info("order refund_failed is {}", entity);

        return beanConverter.convert(entity);
    }

    @Transactional
    public ServiceOrderBean scoreOrder(boolean checkUser, long userId, long orderId, float score) {
        logger.info("score order={} by user={} checkFlag={} score={}", orderId, userId, checkUser, score);
        ServiceOrderEntity entity = repository.findOne(orderId);
        if (null == entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (checkUser) {
            if (entity.getUserId() != userId) {
                logger.error("order not belong to user");
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }
        }

        score = score < 0 ? 0 : score;

        entity.setScore(score);
        entity = repository.save(entity);
        logger.info("order scored is {}", entity);
        return beanConverter.convert(entity);
    }

    @Transactional
    private List<Long> checkOrderNeedComplete() {
        // get all orders in IN_PROCESS status
        List<ServiceOrderEntity> entities = repository.findByOrderStatus(OrderStatus.IN_PROCESS);
        List<ServiceOrderBean> beans = new ArrayList<>();
        if (!VerifyUtil.isListEmpty(entities)) {
            for (ServiceOrderEntity entity : entities) {
                if (null==entities) { continue; }
                ServiceOrderBean bean = beanConverter.convert(entity);
                beans.add(bean);
            }
        }
        entities = null;
        if (VerifyUtil.isListEmpty(beans)) { return null; }


        // get order that has record patient visitation
        List<Long> orderIds = new ArrayList<>();
        for (ServiceOrderBean tmp : beans) {
            if (null==tmp) {continue;}
            orderIds.add(tmp.getId());
        }
        if (VerifyUtil.isListEmpty(orderIds)) { return null; }
        Map<Long, Boolean> orderRecordedVisitPatient = nurseVisitPatientService.isRecordForOrder(orderIds);

        // get the time 7 days ago
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date _7DaysAgo = calendar.getTime();
        long _7DaysAgoMilliSecond = _7DaysAgo.getTime();

        // get all order can be completed
        orderIds.clear();
        for (ServiceOrderBean tmp : beans) {
            if (null==tmp) {continue;}
            Boolean hasRecordPatientVisitation = orderRecordedVisitPatient.get(tmp.getId());
            long serviceEndTime = tmp.calculateServiceEndTime();
            if (_7DaysAgoMilliSecond > serviceEndTime && Boolean.TRUE.equals(hasRecordPatientVisitation)) {
                orderIds.add(tmp.getId());
            }
        }

        // set all order can be completed to COMPLETED
        return autoCompletedOrders(orderIds);
    }

    @Transactional
    private List<Long> autoCompletedOrders(List<Long> orderIds) {
        int size = null==orderIds ? 0 : orderIds.size();
        logger.info("auto complete orders, size={}", size);
        if (0==size) {
            return null;
        }
        List<ServiceOrderEntity> entities = repository.findAll(orderIds);
        if (VerifyUtil.isListEmpty(entities)) {
            return null;
        }

        long updateCount = 0;
        orderIds = new ArrayList<>();
        for (ServiceOrderEntity tmp : entities) {
            if (null==tmp) { continue; }
            if (!OrderStatus.IN_PROCESS.equals(tmp.getOrderStatus())) {continue;}
            tmp.setOrderStatus(OrderStatus.COMPLETED);
            updateCount ++;
            orderIds.add(tmp.getId());
        }
        repository.save(entities);
        logger.info("order completed count={}", updateCount);

        return orderIds;
    }


    //============================================================================================
    //                                       Adding
    //============================================================================================
    @Transactional
    public ServiceOrderBean addOrder(long serviceItemId, long userId, long patientId, String address,
                                     String strStartTime, int count, String leaveAMessage, String orderStatus) {
        logger.debug("add service order by serviceItemId={} userId={} patientId={} addressId={} strStartTime={} count={} leaveAMessage={} orderStatus={}",
                serviceItemId, userId, patientId, address, strStartTime, count, leaveAMessage, orderStatus);

        OrderStatus newOrderStatus = OrderStatus.parseString(orderStatus);
        if (null!=newOrderStatus && !OrderStatus.TO_PAY.equals(newOrderStatus) && !OrderStatus.PAID.equals(newOrderStatus)) {
            logger.error("new order status is not NULL, TO_PAY or PAID");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        if (!serviceCategoryItemService.existItem(serviceItemId)) {
            logger.error("service item not exists");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!userService.existUser(userId)) {
            logger.error("user not exists");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (patientId != 0 && !patientService.existPatient(patientId)) {
            logger.error("patient not exists");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (VerifyUtil.isStringEmpty(address)) {
            logger.error("address is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        long lStartTime = NumberUtil.getTime(strStartTime, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        if (lStartTime < 0) {
            logger.error("start time not valid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (count < 0) {
            logger.error("time duration not valid");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        String orderNo = getOrderNo();
        if (VerifyUtil.isStringEmpty(orderNo)) {
            logger.error("create orderNo failed!");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        JSONUtil jsonUtil = JSONUtil.newInstance();

        // get service item
        ServiceItemBean serviceItem = serviceCategoryItemService.getItemById(serviceItemId);
        if (!YesNoEnum.YES.equals(serviceItem.getManagerApproved())) {
            logger.error("service item not approved by administrator!");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        String serviceItemJson = jsonUtil.toJsonString(serviceItem);

        // get vendor
        ServiceVendorType vendorType = serviceItem.getVendorType();
        ServiceVendorBean vendor = serviceItem.getVendor();
        HospitalBean vendorHospital = serviceItem.getHospital();
        HospitalDepartmentBean vendorHospitalDepart = serviceItem.getHospitalDepartment();
        String vendorJson = null;
        String vendorDepartJson = null;
        /* vendor is hospital */
        if (ServiceVendorType.HOSPITAL.equals(vendorType)) {
            vendorJson = jsonUtil.toJsonString(vendorHospital);
            if (null!=vendorHospitalDepart) {
                vendorDepartJson= jsonUtil.toJsonString(vendorHospitalDepart);
            }

        }
        /* vendor is company */
        else if (ServiceVendorType.COMPANY.equals(vendorType)) {
            vendorJson = jsonUtil.toJsonString(vendor);
        }

        logger.debug("hospital ========= {}", serviceItem.getHospital());
        logger.debug("vendor ========= {}", serviceItem.getVendor());

        // get service category and parent category
        List<ServiceCategoryBean> serviceCategoryAndParent = serviceCategoryItemService.getCategoryAndParentById(serviceItem.getCategoryId());
        ServiceCategoryBean serviceCategory = null;
        ServiceCategoryBean serviceTopCategory = null;
        String serviceCategoryJson = null;
        String serviceTopCategoryJson = null;
        if (!VerifyUtil.isListEmpty(serviceCategoryAndParent)) {
            serviceCategory = serviceCategoryAndParent.get(0);
            if (serviceCategoryAndParent.size() == 2) {
                serviceTopCategory = serviceCategoryAndParent.get(1);
                if (serviceTopCategory.getId() == serviceItem.getCategoryId()) {
                    ServiceCategoryBean tmp = serviceTopCategory;
                    serviceTopCategory = serviceCategory;
                    serviceCategory = tmp;
                }
            }
            if (null != serviceCategory) {
                serviceCategoryJson = jsonUtil.toJsonString(serviceCategory);
            }
            if (null != serviceTopCategory) {
                serviceTopCategoryJson = jsonUtil.toJsonString(serviceTopCategory);
            }
        }

        // get patient
        PatientBean patient = patientService.getOneById(patientId);
        String patientJson = jsonUtil.toJsonString(patient);

        //===========================================
        //               new order
        //===========================================
        ServiceOrderEntity entity = new ServiceOrderEntity();
        entity.setServiceItemId(serviceItemId);
        entity.setServiceItem(serviceItemJson);
        entity.setManagedBy(serviceItem.getManagedBy());

        entity.setVendorId(serviceItem.getVendorId());
        entity.setVendorDepartId(serviceItem.getVendorDepartId());
        entity.setVendorType(serviceItem.getVendorType());
        entity.setVendor(vendorJson);
        entity.setVendorDepart(vendorDepartJson);

        entity.setCategoryId(0L);
        if (null != serviceCategory) {
            entity.setCategoryId(serviceCategory.getId());
            entity.setCategory(serviceCategoryJson);
        }

        entity.setTopCategoryId(0L);
        if (null != serviceTopCategory) {
            entity.setTopCategoryId(serviceTopCategory.getId());
            entity.setTopCategory(serviceTopCategoryJson);
        }

        entity.setUserId(userId);

        entity.setPatientId(0L);
        if (null != patient) {
            entity.setPatientId(patientId);
            entity.setPatient(patientJson);
        }

        entity.setAddress(address);

        entity.setServiceStartTime(new Date(lStartTime));
        entity.setServiceTimeDuration(serviceItem.getServiceTimeDuration() * count);
        entity.setServiceTimeUnit(serviceItem.getServiceTimeUnit());
        entity.setTotalPriceCent(serviceItem.getServicePriceCent() * count);
        entity.setTotalDiscountCent(serviceItem.getServiceDiscountCent() * count);
        entity.setTotalIncomeCent(serviceItem.getServerIncomeCent() * count);
        entity.setItemCount(count);
        entity.setNeedVisitPatientRecord(serviceItem.getNeedVisitPatientRecord());
        entity.setOrderNo(orderNo);
        entity.setLeaveAMessage(leaveAMessage);

        entity.setOrderStatus(newOrderStatus);
        if (null==newOrderStatus) {
            if (entity.getTotalPriceCent() - entity.getTotalDiscountCent() > 0) {
                entity.setOrderStatus(OrderStatus.TO_PAY);
            } else {
                entity.setOrderStatus(OrderStatus.PAID);
            }
        }

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

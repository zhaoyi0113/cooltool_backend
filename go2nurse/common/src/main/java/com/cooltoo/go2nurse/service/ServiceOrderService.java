package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.ManagedBy;
import com.cooltoo.constants.PaymentPlatform;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.constants.*;
import com.cooltoo.go2nurse.entities.NurseOrderRelationEntity;
import com.cooltoo.go2nurse.openapp.WeChatService;
import com.cooltoo.go2nurse.payment.IPayment;
import com.cooltoo.go2nurse.payment.PaymentFactory;
import com.cooltoo.go2nurse.payment.PaymentPingPP;
import com.cooltoo.go2nurse.payment.PaymentWeChat;
import com.cooltoo.go2nurse.repository.NurseOrderRelationRepository;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.cooltoo.util.JSONUtil;
import com.cooltoo.beans.HospitalBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.converter.ServiceOrderBeanConverter;
import com.cooltoo.go2nurse.entities.ServiceOrderEntity;
import com.cooltoo.go2nurse.repository.ServiceOrderRepository;
import com.cooltoo.util.NumberUtil;
import com.cooltoo.util.VerifyUtil;
import com.pingplusplus.model.Charge;
import com.pingplusplus.model.Event;
import com.pingplusplus.model.Refund;
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

    public static final String RefundReturnValue = "refund_return_value";

    private static final Sort sort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "time"),
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private ServiceOrderRepository repository;
    @Autowired private ServiceOrderBeanConverter beanConverter;

    @Autowired private UserService userService;
    @Autowired private ServiceVendorCategoryAndItemService serviceCategoryItemService;
    @Autowired private PatientService patientService;
    @Autowired private ServiceOrderChargeService orderChargeService;
    @Autowired private WeChatService weChatService;
    @Autowired private NurseVisitPatientService nurseVisitPatientService;

    @Autowired private NurseOrderRelationRepository nurseOrderRelationRepository;

    @Autowired private Go2NurseUtility utility;

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

    public ServiceOrderBean getOneOrderByOrderNo(String orderNo) {
        List<ServiceOrderBean> beans = getOrderByOrderNo(orderNo);
        if (beans.size()>1) {
            logger.error("orderNo={} get more than one orders", orderNo);
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (beans.isEmpty()) {
            logger.error("orderNo={} get no orders", orderNo);
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        return beans.get(0);
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

    public List<Long> isOrderIdExisted(List<Long> orderIds, OrderStatus orderStatus, CommonStatus status) {
        if (VerifyUtil.isListEmpty(orderIds)) {
            return new ArrayList<>();
        }
        logger.info("get service order id by orderId size={} and orderStatus={}", orderIds.size(), orderStatus);
        List<Long> resultSet = repository.findByIdInAndOrderStatus(orderIds, orderStatus, status);
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
                                       OrderStatus orderStatus,
                                       CommonStatus status
    ) {
        logger.info("get order by userId={} patientId={} itemId={} categoryId={} topCategoryId={} vendorType={} vendorId={} vendorDepartId={} orderStatus={}",
                userId, patientId, serviceItemId, categoryId, topCategoryId, vendorType, vendorId, vendorDepartId, orderStatus);
        long count = repository.countByConditions(serviceItemId, userId, patientId, categoryId, topCategoryId, orderStatus, vendorType, vendorId, vendorDepartId, status);
        logger.info("count is {}", count);
        return count;
    }

    public List<ServiceOrderBean> getOrderByConditions(Long serviceItemId, Long userId, Long patientId,
                                                       Long categoryId, Long topCategoryId,
                                                       ServiceVendorType vendorType, Long vendorId, Long vendorDepartId,
                                                       OrderStatus orderStatus,
                                                       CommonStatus status,
                                                       int pageIndex, int sizePerPage
    ) {
        logger.info("get order by userId={} patientId={} itemId={} categoryId={} topCategoryId={} vendorType={} vendorId={} vendorDepartId={} orderStatus={} pageIndex={} sizePerPage={}",
                userId, patientId, serviceItemId, categoryId, topCategoryId, vendorType, vendorId, vendorDepartId, orderStatus, pageIndex, sizePerPage);
        PageRequest pageRequest = new PageRequest(pageIndex, sizePerPage, sort);
        Page<ServiceOrderEntity> entities = repository.findByConditions(serviceItemId, userId, patientId, categoryId, topCategoryId, orderStatus, vendorType, vendorId, vendorDepartId, status, pageRequest);
        List<ServiceOrderBean> beans = entitiesToBeans(entities);
        fillOtherProperties(beans);
        logger.info("count is {}", beans.size());
        return beans;
    }

    public List<ServiceOrderBean> getOrderByConditions(List<OrderStatus> orderStatus,
                                                       ServiceVendorType vendorType, Long vendorId, Long departId,
                                                       ManagedBy managedBy,
                                                       CommonStatus status,
                                                       int pageIndex, int sizePerPage
    ) {
        logger.info("get order by orderStatus={} vendorType={} vendorId={} departId={} managedBy={} at pageIndex={} sizePerPage={}",
                orderStatus, vendorType, vendorId, managedBy, pageIndex, sizePerPage);
        PageRequest pageRequest = new PageRequest(pageIndex, sizePerPage, sort);
        Page<ServiceOrderEntity> entities = null;
        if (!VerifyUtil.isListEmpty(orderStatus)) {
            if (null == vendorType) {
                entities = repository.findByConditions(orderStatus, managedBy, status, pageRequest);
            } else {
                entities = repository.findByConditions(orderStatus, vendorType, vendorId, departId, managedBy, status, pageRequest);
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

    public List<ServiceOrderBean> getOrderByUserId(long userId, CommonStatus status) {
        logger.info("get service order by userId={} status={}", userId, status);
        List<ServiceOrderEntity> entities = repository.findByUserIdAndStatus(userId, status, sort);
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
        Map<Long, List<ServiceOrderChargeBean>> orderId2Charge = orderChargeService.getOrderPingPPResult(AppType.GO_2_NURSE, orderIds);

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

        // get visit record ids
        Map<Long, Long> orderIdToVisitRecordId = nurseVisitPatientService.getOrdersVisitRecordIds(orderIds);

        Date _1970 = new Date(0); // 1970-01-00 08:00:00
        for (ServiceOrderBean tmp : beans) {
            List<ServiceOrderChargeBean> charges = orderId2Charge.get(tmp.getId());
            tmp.setPingPP(null==charges ? new ArrayList<>() : charges);

            Date fetchTime = orderIdToFetchTime.get(tmp.getId());
            tmp.setFetchTime(null==fetchTime ? _1970 : fetchTime);

            Long visitRecordId = orderIdToVisitRecordId.get(tmp.getId());
            tmp.setProperty(ServiceOrderBean.ORDER_VISIT_RECORD_ID, null==visitRecordId ? 0L : visitRecordId);
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

        boolean changed = false;
        String  newPatientJson       = null;
        String  newAddress           = null;
        Date    newStartTime         = null;
        int     newTimeDuration      = -1;
        int     newTotalPriceCent    = -1;
        int     newTotalDiscountCent = -1;
        int     newTotalIncomeCent   = -1;
        int     newItemCount         = -1;
        String  newLeaveMessage      = null;
        JSONUtil jsonUtil = JSONUtil.newInstance();

        ServiceOrderBean orderBean = beanConverter.convert(entity);

        if (patientId != null && patientService.existPatient(patientId) && patientId!=orderBean.getPatientId()) {
            PatientBean patient = patientService.getOneById(patientId);
            newPatientJson = jsonUtil.toJsonString(patient);
            changed = true;
        }

        if (!VerifyUtil.isStringEmpty(address) && !address.equals(orderBean.getAddress())) {
            newAddress = address;
            changed = true;
        }

        Long lStartTime = NumberUtil.getTime(strStartTime, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        if (lStartTime != null && lStartTime!=orderBean.getServiceStartTime().getTime()) {
            newStartTime = new Date(lStartTime);
            changed = true;
        }

        if (null!=count && count!=orderBean.getItemCount()) {
            ServiceOrderBean bean = beanConverter.convert(entity);
            ServiceItemBean item  = bean.getServiceItem();
            newTimeDuration       = count * item.getServiceTimeDuration();
            newTotalPriceCent     = count * item.getServicePriceCent();
            newTotalDiscountCent  = count * item.getServiceDiscountCent();
            newTotalIncomeCent    = count * item.getServerIncomeCent();
            newItemCount          = count;
            changed = true;
        }

        if (!VerifyUtil.isStringEmpty(leaveAMessage) && !leaveAMessage.equals(orderBean.getLeaveAMessage())) {
            newLeaveMessage = leaveAMessage;
            changed = true;
        }

        if (changed) {
            if (!OrderStatus.TO_PAY.equals(entity.getOrderStatus())) {
                logger.info("the order is in status={}, can not be modified", entity.getOrderStatus());
                throw new BadRequestException(ErrorCode.DATA_ERROR);
            }

            if (null!=newPatientJson) {
                entity.setPatientId(patientId);
                entity.setPatient(newPatientJson);
            }
            if (null!=newAddress) {
                entity.setAddress(address);
            }
            if (null!=newStartTime) {
                entity.setServiceStartTime(new Date(lStartTime));
            }

            if (newTimeDuration>0) {
                entity.setServiceTimeDuration(newTimeDuration);
            }
            if (newTotalPriceCent>0) {
                entity.setTotalPriceCent(newTotalPriceCent);
            }
            if (newTotalDiscountCent>0) {
                entity.setTotalDiscountCent(newTotalDiscountCent);
            }
            if (newTotalIncomeCent>0) {
                entity.setTotalIncomeCent(newTotalIncomeCent);
            }
            if (newItemCount>0) {
                entity.setItemCount(newItemCount);
            }
            if (null!=newLeaveMessage) {
                entity.setLeaveAMessage(newLeaveMessage);
            }

            entity = repository.save(entity);
        }

        ServiceOrderBean bean = beanConverter.convert(entity);
        logger.info("service order added is {}", bean);
        return bean;
    }

    @Transactional
    public void updateOrderStatusForHeadNurse(long orderId, OrderStatus orderStatus) {
        logger.info("update service order={} orderStatus={}", orderId, orderStatus);

        if (OrderStatus.CANCELLED.equals(orderStatus)) {
            cancelOrder(false, 0L, orderId);
            return;
        }
        if (OrderStatus.COMPLETED.equals(orderStatus)) {
            completedOrder(false, 0L, orderId);
            return;
        }

        ServiceOrderEntity entity = repository.findOne(orderId);
        if (null == entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (null == orderStatus) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        boolean canChangeStatus = true;
        if (!OrderStatus.REFUND_IN_PROCESS.equals(orderStatus)
         && !OrderStatus.PAID.equals(entity.getOrderStatus())
         && !OrderStatus.WAIT_NURSE_FETCH.equals(entity.getOrderStatus())
         && !OrderStatus.IN_PROCESS.equals(entity.getOrderStatus())) {
            canChangeStatus = false;
        }
        if (!canChangeStatus) {
            logger.info("the order is in status={}, can not be modified", entity.getOrderStatus());
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        boolean changed = false;
        if (null!=orderStatus && !orderStatus.equals(entity.getOrderStatus())) {
            entity.setOrderStatus(orderStatus);
            changed = true;
        }
        if (changed) {
            entity = repository.save(entity);
        }

        ServiceOrderBean bean = beanConverter.convert(entity);
        logger.info("service order added is {}", bean);
        return;
    }

    @Transactional
    public Charge payForServiceByPingPP(Long userId, Long orderId, String channel, String clientIP) {
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
        PaymentPingPP       payment    = (PaymentPingPP) PaymentFactory.newPayment(PaymentFactory.TYPE_PingPP);
        Map<String, Object> parameters = payment.preparePayment(
                utility.getPingPPAPIKey(), utility.getPingPPRSAPrivateKey(), utility.getPingPPAPPId(),
                clientIP, channel, orderNo,
                order.getTotalConsumptionCent()-order.getPreferentialCent(),
                order.getServiceItem().getName(), order.getServiceItem().getDescription(), order.getLeaveAMessage(), extra
        );
        Map<String, Object> payResponse = payment.pay(parameters);

        /* invoke PingPP SDK failed! */
        Object objCharge = payResponse.get(IPayment.RETURN_VALUE);
        if (!(objCharge instanceof Charge)) {
            entity.setOrderStatus(OrderStatus.CREATE_CHARGE_FAILED);
            repository.save(entity);
            logger.info("create charge object failed ");
            return null;
        }

        Charge charge = (Charge) objCharge;
        /* record PingPP charge instance, and wait for callback! */
        orderChargeService.addOrderCharge(order.getId(), AppType.GO_2_NURSE, orderNo, PaymentPlatform.PingPP, channel, ChargeType.CHARGE, charge.getId(), charge.toString());

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
        PaymentWeChat       payment   = (PaymentWeChat) PaymentFactory.newPayment(PaymentFactory.TYPE_WeChat);
        Map<String, String> parameters = payment.preparePayment(
                "api_key", weChatAccount.getAppId(), weChatAccount.getMchId(),
                "WEB", "JSAPI", openId,
                clientIP, wechatNo, "订单=" + orderNo + " 描述=" + order.getServiceItem().getName(),
                "CNY", order.getTotalConsumptionCent()-order.getPreferentialCent(), utility.getWechatNotifyUrl());
        Map<String, String> payResponse = payment.pay(parameters);
        /* WeChat payment failed! */
        if (!"SUCCESS".equalsIgnoreCase((String) payResponse.get("return_code"))
         || !"SUCCESS".equalsIgnoreCase((String) payResponse.get("result_code"))) {
            entity.setOrderStatus(OrderStatus.CREATE_CHARGE_FAILED);
            repository.save(entity);
            logger.error("WeChat make order failed!");
            throw new BadRequestException(ErrorCode.PAY_FAILED);
        }
        if (!payment.checkSign(payResponse)) {
            entity.setOrderStatus(OrderStatus.CREATE_CHARGE_FAILED);
            repository.save(entity);
            logger.error("WeChat sign not match!");
            throw new BadRequestException(ErrorCode.PAY_FAILED);
        }

        /* WeChat payment success!  record WeChat payment information, and wait for callback! */
        orderChargeService.addOrderCharge(order.getId(), AppType.GO_2_NURSE, orderNo, PaymentPlatform.WX, "wx", ChargeType.CHARGE, wechatNo, payResponse.toString());
        payResponse.remove("mch_id");
        payResponse.remove("device_info");

        return signWeChatResponse(weChatAccount.getAppId(), payResponse.get("prepay_id"),  payResponse.get("nonce_str"));
    }

    private Map<String, String> signWeChatResponse(String appid, String prepayId, String noncestr) {
        StringBuffer buffer = new StringBuffer();
        String signType = "MD5";
        String timeStamp = System.currentTimeMillis()+"";
        buffer.append("appId=").append(appid)
              .append("&nonceStr=").append(noncestr)
              .append("&package=prepay_id=").append(prepayId)
              .append("&signType="+signType)
              .append("&timeStamp=").append(timeStamp)
              .append("&key=").append(utility.getWechatApiKey());
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
    public Map<String, Object> chargeWebHooks(String webHooksBody) {
        Map<String, Object> returnVal = new HashMap<>();
        PaymentPingPP pingPP = (PaymentPingPP) PaymentFactory.newPayment(PaymentFactory.TYPE_PingPP);
        PaymentWeChat weChat = (PaymentWeChat) PaymentFactory.newPayment(PaymentFactory.TYPE_WeChat);

        Map<String, String> parameter = new HashMap<>();
        parameter.put(IPayment.WEB_HOOK_BODY, webHooksBody);

        ServiceOrderBean order = null;

        Map<String, Object> notifyResult = pingPP.processNotify(parameter);
        if (notifyResult.get(IPayment.RETURN_VALUE) instanceof Event) {
            Event event = (Event) notifyResult.get(IPayment.RETURN_VALUE);
            Object eventObj = event.getData().getObject();
            if (eventObj instanceof Charge) {
                logger.info("this is ping++ charge web-hook");
                order = updateOrderCharge(((Charge)eventObj).getId(), event.getId(), webHooksBody, OrderStatus.PAID);
            }
            else if (eventObj instanceof Refund) {
                logger.info("this is ping++ refund web-hook");
                order = updateOrderCharge(((Refund)eventObj).getId(), event.getId(), webHooksBody, OrderStatus.REFUND_COMPLETED);
            }

            returnVal.put("order", order);
            returnVal.put("message", event);
            return returnVal;
        }

        notifyResult = weChat.processNotify(parameter);
        if (notifyResult.get(IPayment.RETURN_VALUE) instanceof Map) {
            logger.info("this is WeChat charge web-hook");
            Map event = (Map) notifyResult.get(IPayment.RETURN_VALUE);
            String outTradeNo = event.get("out_trade_no").toString();
            order = updateOrderCharge(outTradeNo, outTradeNo, JSONUtil.newInstance().toJsonString(event), OrderStatus.PAID);
            returnVal.put("order", order);
            returnVal.put("message", weChat.processReturnValue(parameter));
            return returnVal;
        }

        return returnVal;
    }

    @Transactional
    private ServiceOrderBean updateOrderCharge(String chargeId, String webhooksEventId, String webhooksEventJson, OrderStatus orderStatus) {
        logger.info("order charge webhooks event callback chargeId={} webhooksEventId={} webhooksEventJson={} orderStatus={}",
                chargeId, webhooksEventId, webhooksEventJson, orderStatus);
        ServiceOrderChargeBean charge = orderChargeService.orderChargePingPpWebhooks(chargeId, webhooksEventId, webhooksEventJson);
        long orderId = charge.getOrderId();
        ServiceOrderEntity order = repository.findOne(orderId);
        if (null == order) {
            logger.error("order not exist");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (!OrderStatus.TO_PAY.equals(order.getOrderStatus())
         && !OrderStatus.REFUND_IN_PROCESS.equals(order.getOrderStatus())
        ) {
            logger.error("order status={}, not to_pay or refund_in_process", order.getOrderStatus());
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        order.setOrderStatus(orderStatus);

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
         || OrderStatus.IN_PROCESS.equals(entity.getOrderStatus())
         || OrderStatus.WAIT_NURSE_FETCH.equals(entity.getOrderStatus())
         || OrderStatus.PAID.equals(entity.getOrderStatus())
        ) {
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
    public ServiceOrderBean completeRefundOfOrder(boolean checkUser, long userId, long orderId, Integer refundAmount, String refundReason) {
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
        if (!OrderStatus.REFUND_FAILED.equals(entity.getOrderStatus())
         && !OrderStatus.REFUND_IN_PROCESS.equals(entity.getOrderStatus())) {
            logger.error("the order is in status={}, refund can not be processed", entity.getOrderStatus());
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        // get user payment information
        List<ServiceOrderChargeBean> charges = orderChargeService.getOrderPingPPResult(AppType.GO_2_NURSE, orderId);
        ServiceOrderChargeBean charge = null;
        ServiceOrderChargeBean failedRefund = null;
        if (!VerifyUtil.isListEmpty(charges)) {
            for (ServiceOrderChargeBean tmp : charges) {
                if (ChargeType.CHARGE.equals(tmp.getChargeType())
                 && ChargeStatus.CHARGE_SUCCEED.equals(tmp.getChargeStatus())) {
                    charge = tmp;
                }
                if (null==failedRefund
                 && ChargeType.REFUND.equals(tmp.getChargeType())
                 && (ChargeStatus.CHARGE_FAILED.equals(tmp.getChargeStatus()) || ChargeStatus.CHARGE_CREATED.equals(tmp.getChargeStatus()))
                ) {
                    failedRefund = tmp;
                }
            }
        }

        // user do not pay for the order
        if (null==charge) {
            entity.setOrderStatus(OrderStatus.REFUND_COMPLETED);
            entity.setCompletedTime(new Date());
            entity = repository.save(entity);
            logger.info("user do not pay for the order. order's refund completed is {}", entity);
            return beanConverter.convert(entity);
        }

        // refund the fee of the order
        Map<String, Object> refundResult = null;
        if (PaymentPlatform.PingPP.equals(charge.getPaymentPlatform())) {
            refundResult = refundByPingPP(charge, refundReason, refundAmount);
        }
        else if (PaymentPlatform.WX.equals(charge.getPaymentPlatform())) {
            refundResult = refundByWeChat(charge, failedRefund, refundAmount);
        }
        if (null!=refundReason && null!=refundResult.get(RefundReturnValue)) {
            Object refundValue = refundResult.get(RefundReturnValue);
            if (PaymentPlatform.PingPP.equals(charge.getPaymentPlatform())) {
                Refund refund = (Refund) refundValue;
                /* record PingPP refund instance, and wait for callback via web_hooks! */
                Object orderCharge = orderChargeService.addOrderCharge(orderId, AppType.GO_2_NURSE, entity.getOrderNo(), PaymentPlatform.PingPP, charge.getChannel(), ChargeType.REFUND, refund.getId(), refund.toString());
                if (null!=orderCharge) {
                    entity.setOrderStatus(OrderStatus.REFUND_PROCESSED);
                    entity.setCompletedTime(new Date());
                    entity = repository.save(entity);
                }
            }
            else if (PaymentPlatform.WX.equals(charge.getPaymentPlatform())) {
                Map wechatRefundResponse = (Map) refundValue;
                String wechatReturnCode  = (String) wechatRefundResponse.get("return_code");
                String wechatRefundNo    = (String) wechatRefundResponse.get("out_refund_no");
                /* Record WeChat refund instance! */
                orderChargeService.addOrderCharge(orderId, AppType.GO_2_NURSE, entity.getOrderNo(), PaymentPlatform.WX, "wx", ChargeType.REFUND, wechatRefundNo, JSONUtil.newInstance().toJsonString(wechatRefundResponse));
                /* WeChat refund success, and set order status to REFUND_COMPLETED*/
                if ("SUCCESS".equalsIgnoreCase(wechatReturnCode)) {
                    wechatRefundResponse.put("refund_reason", (null==refundReason||refundReason.isEmpty()) ? "无" : refundReason);
                    orderChargeService.orderChargePingPpWebhooks(wechatRefundNo, wechatRefundNo, JSONUtil.newInstance().toJsonString(wechatRefundResponse));

                    entity.setOrderStatus(OrderStatus.REFUND_COMPLETED);
                    entity.setCompletedTime(new Date());
                    entity = repository.save(entity);
                }
            }
        }
        else {
            if (PaymentPlatform.PingPP.equals(charge.getPaymentPlatform())) {
                logger.error("Ping++ refund is failed. message is {}", refundResult);
            }
            else if (PaymentPlatform.WX.equals(charge.getPaymentPlatform())) {
                logger.error("WeChat refund is failed. message is {}", refundResult);
            }
        }

        return beanConverter.convert(entity);
    }

    private Map<String, Object> refundByWeChat(ServiceOrderChargeBean charge, ServiceOrderChargeBean failedRefund, Integer refundAmount) {
        Map<String, Object> returnVal = new HashMap<>();

        String chargeJson = charge.getWebhooksEventJson();
        Map<String, String> chargeMap = JSONUtil.newInstance().parseJsonMap(chargeJson, String.class, String.class);
        String  appId      = chargeMap.get("appid");
        String  mchId      = chargeMap.get("mch_id");
        String  opUserId   = mchId;
        String  devInfo    = chargeMap.get("device_info");
        String  transId    = chargeMap.get("transaction_id");
        String  feeType    = chargeMap.get("fee_type");
        Integer totalFee   = VerifyUtil.parseIntIds(chargeMap.get("total_fee")).get(0);
        String  outRefundNo= NumberUtil.createNoncestr(31);
        // 微信提示: 一笔退款失败后重新提交，请不要更换退款单号，请使用原商户退款单号。
        if (null!=failedRefund) {
            outRefundNo = failedRefund.getChargeId();
        }
        else {
            for (int i=5; i>=0 && orderChargeService.existsChargeId(outRefundNo); i--) {
                outRefundNo = NumberUtil.createNoncestr(31);
            }
            if (orderChargeService.existsChargeId(outRefundNo)) {
                returnVal.put(RefundReturnValue, null);
                return returnVal;
            }
        }
        if (null==refundAmount) {
            refundAmount  = totalFee;
        }

        PaymentWeChat payment = (PaymentWeChat) PaymentFactory.newPayment(PaymentFactory.TYPE_WeChat);
        Map<String, String> parameters = payment.prepareRefund(utility.getWechatApiKey(), appId, mchId, opUserId, devInfo, transId, null, outRefundNo, totalFee, refundAmount, feeType);
        Map<String, String> refundResponse = payment.refund(parameters);

        returnVal.put(RefundReturnValue, refundResponse);
        return returnVal;
    }

    private Map<String, Object> refundByPingPP(ServiceOrderChargeBean charge, String refundReason, Integer refundAmount) {
        PaymentPingPP payment = (PaymentPingPP) PaymentFactory.newPayment(PaymentFactory.TYPE_PingPP);
        Map<String, Object> parameters = payment.prepareRefund(utility.getPingPPAPIKey(), utility.getPingPPRSAPrivateKey(), charge.getChargeId(), refundAmount, refundReason, null);
        Map<String, Object> refundResponse = payment.refund(parameters);

        refundResponse.put(RefundReturnValue, refundResponse.get(IPayment.RETURN_VALUE));
        return refundResponse;
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
        Long lStartTime = NumberUtil.getTime(strStartTime, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        if (null==lStartTime) {
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

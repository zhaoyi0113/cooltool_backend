package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.service.ServiceOrderService;
import com.cooltoo.util.NumberUtil;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by hp on 2016/9/9.
 */
@Transactional
@DatabaseSetups({
        @DatabaseSetup("classpath:/com/cooltoo/services/service_category_data.xml"),
        @DatabaseSetup("classpath:/com/cooltoo/services/service_vendor_data.xml"),
        @DatabaseSetup("classpath:/com/cooltoo/services/service_item_data.xml"),
        @DatabaseSetup("classpath:/com/cooltoo/services/service_order_data.xml"),
        @DatabaseSetup("classpath:/com/cooltoo/services/service_order_charge_pingpp_data.xml"),
        @DatabaseSetup("classpath:/com/cooltoo/services/user_data.xml"),
        @DatabaseSetup("classpath:/com/cooltoo/services/patient_data.xml"),
        @DatabaseSetup("classpath:/com/cooltoo/services/user_patient_relation_data.xml"),
        @DatabaseSetup("classpath:/com/cooltoo/services/user_address_data.xml")
})
public class ServiceOrderServiceTest extends AbstractCooltooTest {

    @Autowired public ServiceOrderService orderService;

    @Test
    public void countAllOrder() {
        long count = orderService.countAllOrder();
        Assert.assertEquals(7, count);
    }

    @Test
    public void getOrder() {
        List<ServiceOrderBean> orders = orderService.getOrder(0, 2);
        Assert.assertEquals(2, orders.size());
        Assert.assertEquals(283L, orders.get(0).getId());
        Assert.assertEquals(282L, orders.get(1).getId());

        orders = orderService.getOrder(2, 2);
        Assert.assertEquals(2, orders.size());
        Assert.assertEquals(278L, orders.get(0).getId());
        Assert.assertEquals(277L, orders.get(1).getId());
    }

    @Test
    public void countOrderByConditions() {
        long userId = 452;
        long categoryId = 204;
        long topCategoryId = 200;
        OrderStatus orderStatus = OrderStatus.CANCELLED;
        long count = orderService.countOrderByConditions(null, userId, null, null, null, null, null);
        Assert.assertEquals(4, count);

        count = orderService.countOrderByConditions(null, null, categoryId, null, null, null, null);
        Assert.assertEquals(3, count);

        count = orderService.countOrderByConditions(null, null, null, topCategoryId, null, null, null);
        Assert.assertEquals(6, count);

        count = orderService.countOrderByConditions(null, null, null, null, null, null, orderStatus);
        Assert.assertEquals(0, count);
    }

    @Test
    public void getOrderByConditions() {
        long userId = 452;
        long categoryId = 204;
        long topCategoryId = 200;
        OrderStatus orderStatus = OrderStatus.CANCELLED;
        List<ServiceOrderBean> beans = orderService.getOrderByConditions(null, userId, null, null, null, null, null, 0, 10);
        Assert.assertEquals(4, beans.size());
        Assert.assertEquals(283, beans.get(0).getId());
        Assert.assertEquals(282, beans.get(1).getId());
        Assert.assertEquals(280, beans.get(2).getId());
        Assert.assertEquals(279, beans.get(3).getId());

        beans = orderService.getOrderByConditions(null, null, categoryId, null, null, null, null, 0, 10);
        Assert.assertEquals(3, beans.size());
        Assert.assertEquals(278, beans.get(0).getId());
        Assert.assertEquals(277, beans.get(1).getId());
        Assert.assertEquals(276, beans.get(2).getId());

        beans = orderService.getOrderByConditions(null, null, null, topCategoryId, null, null, null, 0, 10);
        Assert.assertEquals(6, beans.size());
        Assert.assertEquals(283, beans.get(0).getId());
        Assert.assertEquals(280, beans.get(1).getId());
        Assert.assertEquals(279, beans.get(2).getId());
        Assert.assertEquals(278, beans.get(3).getId());
        Assert.assertEquals(277, beans.get(4).getId());
        Assert.assertEquals(276, beans.get(5).getId());

        beans = orderService.getOrderByConditions(null, null, null, null, null, null, orderStatus, 0, 10);
        Assert.assertEquals(0, beans.size());
    }

    @Test
    public void getOrderByOrderId() {
        long orderId = 283;
        List<ServiceOrderBean> orders = orderService.getOrderByOrderId(orderId);
        Assert.assertEquals(1, orders.size());
        Assert.assertEquals(283, orders.get(0).getId());
    }

    @Test
    public void getOrderByUserId() {
        long userId = 452;
        List<ServiceOrderBean> beans = orderService.getOrderByUserId(userId);
        Assert.assertEquals(4, beans.size());
        Assert.assertEquals(283, beans.get(0).getId());
        Assert.assertEquals(282, beans.get(1).getId());
        Assert.assertEquals(280, beans.get(2).getId());
        Assert.assertEquals(279, beans.get(3).getId());
    }

    @Test
    public void updateOrder() {
    /*
    TO_PAY(0, "TO_PAY"), // 等待支付
    TO_DISPATCH(1, "TO_DISPATCH"), // 等待接单
    TO_SERVICE(2, "TO_SERVICE"), // 等待服务
    IN_PROCESS(3, "IN_PROCESS"), // 服务中
    COMPLETED(4, "COMPLETED"),  // 服务完成
        <go2nurse_service_order id="276"     order_status="1"
        <go2nurse_service_order id="278"     order_status="1"
        <go2nurse_service_order id="279"     order_status="5"
        <go2nurse_service_order id="280"     order_status="4"
        <go2nurse_service_order id="282"     order_status="2"
    */
        long orderId = 276;
        long patientId = 17;
        long addressId = 16;
        String startTime ="2016-09-21 12:00:00";
        long lStartTime = NumberUtil.getTime(startTime, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        int count = 15;
        String leaveMessage = "message test";
        int preferentialCent = 1000;
        ServiceOrderBean order = orderService.updateOrder(orderId, patientId, addressId, startTime, count, leaveMessage, preferentialCent);
        Assert.assertEquals(orderId, order.getId());
        Assert.assertEquals(patientId, order.getPatientId());
        Assert.assertEquals(addressId, order.getAddressId());
        Assert.assertEquals(lStartTime, order.getServiceStartTime().getTime());
        Assert.assertEquals(count, order.getItemCount());
        Assert.assertEquals(leaveMessage, order.getLeaveAMessage());
        Assert.assertEquals(preferentialCent, order.getPreferentialCent());

        orderId = 279;
        Throwable th = null;
        try {
            order = orderService.updateOrder(orderId, patientId, addressId, startTime, count, leaveMessage, preferentialCent);
        } catch (Exception ex) { th = ex; }
        Assert.assertNotNull(th);

        orderId = 280;
        th = null;
        try {
            order = orderService.updateOrder(orderId, patientId, addressId, startTime, count, leaveMessage, preferentialCent);
        } catch (Exception ex) { th = ex; }
        Assert.assertNotNull(th);

        orderId = 282;
        th = null;
        try {
            order = orderService.updateOrder(orderId, patientId, addressId, startTime, count, leaveMessage, preferentialCent);
        } catch (Exception ex) { th = ex; }
        Assert.assertNotNull(th);

    }

    @Test
    public void payForService() {
        long orderId = 276;
        long userId = 1;
        String channel = "";
        String clientIp = "";
        Throwable th = null;
        try {
            orderService.payForService(userId, orderId, channel, clientIp);
        } catch (Exception ex) { th=ex; }
        Assert.assertNotNull(th);

        userId = 463;
        th = null;
        try {
            orderService.payForService(userId, orderId, channel, clientIp);
        } catch (Exception ex) { th=ex; }
        Assert.assertNotNull(th);

        channel = "wx";
        Object charge = (Object)orderService.payForService(userId, orderId, channel, clientIp);
        Assert.assertNull(charge);

        clientIp = "127.0.0.1";
        charge = (Object)orderService.payForService(userId, orderId, channel, clientIp);
        Assert.assertNotNull(charge);

    }

    @Test
    public void cancelOrder() {
        long orderId = 276;
        long userId = 1;
        ServiceOrderBean order = null;
        Throwable th = null;
        try {
            order = orderService.cancelOrder(true, userId, orderId);
        } catch (Exception ex) { th = ex; }
        Assert.assertNotNull(th);

        userId = 463;
        th = null;
        try {
            order = orderService.cancelOrder(true, userId, orderId);
        } catch (Exception ex) { th = ex; }
        Assert.assertNull(th);
        Assert.assertEquals(orderId, order.getId());
        Assert.assertEquals(OrderStatus.CANCELLED, order.getOrderStatus());

        orderId = 279;
        th = null;
        try {
            order = orderService.cancelOrder(true, userId, orderId);
        } catch (Exception ex) { th = ex; }
        Assert.assertNotNull(th);

        orderId = 280;
        th = null;
        try {
            order = orderService.cancelOrder(true, userId, orderId);
        } catch (Exception ex) { th = ex; }
        Assert.assertNotNull(th);

        orderId = 282;
        th = null;
        try {
            order = orderService.cancelOrder(true, userId, orderId);
        } catch (Exception ex) { th = ex; }
        Assert.assertNotNull(th);
    }

    @Test
    public void addOrder() {
        long itemId = 11;
        long userId = 463;
        long patientId = 17;
        long addressId = 16;
        String startTime ="2016-09-21 12:00:00";
        long lStartTime = NumberUtil.getTime(startTime, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        int count = 15;
        String leaveMessage = "message test";
        int preferentialCent = 1000;
        ServiceOrderBean order = orderService.addOrder(itemId, userId, patientId, addressId, startTime, count, leaveMessage, preferentialCent);
        Assert.assertTrue(order.getId()>0);
        Assert.assertEquals(userId, order.getUserId());
        Assert.assertEquals(patientId, order.getPatientId());
        Assert.assertEquals(addressId, order.getAddressId());
        Assert.assertEquals(lStartTime, order.getServiceStartTime().getTime());
        Assert.assertEquals(count, order.getItemCount());
        Assert.assertEquals(leaveMessage, order.getLeaveAMessage());
        Assert.assertEquals(preferentialCent, order.getPreferentialCent());
    }
}

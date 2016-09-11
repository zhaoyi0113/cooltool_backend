package com.cooltoo.services;

import com.cooltoo.AbstractCooltooTest;
import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.service.ServiceOrderChargePingPPService;
import com.cooltoo.go2nurse.service.ServiceOrderService;
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
        @DatabaseSetup("classpath:/com/cooltoo/services/user_patient_relation_data.xml")
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

    }

    @Test
    public void payForService() {
    }

    @Test
    public void cancelOrder() {
    }

    @Test
    public void addOrder() {
    }
}

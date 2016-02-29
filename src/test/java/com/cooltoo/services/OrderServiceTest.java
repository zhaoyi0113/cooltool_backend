package com.cooltoo.services;

import com.cooltoo.beans.BadgeBean;
import com.cooltoo.beans.OrderBean;
import com.cooltoo.serivces.OrderService;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by lg380357 on 2016/2/29.
 */
@Transactional
public class OrderServiceTest extends AbstractCooltooTest {
    @Autowired
    private OrderService service;

    @Test
    public void testNewOrder() {
        OrderBean bean = new OrderBean();
        bean.setName("name_1");
        bean.setCash(new BigDecimal("10.03"));
        bean.setCount(44);
        long id = service.newOrder(bean);
        Assert.assertTrue(id>0);
        List<OrderBean> all = service.getAll();
        Assert.assertTrue(all.size()>0);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/order_data.xml")
    public void testGetAll(){
        List<OrderBean> all = service.getAll();
        Assert.assertEquals(5, all.size());
    }

    @Test
    @DatabaseSetup(value="classpath:/com/cooltoo/services/order_data.xml")
    public void testDeleteOrder() {
        OrderBean bean = service.deleteOrder(5);
        Assert.assertNotNull(bean);
        Assert.assertEquals(5, bean.getId());
        bean = service.getOrder(5);
        Assert.assertNull(bean);
    }

    @Test
    @DatabaseSetup(value = "classpath:/com/cooltoo/services/order_data.xml")
    public void testUpdateBadget(){
        OrderBean bean = service.updateOrder(1, "name222222", 22, new BigDecimal("22.22"));
        Assert.assertNotNull(bean);
        Assert.assertEquals(1, bean.getId());
        Assert.assertEquals("name222222", bean.getName());
        Assert.assertEquals(22, bean.getCount());
        Assert.assertEquals(new BigDecimal("22.22"), bean.getCash());
    }
}

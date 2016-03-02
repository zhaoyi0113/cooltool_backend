package com.cooltoo.converter;

import com.cooltoo.beans.OrderBean;
import com.cooltoo.entities.OrderEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by lg380357 on 2016/2/29.
 */
@Component
public class OrderBeanConverter implements Converter<OrderEntity, OrderBean> {
    @Override
    public OrderBean convert(OrderEntity entity) {
        OrderBean order = new OrderBean();
        order.setId(entity.getId());
        order.setName(entity.getName());
        order.setCash(entity.getPrice() );
        order.setCount(entity.getCount());
        return order;
    }
}

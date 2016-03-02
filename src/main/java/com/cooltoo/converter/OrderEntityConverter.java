package com.cooltoo.converter;

import com.cooltoo.beans.OrderBean;
import com.cooltoo.entities.OrderEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Created by lg380357 on 2016/2/29.
 */
@Component
public class OrderEntityConverter implements Converter<OrderBean, OrderEntity> {
    @Override
    public OrderEntity convert(OrderBean bean) {
        OrderEntity entity = new OrderEntity();
        entity.setId(bean.getId());
        entity.setName(bean.getName());
        entity.setCount(bean.getCount());
        entity.setPrice(bean.getCash() );
        return entity;
    }
}

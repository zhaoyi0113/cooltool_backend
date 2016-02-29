package com.cooltoo.serivces;

import com.cooltoo.beans.OrderBean;
import com.cooltoo.converter.OrderBeanConverter;
import com.cooltoo.converter.OrderEntityConverter;
import com.cooltoo.entities.OrderEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lg380357 on 2016/2/29.
 */
@Service("OrderService")
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderBeanConverter beanConverter;
    @Autowired
    private OrderEntityConverter entityConverter;

    @Transactional
    public long newOrder(String name, int count, BigDecimal cash) {
        OrderBean bean = new OrderBean();
        bean.setName(name);
        bean.setCash(cash);
        bean.setCount(count);
        return newOrder(bean);
    }

    @Transactional
    public long newOrder(OrderBean bean) {
        OrderEntity entity = entityConverter.convert(bean);
        entity = orderRepository.save(entity);
        return entity.getId();
    }

    public OrderBean getOrder(long id) {
        OrderEntity entity = orderRepository.findOne(id);
        if (null == entity) {
            return null;
        }
        return beanConverter.convert(entity);
    }

    public List<OrderBean> getAll() {
        Iterable<OrderEntity> all = orderRepository.findAll();
        List<OrderBean> beanList = new ArrayList<OrderBean>();
        for (OrderEntity entity : all) {
            OrderBean bean = beanConverter.convert(entity);
            beanList.add(bean);
        }
        return beanList;
    }

    @Transactional
    public OrderBean deleteOrder(long id) {
        OrderEntity entity = orderRepository.findOne(id);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.ORDER_NOT_EXIST);
        }
        orderRepository.delete(id);
        return beanConverter.convert(entity);
    }

    @Transactional
    public OrderBean updateOrder(long id, String name, int count, BigDecimal cash) {
        OrderEntity entity = orderRepository.findOne(id);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.ORDER_NOT_EXIST);
        }
        boolean changed = false;
        if (null!=name && !"".equals(name) && !entity.getName().equals(name)) {
            entity.setName(name);
            changed = true;
        }
        if (entity.getCash() != cash && cash.doubleValue() > 0.0) {
            entity.setCash(cash);
            changed = true;
        }
        if (entity.getCount()!=count && count > 0) {
            entity.setCount(count);
            changed = true;
        }
        if (changed) {
            entity = orderRepository.save(entity);
        }
        return beanConverter.convert(entity);
    }
}

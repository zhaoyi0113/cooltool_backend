package com.cooltoo.repository;

import com.cooltoo.entities.OrderEntity;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by lg380357 on 2016/2/29.
 */
public interface  OrderRepository extends CrudRepository<OrderEntity, Long> {
}

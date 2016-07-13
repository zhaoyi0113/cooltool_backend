package com.cooltoo.go2nurse.service;

import com.cooltoo.go2nurse.converter.ServiceOrderBeanConverter;
import com.cooltoo.go2nurse.repository.ServiceOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hp on 2016/7/13.
 */
@Service("ServiceOrderService")
public class ServiceOrderService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceOrderService.class);

    @Autowired private ServiceOrderRepository repository;
    @Autowired private ServiceOrderBeanConverter beanConverter;

}

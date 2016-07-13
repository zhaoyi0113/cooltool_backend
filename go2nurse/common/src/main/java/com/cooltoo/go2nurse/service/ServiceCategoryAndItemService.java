package com.cooltoo.go2nurse.service;

import com.cooltoo.go2nurse.converter.ServiceCategoryBeanConverter;
import com.cooltoo.go2nurse.converter.ServiceItemBeanConverter;
import com.cooltoo.go2nurse.repository.ServiceCategoryRepository;
import com.cooltoo.go2nurse.repository.ServiceItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hp on 2016/7/13.
 */
@Service("ServiceCategoryAndItemService")
public class ServiceCategoryAndItemService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceCategoryAndItemService.class);

    @Autowired private ServiceCategoryRepository categoryRep;
    @Autowired private ServiceCategoryBeanConverter categoryBeanConverter;
    @Autowired private ServiceItemRepository itemRep;
    @Autowired private ServiceItemBeanConverter itemBeanConverter;

}

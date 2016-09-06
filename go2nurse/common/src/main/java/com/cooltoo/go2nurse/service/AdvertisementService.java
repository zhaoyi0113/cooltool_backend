package com.cooltoo.go2nurse.service;

import com.cooltoo.go2nurse.converter.AdvertisementBeanConverter;
import com.cooltoo.go2nurse.repository.AdvertisementRepository;
import com.cooltoo.go2nurse.service.file.UserGo2NurseFileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hp on 2016/9/6.
 */
@Service("AdvertisementService")
public class AdvertisementService {

    private final static Logger logger = LoggerFactory.getLogger(AdvertisementService.class);

    @Autowired private AdvertisementRepository repository;
    @Autowired private AdvertisementBeanConverter beanConverter;

    @Autowired private UserGo2NurseFileStorageService userFileStorage;


}

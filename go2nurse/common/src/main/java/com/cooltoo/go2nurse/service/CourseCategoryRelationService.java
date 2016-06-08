package com.cooltoo.go2nurse.service;

import com.cooltoo.go2nurse.converter.CourseCategoryRelationBeanConverter;
import com.cooltoo.go2nurse.repository.CourseCategoryRelationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by hp on 2016/6/8.
 */
@Service("CourseCategoryRelationService")
public class CourseCategoryRelationService {

    private static final Logger logger = LoggerFactory.getLogger(CourseCategoryRelationService.class);

    @Autowired private CourseCategoryRelationRepository repository;
    @Autowired private CourseCategoryRelationBeanConverter beanConverter;
    @Autowired private CourseService courseService;
    @Autowired private CourseCategoryService categoryService;

}

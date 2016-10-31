package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.CourseBean;
import com.cooltoo.go2nurse.beans.CourseCategoryBean;
import com.cooltoo.go2nurse.beans.CourseCategoryRelationBean;
import com.cooltoo.go2nurse.constants.CourseStatus;
import com.cooltoo.go2nurse.converter.CourseCategoryRelationBeanConverter;
import com.cooltoo.go2nurse.entities.CourseCategoryRelationEntity;
import com.cooltoo.go2nurse.repository.CourseCategoryRelationRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by hp on 2016/6/8.
 */
@Service("CourseCategoryRelationService")
public class CourseCategoryRelationService {

    private static final Logger logger = LoggerFactory.getLogger(CourseCategoryRelationService.class);

    private static final Sort relationSort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private CourseCategoryRelationRepository relationRepository;
    @Autowired private CourseCategoryRelationBeanConverter relationBeanConverter;
    @Autowired private CourseService courseService;
    @Autowired private CourseCategoryService categoryService;

    //=================================================================
    //                       getter
    //=================================================================

    public CourseCategoryRelationBean getRelation(long courseId, long categoryId) {
        List<CourseCategoryRelationEntity> relations = relationRepository.findByCourseIdAndCourseCategoryId(courseId, categoryId, relationSort);
        if (!VerifyUtil.isListEmpty(relations)) {
            CourseCategoryRelationEntity relation = relations.get(0);
            return relationBeanConverter.convert(relation);
        }
        return null;
    }

    public List<CourseCategoryBean> getCategoryByCourseId(String strCategoryStatus, long courseId) {
        logger.info("get course category at status={} by courseId={}", strCategoryStatus, courseId);
        List<Long> categoryIds = relationRepository.findCategoryIdByStatusAndCourseId(CommonStatus.ENABLED, courseId);
        List<CourseCategoryBean> categories = categoryService.getCategoryByStatusAndIds(strCategoryStatus, categoryIds);
        logger.info("count is {}", categories.size());
        return categories;
    }

    public List<CourseCategoryBean> getCategoryByCourseId(String strCategoryStatus, List<Long> courseIds) {
        logger.info("get course category at status={} by courseIds={}", strCategoryStatus, courseIds);
        List<Long> categoryIds = relationRepository.findCategoryIdByStatusAndCourseIdIn(CommonStatus.ENABLED, courseIds);
        List<CourseCategoryBean> categories = categoryService.getCategoryByStatusAndIds(strCategoryStatus, categoryIds);
        logger.info("count is {}", categories.size());
        return categories;
    }

    public List<CourseBean> getCourseByCategoryId(String strCourseStatus, long categoryId) {
        logger.info("get course at status={} by categoryId={}", strCourseStatus, categoryId);
        CourseStatus courseStatus = CourseStatus.parseString(strCourseStatus);
        List<Long> courseIds = relationRepository.findCourseIdByStatusAndCategoryId(CommonStatus.ENABLED, Arrays.asList(new Long[]{categoryId}));
        List<CourseBean> courses = courseService.getCourseByStatusAndIds(courseStatus, courseIds, null, null);
        logger.info("count is {}", courses.size());
        return courses;
    }


    //============================================================================
    //                 delete relation permanently
    //============================================================================
    @Transactional
    public List<Long> deleteRelationPermanentlyByCourseIds(List<Long> courseIds) {
        logger.info("delete relation by courseIds={}", courseIds);
        if (VerifyUtil.isListEmpty(courseIds)) {
            return new ArrayList<>();
        }
        List<CourseCategoryRelationEntity> set = relationRepository.findByStatusAndCourseIdIn(null, courseIds);
        relationRepository.delete(set);
        return courseIds;
    }

    //=================================================================
    //         add
    //=================================================================

    @Transactional
    public CourseCategoryRelationBean setCourseRelation(long courseId, long categoryId) {
        logger.info("set course relation courseId={} categoryId={}", courseId, categoryId);
        if (!categoryService.existsCategory(categoryId)) {
            logger.info("category not exists");
            return null;
        }
        if (!courseService.existCourse(courseId)) {
            logger.info("course not exists");
            return null;
        }
        CourseCategoryRelationBean relationBean;
        List<CourseCategoryRelationEntity> relations = relationRepository.findByCourseId(courseId, relationSort);
        if (VerifyUtil.isListEmpty(relations)) {
            CourseCategoryRelationEntity entity = createEntity(courseId, categoryId);
            entity = relationRepository.save(entity);
            relationBean = relationBeanConverter.convert(entity);
        }
        else {
            CourseCategoryRelationEntity relationAdded = null;
            for (CourseCategoryRelationEntity relation : relations) {
                if (relation.getCourseCategoryId()==categoryId) {
                    relationAdded = relation;
                }
            }
            if (null!=relationAdded) {
                relations.remove(relationAdded);
            }
            if (!VerifyUtil.isListEmpty(relations)) {
                relationRepository.delete(relations);
            }
            if (null==relationAdded) {
                relationAdded = createEntity(courseId, categoryId);
            }
            relationAdded.setStatus(CommonStatus.ENABLED);
            relationRepository.save(relationAdded);

            relationBean = relationBeanConverter.convert(relationAdded);
        }
        return relationBean;
    }

    private CourseCategoryRelationEntity createEntity(long courseId, long categoryId) {
        CourseCategoryRelationEntity entity = new CourseCategoryRelationEntity();
        entity.setCourseId(courseId);
        entity.setCourseCategoryId(categoryId);
        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        return entity;
    }

}

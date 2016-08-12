package com.cooltoo.go2nurse.service;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.CourseBean;
import com.cooltoo.go2nurse.beans.CourseCategoryBean;
import com.cooltoo.go2nurse.beans.CourseCategoryRelationBean;
import com.cooltoo.go2nurse.converter.CourseCategoryBeanConverter;
import com.cooltoo.go2nurse.converter.CourseCategoryRelationBeanConverter;
import com.cooltoo.go2nurse.entities.CourseCategoryEntity;
import com.cooltoo.go2nurse.entities.CourseCategoryRelationEntity;
import com.cooltoo.go2nurse.repository.CourseCategoryRelationRepository;
import com.cooltoo.go2nurse.repository.CourseCategoryRepository;
import com.cooltoo.go2nurse.service.file.UserGo2NurseFileStorageService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

/**
 * Created by hp on 2016/6/8.
 */
@Service("CourseCategoryService")
public class CourseCategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CourseCategoryService.class);

    public static final String category_all = "all";
    public static final String category_others = "others";

    private static final Sort categorySort = new Sort(
            new Sort.Order(Sort.Direction.ASC, "id")
    );

    private static final Sort relationSort = new Sort(
            new Sort.Order(Sort.Direction.DESC, "id")
    );

    @Autowired private CourseCategoryRepository repository;
    @Autowired private CourseCategoryBeanConverter beanConverter;
    @Autowired private UserGo2NurseFileStorageService fileStorageService;

    @Autowired private CourseService courseService;
    @Autowired private CourseCategoryRelationRepository relationRepository;
    @Autowired private CourseCategoryRelationBeanConverter relationBeanConverter;

    //=================================================================
    //         relation getter
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
        logger.info("get course category at status={} by courseId={}",
                strCategoryStatus, courseId);
        List<Long> categoryIds = relationRepository.findCategoryIdByStatusAndCourseId(CommonStatus.ENABLED, courseId);
        List<CourseCategoryBean> categories = getCategoryByStatusAndIds(strCategoryStatus, categoryIds);
        logger.info("count is {}", categories.size());
        return categories;
    }

    public List<CourseCategoryBean> getCategoryByCourseId(String strCategoryStatus, List<Long> courseIds) {
        logger.info("get course category at status={} by courseIds={}",
                strCategoryStatus, courseIds);
        List<Long> categoryIds = relationRepository.findCategoryIdByStatusAndCourseIdIn(CommonStatus.ENABLED, courseIds);
        List<CourseCategoryBean> categories = getCategoryByStatusAndIds(strCategoryStatus, categoryIds);
        logger.info("count is {}", categories.size());
        return categories;
    }

    public Map<CourseCategoryBean, List<CourseBean>> getCategoryRelationByCourse(List<CourseBean> courses) {
        logger.info("get course category to course Map by courses", courses);
        if (VerifyUtil.isListEmpty(courses)) {
            logger.info("courses is empty");
            return new HashMap<>();
        }
        List<Long> courseIds = new ArrayList<>();
        for (CourseBean course : courses) {
            if (courseIds.contains(course.getId())) {
                continue;
            }
            courseIds.add(course.getId());
        }
        List<CourseCategoryRelationEntity> relations = relationRepository.findByStatusAndCourseIdIn(CommonStatus.ENABLED, courseIds);
        return getCategoryRelationByCourseAndRelation(relations, courses);
    }

    public Map<CourseCategoryBean, List<CourseBean>> getCategoryRelationByCourseId(List<Long> courseIds) {
        logger.info("get course category to course Map by courseIds={}", courseIds);
        if (VerifyUtil.isListEmpty(courseIds)) {
            logger.info("courseIds is empty");
            return new HashMap<>();
        }
        List<CourseBean> courses = courseService.getCourseByIds(courseIds);
        List<CourseCategoryRelationEntity> relations = relationRepository.findByStatusAndCourseIdIn(CommonStatus.ENABLED, courseIds);
        return getCategoryRelationByCourseAndRelation(relations, courses);
    }

    private Map<CourseCategoryBean, List<CourseBean>> getCategoryRelationByCourseAndRelation(
            List<CourseCategoryRelationEntity> relations, List<CourseBean> courses) {
        logger.info("get course category to course Map by relations and courses");
        if (VerifyUtil.isListEmpty(relations) || VerifyUtil.isListEmpty(courses)) {
            return new HashMap<>();
        }

        Map<Long, List<Long>> courseIdToCategoryIds = new HashMap<>();
        List<Long> categoriesId = new ArrayList<>();

        for (CourseCategoryRelationEntity relation : relations) {
            Long courseId = relation.getCourseId();
            List<Long> tmpCategoriesId = courseIdToCategoryIds.get(courseId);
            if (null==tmpCategoriesId) {
                tmpCategoriesId = new ArrayList<>();
                courseIdToCategoryIds.put(courseId, tmpCategoriesId);
            }

            Long categoryId = relation.getCourseCategoryId();
            if (!tmpCategoriesId.contains(categoryId)) {
                tmpCategoriesId.add(categoryId);
            }

            if (!categoriesId.contains(categoryId)) {
                categoriesId.add(categoryId);
            }
        }

        Map<CourseCategoryBean, List<CourseBean>> returnValue =
                fillCategoryToCourseMap(courseIdToCategoryIds, categoriesId, courses);
        logger.info("count is {}", returnValue.size());
        return returnValue;
    }

    private Map<CourseCategoryBean, List<CourseBean>> fillCategoryToCourseMap(
            Map<Long, List<Long>> courseIdToCategoriesId,
            List<Long> categoriesId,
            List<CourseBean> courses) {

        Map<Long, CourseCategoryBean> categoryIdToBean = new HashMap<>();
        List<CourseCategoryBean> categories = getCategoryByStatusAndIds("ALL", categoriesId);
        for (CourseCategoryBean category : categories) {
            categoryIdToBean.put(category.getId(), category);
        }

        // all courses sor
        List<CourseBean> allCourseSortedByReadStatus = new ArrayList<>();
        CourseCategoryBean courseCategoryAllSortedByReadStatus = new CourseCategoryBean();
        courseCategoryAllSortedByReadStatus.setId(-1);
        courseCategoryAllSortedByReadStatus.setName(category_all);
        courseCategoryAllSortedByReadStatus.setIntroduction(category_all);
        // all course without any category property
        CourseCategoryBean others = new CourseCategoryBean();
        others.setId(-2);
        others.setName(category_others);
        others.setIntroduction(category_others);

        Map<CourseCategoryBean, List<CourseBean>> categoryToCourses = new HashMap<>();
        categoryToCourses.put(courseCategoryAllSortedByReadStatus, allCourseSortedByReadStatus);

        for (CourseBean course : courses) {
            // construct all course sorted by read status
            if (!ReadingStatus.READ.equals(course.getReading())) {
                allCourseSortedByReadStatus.add(course);
            }

            Long tmpCourseId = course.getId();
            List<Long> tmpCategoriesId = courseIdToCategoriesId.get(tmpCourseId);

            // belong no category
            if (VerifyUtil.isListEmpty(tmpCategoriesId)) {
                List<CourseBean> tmpCourses = categoryToCourses.get(others);
                if (null==tmpCourses) {
                    tmpCourses = new ArrayList<>();
                    categoryToCourses.put(others, tmpCourses);
                }
                tmpCourses.add(course);
                continue;
            }

            for (Long tmpCategoryId : tmpCategoriesId) {
                CourseCategoryBean tmpCategory = categoryIdToBean.get(tmpCategoryId);
                if (null==tmpCategory) {
                    tmpCategory = others;
                }

                List<CourseBean> tmpCourses = categoryToCourses.get(tmpCategory);
                if (null==tmpCourses) {
                    tmpCourses = new ArrayList<>();
                    categoryToCourses.put(tmpCategory, tmpCourses);
                }
                tmpCourses.add(course);
            }
        }

        for (CourseBean course : courses) {
            // construct all course sorted by read status
            if (ReadingStatus.READ.equals(course.getReading())) {
                allCourseSortedByReadStatus.add(course);
            }
        }

        return categoryToCourses;
    }


    public List<CourseBean> getCourseByCategoryId(String strCourseStatus, long categoryId) {
        logger.info("get course at status={} by categoryId={}",
                strCourseStatus, categoryId);
        List<Long> courseIds = relationRepository.findCourseIdByStatusAndCategoryId(CommonStatus.ENABLED, categoryId);
        List<CourseBean> courses = courseService.getCourseByStatusAndIds(strCourseStatus, courseIds);
        logger.info("count is {}", courses.size());
        return courses;
    }

    //==============================================================
    //                  category getter
    //==============================================================

    public boolean existsCategory(long categoryId) {
        boolean exists = repository.exists(categoryId);
        logger.info("exists category={}, exists={}", categoryId, exists);
        return exists;
    }

    public long countByStatus(String strStatus) {
        logger.info("count course category by statues={}", strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        long count = 0;
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                count = repository.count();
            }
        }
        else {
            count = repository.countByStatus(status);
        }
        logger.info("course category count is {}", count);
        return count;
    }

    public List<CourseCategoryBean> getCategoryByStatus(String strStatus, int pageIndex, int sizeOfPage) {
        logger.info("get course category by status={} at page={} size={}", strStatus, pageIndex, sizeOfPage);
        // get all category
        CommonStatus status = CommonStatus.parseString(strStatus);
        PageRequest page = new PageRequest(pageIndex, sizeOfPage, categorySort);
        Page<CourseCategoryEntity> resultSet = null;
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                resultSet = repository.findAll(page);
            }
        }
        else {
            resultSet = repository.findByStatus(status, page);
        }

        List<CourseCategoryBean> beans = entitiesToBeans(resultSet);
        fillOtherProperties(beans);
        return beans;
    }

    public List<CourseCategoryBean> getCategoryByStatusAndIds(String strStatus, List<Long> categoryIds) {
        logger.info("get course category by status={} ids={}", strStatus, categoryIds);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<CourseCategoryEntity> resultSet = null;
        if (null==status) {
            if ("ALL".equalsIgnoreCase(strStatus)) {
                resultSet = repository.findByIdIn(categoryIds, categorySort);
            }
        }
        else {
            resultSet = repository.findByStatusAndIdIn(status, categoryIds, categorySort);
        }
        List<CourseCategoryBean> beans = entitiesToBeans(resultSet);
        fillOtherProperties(beans);
        logger.info("get course category is {}", beans);
        return beans;
    }

    private List<CourseCategoryBean> entitiesToBeans(Iterable<CourseCategoryEntity> entities) {
        List<CourseCategoryBean> beans = new ArrayList<>();
        if (null!=entities) {
            CourseCategoryBean bean;
            for (CourseCategoryEntity entity : entities) {
                bean = beanConverter.convert(entity);
                beans.add(bean);
            }
        }
        return beans;
    }

    private void fillOtherProperties(List<CourseCategoryBean> beans) {
        if (VerifyUtil.isListEmpty(beans)) {
            return;
        }

        List<Long> imageIds = new ArrayList<>();
        for (CourseCategoryBean bean : beans) {
            imageIds.add(bean.getImageId());
        }
        Map<Long, String> imageId2Path = fileStorageService.getFileUrl(imageIds);
        for (CourseCategoryBean bean : beans) {
            long imageId = bean.getImageId();
            String imagePath = imageId2Path.get(imageId);
            if (VerifyUtil.isStringEmpty(imagePath)) {
                imagePath = "";
            }
            bean.setImageUrl(imagePath);
        }
    }

    //=================================================================
    //         update
    //=================================================================
    @Transactional
    public CourseCategoryBean updateCategory(long categoryId, String name, String introduction, String strStatus, String imageName, InputStream image) {
        boolean    changed = false;
        String     imgUrl = "";
        logger.info("update category {} by name={} introduction={} status={} imageName={} image={}",
                categoryId, name, introduction, strStatus, imageName, null!=image);
        CourseCategoryEntity entity = repository.findOne(categoryId);
        if (null==entity) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        if (!VerifyUtil.isStringEmpty(name)) {
            if (!name.equals(entity.getName())) {
                long count = repository.countByName(name);
                if (count<=0) {
                    entity.setName(name);
                    changed = true;
                }
            }
        }

        if (!VerifyUtil.isStringEmpty(introduction)) {
            if (!introduction.equals(entity.getIntroduction())) {
                entity.setIntroduction(introduction);
                changed = true;
            }
        }

        CommonStatus status = CommonStatus.parseString(strStatus);
        if (null!=status && !status.equals(entity.getStatus())) {
            entity.setStatus(status);
            changed = true;
        }

        if (null!=image) {
            if (VerifyUtil.isStringEmpty(imageName)) {
                imageName = "category_tmp_" + System.nanoTime();
            }
            long imgId = fileStorageService.addFile(entity.getImageId(), imageName, image);
            if (imgId>0) {
                imgUrl = fileStorageService.getFileURL(imgId);
                entity.setImageId(imgId);
                changed = true;
            }
        }

        if (changed) {
            entity = repository.save(entity);
        }
        CourseCategoryBean bean = beanConverter.convert(entity);
        bean.setImageUrl(imgUrl);
        logger.info("updated is {}", bean);
        return bean;
    }

    @Transactional
    public CourseCategoryRelationBean updateCourseRelation(long courseId, long categoryId, String strStatus) {
        logger.info("update course={} category={} 's relation status to {}", courseId, categoryId, strStatus);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<CourseCategoryRelationEntity> relations = relationRepository.findByCourseIdAndCourseCategoryId(courseId, categoryId, relationSort);
        if (!VerifyUtil.isListEmpty(relations) && null!=status) {
            CourseCategoryRelationEntity relation = relations.get(0);
            relations.remove(0);
            if (!VerifyUtil.isListEmpty(relations)) {
                logger.warn("remove is duplicated data");
                relationRepository.delete(relations);
            }
            relation.setStatus(status);
            relation = relationRepository.save(relation);
            return relationBeanConverter.convert(relation);
        }
        logger.info("relation not exists");
        return null;
    }

    //=================================================================
    //         add
    //=================================================================
    @Transactional
    public CourseCategoryBean addCategory(String name, String introduction, String imageName, InputStream image) {
        logger.info("add tag category : name={} introduction={} imageName={} image={}",
                name, introduction, imageName, (null!=image));
        name = VerifyUtil.isStringEmpty(name) ? "" : name.trim();
        String imageUrl = null;
        if (VerifyUtil.isStringEmpty(name)) {
            logger.error("add category : name is empty");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        else if (repository.countByName(name)>0) {
            logger.error("add tag : name is exist");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }

        CourseCategoryEntity entity = new CourseCategoryEntity();
        entity.setName(name);

        if (!VerifyUtil.isStringEmpty(introduction)) {
            entity.setIntroduction(introduction.trim());
        }

        if (null!=image) {
            if (VerifyUtil.isStringEmpty(imageName)) {
                imageName = "category_tmp_" + System.nanoTime();
            }
            long fileId = fileStorageService.addFile(-1, imageName, image);
            if (fileId>0) {
                entity.setImageId(fileId);
                imageUrl = fileStorageService.getFileURL(fileId);
            }
        }

        entity.setTime(new Date());
        entity.setStatus(CommonStatus.ENABLED);
        entity = repository.save(entity);

        CourseCategoryBean bean = beanConverter.convert(entity);
        bean.setImageUrl(imageUrl);

        return bean;
    }

    @Transactional
    public CourseCategoryRelationBean setCourseRelation(long courseId, long categoryId) {
        logger.info("set course relation courseId={} categoryId={}", courseId, categoryId);
        if (!existsCategory(categoryId)) {
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

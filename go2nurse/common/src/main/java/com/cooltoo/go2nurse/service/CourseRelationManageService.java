package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ReadingStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.constants.CourseStatus;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
import com.cooltoo.go2nurse.entities.CourseCategoryRelationEntity;
import com.cooltoo.go2nurse.entities.CourseDiagnosticRelationEntity;
import com.cooltoo.go2nurse.repository.CourseCategoryRelationRepository;
import com.cooltoo.go2nurse.repository.CourseDepartmentRelationRepository;
import com.cooltoo.go2nurse.repository.CourseDiagnosticRelationRepository;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.cooltoo.services.CommonDepartmentService;
import com.cooltoo.services.CommonHospitalService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by hp on 2016/6/12.
 */
@Service("CourseRelationManageService")
public class CourseRelationManageService {

    private static final Logger logger = LoggerFactory.getLogger(CourseRelationManageService.class);

    public static final String category_all = "all";
    public static final String category_others = "others";

    @Autowired private Go2NurseUtility utility;

    @Autowired private CourseService courseService;
    @Autowired private DiagnosticPointService diagnosticService;
    @Autowired private CommonHospitalService hospitalService;
    @Autowired private CommonDepartmentService departmentService;
    @Autowired private CourseCategoryService categoryService;


    @Autowired private CategoryCourseOrderService categoryCourseOrderService;
    @Autowired private UserCourseRelationService userCourseService;
    @Autowired private CourseCategoryRelationRepository courseCategoryRelation;
    @Autowired private CourseCategoryRelationService    courseCategoryRelationService;
    @Autowired private CourseDepartmentRelationRepository courseDepartmentRelation;
    @Autowired private CourseDepartmentRelationService    courseDepartmentRelationService;
    @Autowired private CourseDiagnosticRelationRepository diagnosticRelation;
    @Autowired private CourseDiagnosticRelationService    diagnosticRelationService;

    //==============================================================================
    //                    getting
    //==============================================================================

    public List<CourseBean> getCourseByHospitalDepartmentDiagnosticCategory(Integer hospitalId, Integer departmentId, boolean containHospitalCourse,
                                                                            Long diagnosticId,
                                                                            List<Long> categoryIds,
                                                                            boolean isAdmin,
                                                                            Integer pageIndex, Integer sizePerPage) {
        logger.info("get courses by hospitalId={} departmentId={} containHospitalCourse={} categoryIds={} diagnosticId={} isAdmin={} pageIndex={} sizePerPage={}",
                hospitalId, departmentId, containHospitalCourse, categoryIds, diagnosticId, isAdmin, pageIndex, sizePerPage);

        // judge category ids are empty
        if (null!=categoryIds && VerifyUtil.isListEmpty(categoryIds)) {
            return new ArrayList<>();
        }

        // get course in department
        List<Long> coursesInDepartment = new ArrayList();
        boolean searchDepartment = false;
        if (null!=hospitalId) {
            if (!isAdmin && null==departmentId) {
                departmentId = 0;
            }
            coursesInDepartment = courseDepartmentRelation.findCourseIdByHospitalDepartmentAndStatus(hospitalId, departmentId, CommonStatus.ENABLED);
            // get course just in hospital
            if (containHospitalCourse && 0!=departmentId) {
                List<Long> tmpCourseInHos = courseDepartmentRelation.findCourseIdByHospitalDepartmentAndStatus(hospitalId, 0, CommonStatus.ENABLED);
                for (Long tmpId : tmpCourseInHos) {
                    if (coursesInDepartment.contains(tmpId)) {
                        continue;
                    }
                    coursesInDepartment.add(tmpId);
                }
            }
            searchDepartment = true;
        }

        // get course in category
        List<Long> coursesInCategory = new ArrayList<>();
        boolean searchCategory = false;
        if (null!=categoryIds && !VerifyUtil.isListEmpty(categoryIds) ) {
            if (!VerifyUtil.isListEmpty(coursesInDepartment)) {
                coursesInCategory = courseCategoryRelation.findCourseIdByStatusAndCategoryIdCourseIds(CommonStatus.ENABLED, categoryIds, coursesInDepartment);
            }
            else {
                coursesInCategory = courseCategoryRelation.findCourseIdByStatusAndCategoryId(CommonStatus.ENABLED, categoryIds);
            }
            searchCategory = true;
        }

        // get course in diagnostic
        List<Long> coursesInDiagnostic = new ArrayList<>();
        boolean searchDiagnostic = false;
        if (null!=diagnosticId) {
            if (!VerifyUtil.isListEmpty(coursesInDepartment)) {
                coursesInDiagnostic = diagnosticRelation.findCourseIdByStatusDiagnosticIdCourseIds(CommonStatus.ENABLED, diagnosticId, coursesInDepartment);
            }
            else {
                coursesInDiagnostic = diagnosticRelation.findCourseIdByStatusDiagnosticId(CommonStatus.ENABLED, diagnosticId);
            }
            searchDiagnostic = true;
        }

        // courseId useful
        List<Long> coursesId = null;
        if (searchDepartment && searchCategory && searchDiagnostic) {
            coursesId = intersection(coursesInDepartment, coursesInCategory);
            coursesId = intersection(coursesId, coursesInDiagnostic);
        }
        else if (searchDepartment && searchCategory && !searchDiagnostic) {
            coursesId = intersection(coursesInDepartment, coursesInCategory);
        }
        else if (searchDepartment && !searchCategory && searchDiagnostic) {
            coursesId = intersection(coursesInDepartment, coursesInDiagnostic);
        }
        else if (!searchDepartment && searchCategory && searchDiagnostic) {
            coursesId = intersection(coursesInCategory, coursesInDiagnostic);
        }
        else {
            if (searchDepartment) {
                coursesId = coursesInDepartment;
            }
            else if (searchCategory) {
                coursesId = coursesInCategory;
            }
            else if (searchDiagnostic) {
                coursesId = coursesInDiagnostic;
            }
        }
        // get courses
        List<CourseBean> result = null;
        if (!isAdmin) {
            result = courseService.getCourseByStatusAndIds(CourseStatus.ENABLE, coursesId, pageIndex, sizePerPage);
        }
        else {
            result = courseService.getCourseByStatusAndIds(null, coursesId, pageIndex, sizePerPage);
        }
        logger.info("courses size={}", result.size());
        return result;
    }

    private List<Long> intersection(List<Long> set1, List<Long> set2) {
        List<Long> result = new ArrayList<>();
        if (VerifyUtil.isListEmpty(set1) || VerifyUtil.isListEmpty(set2)) {
            return result;
        }

        for (Long tmp : set1) {
            if (set2.contains(tmp)) {
                if (!result.contains(tmp)) {
                    result.add(tmp);
                }
            }
        }
        return result;
    }

    private List<CourseBean> merge(List<CourseBean> set1, List<CourseBean> set2) {
        List<CourseBean> result = new ArrayList<>();
        if (VerifyUtil.isListEmpty(set1) && VerifyUtil.isListEmpty(set2)) {
            return result;
        }

        for (CourseBean tmp1 : set1) {
            result.add(tmp1);
        }
        boolean exist;
        for (CourseBean tmp2 : set2) {
            exist = false;
            for (CourseBean tmp1 : set1) {
                if (tmp1.getId() != tmp2.getId()) {
                    continue;
                }
                exist = true;
                break;
            }
            if (!exist) {
                result.add(tmp2);
            }
        }
        return result;
    }

    public Map<CourseCategoryBean, List<CourseBean>> categoryToCourses(List<CourseBean> courses) {
        logger.info("courses divide into groups of category");
        Map<CourseCategoryBean, List<CourseBean>> result = new HashMap<>();
        if (VerifyUtil.isListEmpty(courses)) {
            return result;
        }

        List<Long> categoriesId = new ArrayList<>();
        List<Long> coursesId = new ArrayList<>();
        for (CourseBean tmp : courses) {
            coursesId.add(tmp.getId());
        }
        Map<Long, Long> coursesIdToCategoryId = new HashMap<>();
        List<CourseCategoryRelationEntity> relations = courseCategoryRelation.findByStatusAndCourseIdIn(CommonStatus.ENABLED, coursesId);
        for (CourseCategoryRelationEntity tmp : relations) {
            coursesIdToCategoryId.put(tmp.getCourseId(), tmp.getCourseCategoryId());
            categoriesId.add(tmp.getCourseCategoryId());
        }
        Map<Long, CourseCategoryBean> categoryIdToBean = categoryService.getIdToBeanByStatusAndIds(CommonStatus.ENABLED.name(), categoriesId);


        // all courses
        List<CourseBean> allCourseSortedByReadStatus = new ArrayList<>();
        CourseCategoryBean courseCategoryAllSortedByReadStatus = new CourseCategoryBean();
        courseCategoryAllSortedByReadStatus.setId(-1);
        courseCategoryAllSortedByReadStatus.setName(category_all);
        courseCategoryAllSortedByReadStatus.setIntroduction(category_all);
        result.put(courseCategoryAllSortedByReadStatus, allCourseSortedByReadStatus);
        // all course without any category property
        CourseCategoryBean others = new CourseCategoryBean();
        others.setId(-2);
        others.setName(category_others);
        others.setIntroduction(category_others);
        result.put(others, new ArrayList<>());

        for (CourseBean tmp : courses) {
            // construct all course sorted by read status
            if (!ReadingStatus.READ.equals(tmp.getReading())) {
                allCourseSortedByReadStatus.add(tmp);
            }

            // set course to the category that it belong to
            Long categoryId = coursesIdToCategoryId.get(tmp.getId());
            CourseCategoryBean category = categoryIdToBean.get(categoryId);
            if (null==category) {
                category = others;
            }
            List<CourseBean> tmpCourses = result.get(category);
            if (null == tmpCourses) {
                tmpCourses = new ArrayList<>();
                result.put(category, tmpCourses);
            }
            tmpCourses.add(tmp);
        }

        for (CourseBean course : courses) {
            // construct all course sorted by read status
            if (ReadingStatus.READ.equals(course.getReading())) {
                allCourseSortedByReadStatus.add(course);
            }
        }

        return result;
    }

    public Map<DiagnosticEnumeration, List<CourseBean>> diagnosticEnumToCourses(List<CourseBean> courses) {
        logger.info("courses divide into groups of diagnostic");
        Map<DiagnosticEnumeration, List<CourseBean>> result = new HashMap<>();
        if (VerifyUtil.isListEmpty(courses)) {
            return result;
        }

        List<Long> coursesId = new ArrayList<>();
        for (CourseBean tmp : courses) {
            coursesId.add(tmp.getId());
        }

        List<CourseDiagnosticRelationEntity> relations = diagnosticRelation.findByStatusCoursesIds(CommonStatus.ENABLED, coursesId);
        Map<Long, List<Long>> courseIdToDiagnostic = new HashMap<>();
        if (!VerifyUtil.isListEmpty(relations)) {
            for (CourseDiagnosticRelationEntity tmp : relations) {
                List<Long> diagnostic = courseIdToDiagnostic.get(tmp.getCourseId());
                if (null==diagnostic) {
                    diagnostic = new ArrayList<>();
                    courseIdToDiagnostic.put(tmp.getCourseId(), diagnostic);
                }
                diagnostic.add(tmp.getDiagnosticId());
            }
        }

        for (CourseBean tmp : courses) {
            List<Long> diagnosticId = courseIdToDiagnostic.get(tmp.getId());
            if (null==diagnosticId) {
                continue;
            }
            for (Long tmpId : diagnosticId) {
                DiagnosticEnumeration diagnostic = DiagnosticEnumeration.parseInt(tmpId.intValue());
                List<CourseBean> tmpCourses = result.get(diagnostic);
                if (null == tmpCourses) {
                    tmpCourses = new ArrayList<>();
                    result.put(diagnostic, tmpCourses);
                }
                tmpCourses.add(tmp);
            }
        }

        return result;
    }

    public List<CoursesGroupBean> getHospitalCoursesGroupByDiagnostic(Long userId, Integer hospital, Integer department) {
        Long extensionNursingId = Long.valueOf(DiagnosticEnumeration.EXTENSION_NURSING.ordinal());
        List<CourseBean> courses = getCourseByHospitalDepartmentDiagnosticCategory(hospital, department, false, null, null, false, null, null);
        List<CourseBean> extensionNursingCourses = getCourseByHospitalDepartmentDiagnosticCategory(hospital, null, false, extensionNursingId, null, false, null, null);
        userCourseService.setCourseReadStatus(userId, courses);
        userCourseService.setCourseReadStatus(userId, extensionNursingCourses);

        Map<DiagnosticEnumeration, List<CourseBean>> diagnosticToCourses = diagnosticEnumToCourses(courses);
        List<CourseBean> tmpCourse = diagnosticToCourses.get(DiagnosticEnumeration.EXTENSION_NURSING);
        if (!VerifyUtil.isMapEmpty(diagnosticToCourses)) {
            extensionNursingCourses = merge(tmpCourse, extensionNursingCourses);
            diagnosticToCourses.put(DiagnosticEnumeration.EXTENSION_NURSING, extensionNursingCourses);
        }
        else {
            return new ArrayList<>();
        }

        List<CoursesGroupBean> diagnosticGroup = CoursesGroupBean.parseObjectToBean(diagnosticToCourses, true);

        Map<CourseCategoryBean, List<CourseBean>> categoryToCourses = categoryToCourses(extensionNursingCourses);
        List<CoursesGroupBean> categoryGroup = CoursesGroupBean.parseObjectToBean(categoryToCourses, false);
        CoursesGroupBean.sortCourseArrays(categoryGroup);

        for (CoursesGroupBean tmp : diagnosticGroup) {
            if (tmp.getId() == extensionNursingId.intValue()) {
                tmp.setCourses(categoryGroup);
            }
        }

        return diagnosticGroup;
    }

    public CoursesGroupBean getHospitalCoursesGroupByDiagnostic(Long userId, Integer hospital, Integer department, Long diagnosticId) {
        final Long extensionNursingId = Long.valueOf(DiagnosticEnumeration.EXTENSION_NURSING.ordinal());
        List<CourseBean> courses = getCourseByHospitalDepartmentDiagnosticCategory(hospital, department, false, diagnosticId, null, false, null, null);
        if (extensionNursingId==diagnosticId) {
            List<CourseBean> tmpCourses = getCourseByHospitalDepartmentDiagnosticCategory(hospital, null, false, diagnosticId, null, false, null, null);
            courses = merge(courses, tmpCourses);
        }
        userCourseService.setCourseReadStatus(userId, courses);

        Map<DiagnosticEnumeration, List<CourseBean>> diagnosticToCourses = diagnosticEnumToCourses(courses);
        List<CoursesGroupBean> allGroups = CoursesGroupBean.parseObjectToBean(diagnosticToCourses, true);

        CoursesGroupBean theOneFound = null;
        for (CoursesGroupBean tmp : allGroups) {
            if (tmp.getId() == diagnosticId) {
                theOneFound = tmp;
                break;
            }
        }

        if (null!=theOneFound && extensionNursingId==diagnosticId) {
            Map<CourseCategoryBean, List<CourseBean>> categoryToCourses = categoryToCourses(courses);
            List<CoursesGroupBean> categoryGroup = CoursesGroupBean.parseObjectToBean(categoryToCourses, false);
            CoursesGroupBean.sortCourseArrays(categoryGroup);
            theOneFound.setCourses(categoryGroup);
        }

        return theOneFound;
    }

    public List<CoursesGroupBean> getHospitalCoursesGroupByCategory(Long userId, Integer hospital, Integer department) {
        List<CourseBean> courses = getCourseByHospitalDepartmentDiagnosticCategory(hospital, department, false, null, null, false, null, null);
        userCourseService.setCourseReadStatus(userId, courses);

        Map<CourseCategoryBean, List<CourseBean>> categoryToCourses = categoryToCourses(courses);
        List<CoursesGroupBean> categoryGroup = CoursesGroupBean.parseObjectToBean(categoryToCourses, false);
        CoursesGroupBean.sortCourseArrays(categoryGroup);

        return categoryGroup;
    }

    public List<CoursesGroupBean> getHospitalCoursesGroupByCategory(Long userId, Integer hospital, Integer department, List<Long> categoryIds) {
        if (VerifyUtil.isListEmpty(categoryIds)) {
            return new ArrayList<>();
        }

        List<CourseBean> courses = getCourseByHospitalDepartmentDiagnosticCategory(hospital, department, false, null, categoryIds, false, null, null);
        userCourseService.setCourseReadStatus(userId, courses);

        Map<CourseCategoryBean, List<CourseBean>> categoryToCourses = categoryToCourses(courses);
        List<CoursesGroupBean> categoryGroup = CoursesGroupBean.parseObjectToBean(categoryToCourses, false);

        Map<CategoryCoursesOrderGroup, List<Long>> categoryCourseOrder = null;
        if (null!=hospital && null!=department && !VerifyUtil.isListEmpty(categoryIds)) {
            categoryCourseOrder = categoryCourseOrderService.getCategoryGroupToCourseIdsSorted(hospital, department, categoryIds);
        }

        List<CoursesGroupBean> resultGroup = new ArrayList<>();
        for (Long tmpId : categoryIds) {
            for (int i = 0; i < categoryGroup.size(); i++) {
                CoursesGroupBean tmp = categoryGroup.get(i);
                if (tmpId==tmp.getId()) {
                    resultGroup.add(tmp);
                }
            }
        }

        if (!VerifyUtil.isMapEmpty(categoryCourseOrder)) {
            CategoryCoursesOrderGroup orderGroup = new CategoryCoursesOrderGroup();
            orderGroup.setHospitalId(hospital);
            orderGroup.setDepartmentId(department);
            for (CoursesGroupBean tmp : resultGroup) {
                orderGroup.setCategoryId(tmp.getId());
                orderGroup.resetHashCode();

                List<Long> courseIdSorted = categoryCourseOrder.get(orderGroup);
                if (VerifyUtil.isListEmpty(courseIdSorted)) {
                    continue;
                }

                sortCourseByCourseIdSorted(tmp, courseIdSorted);
            }
        }

        return resultGroup;
    }

    private void sortCourseByCourseIdSorted(CoursesGroupBean courseGroup, List<Long> coursesIdSorted) {
        // is null or empty
        if (null==courseGroup
                || !(courseGroup.getCourses() instanceof List)
                || VerifyUtil.isListEmpty(((List)courseGroup.getCourses()))
                || VerifyUtil.isListEmpty(coursesIdSorted)) {
            return;
        }

        // is course
        List set = (List)courseGroup.getCourses();
        Object obj = set.get(0);
        if (!(obj instanceof CourseBean)) {
            return;
        }

        List<CourseBean> courses = (List<CourseBean>) courseGroup.getCourses();
        List<CourseBean> courseSorted = new ArrayList<>();
        for (Long tmpId : coursesIdSorted) {
            for (int i = 0; i<courses.size(); i ++) {
                CourseBean tmp = courses.get(i);
                if (tmp.getId() == tmpId) {
                    courseSorted.add(tmp);
                    courses.remove(i);
                    break;
                }
            }
        }
        for (CourseBean tmp : courses) {
            courseSorted.add(tmp);
        }
        courses.clear();

        courseGroup.setCourses(coursesIdSorted);
    }



    //================================================================================
    //                    get course relation information
    //================================================================================
    public boolean hospitalExist(int hospitalId) {
        return hospitalService.existHospital(hospitalId);
    }

    public boolean departmentExist(int departmentId) {
        return departmentService.existsDepartment(departmentId);
    }

    public boolean diagnosticExist(long diagnosticId) {
        return diagnosticService.exitsDiagnostic(diagnosticId);
    }

    public boolean categoryExist(long categoryId) {
        return categoryService.existsCategory(categoryId);
    }

    public List<HospitalBean> getHospitalByCourseId(long courseId, String strStatus) {
        logger.info("get hospital by course id={} and status={}", courseId, strStatus);
        List<Integer> hospitalIds = courseDepartmentRelationService.getHospitalByCourseId(courseId, strStatus);
        List<HospitalBean> hospitals = hospitalService.getHospitalByIds(hospitalIds);
        logger.info("hospital is {}", hospitals);
        return hospitals;
    }

    public List<HospitalDepartmentBean> getDepartmentByCourseId(long courseId, String strStatus) {
        logger.info("get department by course id={} and status={}", courseId, strStatus);
        List<Long> courseIds = Arrays.asList(new Long[]{courseId});
        List<Integer> departmentIds = courseDepartmentRelationService.getDepartmentByCourseId(courseIds, strStatus);
        List<HospitalDepartmentBean> departments = departmentService.getByIds(departmentIds, utility.getHttpPrefixForNurseGo());
        logger.info("department is {}", departments);
        return departments;
    }

    public List<DiagnosticEnumerationBean> getDiagnosticByCourseId(long courseId, String strStatus) {
        logger.info("get diagnostic by course id={} and status={}", courseId, strStatus);
        List<Long> courseIds = Arrays.asList(new Long[]{courseId});
        List<Long> diagnosticIds = diagnosticRelationService.getDiagnosticByCourseId(courseIds);
        List<DiagnosticEnumerationBean> diagnostics = diagnosticService.getDiagnosticByIds(diagnosticIds);
        logger.info("diagnostic is {}", diagnostics);
        return diagnostics;
    }

    public List<CourseCategoryBean> getCategoryByCourseId(long courseId, String strStatus) {
        logger.info("get category by course id={} and status={}", courseId, strStatus);
        List<CourseCategoryBean> diagnostics = courseCategoryRelationService.getCategoryByCourseId(strStatus, courseId);
        logger.info("category is {}", diagnostics);
        return diagnostics;
    }

    //================================================================================
    //                    get course relation information
    //================================================================================

    public Integer[] getHospitalDepartmentId(String hospitalUniqueId, String departmentUniqueId) {
        logger.info("get diagnostic to courses map in department by hospitalUniqueId={} departmentUniqueId={}",
                hospitalUniqueId, departmentUniqueId);
        List<HospitalBean> hospitals = hospitalService.getHospitalByUniqueId(hospitalUniqueId);
        int hospitalSize = VerifyUtil.isListEmpty(hospitals) ? 0 : hospitals.size();
        if (hospitalSize!=1) {
            logger.error("hospital size is not 1, size is {}", hospitalSize);
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        int hospitalId = hospitals.get(0).getId();
        logger.info("hospitalId={}", hospitalId);

        List<HospitalDepartmentBean> departments = departmentService.getDepartmentByUniqueId(departmentUniqueId, null);
        int departmentSize = VerifyUtil.isListEmpty(departments) ? 0 : departments.size();
        if (departmentSize!=1) {
            logger.error("department size is not 1, size is {}", departmentSize);
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (departments.get(0).getHospitalId() != hospitalId) {
            logger.error("department not belong to the hospital");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        int departmentId = departments.get(0).getId();

        logger.info("departmentId={}", departmentId);

        return new Integer[]{hospitalId, departmentId};
    }

    //==============================================================================
    //                    deleted relation permanently
    //==============================================================================

    @Transactional
    public boolean deleteRelationsByCourseId(List<Long> courseIds) {
        courseCategoryRelationService.deleteRelationPermanentlyByCourseIds(courseIds);
        courseDepartmentRelationService.deleteRelationPermanentlyByCourseIds(courseIds);
        diagnosticRelationService.deleteRelationPermanentlyByCourseIds(courseIds);
        return true;
    }

    //==============================================================================
    //                    update
    //==============================================================================

    @Transactional
    public CourseDiagnosticRelationBean updateCourseToDiagnostic(long courseId, long diagnosticId, String status) {
        CourseDiagnosticRelationBean relation = diagnosticRelationService.updateStatus(courseId, diagnosticId, status);
        return relation;
    }

    //==============================================================================
    //                    add
    //==============================================================================
    @Transactional
    public boolean addCourseToDiagnostic(long courseId, long diagnosticId) {
//        if (!diagnostic.exists(diagnosticId)) {
//            logger.error("diagnostic not exists");
//            throw new BadRequestException(ErrorCode.DATA_ERROR);
//        }
        if (!DiagnosticEnumeration.exists((int)diagnosticId)) {
            logger.error("diagnostic not exists");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (!courseService.existCourse(courseId)) {
            logger.error("course not exists");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        CourseDiagnosticRelationBean relation = diagnosticRelationService.addCourseToDiagnostic(courseId, diagnosticId);
        return null!=relation && relation.getId()>0;
    }

    //==============================================================================
    //                    set
    //==============================================================================

    @Transactional
    public List<Integer> setCourseToDepartmentRelationship(long courseId, List<Integer> departmentIds) {
        logger.info("set course_to_department relationship, courseId={} departmentIds={}",
                courseId, departmentIds);
        List<Integer> settingDepartmentIds = courseDepartmentRelationService.setCourseToDepartmentRelation(courseId, departmentIds);
        logger.info("set department ids is {}", settingDepartmentIds);
        return settingDepartmentIds;
    }

    @Transactional
    public List<Long> setCourseToDiagnosticRelationship(long courseId, List<Long> diagnosticIds) {
        logger.info("set course_to_diagnostic relationship, courseId={} diagnosticIds={}",
                courseId, diagnosticIds);
        List<Long> settingDiagnosticIds = diagnosticRelationService.setCourseToDiagnosticRelation(courseId, diagnosticIds);
        logger.info("set diagnostic ids is {}", settingDiagnosticIds);
        return settingDiagnosticIds;
    }

    @Transactional
    public Long setCourseToCategoryRelationship(long courseId, Long categoryId) {
        logger.info("set course_to_category relationship, courseId={} categoryId={}",
                courseId, categoryId);
        courseCategoryRelationService.setCourseRelation(courseId, categoryId);
        logger.info("set category ids is {}", categoryId);
        return categoryId;
    }
}

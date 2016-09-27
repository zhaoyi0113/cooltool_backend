package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.entities.HospitalDepartmentEntity;
import com.cooltoo.entities.HospitalEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.constants.CourseStatus;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
import com.cooltoo.go2nurse.converter.DiagnosticEnumerationBeanConverter;
import com.cooltoo.go2nurse.repository.CourseCategoryRelationRepository;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.cooltoo.repository.HospitalDepartmentRepository;
import com.cooltoo.repository.HospitalRepository;
import com.cooltoo.services.CommonDepartmentService;
import com.cooltoo.services.CommonHospitalService;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by hp on 2016/6/12.
 */
@Service("CourseRelationManageService")
public class CourseRelationManageService {

    private static final Logger logger = LoggerFactory.getLogger(CourseRelationManageService.class);

    public static final String key_all_courses_in_hospital = "all_courses_in_hospital";
    public static final String key_diagnostic = "diagnostic";
    public static final String key_department = "department";
    public static final String key_others = "others";

    @Autowired private Go2NurseUtility utility;
    @Autowired private DiagnosticEnumerationBeanConverter diagnosticBeanConverter;

    @Autowired private CommonHospitalService hospitalService;
    @Autowired private HospitalRepository hospital;
    @Autowired private CommonDepartmentService departmentService;
    @Autowired private HospitalDepartmentRepository department;
    @Autowired private DiagnosticPointService diagnostic;
    @Autowired private CourseService course;

    @Autowired private CourseHospitalRelationService hospitalRelation;
    @Autowired private CourseDepartmentRelationService departmentRelation;
    @Autowired private CourseDiagnosticRelationService diagnosticRelation;
    @Autowired private CourseCategoryRelationRepository categoryRelation;

    //==============================================================================
    //                    getting
    //==============================================================================
    public boolean hospitalExist(int hospitalId) {
        return hospital.exists(hospitalId);
    }

    public boolean departmentExist(int departmentId) {
        return department.exists(departmentId);
    }

    public boolean diagnosticExist(long diagnosticId) {
        return diagnostic.exitsDiagnostic(diagnosticId);
    }

    public List<CourseBean> getCoursesByHospitalAndCategory(int hospitalId, long categoryId) {
        logger.info("get courses by hospitalId={} categoryId={}", hospitalId, categoryId);

        List<Long> courseIdInHospital = hospitalRelation.getCourseInHospital(hospitalId, CommonStatus.ENABLED.name());
        List<Long> courseIdInCategory = categoryRelation.findCourseIdByStatusAndCategoryId(CommonStatus.ENABLED, categoryId);

        List<Long> courseIdInHospitalAndCategory = new ArrayList<>();
        for (Long courseId : courseIdInHospital) {
            if (courseIdInCategory.contains(courseId)) {
                courseIdInHospitalAndCategory.add(courseId);
            }
        }

        List<CourseBean> courseInHospitalAndCategory;
        if (VerifyUtil.isListEmpty(courseIdInHospitalAndCategory)) {
            courseInHospitalAndCategory = new ArrayList<>();
        }
        else {
            courseInHospitalAndCategory = course.getCourseByStatusAndIds(CourseStatus.ENABLE.name(), courseIdInHospitalAndCategory);
        }

        logger.info("course size is {}", courseInHospitalAndCategory.size());
        return courseInHospitalAndCategory;
    }

    public List<HospitalBean> getHospitalByCourseId(long courseId, String strStatus) {
        logger.info("get hospital by course id={} and status={}", courseId, strStatus);
        List<Integer> hospitalIds = hospitalRelation.getHospitalByCourseId(courseId, strStatus);
        List<HospitalBean> hospitals = hospitalService.getHospitalByIds(hospitalIds);
        logger.info("hospital is {}", hospitals);
        return hospitals;
    }

    public List<HospitalDepartmentBean> getDepartmentByCourseId(long courseId, String strStatus) {
        logger.info("get department by course id={} and status={}", courseId, strStatus);
        List<Long> courseIds = Arrays.asList(new Long[]{courseId});
        List<Integer> departmentIds = departmentRelation.getDepartmentByCourseId(courseIds, strStatus);
        List<HospitalDepartmentBean> departments = departmentService.getByIds(departmentIds, utility.getHttpPrefixForNurseGo());
        logger.info("department is {}", departments);
        return departments;
    }

    public List<DiagnosticEnumerationBean> getDiagnosticByCourseId(long courseId, String strStatus) {
        logger.info("get diagnostic by course id={} and status={}", courseId, strStatus);
        List<Long> courseIds = Arrays.asList(new Long[]{courseId});
        List<Long> diagnosticIds = diagnosticRelation.getDiagnosticByCourseId(courseIds, strStatus);
        List<DiagnosticEnumerationBean> diagnostics = diagnostic.getDiagnosticByIds(diagnosticIds);
        logger.info("diagnostic is {}", diagnostics);
        return diagnostics;
    }

    public List<Long> getEnabledCoursesIdInExtensionNursing() {
        logger.info("get course ids in extension nursing");
        List<Long> coursesId = hospitalRelation.getCourseInHospital(-1, CommonStatus.ENABLED.name());
        logger.info("count is {}", coursesId.size());
        return coursesId;
    }

    public List<CourseBean> getEnabledCoursesInExtensionNursing() {
        logger.info("get course ids in extension nursing");
        List<Long> coursesId = hospitalRelation.getCourseInHospital(-1, CommonStatus.ENABLED.name());
        List<CourseBean> courses = course.getCourseByStatusAndIds(CourseStatus.ENABLE.name(), coursesId);
        logger.info("count is {}", courses.size());
        return courses;
    }

    public List<CourseBean> getEnabledCoursesByHospitalIdAndDiagnosticId(int hospitalId, long diagnosticId) {
        logger.info("get courses by hospitalId={} diagnosticId={}", hospitalId, diagnosticId);
        List<Long> coursesId = hospitalRelation.getCourseInHospital(hospitalId, CommonStatus.ENABLED.name());
        List<Long> coursesIdInDiagnostic = diagnosticRelation.judgeCourseInDiagnostic(diagnosticId, coursesId, CommonStatus.ENABLED.name());
        List<CourseBean> coursesInDiagnostic = course.getCourseByIds(coursesIdInDiagnostic);
        logger.info("count is {}", coursesInDiagnostic.size());
        return coursesInDiagnostic;
    }

    public List<Long> getEnabledCoursesIdByHospitalIdAndDepartmentId(Integer hospitalId, Integer departmentId) {
        logger.info("get course ids by hospitalId={} departmentId={}", hospitalId, departmentId);
        if (!hospitalExist(hospitalId)) {
            logger.error("hospital not exists!");
            return new ArrayList<>();
        }
        if (!departmentExist(departmentId)) {
            logger.error("department not exists!");
            return new ArrayList<>();
        }
        List<Long> coursesInHospital = hospitalRelation.getCourseInHospital(hospitalId, CommonStatus.ENABLED.name());
        List<Long> coursesInHospitalDepartment = departmentRelation.judgeCourseInDepartment(departmentId, coursesInHospital, CommonStatus.ENABLED.name());
        logger.info("course in hospital and department={}", coursesInHospitalDepartment);
        return coursesInHospitalDepartment;
    }

    public Map<Long, List<Long>> getDiagnosticIdToCoursesIdMapInDepartment(Integer hospitalId, Integer departmentId) {
        logger.info("get diagnostic id to courses ids map in department by hospitalId={} departmentId={}", hospitalId, departmentId);
        List<Long> courseInDepartment = getEnabledCoursesIdByHospitalIdAndDepartmentId(hospitalId, departmentId);
        Map<Long, List<Long>> diagnosticToCourses = diagnosticRelation.getDiagnosticToCourseIds(courseInDepartment, CommonStatus.ENABLED);
        logger.info("diagnostic to courses map in department size is {}", diagnosticToCourses.size());
        return diagnosticToCourses;
    }

    public List<CourseBean> getAllCourseByHospitalOrDepartmentId(int hospitalId, int departmentId) {
        logger.info("get course in hospital={} department={}", hospitalId, departmentId);
        boolean searchHospital = hospital.exists(hospitalId) || -1==hospitalId;
        boolean searchDepartment = department.exists(departmentId);

        List<Long> courseIds;
        if (!searchHospital && !searchDepartment) {
            courseIds = null;
        }
        else if (searchHospital && !searchDepartment) {
            courseIds = hospitalRelation.getCourseInHospital(hospitalId, CommonStatus.ENABLED.name());
        }
        else if (!searchHospital && searchDepartment) {
            courseIds = departmentRelation.getCourseInDepartment(departmentId, CommonStatus.ENABLED.name());
        }
        else {
            courseIds = hospitalRelation.getCourseInHospital(hospitalId, CommonStatus.ENABLED.name());
            courseIds = departmentRelation.judgeCourseInDepartment(departmentId, courseIds, CommonStatus.ENABLED.name());
        }
        List<CourseBean> courses = course.getCourseByIds(courseIds);
        logger.info("count is {}", courses.size());
        return courses;
    }

    public Map<DiagnosticEnumeration, List<CourseBean>> getDiagnosticToCoursesMapInDepartment(String hospitalUniqueId, String departmentUniqueId) {
        logger.info("get diagnostic to courses map in department by hospitalUniqueId={} departmentUniqueId={}",
                hospitalUniqueId, departmentUniqueId);
        List<HospitalEntity> hospitals = hospital.findByUniqueId(hospitalUniqueId);
        int hospitalSize = VerifyUtil.isListEmpty(hospitals) ? 0 : hospitals.size();
        if (hospitalSize!=1) {
            logger.info("hospital size is not 1, size is {}", hospitalSize);
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        int hospitalId = hospitals.get(0).getId();
        logger.info("hospitalId={}", hospitalId);

        List<HospitalDepartmentEntity> departments = department.findByUniqueId(departmentUniqueId);
        int departmentSize = VerifyUtil.isListEmpty(departments) ? 0 : departments.size();
        if (departmentSize!=1) {
            logger.info("department size is not 1, size is {}", departmentSize);
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        int departmentId = departments.get(0).getId();
        logger.info("departmentId={}", departmentId);

        return getDiagnosticToCoursesMapInDepartment(hospitalId, departmentId);
    }

    public Map<DiagnosticEnumeration, List<CourseBean>> getDiagnosticToCoursesMapInDepartment(Integer hospitalId, Integer departmentId) {
        logger.info("get diagnostic to courses map in department by hospitalId={} departmentId={}", hospitalId, departmentId);
        List<Long> coursesIdInDepartment = getEnabledCoursesIdByHospitalIdAndDepartmentId(hospitalId, departmentId);
        List<CourseBean> coursesBeanInDepartment = course.getCourseByStatusAndIds(CourseStatus.ENABLE.name(), coursesIdInDepartment);
        Map<Long, CourseBean> courseIdToBean = new HashMap<>();
        for (CourseBean course : coursesBeanInDepartment) {
            courseIdToBean.put(course.getId(), course);
        }
        Map<Long, List<Long>> diagnosticIdToCoursesId = diagnosticRelation.getDiagnosticToCourseIds(coursesIdInDepartment, CommonStatus.ENABLED);
        Map<DiagnosticEnumeration, List<CourseBean>> diagnosticToCourses = new HashMap<>();
        Set<Long> keys = diagnosticIdToCoursesId.keySet();
        for (Long key : keys) {
            List<Long> coursesId = diagnosticIdToCoursesId.get(key);
            DiagnosticEnumeration diagnostic = DiagnosticEnumeration.parseInt(key.intValue());
            List<CourseBean> courses = new ArrayList<>();
            for (Long courseId : coursesId) {
                CourseBean course = courseIdToBean.get(courseId);
                if (null!=course) {
                    courses.add(course);
                }
            }
            diagnosticToCourses.put(diagnostic, courses);
        }
        logger.info("diagnostic to courses map in department size is {}", diagnosticToCourses.size());
        return diagnosticToCourses;
    }

    public Map<String, List<Long>> getCoursesIdByConditions(List<Long> courseIds,
                                                            int hospitalId, int departmentId, long diagnosticId,
                                                            String strHospitalRelationStatus, String strCourseStatus
    ) {
        logger.info("get courses id in hospital={} department={} diagnostic={} with hospitalRelationStatus={} and courseStatus={}",
                hospitalId, departmentId, diagnosticId, strHospitalRelationStatus, strCourseStatus);
        logger.info("course id must in {}", courseIds);

        Map<String, List<Long>> hospitalDepartmentDiagnostic = new HashMap<>();

        List<Long> courseInHospital = hospitalRelation.getCourseInHospital(hospitalId, strHospitalRelationStatus);
        if (VerifyUtil.isListEmpty(courseInHospital)) {
            return hospitalDepartmentDiagnostic;
        }

        if (!VerifyUtil.isListEmpty(courseIds)) {
            List<Long> coursesIdNotInHospital = new ArrayList<>();
            List<Long> validCoursesInHospital = new ArrayList<>();
            for (Long courseIdInHospital : courseInHospital) {
                if (courseIds.contains(courseIdInHospital)) {
                    validCoursesInHospital.add(courseIdInHospital);
                }
                else {
                    coursesIdNotInHospital.add(courseIdInHospital);
                }
            }
            courseInHospital = validCoursesInHospital;
            hospitalDepartmentDiagnostic.put(key_others, coursesIdNotInHospital);
        }
        List<Long> courseInStatus = course.getCourseIdByStatusAndIds(strCourseStatus, courseInHospital);
        hospitalDepartmentDiagnostic.put(key_all_courses_in_hospital, courseInStatus);

        List<Long> courseInDepartment = new ArrayList<>();
        boolean checkCoursesInDepartment = departmentId>0;
        if (checkCoursesInDepartment) {
            courseInDepartment = departmentRelation.judgeCourseInDepartment(departmentId, courseInHospital, CommonStatus.ENABLED.name());
            hospitalDepartmentDiagnostic.put(key_department, courseInDepartment);
        }
        else {
            hospitalDepartmentDiagnostic.put(key_department, new ArrayList<>());
        }

        boolean checkCoursesInDiagnostic = diagnosticId>=0;
        if (checkCoursesInDiagnostic) {
            List<Long> courseInDiagnostic = diagnosticRelation.judgeCourseInDiagnostic(diagnosticId, courseInDepartment, CommonStatus.ENABLED.name());
            hospitalDepartmentDiagnostic.put(key_diagnostic, courseInDiagnostic);
        }
        else {
            hospitalDepartmentDiagnostic.put(key_diagnostic, new ArrayList<>());
        }
        return hospitalDepartmentDiagnostic;
    }

    public Map<String, List<CourseBean>> getCoursesByConditions(List<Long> courseIds,
                                                                int hospitalId, int departmentId, long diagnosticId,
                                                                String strRelationStatus, String strCourseStatus) {
        Map<String, List<Long>> courseIdsInHospitalDepartmentDiagnostic = getCoursesIdByConditions(courseIds, hospitalId, departmentId, diagnosticId, strRelationStatus, strCourseStatus);
        List<Long> coursesIdInHospital = courseIdsInHospitalDepartmentDiagnostic.get(key_all_courses_in_hospital);
        List<Long> coursesIdInDepartment = courseIdsInHospitalDepartmentDiagnostic.get(key_department);
        List<Long> coursesIdInDiagnostic = courseIdsInHospitalDepartmentDiagnostic.get(key_diagnostic);
        List<Long> coursesIdInOther = courseIdsInHospitalDepartmentDiagnostic.get(key_others);

        Map<String, List<CourseBean>> coursesInHospitalDepartmentDiagnostic = new HashMap<>();
        if (!VerifyUtil.isListEmpty(coursesIdInOther)) {
            List<CourseBean> courses = course.getCourseByIds(coursesIdInOther);
            coursesInHospitalDepartmentDiagnostic.put(key_others, courses);
        }
        if (!VerifyUtil.isListEmpty(coursesIdInHospital)) {
            List<CourseBean> courses = course.getCourseByIds(coursesIdInHospital);
            coursesInHospitalDepartmentDiagnostic.put(key_all_courses_in_hospital, courses);

            if (!VerifyUtil.isListEmpty(courses)) {
                List<CourseBean> coursesInDepartment = new ArrayList<>();
                if (!VerifyUtil.isListEmpty(coursesIdInDepartment)) {
                    for (Long courseId : coursesIdInDepartment) {
                        for (CourseBean course : courses) {
                            if (course.getId()==courseId) {
                                coursesInDepartment.add(course);
                                break;
                            }
                        }
                    }
                }
                coursesInHospitalDepartmentDiagnostic.put(key_department, coursesInDepartment);

                List<CourseBean> coursesInDiagnostic = new ArrayList<>();
                if (!VerifyUtil.isListEmpty(coursesIdInDiagnostic)) {
                    for (Long courseId : coursesIdInDiagnostic) {
                        for (CourseBean course : courses) {
                            if (course.getId()==courseId) {
                                coursesInDiagnostic.add(course);
                                break;
                            }
                        }
                    }
                }
                coursesInHospitalDepartmentDiagnostic.put(key_diagnostic, coursesInDiagnostic);
            }
        }
        else {
            coursesInHospitalDepartmentDiagnostic.put(key_all_courses_in_hospital, new ArrayList<>());
            coursesInHospitalDepartmentDiagnostic.put(key_department, new ArrayList<>());
            coursesInHospitalDepartmentDiagnostic.put(key_diagnostic, new ArrayList<>());
        }

        return coursesInHospitalDepartmentDiagnostic;
    }


    //==============================================================================
    //                    update
    //==============================================================================
    @Transactional
    public CourseHospitalRelationBean updateCourseToHospital(long courseId, int hospitalId, String status) {
        CourseHospitalRelationBean relation = hospitalRelation.updateStatus(courseId, hospitalId, status);
        return relation;
    }

    @Transactional
    public CourseDepartmentRelationBean updateCourseToDepartment(long courseId, int departmentId, String status) {
        CourseDepartmentRelationBean relation = departmentRelation.updateStatus(courseId, departmentId, status);
        return relation;
    }

    @Transactional
    public CourseDiagnosticRelationBean updateCourseToDiagnostic(long courseId, long diagnosticId, String status) {
        CourseDiagnosticRelationBean relation = diagnosticRelation.updateStatus(courseId, diagnosticId, status);
        return relation;
    }

    //==============================================================================
    //                    add
    //==============================================================================
    @Transactional
    public boolean addCourseToHospital(long courseId, int hospitalId) {
        if (!hospitalExist(hospitalId) && -1!=hospitalId) {
            logger.error("hospital not exists");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (!course.existCourse(courseId)) {
            logger.error("course not exists");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        CourseHospitalRelationBean relation = hospitalRelation.addCourseToHospital(courseId, hospitalId);
        return null!=relation && relation.getId()>0;
    }

    @Transactional
    public boolean addCourseToDepartment(long courseId, int departmentId) {
        if (!departmentExist(departmentId)) {
            logger.error("department not exists");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        if (!course.existCourse(courseId)) {
            logger.error("course not exists");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        CourseDepartmentRelationBean relation = departmentRelation.addCourseToDepartment(courseId, departmentId);
        return null != relation && relation.getId() > 0;
    }

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
        if (!course.existCourse(courseId)) {
            logger.error("course not exists");
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        CourseDiagnosticRelationBean relation = diagnosticRelation.addCourseToDiagnostic(courseId, diagnosticId);
        return null!=relation && relation.getId()>0;
    }

    //==============================================================================
    //                    set
    //==============================================================================

    @Transactional
    public List<Integer> setCourseToHospitalRelationship(long courseId, List<Integer> hospitalIds) {
        logger.info("set course_to_hospital relationship, courseId={} hospitalIds={}",
                courseId, hospitalIds);
        List<Integer> settingDepartmentIds = hospitalRelation.setCourseToHospitalRelation(courseId, hospitalIds);
        logger.info("set hospital ids is {}", settingDepartmentIds);
        return settingDepartmentIds;
    }

    @Transactional
    public List<Integer> setCourseToDepartmentRelationship(long courseId, List<Integer> departmentIds) {
        logger.info("set course_to_department relationship, courseId={} departmentIds={}",
                courseId, departmentIds);
        List<Integer> settingDepartmentIds = departmentRelation.setCourseToDepartmentRelation(courseId, departmentIds);
        logger.info("set department ids is {}", settingDepartmentIds);
        return settingDepartmentIds;
    }

    @Transactional
    public List<Long> setCourseToDiagnosticRelationship(long courseId, List<Long> diagnosticIds) {
        logger.info("set course_to_diagnostic relationship, courseId={} diagnosticIds={}",
                courseId, diagnosticIds);
        List<Long> settingDiagnosticIds = diagnosticRelation.setCourseToDiagnosticRelation(courseId, diagnosticIds);
        logger.info("set diagnostic ids is {}", settingDiagnosticIds);
        return settingDiagnosticIds;
    }
}

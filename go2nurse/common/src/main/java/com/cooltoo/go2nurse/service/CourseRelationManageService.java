package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
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

    @Autowired private CommonHospitalService hospitalService;
    @Autowired private HospitalRepository hospital;
    @Autowired private CommonDepartmentService departmentService;
    @Autowired private HospitalDepartmentRepository department;
    @Autowired private DiagnosticPointService diagnostic;
    @Autowired private CourseService course;

    @Autowired private CourseHospitalRelationService hospitalRelation;
    @Autowired private CourseDepartmentRelationService departmentRelation;
    @Autowired private CourseDiagnosticRelationService diagnosticRelation;

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
        List<HospitalDepartmentBean> departments = departmentService.getByIds(departmentIds, utility.getHttpPrefix());
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

    public Map<String, List<Long>> getCoursesIdByConditions(List<Long> courseIds,
                                                            int hospitalId, int departmentId, long diagnosticId,
                                                            String strHospitalRelationStatus, String strCourseStatus
    ) {
        logger.info("get courses id in hospital={} department={} diagnostic={} with hospitalRelationStatus={} and courseStatus={}",
                hospitalId, departmentId, diagnosticId, strHospitalRelationStatus, strCourseStatus);
        logger.info("course id must in {}", courseIds);

        Map<String, List<Long>> hospitalDepartmentDiagnostic = new HashMap<>();
        boolean hospitalExists = hospitalExist(hospitalId);
        if (!hospitalExists) {
            logger.error("hospital not exists!");
            return hospitalDepartmentDiagnostic;
        }

        List<Long> courseInHospital = hospitalRelation.getCourseInHospital(hospitalId, strHospitalRelationStatus);
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

        List<Long> courseInDepartment = null;
        boolean checkCoursesInDepartment = departmentId>0;
        if (checkCoursesInDepartment) {
            courseInDepartment = departmentRelation.judgeCourseInDepartment(departmentId, courseInHospital, CommonStatus.ENABLED.name());
            hospitalDepartmentDiagnostic.put(key_department, courseInDepartment);
        }
        else {
            hospitalDepartmentDiagnostic.put(key_department, new ArrayList<>());
        }

        List<Long> courseInDiagnostic = null;
        boolean checkCoursesInDiagnostic = diagnosticId>=0;
        if (checkCoursesInDiagnostic) {
            courseInDiagnostic = diagnosticRelation.judgeCourseInDiagnostic(diagnosticId, courseInDepartment, CommonStatus.ENABLED.name());
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
        if (!hospitalExist(hospitalId)) {
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

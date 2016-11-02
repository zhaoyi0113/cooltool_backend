package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
import com.cooltoo.go2nurse.converter.DiagnosticEnumerationBeanConverter;
import com.cooltoo.go2nurse.service.CourseRelationManageService;
import com.cooltoo.go2nurse.service.CourseService;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/6/13.
 */
@Path("/admin/course/relation")
public class CourseRelationManageAPI {

    @Autowired private CourseService courseService;
    @Autowired private CourseRelationManageService relationManage;
    @Autowired private DiagnosticEnumerationBeanConverter diagnosticEnumBeanConverter;
    @Autowired private Go2NurseUtility utility;

    @Path("/conditions/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countCourseByConditions(@Context HttpServletRequest request,
                                            @QueryParam("hospital_id")   @DefaultValue("") String strHospitalId,
                                            @QueryParam("department_id") @DefaultValue("") String strDepartmentId,
                                            @QueryParam("diagnostic_id") @DefaultValue("") String strDiagnosticId,
                                            @QueryParam("category_id")   @DefaultValue("") String strCategoryId,
                                            @QueryParam("course_id") @DefaultValue("") String strCourseId
    ) {
        Integer departmentId = VerifyUtil.isIds(strDepartmentId) ? VerifyUtil.parseIntIds(strDepartmentId).get(0) : null;
        Long diagnosticId = VerifyUtil.isIds(strDiagnosticId) ? VerifyUtil.parseLongIds(strDiagnosticId).get(0) : null;
        Integer hospitalId = VerifyUtil.isIds(strHospitalId) ? VerifyUtil.parseIntIds(strHospitalId).get(0) : null;
        List<Long> categoryId = VerifyUtil.isIds(strCategoryId) ? VerifyUtil.parseLongIds(strCategoryId) : null;
        Long courseId = VerifyUtil.isIds(strCourseId) ? VerifyUtil.parseLongIds(strCourseId).get(0) : null;
        long coursesCount;
        if (null==courseId) {
            coursesCount = relationManage.countCourseByHospitalDepartmentDiagnosticCategory(
                    hospitalId, departmentId, false,
                    diagnosticId,
                    categoryId,
                    true);
        }
        else {
            coursesCount = courseService.existCourse(courseId) ? 1 : 0;
        }
        return Response.ok(coursesCount).build();
    }

    @Path("/conditions")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCourseByConditions(@Context HttpServletRequest request,
                                          @QueryParam("hospital_id")   @DefaultValue("") String strHospitalId,
                                          @QueryParam("department_id") @DefaultValue("") String strDepartmentId,
                                          @QueryParam("diagnostic_id") @DefaultValue("") String strDiagnosticId,
                                          @QueryParam("category_id")   @DefaultValue("") String strCategoryId,
                                          @QueryParam("course_id") @DefaultValue("") String strCourseId,
                                          @QueryParam("index") @DefaultValue("0") int pageIndex,
                                          @QueryParam("number") @DefaultValue("10") int sizePerPage
                                          @
    ) {
        Integer departmentId = VerifyUtil.isIds(strDepartmentId) ? VerifyUtil.parseIntIds(strDepartmentId).get(0) : null;
        Long diagnosticId = VerifyUtil.isIds(strDiagnosticId) ? VerifyUtil.parseLongIds(strDiagnosticId).get(0) : null;
        Integer hospitalId = VerifyUtil.isIds(strHospitalId) ? VerifyUtil.parseIntIds(strHospitalId).get(0) : null;
        List<Long> categoryId = VerifyUtil.isIds(strCategoryId) ? VerifyUtil.parseLongIds(strCategoryId) : null;
        Long courseId = VerifyUtil.isIds(strCourseId) ? VerifyUtil.parseLongIds(strCourseId).get(0) : null;
        List<CourseBean> courses;
        if (null==courseId) {
            courses = relationManage.getCourseByHospitalDepartmentDiagnosticCategory(
                    hospitalId, departmentId, false,
                    diagnosticId,
                    categoryId,
                    true,
                    pageIndex, sizePerPage);
        }
        else {
            courses = new ArrayList<>();
            CourseBean course = courseService.existCourse(courseId)
                    ? courseService.getCourseById(courseId, utility.getHttpPrefix())
                    : null;
            if (null!=course) {
                course.setContent("");
                courses.add(course);
            }
        }
        return Response.ok(courses).build();
    }

    @Path("/diagnostic/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response editDiagnosticRelation(@Context HttpServletRequest request,
                                          @FormParam("diagnostic_id") @DefaultValue("0") long diagnosticId,
                                          @FormParam("course_id") @DefaultValue("0") long courseId,
                                          @FormParam("relation_status") @DefaultValue("") String relationStatus
    ) {
        CourseDiagnosticRelationBean relation = relationManage.updateCourseToDiagnostic(courseId, diagnosticId, relationStatus);
        return Response.ok(relation).build();
    }

    @Path("/diagnostic")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDiagnosticRelation(@Context HttpServletRequest request,
                                          @FormParam("diagnostic_id") @DefaultValue("0") long diagnosticId,
                                          @FormParam("course_id") @DefaultValue("0") long courseId
    ) {
        boolean success = relationManage.addCourseToDiagnostic(courseId, diagnosticId);
        return Response.ok(success).build();
    }

    @Path("/diagnostic/all_enumeration")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllDiagnosticEnum(@Context HttpServletRequest request) {
        List<DiagnosticEnumeration> diagnosticEnum = DiagnosticEnumeration.getAllDiagnostic();
        List<DiagnosticEnumerationBean> diagnosticEnumBean = new ArrayList<>();
        for (DiagnosticEnumeration diagnostic : diagnosticEnum) {
            diagnosticEnumBean.add(diagnosticEnumBeanConverter.convert(diagnostic));
        }
        return Response.ok(diagnosticEnumBean).build();
    }
}

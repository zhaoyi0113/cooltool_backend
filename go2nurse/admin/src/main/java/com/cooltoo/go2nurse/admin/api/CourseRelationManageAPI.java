package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.go2nurse.beans.*;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
import com.cooltoo.go2nurse.converter.DiagnosticEnumerationBeanConverter;
import com.cooltoo.go2nurse.service.CourseRelationManageService;
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

    @Autowired private CourseRelationManageService relationManage;
    @Autowired private DiagnosticEnumerationBeanConverter diagnosticEnumBeanConverter;

    @Path("/conditions/by_hospital_department")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCourseByHospitalOrDepartmentId(@Context HttpServletRequest request,
                                                      @QueryParam("hospital_id") @DefaultValue("0") int hospitalId,
                                                      @QueryParam("department_id") @DefaultValue("0") int departmentId
    ) {
        List<CourseBean> courses = relationManage.getAllCourseByHospitalOrDepartmentId(hospitalId, departmentId);
        return Response.ok(courses).build();
    }

    @Path("/conditions")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCourse(@Context HttpServletRequest request,
                              @QueryParam("hospital_id") @DefaultValue("0") int hospitalId,
                              @QueryParam("department_id") @DefaultValue("0") int departmentId,
                              @QueryParam("diagnostic_id") @DefaultValue("0") long diagnosticId,
                              // course_hospital_relation_status: ALL/ENABLED/DISABLED
                              @QueryParam("course_hospital_relation_status") @DefaultValue("ALL") String courseHospitalRelationStatus,
                              // course_hospital_relation_status: ALL/DISABLE/ENABLE/EDITING
                              @QueryParam("course_status") @DefaultValue("ALL") String courseStatus
    ) {
        Map<String, List<CourseBean>> courses = relationManage.getCoursesByConditions(null, hospitalId, departmentId, diagnosticId,
                courseHospitalRelationStatus, courseStatus);
        return Response.ok(courses).build();
    }

    @Path("/hospital/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response editHospitalRelation(@Context HttpServletRequest request,
                                        @FormParam("hospital_id") @DefaultValue("0") int hospitalId,
                                        @FormParam("course_id") @DefaultValue("0") long courseId,
                                        @FormParam("relation_status") @DefaultValue("") String relationStatus
    ) {
        CourseHospitalRelationBean relation = relationManage.updateCourseToHospital(courseId, hospitalId, relationStatus);
        return Response.ok(relation).build();
    }

    @Path("/department/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response editDepartmentRelation(@Context HttpServletRequest request,
                                          @FormParam("department_id") @DefaultValue("0") int departmentId,
                                          @FormParam("course_id") @DefaultValue("0") long courseId,
                                          @FormParam("relation_status") @DefaultValue("") String relationStatus
    ) {
        CourseDepartmentRelationBean relation = relationManage.updateCourseToDepartment(courseId, departmentId, relationStatus);
        return Response.ok(relation).build();
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

    @Path("/hospital")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addHospitalRelation(@Context HttpServletRequest request,
                                        @FormParam("hospital_id") @DefaultValue("0") int hospitalId,
                                        @FormParam("course_id") @DefaultValue("0") long courseId
    ) {
        boolean success = relationManage.addCourseToHospital(courseId, hospitalId);
        return Response.ok(success).build();
    }

    @Path("/department")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDepartmentRelation(@Context HttpServletRequest request,
                                          @FormParam("department_id") @DefaultValue("0") int departmentId,
                                          @FormParam("course_id") @DefaultValue("0") long courseId
    ) {
        boolean success = relationManage.addCourseToDepartment(courseId, departmentId);
        return Response.ok(success).build();
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

    @Path("/diagnostic/name")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addDiagnosticRelation(@Context HttpServletRequest request,
                                          @FormParam("diagnostic_name") @DefaultValue("") String diagnosticName,
                                          @FormParam("course_id") @DefaultValue("0") long courseId
    ) {
        DiagnosticEnumeration diagnostic = DiagnosticEnumeration.parseString(diagnosticName);
        boolean success = relationManage.addCourseToDiagnostic(courseId, diagnostic.ordinal());
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

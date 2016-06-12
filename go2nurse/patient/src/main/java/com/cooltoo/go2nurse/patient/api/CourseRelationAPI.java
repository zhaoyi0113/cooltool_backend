package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.CourseBean;
import com.cooltoo.go2nurse.constants.CourseStatus;
import com.cooltoo.go2nurse.service.CourseRelationManageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/6/13.
 */
@Path("/course/relation")
public class CourseRelationAPI {

    @Autowired private CourseRelationManageService relationManage;

    @Path("/conditions")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCourse(@Context HttpServletRequest request,
                              @QueryParam("hospital_id") @DefaultValue("0") int hospitalId,
                              @QueryParam("department_id") @DefaultValue("0") int departmentId,
                              @QueryParam("diagnostic_id") @DefaultValue("0") int diagnosticId
    ) {
        Map<String, List<CourseBean>> courses = relationManage.getCoursesByConditions(hospitalId, departmentId, diagnosticId, CommonStatus.ENABLED.name(), CourseStatus.ENABLE.name());
        return Response.ok(courses).build();
    }
}

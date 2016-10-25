package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.go2nurse.beans.CourseBean;
import com.cooltoo.go2nurse.beans.CoursesGroupBean;
import com.cooltoo.go2nurse.constants.CourseStatus;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.CourseRelationManageService;
import com.cooltoo.go2nurse.service.CourseService;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hp on 2016/6/9.
 */
@Path("/course")
public class CourseAPI {

    private static final Logger logger = LoggerFactory.getLogger(CourseAPI.class.getName());

    @Autowired private CourseService courseService;
    @Autowired private CourseRelationManageService courseRelationManage;
    @Autowired private Go2NurseUtility utility;

    @Path("/get_by_name")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getCourseByName(@Context HttpServletRequest request,
                                    @QueryParam("name_like") @DefaultValue("") String nameLike
    ) {
        List<CourseBean> courses = courseService.getCourseByNameAndStatus(nameLike, CourseStatus.ENABLE.name());
        return Response.ok(courses).build();
    }

    @Path("/get_by_category/{category_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCourseByCategoryId(@Context HttpServletRequest request,
                                          @PathParam("category_id") @DefaultValue("0") long categoryId
    ) {
        String status = CourseStatus.ENABLE.name();
        logger.info(" get courses by status={} categoryId={}", status, categoryId);
        List<CoursesGroupBean> categoryGroup = courseRelationManage.getHospitalCoursesGroupByCategory(null, null, null, Arrays.asList(new Long[]{categoryId}));
        if (!VerifyUtil.isListEmpty(categoryGroup)) {
            return Response.ok(categoryGroup.get(0)).build();
        }
        return Response.ok().build();
    }

    @Path("/categories/{ids}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCourseUnderCategories(@PathParam("ids") String ids){
        ids = (""+ids).replace('_', ',');
        List<Long> categoryIds = VerifyUtil.parseLongIds(ids);
        String status = CourseStatus.ENABLE.name();
        logger.info(" get courses by status={} categoryId={}", status, ids);
        List<CoursesGroupBean> categoryGroup = courseRelationManage.getHospitalCoursesGroupByCategory(null, null, null, categoryIds);
        return Response.ok(categoryGroup).build();
    }

    // 获取课程详情
    @Path("/get_course_detail")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getCourseDetailById(@Context HttpServletRequest request,
                                        @QueryParam("course_id") @DefaultValue("0") long courseId
    ) {
        logger.info("get detail course by id={} nginxPrefix={}", courseId, utility.getHttpPrefix());

        CourseBean course = courseService.getCourseById(courseId, utility.getHttpPrefix());
        return Response.ok(course).build();
    }

    // 获取课程详情
    @Path("/course_detail_html")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response getCourseDetailHtmlById(@Context HttpServletRequest request,
                                            @QueryParam("course_id") @DefaultValue("0") long courseId
    ) {
        logger.info("get detail course by id={} nginxPrefix={}", courseId, utility.getHttpPrefix());

        CourseBean course = courseService.getCourseById(courseId, utility.getHttpPrefix());
        return Response.ok(course.getContent()).build();
    }
}

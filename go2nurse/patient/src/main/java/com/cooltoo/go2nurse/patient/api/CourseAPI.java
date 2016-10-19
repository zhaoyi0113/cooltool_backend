package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.CourseBean;
import com.cooltoo.go2nurse.beans.CourseCategoryBean;
import com.cooltoo.go2nurse.constants.CourseStatus;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.patient.beans.CategoryCoursesBean;
import com.cooltoo.go2nurse.service.CourseCategoryService;
import com.cooltoo.go2nurse.service.CourseService;
import com.cooltoo.go2nurse.service.file.AbstractGo2NurseFileStorageService;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by hp on 2016/6/9.
 */
@Path("/course")
public class CourseAPI {

    private static final Logger logger = LoggerFactory.getLogger(CourseAPI.class.getName());

    @Autowired
    private CourseService courseService;
    @Autowired
    private CourseCategoryService categoryService;
    @Autowired
    private Go2NurseUtility utility;

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
        List<CourseBean> courses = categoryService.getCourseByCategoryId(status, categoryId);
        logger.info("count = {}", courses.size());
        CourseCategoryBean category = categoryService.getCategoryById(categoryId);
        CategoryCoursesBean ccB = new CategoryCoursesBean();
        ccB.setName(category.getName());
        ccB.setId(category.getId());
        ccB.setImageUrl(category.getImageUrl());
        ccB.setCourses(courses);
        return Response.ok(ccB).build();
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

    /**
     * 获得学习页面数据
     * @param request
     * @param page
     * @param number
     * @return
     */
    @Path("/get_available_categories/{page}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getAvailableCategories(@Context HttpServletRequest request,
                                           @PathParam("page") int page, @PathParam("number") int number) {
        List<CourseCategoryBean> categories = categoryService.getCategoryByStatus(
                CommonStatus.ENABLED.name(), page, number);

        return Response.ok(categories).build();
    }

    @Path("/categories/get_by_course_ids")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getCategoriesByCourseId(@Context HttpServletRequest request,
                                            @QueryParam("course_ids") @DefaultValue("0") String strCourseIds
    ) {
        List<Long> courseIds = VerifyUtil.parseLongIds(strCourseIds);
        List<CourseCategoryBean> categories = categoryService.getCategoryByCourseId(CommonStatus.ENABLED.name(), courseIds);
        return Response.ok(categories).build();
    }


}

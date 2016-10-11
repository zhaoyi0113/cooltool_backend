package com.cooltoo.nurse360.admin.api;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.nurse360.beans.Nurse360CourseBean;
import com.cooltoo.nurse360.beans.Nurse360CourseCategoryBean;
import com.cooltoo.nurse360.service.CourseCategoryServiceForNurse360;
import com.cooltoo.nurse360.service.CourseHospitalRelationServiceForNurse360;
import com.cooltoo.nurse360.service.CourseServiceForNurse360;
import com.cooltoo.nurse360.util.Nurse360Utility;
import com.cooltoo.util.HtmlParser;
import com.cooltoo.util.VerifyUtil;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/admin/course")
public class CourseManageAPI {

    private static final Logger logger = LoggerFactory.getLogger(CourseManageAPI.class.getName());

    @Autowired private CourseHospitalRelationServiceForNurse360 courseHospitalRelationService;
    @Autowired private CourseCategoryServiceForNurse360 categoryService;
    @Autowired private CourseServiceForNurse360 courseService;
    @Autowired private Nurse360Utility utility;

    // status ==> all/enable/disable/editing
    @Path("/count/{status}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countCourse(@Context HttpServletRequest request,
                                @PathParam("status") @DefaultValue("") String status
    ) {
        logger.info("get course count by status={}", status);
        long count = courseService.countByNameLikeAndStatus(null, status);
        logger.info("count = {}", count);
        return Response.ok(count).build();
    }

    // status ==> all/enable/disable/editing
    @Path("/{status}/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCourseByStatus(@Context HttpServletRequest request,
                                      @PathParam("status") @DefaultValue("") String status,
                                      @PathParam("index")  @DefaultValue("0") int index,
                                      @PathParam("number") @DefaultValue("10") int number
    ) {
        logger.info("get course by status={} at page={}, {}/page", status, index, number);
        List<Nurse360CourseBean> courses = courseService.getCourseByNameAndStatus(null, status, index, number);
        logger.info("count = {}", courses.size());
        return Response.ok(courses).build();
    }

    @Path("/hospital_department/count/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countCourseByHospitalDepartment(@Context HttpServletRequest request,
                                                    @QueryParam("hospital_id") @DefaultValue("0") int hospitalId,
                                                    @QueryParam("department_id") String strDepartmentId
    ) {
        Integer departmentId = VerifyUtil.isIds(strDepartmentId) ? VerifyUtil.parseIntIds(strDepartmentId).get(0) : null;
        List<Long> courseIds = courseHospitalRelationService.getCourseInHospitalAndDepartment(hospitalId, departmentId, "ALL");
        courseIds = courseService.getCourseIdByStatusAndIds("ALL", courseIds);
        int count = VerifyUtil.isListEmpty(courseIds) ? 0 : courseIds.size();
        logger.info("count = {}", count);
        return Response.ok(count).build();
    }

    @Path("/hospital_department")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCourseByHospitalDepartment(@Context HttpServletRequest request,
                                                  @QueryParam("hospital_id") @DefaultValue("0") int hospitalId,
                                                  @QueryParam("department_id") String strDepartmentId,
                                                  @QueryParam("index")  @DefaultValue("0") int index,
                                                  @QueryParam("number") @DefaultValue("10") int number
    ) {
        Integer departmentId = VerifyUtil.isIds(strDepartmentId) ? VerifyUtil.parseIntIds(strDepartmentId).get(0) : null;
        List<Long> courseIds = courseHospitalRelationService.getCourseInHospitalAndDepartment(hospitalId, departmentId, "ALL");
        List<Nurse360CourseBean> courses = courseService.getCourseByIds(courseIds, index, number);
        logger.info("count = {}", courses.size());
        return Response.ok(courses).build();
    }

    @Path("/get_by_course_ids")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getCourseByCourseIds(@Context HttpServletRequest request,
                                         @QueryParam("course_ids") @DefaultValue("0") String strCourseIds
    ) {
        logger.info("get course by ids={}", strCourseIds);
        List<Long> courseIds = VerifyUtil.parseLongIds(strCourseIds);
        List<Nurse360CourseBean> courses = courseService.getCourseByIds(courseIds);
        logger.info("count = {}", courses.size());
        return Response.ok(courses).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteCourseByCourseIds(@Context HttpServletRequest request,
                                            @FormParam("course_ids") @DefaultValue("0") String ids
    ) {
        logger.info("delete course by ids={}", ids);
        String deleteIds = courseService.deleteByIds(ids);
        logger.info("ids={}", ids);
        return Response.ok(deleteIds).build();
    }


    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createCourse(@Context HttpServletRequest request,
                                 @FormParam("name") @DefaultValue("") String name,
                                 @FormParam("introduction") @DefaultValue("") String introduction,
                                 @FormParam("link") @DefaultValue("") String link,
                                 @FormParam("keyword") @DefaultValue("") String keyword,
                                 @FormParam("category_id") @DefaultValue("-1") long categoryId,
                                 @FormParam("hospital_id") @DefaultValue("-1") int hospitalId,
                                 @FormParam("department_ids") @DefaultValue("") String strDepartmentIds

    ) {
        logger.info("create course");
        Nurse360CourseBean course = courseService.createCourse(name, introduction, link, keyword, categoryId);
        logger.info("course is {}", course);
        if (null!=course) {
            long courseId = course.getId();
            List<Integer> departmentIds = VerifyUtil.parseIntIds(strDepartmentIds);
            courseHospitalRelationService.setCourseToHospital(courseId, hospitalId, departmentIds);
        }
        return Response.ok(course).build();
    }

    @Path("/edit/base_information")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCourseBasicInfo(@Context HttpServletRequest request,
                                          @FormParam("course_id")@DefaultValue("0") long courseId,
                                          @FormParam("name") @DefaultValue("") String name,
                                          @FormParam("introduction") @DefaultValue("") String introduction,
                                          @FormParam("link") @DefaultValue("") String link,
                                          @FormParam("keyword") @DefaultValue("") String keyword,
                                          @FormParam("category_id") @DefaultValue("-1") long categoryId,
                                          @FormParam("hospital_id") @DefaultValue("-1") int hospitalId,
                                          @FormParam("department_ids") @DefaultValue("") String strDepartmentIds

    ) {
        logger.info("update course basic information");
        Nurse360CourseBean course = courseService.updateCourseBasicInfo(courseId, name, introduction, null, null, link, keyword, categoryId);
        logger.info("course is {}", course);
        if (null!=course) {
            List<Integer> departmentIds = VerifyUtil.parseIntIds(strDepartmentIds);
            courseHospitalRelationService.setCourseToHospital(courseId, hospitalId, departmentIds);
        }
        return Response.ok(course).build();
    }

    @Path("/edit/front_cover")
    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCourseFrontCover(@Context HttpServletRequest request,
                                             @FormDataParam("course_id") @DefaultValue("0") long courseId,
                                             @FormDataParam("image_name") @DefaultValue("") String imageName,
                                             @FormDataParam("image") InputStream image,
                                             @FormDataParam("image") FormDataContentDisposition disposition

    ) {
        logger.info("update course front cover");
        Nurse360CourseBean course = courseService.updateCourseBasicInfo(courseId, null, null, imageName, image, null, null, -1);
        logger.info("course is {}", course);
        return Response.ok(course).build();
    }

    @Path("/edit/status")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCourseStatus(@Context HttpServletRequest request,
                                       @FormParam("course_id") @DefaultValue("0") long courseId,
                                       @FormParam("status") @DefaultValue("disabled") String status

    ) {
        logger.info("update course={} status={}", courseId, status);
        Nurse360CourseBean bean = courseService.updateCourseStatus(courseId, status);
        logger.info("course is {}", bean);
        return Response.ok(bean).build();
    }

    //=============================================================
    //         edit the course content
    //=============================================================

    // 获取课程详情
    // param={"course_id":"1","nginx_url":"http://nginx_server_ip:port/storage_or_temporary_path/"}
    @Path("/get_detail")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDetailById(@Context HttpServletRequest request,
                                  @QueryParam("course_id") @DefaultValue("0") long courseId
    ) {
        logger.info("get detail course={} nginxPrefix={}", courseId, utility.getHttpPrefix());
        Nurse360CourseBean course = courseService.getCourseById(courseId, utility.getHttpPrefix());
        List<HospitalBean> hospitals = courseHospitalRelationService.getHospitalByCourseId(courseId, CommonStatus.ENABLED.name());
        List<HospitalDepartmentBean> departments = courseHospitalRelationService.getDepartmentByCourseId(courseId, CommonStatus.ENABLED.name());
        Nurse360CourseCategoryBean categories = categoryService.getCategoryById(course.getCategoryId());
        Map<String, Object> retVal = new HashMap<>();
        retVal.put("course", course);
        retVal.put("hospital", hospitals);
        retVal.put("department", departments);
        retVal.put("category", categories);
        return Response.ok(retVal).build();
    }


    // 将课程置为 editing, 并且将课程对应的图片迁至临时文件夹
    @Path("/edit/content/cache")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response cacheCourse2Temporary(@Context HttpServletRequest request,
                                          @FormParam("course_id") @DefaultValue("0") long courseId

    ) {
        logger.info("cache course={} to temporary path", courseId);
        Nurse360CourseBean bean = courseService.moveCourse2Temporary(courseId);
        logger.info("course is {}", bean);
        return Response.ok(bean).build();
    }


    // 课程为 editing 时, 向课程中添加图片，图片缓存在临时文件夹
    @Path("/edit/content/add_image")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addImage2Temporary(@Context HttpServletRequest request,
                                       @FormDataParam("course_id") long courseId,
                                       @FormDataParam("file_name") String imageName,
                                       @FormDataParam("file") InputStream image,
                                       @FormDataParam("file") FormDataContentDisposition disp

    ) {
        logger.info("user cache image to temporary path");
        String relativePath = courseService.createTemporaryFile(courseId, imageName, image);
        logger.info("relative path is {}", relativePath);
        int errorNo = 0;
        if (VerifyUtil.isStringEmpty(relativePath)) {
            errorNo = -1;
        }
        relativePath = HtmlParser.constructUrl(utility.getHttpPrefix(), relativePath);
        StringBuilder retVal = new StringBuilder();
        retVal.append("{")
                .append("\"error\":").append(errorNo).append(",")
                .append("\"url\":\"").append(relativePath).append("\"")
                .append("}");
        logger.info("relative path is {}", relativePath);
        return Response.ok(retVal.toString()).build();
    }


    // 提交课程，将获取置为 disable, 将缓存在临时文件夹的图片，导入 storage 文件夹，
    // 并替换 content 中 <img/> src 的引用值。
    @Path("/edit/content/submit")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCourseContent(@Context HttpServletRequest request,
                                          @FormParam("course_id") long courseId,
                                          @FormParam("content") String content

    ) {
        logger.info("submit course content");
        Nurse360CourseBean course = courseService.updateCourseContent(courseId, content);

        logger.info("course is {}", course);
        return Response.ok(course).build();
    }
}
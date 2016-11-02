package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.CourseBean;
import com.cooltoo.go2nurse.beans.CourseCategoryBean;
import com.cooltoo.go2nurse.beans.DiagnosticEnumerationBean;
import com.cooltoo.go2nurse.service.CourseCategoryRelationService;
import com.cooltoo.go2nurse.service.CourseRelationManageService;
import com.cooltoo.go2nurse.service.CourseService;
import com.cooltoo.go2nurse.util.Go2NurseUtility;
import com.cooltoo.util.HtmlParser;
import com.cooltoo.util.VerifyUtil;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired private CourseRelationManageService courseRelationManageService;
    @Autowired private CourseCategoryRelationService courseCategoryRelation;
    @Autowired private CourseService courseService;
    @Autowired private Go2NurseUtility utility;

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
    @Transactional
    public Response createCourse(@Context HttpServletRequest request,
                                 @FormParam("name") @DefaultValue("") String name,
                                 @FormParam("introduction") @DefaultValue("") String introduction,
                                 @FormParam("link") @DefaultValue("") String link,
                                 @FormParam("keyword") @DefaultValue("") String keyword,
                                 @FormParam("category_id") @DefaultValue("0") long categoryId,
                                 @FormParam("hospital_id") @DefaultValue("0") int hospitalId,
                                 @FormParam("department_ids") @DefaultValue("") String strDepartmentIds,
                                 @FormParam("diagnostic_ids") @DefaultValue("") String strDiagnosticIds

    ) {
        logger.info("create course");
        CourseBean course = courseService.createCourse(name, introduction, link, keyword);
        logger.info("course is {}", course);
        if (null!=course) {
            long courseId = course.getId();
            courseCategoryRelation.setCourseRelation(courseId, categoryId);
            List<Integer> departmentIds = VerifyUtil.parseIntIds(strDepartmentIds);
            if (-1==hospitalId && !departmentIds.contains(Integer.valueOf(-1))) {
                departmentIds.add(Integer.valueOf(-1));
            }
            courseRelationManageService.setCourseToDepartmentRelationship(courseId, departmentIds);
            List<Long> diagnosticIds = VerifyUtil.parseLongIds(strDiagnosticIds);
            courseRelationManageService.setCourseToDiagnosticRelationship(courseId, diagnosticIds);
        }
        return Response.ok(course).build();
    }

    @Path("/base_information")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response updateCourseBasicInfo(@Context HttpServletRequest request,
                                          @FormParam("course_id")@DefaultValue("0") long courseId,
                                          @FormParam("name") @DefaultValue("") String name,
                                          @FormParam("introduction") @DefaultValue("") String introduction,
                                          @FormParam("link") @DefaultValue("") String link,
                                          @FormParam("keyword") @DefaultValue("") String keyword,
                                          @FormParam("category_id") @DefaultValue("0") long categoryId,
                                          @FormParam("hospital_id") @DefaultValue("0") int hospitalId,
                                          @FormParam("department_ids") @DefaultValue("") String strDepartmentIds,
                                          @FormParam("diagnostic_ids") @DefaultValue("") String strDiagnosticIds

    ) {
        logger.info("update course basic information");
        CourseBean course = courseService.updateCourseBasicInfo(courseId, name, introduction, null, null, link, keyword);
        logger.info("course is {}", course);
        if (null!=course) {
            courseCategoryRelation.setCourseRelation(courseId, categoryId);
            List<Integer> departmentIds = VerifyUtil.parseIntIds(strDepartmentIds);
            if (-1==hospitalId && !departmentIds.contains(Integer.valueOf(-1))) {
                departmentIds.add(Integer.valueOf(-1));
            }
            courseRelationManageService.setCourseToDepartmentRelationship(courseId, departmentIds);
            List<Long> diagnosticIds = VerifyUtil.parseLongIds(strDiagnosticIds);
            courseRelationManageService.setCourseToDiagnosticRelationship(courseId, diagnosticIds);
        }
        return Response.ok(course).build();
    }

    @Path("/front_cover")
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
        CourseBean course = courseService.updateCourseBasicInfo(courseId, null, null, imageName, image, null, null);
        logger.info("course is {}", course);
        return Response.ok(course).build();
    }

    @Path("/front_cover")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCourseFrontCover(@Context HttpServletRequest request,
                                           @FormParam("course_id") @DefaultValue("0") long courseId

    ) {
        logger.info("delete course front cover");
        CourseBean course = courseService.deleteCourseFrontCover(courseId);
        logger.info("course is {}", course);
        return Response.ok(course).build();
    }

    @Path("/status")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCourseStatus(@Context HttpServletRequest request,
                                       @FormParam("course_id") @DefaultValue("0") long courseId,
                                       @FormParam("status") @DefaultValue("disabled") String status

    ) {
        logger.info("update course={} status={}", courseId, status);
        CourseBean bean = courseService.updateCourseStatus(courseId, status);
        logger.info("course is {}", bean);
        return Response.ok(bean).build();
    }

    //=============================================================
    //         edit the course content
    //=============================================================

    // 获取课程详情
    // param={"course_id":"1","nginx_url":"http://nginx_server_ip:port/storage_or_temporary_path/"}
    @Path("/detail")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDetailById(@Context HttpServletRequest request,
                                  @QueryParam("course_id") @DefaultValue("0") long courseId
    ) {
        logger.info("get detail course={} nginxPrefix={}", courseId, utility.getHttpPrefix());
        CourseBean course = courseService.getCourseById(courseId, utility.getHttpPrefix());
        List<HospitalBean> hospitals = courseRelationManageService.getHospitalByCourseId(courseId, CommonStatus.ENABLED.name());
        List<HospitalDepartmentBean> departments = courseRelationManageService.getDepartmentByCourseId(courseId, CommonStatus.ENABLED.name());
        List<CourseCategoryBean> categories = courseCategoryRelation.getCategoryByCourseId(CommonStatus.ENABLED.name(), courseId);
        List<DiagnosticEnumerationBean> diagnostics = courseRelationManageService.getDiagnosticByCourseId(courseId, CommonStatus.ENABLED.name());
        Map<String, Object> retVal = new HashMap<>();
        retVal.put("course", course);
        retVal.put("hospital", hospitals);
        retVal.put("department", departments);
        retVal.put("diagnostic", diagnostics);
        retVal.put("category", categories);
        return Response.ok(retVal).build();
    }


    // 将课程置为 editing, 并且将课程对应的图片迁至临时文件夹
    @Path("/content/cache")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response cacheCourse2Temporary(@Context HttpServletRequest request,
                                          @FormParam("course_id") @DefaultValue("0") long courseId

    ) {
        logger.info("cache course={} to temporary path", courseId);
        CourseBean bean = courseService.moveCourse2Temporary(courseId);
        logger.info("course is {}", bean);
        return Response.ok(bean).build();
    }


    // 课程为 editing 时, 向课程中添加图片，图片缓存在临时文件夹
    @Path("/edit/content/add_image")
    @PUT
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
    @Path("/content/submit")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCourseContent(@Context HttpServletRequest request,
                                          @FormParam("course_id") long courseId,
                                          @FormParam("content") String content

    ) {
        logger.info("submit course content");
        CourseBean course = courseService.updateCourseContent(courseId, content);

        logger.info("course is {}", course);
        return Response.ok(course).build();
    }

    @Path("/content/submit/html")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCourseContentWithImageNeedDownload(@Context HttpServletRequest request,
                                                            @FormParam("course_id") long courseId,
                                                            @FormParam("content") String htmlContent
    ) {
        logger.info("submit course with html, need to download images automatically");
        CourseBean course = courseService.updateCourseContent(courseId, htmlContent);
        logger.info("course is {}", course);
        return Response.ok(course).build();
    }
}
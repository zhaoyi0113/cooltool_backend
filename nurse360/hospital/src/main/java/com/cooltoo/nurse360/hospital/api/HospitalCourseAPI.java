package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.constants.CourseStatus;
import com.cooltoo.nurse360.beans.HospitalAdminUserDetails;
import com.cooltoo.nurse360.beans.Nurse360CourseBean;
import com.cooltoo.nurse360.beans.Nurse360CourseCategoryBean;
import com.cooltoo.nurse360.hospital.util.SecurityUtil;
import com.cooltoo.nurse360.service.CourseCategoryServiceForNurse360;
import com.cooltoo.nurse360.service.CourseHospitalRelationServiceForNurse360;
import com.cooltoo.nurse360.service.CourseServiceForNurse360;
import com.cooltoo.nurse360.util.Nurse360Utility;
import com.cooltoo.util.HtmlParser;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.*;

/**
 * Created by zhaolisong on 20/02/2017.
 */
@RestController
@RequestMapping(path = "/nurse360_hospital")
public class HospitalCourseAPI {

    private static final Logger logger = LoggerFactory.getLogger(HospitalCourseAPI.class.getName());

    @Autowired private CourseHospitalRelationServiceForNurse360 courseHospitalRelationService;
    @Autowired private CourseCategoryServiceForNurse360 categoryService;
    @Autowired private CourseServiceForNurse360 courseService;
    @Autowired private Nurse360Utility utility;

    private List<CourseStatus> getCourseStatuses() {
        List<CourseStatus> courseStatuses = new ArrayList<>();
        courseStatuses.add(CourseStatus.DISABLE);
        courseStatuses.add(CourseStatus.EDITING);
        courseStatuses.add(CourseStatus.ENABLE);
        return courseStatuses;
    }

    //=============================================================
    //            Authentication of NURSE/MANAGER Role
    //=============================================================
    @RequestMapping(path = "/courses/nurse/count", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public int countCourse(HttpServletRequest request) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        if (!userDetails.isAdmin()) {
            Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
            Integer hospitalId = null != tmp[0] ? tmp[0].intValue() : null;
            Integer departmentId = null != tmp[1] ? tmp[1].intValue() : null;
            List<Long> courseIds = courseHospitalRelationService.getCourseInHospitalAndDepartment(hospitalId, departmentId, "ALL");
            courseIds = courseService.getCourseIdByStatusesAndIds(getCourseStatuses(), courseIds);
            int count = VerifyUtil.isListEmpty(courseIds) ? 0 : courseIds.size();
            logger.info("count = {}", count);
            return count;
        }
        return 0;
    }

    @RequestMapping(path = "/courses/nurse", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<Nurse360CourseBean> getCourse(HttpServletRequest request,
                                              @RequestParam(defaultValue = "0",  name = "index")  int index,
                                              @RequestParam(defaultValue = "10", name = "number") int number
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        if (!userDetails.isAdmin()) {
            Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
            Integer hospitalId = null != tmp[0] ? tmp[0].intValue() : null;
            Integer departmentId = null != tmp[1] ? tmp[1].intValue() : null;
            List<Long> courseIds = courseHospitalRelationService.getCourseInHospitalAndDepartment(hospitalId, departmentId, "ALL");
            courseIds = courseService.getCourseIdByStatusesAndIds(getCourseStatuses(), courseIds);
            List<Nurse360CourseBean> courses = courseService.getCourseByIds(courseIds, index, number);
            logger.info("courses's count = {}", courses.size());
            return courses;
        }
        return new ArrayList<>();
    }

    @RequestMapping(path = "/courses/nurse/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public Nurse360CourseBean addCourse(HttpServletRequest request,
                                        @RequestParam(defaultValue = "", name = "name")         String name,
                                        @RequestParam(defaultValue = "", name = "introduction") String introduction,
                                        @RequestParam(defaultValue = "", name = "link")         String link,
                                        @RequestParam(defaultValue = "", name = "keyword")      String keyword,
                                        @RequestParam(defaultValue = "0",name = "category_id")  long categoryId
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        if (!userDetails.isAdmin()) {
            Long[] tmp = SecurityUtil.newInstance().getHospitalDepartmentLongId("", "", userDetails);
            Integer departmentId = null != tmp[1] ? tmp[1].intValue() : null;
            Nurse360CourseBean course = courseService.createCourse(name, introduction, link, keyword, categoryId);
            if (null!=course) {
                courseHospitalRelationService.setCourseToHospital(course.getId(), Arrays.asList(new Integer[]{departmentId}));
            }
            logger.info("courses's count = {}", course);
            return course;
        }
        throw new BadRequestException(ErrorCode.NURSE360_NOT_PERMITTED);
    }

    @RequestMapping(path = "/courses/nurse/edit", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public Nurse360CourseBean addCourse(HttpServletRequest request,
                                        @RequestParam(defaultValue = "0",name = "course_id")    long courseId,
                                        @RequestParam(defaultValue = "", name = "name")         String name,
                                        @RequestParam(defaultValue = "", name = "introduction") String introduction,
                                        @RequestParam(defaultValue = "", name = "link")         String link,
                                        @RequestParam(defaultValue = "", name = "keyword")      String keyword,
                                        @RequestParam(defaultValue = "0",name = "category_id")  long categoryId
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        if (!userDetails.isAdmin()) {
            logger.info("update course basic information");
            Nurse360CourseBean course = courseService.updateCourseBasicInfo(courseId, name, introduction, null, null, link, keyword, categoryId);
            logger.info("course is {}", course);
            return course;
        }
        throw new BadRequestException(ErrorCode.NURSE360_NOT_PERMITTED);
    }

    @RequestMapping(path = "/courses/nurse/edit/front/cover", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, consumes = MediaType.MULTIPART_FORM_DATA)
    public Nurse360CourseBean editCourseFrontCover(HttpServletRequest request,
                                                   @RequestParam(defaultValue = "0", name = "course_id")  long          courseId,
                                                   @RequestParam(defaultValue = "",  name = "image_name") String        imageName,
                                                   @RequestPart(required = true,     name = "image")      MultipartFile image
    ) throws IOException {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        if (!userDetails.isAdmin()) {
            logger.info("update course front cover");
            Nurse360CourseBean course = courseService.updateCourseBasicInfo(courseId, null, null, imageName, image.getInputStream(), null, null, -1);
            logger.info("course is {}", course);
            return course;
        }
        throw new BadRequestException(ErrorCode.NURSE360_NOT_PERMITTED);
    }

    @RequestMapping(path = "/courses/nurse/edit/status", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public Nurse360CourseBean editCourseStatus(HttpServletRequest request,
                                               @RequestParam(defaultValue = "0",         name = "course_id")  long   courseId,
                                               @RequestParam(defaultValue = "disabled",  name = "status")     String status
    ) {
        HospitalAdminUserDetails userDetails = SecurityUtil.newInstance().getUserDetails(SecurityContextHolder.getContext().getAuthentication());
        if (!userDetails.isAdmin()) {
            logger.info("update course={} status={}", courseId, status);
            Nurse360CourseBean course = courseService.updateCourseStatus(courseId, status);
            logger.info("course is {}", course);
            return course;
        }
        throw new BadRequestException(ErrorCode.NURSE360_NOT_PERMITTED);
    }

    //=============================================================
    //         edit the course content
    //=============================================================

    // 获取课程详情
    // param={"course_id":"1","nginx_url":"http://nginx_server_ip:port/storage_or_temporary_path/"}
    @RequestMapping(path = "/courses/nurse/get/detail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public Map<String, Object> getCourseDetail(HttpServletRequest request,
                                               @RequestParam(defaultValue = "0", name = "course_id") long courseId
    ) {
        logger.info("get detail course={} nginxPrefix={}", courseId, utility.getHttpPrefix());
        List<HospitalDepartmentBean> departments = courseHospitalRelationService.getDepartmentByCourseId(courseId, CommonStatus.ENABLED.name());
        List<HospitalBean>           hospitals   = courseHospitalRelationService.getHospitalByCourseId(courseId, CommonStatus.ENABLED.name());
        Nurse360CourseBean           course      = courseService.getCourseById(courseId, utility.getHttpPrefix());
        Nurse360CourseCategoryBean   categories  = categoryService.getCategoryById(course.getCategoryId());
        Map<String, Object> retVal = new HashMap<>();
        retVal.put("course", course);
        retVal.put("hospital", hospitals);
        retVal.put("department", departments);
        retVal.put("category", categories);
        return retVal;
    }


    // 将课程置为 editing, 并且将课程对应的图片迁至临时文件夹
    @RequestMapping(path = "/courses/nurse/edit/content/cache", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public Nurse360CourseBean cacheCourse2Temporary(HttpServletRequest request,
                                                    @RequestParam(defaultValue = "0", name = "course_id") long courseId

    ) {
        logger.info("cache course={} to temporary path", courseId);
        Nurse360CourseBean course = courseService.moveCourse2Temporary(courseId);
        logger.info("course is {}", course);
        return course;
    }


    // 课程为 editing 时, 向课程中添加图片，图片缓存在临时文件夹
    @RequestMapping(path = "/courses/nurse/edit/content/add/image", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON, consumes = MediaType.MULTIPART_FORM_DATA)
    public String addImage2Temporary(HttpServletRequest request,
                                     @RequestParam(defaultValue = "0", name = "course_id")  long          courseId,
                                     @RequestParam(defaultValue = "",  name = "image_name") String        imageName,
                                     @RequestPart(required = true,     name = "image")      MultipartFile image

    ) throws IOException {
        logger.info("user cache image to temporary path");
        String relativePath = courseService.createTemporaryFile(courseId, imageName, image.getInputStream());
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
        return retVal.toString();
    }


    // 提交课程，将获取置为 disable, 将缓存在临时文件夹的图片，导入 storage 文件夹，
    // 并替换 content 中 <img/> src 的引用值。
    @RequestMapping(path = "/courses/nurse/edit/content/submit", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public Nurse360CourseBean updateCourseContent(HttpServletRequest request,
                                                  @RequestParam(defaultValue = "0", name = "course_id")  long   courseId,
                                                  @RequestParam(defaultValue = "",  name = "content")    String content

    ) {
        logger.info("submit course content");
        Nurse360CourseBean course = courseService.updateCourseContent(courseId, content);
        logger.info("course is {}", course);
        return course;
    }
}

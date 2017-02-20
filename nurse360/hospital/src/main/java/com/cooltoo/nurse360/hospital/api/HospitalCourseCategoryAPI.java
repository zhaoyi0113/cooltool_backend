package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.nurse360.beans.Nurse360CourseCategoryBean;
import com.cooltoo.nurse360.service.CourseCategoryServiceForNurse360;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.List;

/**
 * Created by zhaolisong on 20/02/2017.
 */
@RestController
@RequestMapping(path = "/nurse360_hospital")
public class HospitalCourseCategoryAPI {

    private static final Logger logger = LoggerFactory.getLogger(HospitalCourseCategoryAPI.class);

    @Autowired
    private CourseCategoryServiceForNurse360 categoryService;


    @RequestMapping(path = "/nurse/course/category/{category_id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public Nurse360CourseCategoryBean getCategoryById(HttpServletRequest request,
                                                      @PathVariable long category_id
    ) {
        logger.info("get category by category id");
        Nurse360CourseCategoryBean category = categoryService.getCategoryById(category_id);
        return category;
    }

    // status ==> all/enabled/disabled/deleted
    @RequestMapping(path = "/nurse/course/category/count/{status}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public long countCategory(HttpServletRequest request,
                              @PathVariable String status
    ) {
        logger.info("get category count by status={}", status);
        long count = categoryService.countByStatus(status);
        logger.info("count = {}", count);
        return count;
    }

    // status ==> all/enabled/disabled/deleted
    @RequestMapping(path = "/nurse/course/category/{status}/{index}/{number}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON)
    public List<Nurse360CourseCategoryBean> getCategoryByStatus(HttpServletRequest request,
                                                                @PathVariable String status,
                                                                @PathVariable int index,
                                                                @PathVariable int number
    ) {
        logger.info("get category by status={} at page={}, {}/page", status, index, number);
        List<Nurse360CourseCategoryBean> categories = categoryService.getCategoryByStatus(status, index, number);
        logger.info("count = {}", categories.size());
        return categories;
    }

    @RequestMapping(path = "/nurse/course/category/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON)
    public Nurse360CourseCategoryBean addCategory(HttpServletRequest request,
                                                  @RequestParam(defaultValue = "", name = "name")        String name,
                                                  @RequestParam(defaultValue = "", name = "introduction")String introduction
    ) {
        logger.info("new category");
        Nurse360CourseCategoryBean category = categoryService.addCategory(name, introduction, null, null);
        return category;
    }

    @RequestMapping(path = "/nurse/course/category/edit", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON)
    public Nurse360CourseCategoryBean updateCategory(HttpServletRequest request,
                                                     @RequestParam(defaultValue = "0",name = "category_id")    long   categoryId,
                                                     @RequestParam(defaultValue = "", name = "name")           String name,
                                                     @RequestParam(defaultValue = "", name = "introduction")   String introduction,
                                                     @RequestParam(defaultValue = "disabled", name = "status") String status
    ) {
        logger.info("update category");
        Nurse360CourseCategoryBean category = categoryService.updateCategory(categoryId, name, introduction, status, null, null);
        return category;
    }

    @RequestMapping(path = "/nurse/course/category/edit/image", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON, consumes = MediaType.MULTIPART_FORM_DATA)
    public Nurse360CourseCategoryBean updateCategory(HttpServletRequest request,
                                                     @RequestParam(defaultValue = "0", name = "category_id") long          categoryId,
                                                     @RequestParam(defaultValue = "",  name = "image_name")  String        imageName,
                                                     @RequestPart(required = true,     name = "image")       MultipartFile image
    ) throws IOException {
        logger.info("update category front cover");
        Nurse360CourseCategoryBean category = categoryService.updateCategory(categoryId, null, null, null, imageName, image.getInputStream());
        return category;
    }
}

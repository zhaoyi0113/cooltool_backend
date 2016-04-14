package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.beans.TagsBean;
import com.cooltoo.beans.TagsCategoryBean;
import com.cooltoo.services.TagsService;
import com.cooltoo.util.VerifyUtil;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.InputStream;
import java.util.List;

/**
 * Created by zhaolisong on 16/4/8.
 */
@Path("/tags")
public class TagsServiceAPI {

    private static final Logger logger = LoggerFactory.getLogger(TagsServiceAPI.class);

    @Autowired
    private TagsService tagsService;

    //=======================================================================
    //    get
    //=======================================================================

    @Path("/tag_count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getTagCount(@Context HttpServletRequest request) {
        logger.info("get all tags count");
        long count = tagsService.getTagCount();
        logger.info("get all tags count is {}", count);
        return Response.ok(count).build();
    }

    @Path("/category_count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getTagCategoryCount(@Context HttpServletRequest request) {
        logger.info("get all tags category count");
        long count = tagsService.getCategoryCount();
        logger.info("get all tags category count is {}", count);
        return Response.ok(count).build();
    }

    @Path("/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getTagByPage(@Context HttpServletRequest request,
                                 @PathParam("index")  @DefaultValue("0")  int index,
                                 @PathParam("number") @DefaultValue("10") int number
    ) {
        logger.info("get all tags at page {} numberOfPage {}", index, number);
        List<TagsBean> tags = tagsService.getTagsByPage(index, number);
        logger.info("get all tags at page {} numberOfPage {}, value is ", index, number, tags);
        return Response.ok(tags).build();
    }

    @Path("/category/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getCategoryByPage(@Context HttpServletRequest request,
                                      @PathParam("index")  @DefaultValue("0")  int index,
                                      @PathParam("number") @DefaultValue("10") int number
    ) {
        logger.info("get all tag category at page {} numberOfPage {}", index, number);
        List<TagsCategoryBean> tags = tagsService.getCategoryByPage(index, number);
        logger.info("get all tag category at page {} numberOfPage {}, value is ", index, number, tags);
        return Response.ok(tags).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getTag(@Context              HttpServletRequest request,
                           @FormParam("tag_ids") String             tagIds) {
        logger.info("get tag by ids={}", tagIds);
        List<TagsBean> tags = null;
        if (!"ALL".equalsIgnoreCase(tagIds)) {
            tags = tagsService.getTagByIds(tagIds);
        }
        else {
            tags = tagsService.getAllTag();
        }
        return Response.ok(tags).build();
    }

    @Path("/tag_without_category")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getTagWithoutCategoryId(@Context HttpServletRequest request) {
        logger.info("get tag with no category belong");
        List<TagsBean> tags = tagsService.getTagsWithoutCategoryId();
        return Response.ok(tags).build();
    }


    @Path("/tag_by_category/{category_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getTagByCategoryId(@Context                  HttpServletRequest request,
                                       @PathParam("category_id") long               categoryId) {
        List<TagsBean> tags = tagsService.getTagsByCategoryId(categoryId);
        return Response.ok(tags).build();
    }

    @Path("/category")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getCategory(@Context                   HttpServletRequest request,
                                @FormParam("category_ids") String             categoryIds) {
        logger.info("get category by ids={}", categoryIds);
        if ("ALL".equalsIgnoreCase(categoryIds)) {
            List<TagsCategoryBean> categories = tagsService.getAllCategory();
            return Response.ok(categories).build();
        }
        else {
            List<TagsCategoryBean> categorys = tagsService.getCategoryByIds(categoryIds);
            return Response.ok(categorys).build();
        }
    }

    @Path("/category_with_tags")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getCategoryWithTags(@Context                   HttpServletRequest request,
                                        @FormParam("category_ids") String             categoryIds) {
        logger.info("get category_with_tag by ids={}", categoryIds);
        if ("ALL".equalsIgnoreCase(categoryIds)) {
            List<TagsCategoryBean> categories = tagsService.getAllCategoryWithTags();
            return Response.ok(categories).build();
        }
        else {
            List<TagsCategoryBean> categories = tagsService.getCategoryWithTagsByIds(categoryIds);
            return Response.ok(categories).build();
        }
    }

    //=======================================================================
    //    update
    //=======================================================================

    @Path("/update/tag")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateTag(@Context                  HttpServletRequest request,
                              @FormParam("id")          long               id,
                              @FormParam("category_id") long               categoryId,
                              @FormParam("name")        String             name) {
        logger.info("update tag id={} name={} category_id={}, image_name={} image={}", id, name, categoryId);
        TagsBean bean = tagsService.updateTag(id, categoryId, name, null, null);
        return Response.ok(bean).build();
    }

    @Path("/update/tag_image")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateTag(@Context                      HttpServletRequest request,
                              @FormDataParam("id")          long               id,
                              @FormDataParam("image_name")  String             imageName,
                              @FormDataParam("image")       InputStream        image,
                              @FormDataParam("image")       FormDataContentDisposition imageDis) {
        logger.info("update tag id={} image_name={} image={}", id, imageName, (null!=image));
        if (VerifyUtil.isStringEmpty(imageName) && null!=imageDis) {
            imageName = imageDis.getFileName();
        }
        TagsBean bean = tagsService.updateTag(id, -1, null, imageName, image);
        return Response.ok(bean).build();
    }

    @Path("/update/category")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateCategory(@Context                  HttpServletRequest request,
                                   @FormParam("id")          long               id,
                                   @FormParam("name")        String             name) {
        logger.info("update category id={} name={}", id, name);
        TagsCategoryBean bean = tagsService.updateCategory(id, name, null, null);
        return Response.ok(bean).build();
    }

    @Path("/update/category_image")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateCategory(@Context                      HttpServletRequest request,
                                   @FormDataParam("id")          long               id,
                                   @FormDataParam("image_name")  String             imageName,
                                   @FormDataParam("image")       InputStream        image,
                                   @FormDataParam("image")       FormDataContentDisposition imageDis) {
        logger.info("update category id={} image_name={} image={}", id, imageName, (null!=image));
        if (VerifyUtil.isStringEmpty(imageName) && null!=imageDis) {
            imageName = imageDis.getFileName();
        }
        TagsCategoryBean bean = tagsService.updateCategory(id, null, imageName, image);
        return Response.ok(bean).build();
    }


    //=======================================================================
    //    delete
    //=======================================================================

    @Path("/tag")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response deleteTag(@Context          HttpServletRequest request,
                              @FormParam("ids") String             ids) {
        String deleteIds = tagsService.deleteTagByIds(ids);
        return Response.ok(ids).build();
    }

    @Path("/category")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response deleteCategory(@Context          HttpServletRequest request,
                                   @FormParam("ids") String             ids) {
        String deleteIds = tagsService.deleteCategoryByIds(ids);
        return Response.ok(ids).build();
    }

    @Path("/category_with_tags")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response deleteCategoryWithTags(@Context          HttpServletRequest request,
                                           @FormParam("ids") String             ids) {
        String deleteIds = tagsService.deleteCategoryWithTagsByIds(ids);
        return Response.ok(ids).build();
    }

    //=======================================================================
    //    add
    //=======================================================================

    @Path("/add_tag")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response addTag(@Context                      HttpServletRequest request,
                           @FormDataParam("category_id") long               categoryId,
                           @FormDataParam("name")        String             name,
                           @FormDataParam("image_name")  String             imageName,
                           @FormDataParam("image")       InputStream        image,
                           @FormDataParam("image")       FormDataContentDisposition imageDis) {
        logger.info("add tag name={} category_id={}, image_name={} image={}", name, categoryId, imageName, (null!=image));
        if (VerifyUtil.isStringEmpty(imageName) && null!=imageDis) {
            imageName = imageDis.getFileName();
        }
        TagsBean bean = tagsService.addTags(name, categoryId, imageName, image);
        return Response.ok(bean).build();
    }

    @Path("/add_category")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response addCategory(@Context                      HttpServletRequest request,
                                @FormDataParam("name")        String             name,
                                @FormDataParam("image_name")  String             imageName,
                                @FormDataParam("image")       InputStream        image,
                                @FormDataParam("image")       FormDataContentDisposition imageDis) {
        logger.info("add category name={} image_name={} image={}", name, imageName, (null!=image));
        if (VerifyUtil.isStringEmpty(imageName) && null!=imageDis) {
            imageName = imageDis.getFileName();
        }
        TagsCategoryBean bean = tagsService.addTagCategory(name, imageName, image);
        return Response.ok(bean).build();
    }
}

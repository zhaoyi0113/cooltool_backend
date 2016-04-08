package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.beans.TagsBean;
import com.cooltoo.beans.TagsCategoryBean;
import com.cooltoo.services.TagsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
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

    @Path("/tag/{tag_ids}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getTag(@Context HttpServletRequest request,
                           @PathParam("tag_ids") String tagIds) {
        List<TagsBean> tags = tagsService.getTagByIds(tagIds);
        return Response.ok(tags).build();
    }

    @Path("/tag_without_category")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getTagWithoutCategoryId(@Context HttpServletRequest request) {
        List<TagsBean> tags = tagsService.getTagsWithoutCategoryId();
        return Response.ok(tags).build();
    }


    @Path("/tag_by_category/{category_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getTagByCategoryId(@Context HttpServletRequest request,
                                       @PathParam("category_id") long categoryId) {
        List<TagsBean> tags = tagsService.getTagsByCategoryId(categoryId);
        return Response.ok(tags).build();
    }

    @Path("/category/{category_ids}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getCategory(@Context HttpServletRequest request,
                           @PathParam("category_ids") String categoryIds) {
        if ("ALL".equalsIgnoreCase(categoryIds)) {
            List<TagsCategoryBean> categories = tagsService.getAllCategory();
            return Response.ok(categories).build();
        }
        else {
            List<TagsCategoryBean> categorys = tagsService.getCategoryByIds(categoryIds);
            return Response.ok(categorys).build();
        }
    }

    @Path("/category_with_tags/{category_ids}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getCategoryWithTags(@Context HttpServletRequest request,
                                        @PathParam("category_ids") String categoriesIds) {
        if ("ALL".equalsIgnoreCase(categoriesIds)) {
            List<TagsCategoryBean> categories = tagsService.getAllCategoryWithTags();
            return Response.ok(categories).build();
        }
        else {
            List<TagsCategoryBean> categories = tagsService.getCategoryWithTagsByIds(categoriesIds);
            return Response.ok(categories).build();
        }
    }

    //=======================================================================
    //    get
    //=======================================================================

}

package com.cooltoo.backend.api;

import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.beans.TagsBean;
import com.cooltoo.beans.TagsCategoryBean;
import com.cooltoo.services.TagsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by zhaolisong on 16/4/8.
 */
@Path("/nurse/tags")
public class NurseTagsServiceAPI {

    private static final Logger logger = LoggerFactory.getLogger(NurseTagsServiceAPI.class);

    @Autowired
    private TagsService tagsService;

    //=======================================================================
    //    get
    //=======================================================================

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getTag(@Context HttpServletRequest request,
                           @QueryParam("tag_ids") String tagIds
    ) {
        logger.info("get tag by ids={}", tagIds);
        List<TagsBean> tags = tagsService.getTagByIds(tagIds);
        return Response.ok(tags).build();
    }

    @Path("/tag_without_category")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getTagWithoutCategoryId(@Context HttpServletRequest request) {
        logger.info("get tag with no category belong");
        List<TagsBean> tags = tagsService.getTagsWithoutCategoryId();
        return Response.ok(tags).build();
    }


    @Path("/tag_by_category/{category_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getTagByCategoryId(@Context HttpServletRequest request,
                                       @PathParam("category_id") long categoryId
    ) {
        List<TagsBean> tags = tagsService.getTagsByCategoryId(categoryId);
        return Response.ok(tags).build();
    }

    @Path("/category")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getCategory(@Context HttpServletRequest request,
                                @QueryParam("category_ids") String categoryIds
    ) {
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
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getCategoryWithTags(@Context HttpServletRequest request,
                                        @QueryParam("category_ids") String categoryIds
    ) {
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
}

package com.cooltoo.admin.api;

import com.cooltoo.beans.BadgeBean;
import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.backend.services.BadgeService;
import com.cooltoo.beans.SpecificSocialAbility;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yzzhao on 2/25/16.
 */
@Path("/admin/badge")
public class BadgeAPI {

    private static final Logger logger = LoggerFactory.getLogger(BadgeAPI.class.getName());

    @Autowired
    private BadgeService badgeService;

    @Path("/ability_type")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getAllAbilityType() {
        Map<String, String> abilityTypes = badgeService.getAllAbilityType();
        logger.info("get social abilities types={}", abilityTypes);
        return Response.ok(abilityTypes).build();
    }

    @Path("/abilities_by_type/{abilityType}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getAbilitiesByAbilityType(@Context HttpServletRequest request,
                                              @PathParam("abilityType") @DefaultValue("") String abilityType
    ) {
        List<SpecificSocialAbility> abilities = badgeService.getItemsOfType(abilityType);
        logger.info("get social abilities by type={}, values={}", abilityType, abilities);
        return Response.ok(abilities).build();
    }

    @Path("/count/{ability_type}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response countBadgeByAbilityType(@Context HttpServletRequest request,
                                            @PathParam("ability_type") String ability_type) {
        long count = badgeService.countByAbilityType(ability_type);
        return Response.ok(count).build();
    }

    @Path("/{ability_type}/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response countBadgeByAbilityType(@Context HttpServletRequest request,
                                            @PathParam("ability_type") @DefaultValue("ALL") String abilityType,
                                            @PathParam("index")        @DefaultValue("0")   int    pageIndex,
                                            @PathParam("number")       @DefaultValue("10")  int    number
    ) {
        List<BadgeBean> badges = badgeService.getBadgeByAbilityType(abilityType, pageIndex, number);
        return Response.ok(badges).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response addBadge(@Context HttpServletRequest request,
                             @FormParam("name")                            String name,
                             @FormParam("description")  @DefaultValue("")  String description,
                             @FormParam("grade")        @DefaultValue("1") int    grade,
                             @FormParam("point")        @DefaultValue("0") int    point,
                             @FormParam("ability_id")   @DefaultValue("0") int    abilityId,
                             @FormParam("ability_type") @DefaultValue("")  String abilityType) {
        BadgeBean badge = badgeService.addBadge(name, description, point, grade, abilityId, abilityType, null, null);
        return Response.ok(badge).build();
    }

    @Path("/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editBadgeWithoutImage(@Context HttpServletRequest request,
                                          @FormParam("id")           @DefaultValue("0") int    badgeId,
                                          @FormParam("name")         @DefaultValue("")  String name,
                                          @FormParam("description")  @DefaultValue("")  String description,
                                          @FormParam("grade")        @DefaultValue("1") int    grade,
                                          @FormParam("point")        @DefaultValue("0") int    point,
                                          @FormParam("ability_id")   @DefaultValue("0") int    abilityId,
                                          @FormParam("ability_type") @DefaultValue("")  String abilityType
    ) {
        BadgeBean badge = badgeService.updateBadge(badgeId, name, description, point, grade, abilityId, abilityType, null, null);
        return Response.ok(badge).build();
    }


    @Path("/edit_image")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editBadgeImage(@Context HttpServletRequest request,
                                   @FormDataParam("id")        @DefaultValue("0") int badgeId,
                                   @FormDataParam("file_name") @DefaultValue("")  String imageName,
                                   @FormDataParam("file") InputStream image,
                                   @FormDataParam("file") FormDataContentDisposition imageDisp
    ) {
        BadgeBean badge = badgeService.updateBadge(badgeId, null, null, -1, -1, -1, null, imageName, image);
        return Response.ok(badge).build();
    }


    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response deleteBadge(@Context HttpServletRequest request,
                                @DefaultValue("-1") @FormParam("id") String ids) {
        String deleteIds = badgeService.deleteBadgeByIds(ids);
        return Response.ok(deleteIds).build();
    }
}

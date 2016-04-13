package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.backend.beans.OccupationSkillBean;
import com.cooltoo.backend.converter.OccupationSkillBeanConverter;
import com.cooltoo.backend.entities.OccupationSkillEntity;
import com.cooltoo.backend.services.OccupationSkillService;
import com.cooltoo.constants.OccupationSkillStatus;
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
import java.util.List;

/**
 * Created by zhaolisong on 16/3/25.
 */
@Path("/admin/occupation")
public class OccupationSkillManageAPI {

    private static final Logger logger = LoggerFactory.getLogger(OccupationSkillManageAPI.class.getName());

    @Autowired
    private OccupationSkillService skillService;
    @Autowired
    private OccupationSkillBeanConverter beanConverter;

    @Path("/count/{status}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getSkillCount(@Context HttpServletRequest request,
                                  @PathParam("status") String status
    ) {
        logger.info("get all occupation skill count");
        long count = skillService.getAllSkillCount(status);
        logger.info("get all occupation skill count {}", count);
        return Response.ok(count).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getOccupationSkillList() {
        logger.info("get all occupation skills");
        List<OccupationSkillBean> allSkills = skillService.getAllSkill();
        logger.info(allSkills.toString());
        return Response.ok(allSkills).build();
    }

    @Path("/{status}/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getSkillByStatus(@Context HttpServletRequest request,
                                     @PathParam("status") String status,
                                     @PathParam("index") int pageIndex,
                                     @PathParam("number") int number
    ) {
        logger.info("get all occupation skill by status={} page={} number/page={}", status, pageIndex, number);
        List<OccupationSkillBean> allSkills = skillService.getSkillByStatus(status, pageIndex, number);
        logger.info(allSkills.toString());
        return Response.ok(allSkills).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response addOccupationSkill(@FormParam("name") String name,
                                       @FormParam("factor") int factor) {
        logger.info("add new occupation skill parameters is ==== name={}, factor={}, image={}, disableImage={}.", name, factor, null, null);
        OccupationSkillBean skill = skillService.addNewOccupationSkill(name, factor, null, null);
        logger.info("add new occupation skill is " + skill.toString());
        return Response.ok(skill).build();
    }

//    @DELETE
//    @AdminUserLoginAuthentication(requireUserLogin = true)
//    public Response deleteOccupationSkill(@FormParam("id") int id) {
//        skillService.deleteOccupationSkill(id);
//        return Response.ok().build();
//    }

    @POST
    @Path("/edit")
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editOccupationSkill2(@FormParam("id") int id,
                                         @FormParam("name") String name,
                                         @FormParam("factor") int factor,
                                         @FormParam("status") String status
    ) {
        OccupationSkillBean bean = skillService.editOccupationSkill(id, name, factor, status, null, null);
        return Response.ok(bean).build();
    }

    @POST
    @Path("/edit_without_image")
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editOccupationSkill(@FormParam("id") int id,
                                        @FormParam("name") String name,
                                        @FormParam("factor") int factor,
                                        @FormParam("status") String status
    ) {
        OccupationSkillEntity skill = skillService.editOccupationSkillWithoutImage(id, name, factor, status);
        OccupationSkillBean   bean  = beanConverter.convert(skill);
        return Response.ok(bean).build();
    }

    @POST
    @Path("/edit_image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editOccupationSkillEnableImage(@FormDataParam("id") int id,
                                                   @FormDataParam("file") InputStream image) {
        OccupationSkillBean bean = skillService.editOccupationSkill(id, null, -1, null, image, null);
        return Response.ok(bean).build();
    }

    @POST
    @Path("/edit_disable_image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editOccupationSkillDisableImage(@FormDataParam("id") int id,
                                                    @FormDataParam("file") InputStream image) {
        OccupationSkillBean bean = skillService.editOccupationSkill(id, null, -1, null, null, image);
        return Response.ok(bean).build();
    }
}

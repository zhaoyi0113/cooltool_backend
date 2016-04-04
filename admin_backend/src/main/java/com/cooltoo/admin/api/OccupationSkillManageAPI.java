package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.backend.beans.OccupationSkillBean;
import com.cooltoo.backend.services.OccupationSkillService;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
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


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getOccupationSkillList() {
        logger.info("get all occupation skills");
        List<OccupationSkillBean> allSkills = skillService.getOccupationSkillList();
        logger.info(allSkills.toString());
        return Response.ok(allSkills).build();
    }

    @GET
    @Path("/types")
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getOccupationSkillTypes() {
        return Response.ok(skillService.getAllSkillTypes()).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response addOccupationSkill(
            @FormDataParam("name") String name,
            @FormDataParam("type") String type,
            @FormDataParam("factor") int factor,
            @FormDataParam("file") InputStream imageStream,
            @FormDataParam("disable_file") InputStream disableImageStream) {

        logger.info("add new occupation skill parameters is ==== name={}, type={}, factor={}, image={}, disableImage={}.", name, type, factor, null!=imageStream, null!=disableImageStream);
        OccupationSkillBean skill = skillService.addNewOccupationSkill(name, type, factor, imageStream, disableImageStream);
        logger.info("add new occupation skill is " + skill.toString());
        return Response.ok(skill).build();
    }

    @DELETE
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response deleteOccupationSkill(@FormParam("id") int id) {
        skillService.deleteOccupationSkill(id);
        return Response.ok().build();
    }

    @POST
    @Path("/edit")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editOccupationSkill(@FormDataParam("id") int id,
                                        @FormDataParam("type") String type,
                                        @FormDataParam("name") String name,
                                        @FormDataParam("factor") int factor,
                                        @FormDataParam("file") InputStream imageStream,
                                        @FormDataParam("disable_file") InputStream disableImageStream) {
        skillService.editOccupationSkill(id, name, type, factor, imageStream, disableImageStream);
        return Response.ok().build();
    }

    @POST
    @Path("/edit_without_image")
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editOccupationSkill(@FormParam("id") int id,
                                        @FormParam("name") String name,
                                        @FormParam("type") String type,
                                        @FormParam("factor") int factor
    ) {
        skillService.editOccupationSkillWithoutImage(id, name, type, factor);
        return Response.ok().build();
    }

    @POST
    @Path("/edit_image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editOccupationSkillEnableImage(@FormDataParam("id") int id,
                                                   @FormDataParam("file") InputStream image) {
        skillService.editOccupationSkill(id, null, null, -1, image, null);
        return Response.ok().build();
    }

    @POST
    @Path("/edit_disable_image")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response editOccupationSkillDisableImage(@FormDataParam("id") int id,
                                                    @FormDataParam("file") InputStream image) {
        skillService.editOccupationSkill(id, null, null, -1, null, image);
        return Response.ok().build();
    }
}

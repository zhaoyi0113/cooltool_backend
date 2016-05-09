package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.beans.OfficialConfigBean;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.services.OfficialConfigService;
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
 * Created by zhaolisong on 16/5/10.
 */
@Path("/admin/official_config")
public class OfficialConfigAPI {

    private static final Logger logger = LoggerFactory.getLogger(OfficialConfigAPI.class.getName());

    @Autowired
    private OfficialConfigService configService;

    @Path("/config_keys")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getConfigKeys() {
        List<String> configKeys = configService.getKeys();
        return Response.ok(configKeys).build();
    }

    @Path("/count/{status}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getCountByStatus(@Context HttpServletRequest request,
                                     @PathParam("status") @DefaultValue("") String status
    ) {
        long count = configService.countConfig(status);
        return Response.ok(count).build();
    }

    @Path("/{status}/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getConfigByStatus(@Context HttpServletRequest request,
                                      @PathParam("status") @DefaultValue("") String status,
                                      @PathParam("index") @DefaultValue("0") int index,
                                      @PathParam("number") @DefaultValue("10") int number
    ) {
        List<OfficialConfigBean> configs = configService.getConfig(status, index, number);
        return Response.ok(configs).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response deleteByIds(@Context HttpServletRequest request,
                                @FormParam("config_ids") @DefaultValue("") String configIds
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} delete config by ids={}", userId, configIds);
        configService.deleteByIds(configIds);
        return Response.ok().build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response addConfig(@Context HttpServletRequest request,
                              @FormParam("name") @DefaultValue("") String name,
                              @FormParam("value") @DefaultValue("") String value,
                              @FormParam("status") @DefaultValue("disabled") String status
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} add config by name={} value={} status={}", userId, name, value, status);
        OfficialConfigBean config = configService.addConfig(name, value, status, null, null);
        return Response.ok(config).build();
    }

    @Path("/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateConfig(@Context HttpServletRequest request,
                                 @FormParam("config_id") @DefaultValue("0") int configId,
                                 @FormParam("name") @DefaultValue("") String name,
                                 @FormParam("value") @DefaultValue("") String value,
                                 @FormParam("status") @DefaultValue("disabled") String status
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} update config by id={} name={} value={} status={}", userId, configId, name, value, status);
        OfficialConfigBean config = configService.updateConfig(configId, name, value, status, null, null);
        return Response.ok(config).build();
    }

    @Path("/edit_image")
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateConfigImage(@Context HttpServletRequest request,
                                      @FormDataParam("config_id") @DefaultValue("0") int configId,
                                      @FormDataParam("image_name") @DefaultValue("") String imageName,
                                      @FormDataParam("image") InputStream image,
                                      @FormDataParam("image")FormDataContentDisposition dispos
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} update config by id={} imageName={} disp={}", userId, configId, image, dispos);
        OfficialConfigBean config = configService.updateConfig(configId, null, null, null, imageName, image);
        return Response.ok(config).build();
    }

}

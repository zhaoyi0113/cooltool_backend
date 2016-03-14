package com.cooltoo.admin.api;

import com.cooltoo.admin.beans.BadgeBean;
import com.cooltoo.admin.services.BadgeService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by yzzhao on 2/25/16.
 */
@Path("/badge")
public class BadgeAPI {

    private static final Logger logger = Logger.getLogger(BadgeAPI.class.getName());

    @Autowired
    private BadgeService badgeService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBadges() {
        List<BadgeBean> badges = badgeService.getAllBadge();
        return Response.ok(badges).build();
    }

    @Path("/new")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createBadge(
                                @FormDataParam("name") String name,
                                @DefaultValue("0") @FormDataParam("grade") int grade,
                                @DefaultValue("0") @FormDataParam("point") int point,
                                @FormDataParam("file") InputStream fileInputStream,
                                @FormDataParam("file") FormDataContentDisposition disposition) {
        int id = badgeService.createNewBadge(name, point, grade, fileInputStream,
                                                null != disposition ? disposition.getFileName() : null);
        logger.info("new badge id="+id+" name="+name +" grade="+grade +" point="+point+
                    "filename="+(null==disposition ? "null" : disposition.getFileName()));
        return Response.ok(id).build();
    }

    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBadge(
                                @DefaultValue("-1") @FormDataParam("id") int id) {
        BadgeBean bean = badgeService.deleteBadge(id);
        logger.info("delete badge is " + bean);
        return Response.ok(bean).build();
    }

    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBadge(
                                @DefaultValue("-1") @FormDataParam("id") int id,
                                @FormDataParam("name") String name,
                                @DefaultValue("0") @FormDataParam("grade") int grade,
                                @DefaultValue("0") @FormDataParam("point") int point,
                                @FormDataParam("file") InputStream fileInputStream,
                                @FormDataParam("file") FormDataContentDisposition disposition) {
        BadgeBean bean = badgeService.updateBadge(id, name, grade, point, fileInputStream,
                                                    null != disposition ? disposition.getFileName() : null);
        logger.info("update badge is " + bean);
        return Response.ok(bean).build();
    }
}

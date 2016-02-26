package com.cooltoo.api;

import com.cooltoo.beans.BadgeBean;
import com.cooltoo.serivces.BadgeService;
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

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createNewBadge(@FormDataParam("name") String name,
                                   @DefaultValue("0") @FormDataParam("grade") int grade,
                                   @DefaultValue("0") @FormDataParam("point") int point,
                                   @FormDataParam("file") InputStream fileInputStream,
                                   @FormDataParam("file") FormDataContentDisposition disposition) {
        logger.info("create new badge " + name + ", grade=" + grade + ",point=" + point);
        badgeService.createNewBadge(name, point, grade, fileInputStream, disposition.getFileName());
        return Response.ok().build();
    }

}

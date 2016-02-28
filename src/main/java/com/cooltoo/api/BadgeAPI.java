package com.cooltoo.api;

import com.cooltoo.beans.BadgeBean;
import com.cooltoo.serivces.BadgeService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.ObjectFactory;
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
    public Response crudBadge(
                                    @DefaultValue("null") @FormDataParam("type") String type,
                                    @DefaultValue("0") @FormDataParam("id") int id,
                                    @FormDataParam("name") String name,
                                    @DefaultValue("0") @FormDataParam("grade") int grade,
                                    @DefaultValue("0") @FormDataParam("point") int point,
                                    @FormDataParam("file") InputStream fileInputStream,
                                    @FormDataParam("file") FormDataContentDisposition disposition) {
        Object result = null;
        logger.info("post type:" + type + " id="+id +" name="+name +" grade="+grade +" point="+point);
        if (type instanceof String) {
            if ("delete".equalsIgnoreCase(type)) {
                result = deleteBadge(id);
            }
            else if ("new".equalsIgnoreCase(type)) {
                result = (Integer)createBadge(name, point, grade, fileInputStream, disposition);
            }
            else if ("update".equalsIgnoreCase(type)) {
                result = updateBadge(id,name, point, grade, fileInputStream, disposition);
            }
        }

        Response.ResponseBuilder response = null;
        if (null != result) {
            response = Response.ok(result);
        }
        else {
            response = Response.ok();
        }
        return response.build();
    }

    private Integer createBadge(String name, int grade, int point, InputStream fileInputStream, FormDataContentDisposition disposition) {
        int id = badgeService.createNewBadge(name, point, grade, fileInputStream, null != disposition ? disposition.getFileName() : null);
        return id;
    }

    private BadgeBean deleteBadge(int id) {
        BadgeBean bean = badgeService.getBadgeById(id);
        logger.info(null==bean?"null":bean.toString());
        bean = badgeService.deleteBadge(id);
        return bean;
    }


    private BadgeBean updateBadge(int id, String name, int grade, int point, InputStream fileInputStream, FormDataContentDisposition disposition) {
        BadgeBean bean = badgeService.getBadgeById(id);
        logger.info(null==bean?"null":bean.toString());
        bean = badgeService.updateBadge(id, name, grade, point, fileInputStream, null != disposition ? disposition.getFileName() : null);
        return bean;
    }
}

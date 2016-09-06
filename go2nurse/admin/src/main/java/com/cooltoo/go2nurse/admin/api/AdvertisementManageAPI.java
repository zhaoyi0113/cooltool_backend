package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.go2nurse.beans.AdvertisementBean;
import com.cooltoo.go2nurse.service.AdvertisementService;
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

/**
 * Created by hp on 2016/9/6.
 */
@Path("/admin/advertisement")
public class AdvertisementManageAPI {

    @Autowired private AdvertisementService advertisementService;

    // status ==> all/enabled/disabled/deleted
    @Path("/count/{status}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countAdvertisement(@Context HttpServletRequest request,
                                       @PathParam("status") String status
    ) {
        long count = advertisementService.countAdvertisementByStatus(status);
        return Response.ok(count).build();
    }


    // status ==> all/enabled/disabled/deleted
    @Path("/{status}/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAdvertisementByStatus(@Context HttpServletRequest request,
                                        @PathParam("status") String status,
                                        @PathParam("index")  @DefaultValue("0") int index,
                                        @PathParam("number") @DefaultValue("10") int number
    ) {
        List<AdvertisementBean> activities = advertisementService.getAdvertisementByStatus(status, index, number);
        return Response.ok(activities).build();
    }


    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAdvertisementByIds(@Context HttpServletRequest request,
                                        @FormParam("ids") String ids
    ) {
        String deleteIds = advertisementService.deleteByIds(ids);
        return Response.ok(deleteIds).build();
    }


    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAdvertisement(@Context HttpServletRequest request,
                                   @FormDataParam("image_name") String imageName,
                                   @FormDataParam("image") InputStream image,
                                   @FormDataParam("description") String description,
                                   @FormDataParam("details_url") String detailsUrl

    ) {
        long advertisementId = advertisementService.createAdvertisement(imageName, image, description, detailsUrl);
        return Response.ok(advertisementId).build();
    }

    @Path("/edit")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAdvertisement(@Context HttpServletRequest request,
                                        @FormParam("advertisement_id") long advertisementId,
                                        @FormParam("description") String description,
                                        @FormParam("details_url") String detailsUrl,
                                        @FormParam("status") @DefaultValue("") String status
    ) {
        AdvertisementBean advertisement = advertisementService.updateAdvertisement(advertisementId, description, detailsUrl, status, null, null);
        return Response.ok(advertisement).build();
    }

    @Path("/edit/front_cover")
    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAdvertisementFrontCover(@Context HttpServletRequest request,
                                             @FormDataParam("advertisement_id") long advertisementId,
                                             @FormDataParam("image_name") String imageName,
                                             @FormDataParam("image") InputStream image,
                                             @FormDataParam("image") FormDataContentDisposition disp

    ) {
        AdvertisementBean frontCover = advertisementService.updateAdvertisement(advertisementId, null, null, null, imageName, image);
        return Response.ok(frontCover).build();
    }
}

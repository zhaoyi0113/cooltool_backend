package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.go2nurse.beans.AdvertisementBean;
import com.cooltoo.go2nurse.service.AdvertisementService;
import com.cooltoo.util.VerifyUtil;
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

    @Path("/{advertisement_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countAdvertisement(@Context HttpServletRequest request,
                                       @PathParam("advertisement_id") @DefaultValue("0") Long advertisementId
    ) {
        List<AdvertisementBean> activities = advertisementService.getAdvertisementByIds(advertisementId+"");
        AdvertisementBean one = VerifyUtil.isListEmpty(activities) ? null: activities.get(0);
        return Response.ok(one).build();
    }

    // status ==> all/enabled/disabled/deleted
    // type ==> consultation/appointment
    @Path("/count/{status}/{type}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countAdvertisement(@Context HttpServletRequest request,
                                       @PathParam("status") String status,
                                       @PathParam("type") String type
    ) {
        long count = advertisementService.countAdvertisementByStatus(status, type);
        return Response.ok(count).build();
    }


    // status ==> all/enabled/disabled/deleted
    // type ==> consultation/appointment
    @Path("/{status}/{type}/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAdvertisementByStatus(@Context HttpServletRequest request,
                                        @PathParam("status") String status,
                                        @PathParam("type") String type,
                                        @PathParam("index")  @DefaultValue("0") int index,
                                        @PathParam("number") @DefaultValue("10") int number
    ) {
        List<AdvertisementBean> activities = advertisementService.getAdvertisementByStatusAndType(status, type, index, number);
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
    @Produces(MediaType.APPLICATION_JSON)
    public Response createAdvertisement(@Context HttpServletRequest request,
                                        @FormParam("description") String description,
                                        @FormParam("details_url") String detailsUrl,
                                        @FormParam("type") String type

    ) {
        long advertisementId = advertisementService.createAdvertisement(description, detailsUrl, type);
        return Response.ok(advertisementId).build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAdvertisement(@Context HttpServletRequest request,
                                        @FormParam("advertisement_id") long advertisementId,
                                        @FormParam("description") String description,
                                        @FormParam("details_url") String detailsUrl,
                                        @FormParam("status") @DefaultValue("") String status,
                                        @FormParam("type") String type
    ) {
        AdvertisementBean advertisement = advertisementService.updateAdvertisement(advertisementId, description, detailsUrl, status, type, null, null);
        return Response.ok(advertisement).build();
    }

    @Path("/front_cover")
    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateAdvertisementFrontCover(@Context HttpServletRequest request,
                                                  @FormDataParam("advertisement_id") long advertisementId,
                                                  @FormDataParam("image_name") String imageName,
                                                  @FormDataParam("image") InputStream image,
                                                  @FormDataParam("image") FormDataContentDisposition disp

    ) {
        AdvertisementBean frontCover = advertisementService.updateAdvertisement(advertisementId, null, null, null, null, imageName, image);
        return Response.ok(frontCover).build();
    }

    @Path("/order")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response changeTwoAdvertisementOrder(@Context HttpServletRequest request,
                                                @FormParam("first_ad_id") long _1stId,
                                                @FormParam("first_ad_order") long _1stOrder,
                                                @FormParam("second_ad_id") long _2ndId,
                                                @FormParam("second_ad_order") long _2ndOrder
    ) {
        advertisementService.changeTwoAdvertisementOrder(_1stId, _1stOrder, _2ndId, _2ndOrder);
        return Response.ok().build();
    }
}

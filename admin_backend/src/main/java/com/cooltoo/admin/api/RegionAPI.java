package com.cooltoo.admin.api;

import com.cooltoo.beans.RegionBean;
import com.cooltoo.services.RegionService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by zhaolisong on 16/3/29.
 */
@Path("/admin/region")
public class RegionAPI {

    @Autowired
    private RegionService regionService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getProvince() {
        List<RegionBean> provinces = regionService.getProvince();
        return Response.ok(provinces).build();
    }

    @Path("/{parent_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubRegion(
            @PathParam("parent_id") @DefaultValue("-1") int parentId
    ) {
        List<RegionBean> subRegion = regionService.getSubRegion(parentId);
        return Response.ok(subRegion).build();
    }
}

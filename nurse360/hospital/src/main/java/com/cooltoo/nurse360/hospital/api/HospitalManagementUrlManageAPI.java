package com.cooltoo.nurse360.hospital.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.constants.RequestMethod;
import com.cooltoo.nurse360.beans.HospitalAdminAccessUrlBean;
import com.cooltoo.nurse360.beans.HospitalAdminBean;
import com.cooltoo.nurse360.beans.HospitalManagementUrlBean;
import com.cooltoo.nurse360.hospital.service.HospitalAdminAccessUrlService;
import com.cooltoo.nurse360.hospital.service.HospitalManagementUrlService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/10.
 */
@Path("/admin/hospital_management/url")
public class HospitalManagementUrlManageAPI {

    @Autowired private HospitalManagementUrlService urlService;
    @Autowired private HospitalAdminAccessUrlService accessUrlService;

    @Path("/method")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHttpTypes(@Context HttpServletRequest request) {
        return Response.ok(RequestMethod.getAll()).build();
    }

    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countHospitalHttpUrl(@Context HttpServletRequest request,
                                         @QueryParam("http_url") @DefaultValue("") String httpUrl,
                                         @QueryParam("http_type") @DefaultValue("") String strHttpType, /* GET, POST, PUT, DELETE, HEAD, OPTIONS */
                                         @QueryParam("status") @DefaultValue("") String strStatus /* enabled, disabled */
    ) {
        RequestMethod httpType = RequestMethod.parseString(strHttpType);
        CommonStatus status = CommonStatus.parseString(strStatus);
        long count = urlService.countHospitalMngUrl(httpType, httpUrl, status);
        return Response.ok(count).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countHospitalHttpUrl(@Context HttpServletRequest request,
                                         @QueryParam("http_url") @DefaultValue("") String httpUrl,
                                         @QueryParam("http_type") @DefaultValue("") String strHttpType, /* GET, POST, PUT, DELETE, HEAD, OPTIONS */
                                         @QueryParam("status") @DefaultValue("") String strStatus, /* enabled, disabled */
                                         @QueryParam("index") @DefaultValue("0") int pageIndex,
                                         @QueryParam("number") @DefaultValue("0") int sizePerPage
    ) {
        RequestMethod httpType = RequestMethod.parseString(strHttpType);
        CommonStatus status = CommonStatus.parseString(strStatus);
        List<HospitalManagementUrlBean> urls = urlService.getHospitalMngUrl(httpType, httpUrl, status, pageIndex, sizePerPage);
        return Response.ok(urls).build();
    }

    @Path("/{url_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHospitalHttpUrl(@Context HttpServletRequest request,
                                       @PathParam("url_id") @DefaultValue("0") long urlId
    ) {
        HospitalManagementUrlBean bean = urlService.getHospitalMngUrl(urlId);
        List<HospitalAdminAccessUrlBean> accessUsers = accessUrlService.getAdminMngUrlByUrlId(urlId);
        bean.setProperties(HospitalManagementUrlBean.ACCESS_USERs, getAdmins(accessUsers));
        return Response.ok(bean).build();
    }

    @Path("/status")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateHospitalHttpUrl(@Context HttpServletRequest request,
                                          @FormParam("url_id") @DefaultValue("0") long urlId,
                                          @FormParam("status") @DefaultValue("") String strStatus /* enabled, disabled */
    ) {
        urlId = urlService.updateHospitalMngUrl(urlId, null, CommonStatus.parseString(strStatus));
        return Response.ok(urlId).build();
    }

    //===================================================================================================================
    //            URL Service
    //===================================================================================================================

    @Path("/user")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAdminAccessHttpUrlRelation(@Context HttpServletRequest request,
                                                     @FormParam("admin_id") @DefaultValue("0") long adminId,
                                                     @FormParam("url_id") @DefaultValue("0") long urlId
    ) {
        List<Long> accessUrlIds = accessUrlService.deleteAdminMngUrl(adminId, urlId);
        return Response.ok(accessUrlIds).build();
    }

    public List getAdmins(List<HospitalAdminAccessUrlBean> accessUsers) {
        List<HospitalAdminBean> admins = new ArrayList<>();
        for (HospitalAdminAccessUrlBean tmp : accessUsers) {
            admins.add(tmp.getAdmin());
        }
        return admins;
    }
}

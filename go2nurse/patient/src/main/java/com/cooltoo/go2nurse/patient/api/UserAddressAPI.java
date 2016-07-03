package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.beans.RegionBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.UserAddressBean;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.UserAddressService;
import com.cooltoo.services.RegionService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by hp on 2016/7/2.
 */
@Path("/user/address")
public class UserAddressAPI {

    @Autowired private UserAddressService userAddressService;
    @Autowired private RegionService regionService;

    @Path("/province")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProvince(@Context HttpServletRequest request) {
        List<RegionBean> provinces = regionService.getProvince();
        return Response.ok(provinces).build();
    }

    @Path("/sub_region/{region_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSubRegion(@Context HttpServletRequest request,
                                 @PathParam("region_id") @DefaultValue("0") int regionId
    ) {
        List<RegionBean> subRegion = regionService.getSubRegion(regionId);
        return Response.ok(subRegion).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getUserAddress(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<UserAddressBean> addresses = userAddressService.getUserAddress(userId);
        return Response.ok(addresses).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response addAddress(@Context HttpServletRequest request,
                               @FormParam("province_id") @DefaultValue("-1") int provinceId,
                               @FormParam("city_id") @DefaultValue("-1") int cityId,
                               @FormParam("address") @DefaultValue("") String address,
                               @FormParam("grade") @DefaultValue("-1") int grade
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserAddressBean userAddress = userAddressService.createAddress(userId, provinceId, cityId, grade, address);
        return Response.ok(userAddress).build();
    }

    @Path("/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response updateAddress(@Context HttpServletRequest request,
                                  @FormParam("address_id") @DefaultValue("0") long addressId,
                                  @FormParam("province_id") @DefaultValue("0") int provinceId,
                                  @FormParam("city_id") @DefaultValue("0") int cityId,
                                  @FormParam("address") @DefaultValue("") String address,
                                  @FormParam("grade") @DefaultValue("0") int grade,
                                  @FormParam("status") @DefaultValue("") String status
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserAddressBean userAddress = userAddressService.update(addressId, provinceId, cityId, grade, address, CommonStatus.parseString(status));
        return Response.ok(userAddress).build();
    }

    @Path("/delete")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response deleteAddress(@Context HttpServletRequest request,
                                  @FormParam("address_id") @DefaultValue("0") long addressId
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserAddressBean userAddress = userAddressService.deleteById(addressId);
        return Response.ok(userAddress).build();
    }
}

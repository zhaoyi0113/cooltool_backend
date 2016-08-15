package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.backend.services.HospitalService;
import com.cooltoo.beans.HospitalBean;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Path("/admin/hospital")
public class HospitalAPI {

    private static final Logger logger = LoggerFactory.getLogger(HospitalAPI.class.getName());

    @Autowired
    private HospitalService service;

    @Path("/search/count")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response countSearchHospital(@DefaultValue("true") @FormParam("and_or") boolean andOrOr,
                                        @DefaultValue("") @FormParam("name") String name,
                                        @DefaultValue("") @FormParam("province") String province,
                                        @DefaultValue("") @FormParam("city") String city,
                                        @DefaultValue("") @FormParam("district") String district,
                                        @DefaultValue("") @FormParam("address") String address,
                                        @DefaultValue("") @FormParam("status") String status,
                                        @DefaultValue("") @FormParam("support_go2nurse") String support
    ) {
        Integer iProvince = !VerifyUtil.isIds(province) ? null : VerifyUtil.parseIntIds(province).get(0);
        Integer iCity = !VerifyUtil.isIds(city) ? null : VerifyUtil.parseIntIds(city).get(0);
        Integer iDistrict = !VerifyUtil.isIds(district) ? null : VerifyUtil.parseIntIds(district).get(0);
        Integer iStatus = !VerifyUtil.isIds(status) ? null : VerifyUtil.parseIntIds(status).get(0);
        Integer iSupport = !VerifyUtil.isIds(support) ? null : VerifyUtil.parseIntIds(support).get(0);
        long count = service.countHospitalByConditions(andOrOr, name, iProvince, iCity, iDistrict, address, iStatus, iSupport);
        return Response.ok(count).build();
    }

    @Path("/search")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response searchHospital(@DefaultValue("true") @FormParam("and_or") boolean andOrOr,
                                   @DefaultValue("") @FormParam("name") String name,
                                   @DefaultValue("") @FormParam("province") String province,
                                   @DefaultValue("") @FormParam("city") String city,
                                   @DefaultValue("") @FormParam("district") String district,
                                   @DefaultValue("") @FormParam("address") String address,
                                   @DefaultValue("") @FormParam("status") String status,
                                   @DefaultValue("") @FormParam("support_go2nurse") String support,
                                   @DefaultValue("0")    @FormParam("index")    int     index,
                                   @DefaultValue("10")   @FormParam("number")   int     number) {
        Integer iProvince = !VerifyUtil.isIds(province) ? null : VerifyUtil.parseIntIds(province).get(0);
        Integer iCity = !VerifyUtil.isIds(city) ? null : VerifyUtil.parseIntIds(city).get(0);
        Integer iDistrict = !VerifyUtil.isIds(district) ? null : VerifyUtil.parseIntIds(district).get(0);
        Integer iStatus = !VerifyUtil.isIds(status) ? null : VerifyUtil.parseIntIds(status).get(0);
        Integer iSupport = !VerifyUtil.isIds(support) ? null : VerifyUtil.parseIntIds(support).get(0);
        List<HospitalBean> hospitals = service.searchHospitalByConditions(andOrOr, name, iProvince, iCity, iDistrict, address, iStatus, iSupport, index, number);
        logger.info("get hospital size is {}", hospitals.size());
        return Response.ok(hospitals).build();
    }


    //===============================================================
    //                         deleting
    //===============================================================
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response deleteByIds(@FormParam("id") String ids) {
        List<HospitalBean> ones = service.deleteByIds(ids);
        logger.info("delete hospital is " + ones);
        if (null==ones || ones.isEmpty()) {
            return Response.ok().build();
        }
        if (ones.size()==1) {
            return Response.ok(ones.get(0)).build();
        }
        return Response.ok(ones).build();
    }

    //===============================================================
    //                         editing
    //===============================================================
    @Path("/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response update(
            @DefaultValue("-1") @FormParam("id") int id,
            @FormParam("name") String name,
            @DefaultValue("") @FormParam("alias_name") String aliasName,
            @DefaultValue("-1") @FormParam("province") int province,
            @DefaultValue("-1") @FormParam("city") int city,
            @DefaultValue("-1") @FormParam("district") int district,
            @DefaultValue("") @FormParam("address") String address,
            @DefaultValue("-1") @FormParam("enable") int enable,
            @DefaultValue("-1")   @FormParam("support_go2nurse") int support) {
        HospitalBean one = service.update(id, name, aliasName, province, city, district, address, enable, support);
        logger.info("update hospital is " + one);
        if (null==one) {
            return Response.ok().build();
        }
        return Response.ok(one).build();
    }

    //===============================================================
    //                         adding
    //===============================================================
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response addHospital(
            @FormParam("name") String name,
            @DefaultValue("") @FormParam("alias_name") String aliasName,
            @DefaultValue("-1") @FormParam("province") int province,
            @DefaultValue("-1") @FormParam("city") int city,
            @DefaultValue("-1") @FormParam("district") int district,
            @DefaultValue("") @FormParam("address") String address,
            @DefaultValue("-1") @FormParam("enable") int enable,
            @DefaultValue("-1")   @FormParam("support_go2nurse") int support
    ) {
        int id = service.newOne(name, aliasName, province, city, district, address, enable, support);
        logger.info("new hospital id is " + id);
        return Response.ok(id).build();
    }
}

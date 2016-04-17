package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.backend.services.HospitalService;
import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getAll() {
        List<HospitalBean> all = service.getAll();
        return Response.ok(all).build();
    }

    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getAllCount(@Context HttpServletRequest request) {
        logger.info("get all hospital count");
        long count = service.getHospitalSize();
        logger.info("get all hospital count is {}", count);
        return Response.ok(count).build();
    }

    @Path("/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getAllByPage(@Context HttpServletRequest request,
                                 @PathParam("index")  @DefaultValue("0")  int index,
                                 @PathParam("number") @DefaultValue("10") int number
    ) {
        logger.info("get hospital at page {} numberOfPage {}", index, number);
        List<HospitalBean> page = service.getAllByPage(index, number);
        logger.info("get hospital at page {} numberOfPage {}, value is {}", index, number, page);
        return Response.ok(page).build();
    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getOneById(@DefaultValue("-1") @PathParam("id") int id) {
        HospitalBean bean = service.getOneById(id);
        logger.info("get hospital is " + bean);
        if (null == bean) {
            Response.ok().build();
        }
        return Response.ok(bean).build();
    }

    @Path("/search/count")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response countByConditions(@Context HttpServletRequest request,
                                      @QueryParam("conditions") String conditions) {
        logger.info("output information: {}", conditions);
        return Response.ok().build();
    }

    @Path("/search")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response searchHospital(@DefaultValue("true") @FormParam("and_or")  boolean andOrOr,
                                   @DefaultValue("")     @FormParam("name")     String  name,
                                   @DefaultValue("-1")   @FormParam("province") int     province,
                                   @DefaultValue("-1")   @FormParam("city")     int     city,
                                   @DefaultValue("-1")   @FormParam("district") int     district,
                                   @DefaultValue("")     @FormParam("address")  String  address,
                                   @DefaultValue("0")    @FormParam("index")    int     index,
                                   @DefaultValue("10")   @FormParam("number")   int     number) {
        List<HospitalBean> hospitals = service.searchHospital(andOrOr, name, province, city, district, address);
        logger.info("get hospital is " + hospitals);
        if (null == hospitals) {
            Response.ok().build();
        }
        return Response.ok(hospitals).build();
    }

    @Path("/department")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getDepartments(
            @FormParam("id") @DefaultValue("-1") int id
    ) {
        List<HospitalDepartmentBean> all = service.getAllDepartments(id);
        return Response.ok(all).build();
    }

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

    @Path("/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response update(
            @DefaultValue("-1") @FormParam("id") int id,
            @FormParam("name") String name,
            @DefaultValue("-1") @FormParam("province") int province,
            @DefaultValue("-1") @FormParam("city") int city,
            @DefaultValue("-1") @FormParam("district") int district,
            @DefaultValue("") @FormParam("address") String address,
            @DefaultValue("-1") @FormParam("enable") int enable) {
        HospitalBean one = service.update(id, name, province, city, district, address, enable);
        logger.info("update hospital is " + one);
        if (null==one) {
            return Response.ok().build();
        }
        return Response.ok(one).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response addHospital(
            @FormParam("name") String name,
            @DefaultValue("-1") @FormParam("province") int province,
            @DefaultValue("-1") @FormParam("city") int city,
            @DefaultValue("-1") @FormParam("district") int district,
            @DefaultValue("") @FormParam("address") String address,
            @DefaultValue("-1") @FormParam("enable") int enable
    ) {
        int id = service.newOne(name, province, city, district, address, enable);
        logger.info("new hospital id is " + id);
        return Response.ok(id).build();
    }

    @Path("/set_department")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response setRelation(
            @DefaultValue("-1") @FormParam("id") int id,
            @DefaultValue("-1") @FormParam("department_id") int departmentId
    ) {
        int ret = service.setHospitalAndDepartmentRelation(id, departmentId);
        logger.info("set department to hospital is " + ret);
        return Response.ok().build();
    }
}

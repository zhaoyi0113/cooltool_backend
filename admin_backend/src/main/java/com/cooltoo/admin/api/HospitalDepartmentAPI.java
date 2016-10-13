package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.services.CommonDepartmentService;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by lg380357 on 2016/3/5.
 */
@Path("/admin/hospital_department")
public class HospitalDepartmentAPI {

    private static final Logger logger = LoggerFactory.getLogger(HospitalDepartmentAPI.class.getName());

    @Autowired
    private CommonDepartmentService service;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getAll(@DefaultValue("0") @QueryParam("hospital_id") int hospitalId) {
        List<HospitalDepartmentBean> all = service.getByHospitalId(hospitalId, "");
        return Response.ok(all).build();
    }

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getOneById(@DefaultValue("-1") @PathParam("id") int id) {
        HospitalDepartmentBean bean = service.getById(id, "");
        logger.info("get hospital department is " + bean);
        if (null == bean) {
            Response.ok().build();
        }
        return Response.ok(bean).build();
    }

    @Path("/top_level")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getTopLevelDepartment(@DefaultValue("0") @QueryParam("hospital_id") int hospitalId) {
        List<HospitalDepartmentBean> topLevels = service.getTopLevel(hospitalId, false, 0, "");
        logger.info("get all top level hospital " + topLevels);
        return Response.ok(topLevels).build();
    }

    @Path("/second_level")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getSecondLevelDepartment(@DefaultValue("0") @QueryParam("hospital_id") int hospitalId,
                                             @DefaultValue("0") @QueryParam("parent_id") int parentId
    ) {
        List<HospitalDepartmentBean> topLevels = service.getByParentId(hospitalId, parentId, false, 0, "");
        logger.info("get second level hospital " + topLevels);
        return Response.ok(topLevels).build();
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response deleteByIds(@FormParam("id") String ids) {
        List<HospitalDepartmentBean> ones = service.deleteByIds(ids);
        logger.info("delete hospital department is " + ones);
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
    public Response update(@DefaultValue("-1") @FormParam("id")          int id,
                           @DefaultValue("")   @FormParam("name")        String name,
                           @DefaultValue("")   @FormParam("description") String description,
                           @DefaultValue("-1") @FormParam("enable")      int enable,
                           @DefaultValue("-1") @FormParam("parent_id")   int parentId) {
        HospitalDepartmentBean one = service.update(id, name, description, enable, parentId, null, null, "");
        logger.info("update hospital department is " + one);
        if (null==one) {
            return Response.ok().build();
        }
        return Response.ok(one).build();
    }

    @Path("/edit_image")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateImage(@FormDataParam("id")       int                        id,
                                @FormDataParam("file")     InputStream                file) {
        HospitalDepartmentBean one = service.update(id, null, null, -1, -1, file, null, "");
        logger.info("update hospital department is " + one);
        return Response.ok(one).build();
    }

    @Path("/edit_disable_image")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateDisableImage(@FormDataParam("id")       int                        id,
                                       @FormDataParam("file")     InputStream                file) {
        HospitalDepartmentBean one = service.update(id, null, null, -1, -1, null, file, "");
        logger.info("update hospital department is " + one);
        return Response.ok(one).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response newOne(@DefaultValue("0") @FormParam("hospital_id") int hospitalId,
                           @DefaultValue("")   @FormParam("name") String name,
                           @DefaultValue("")   @FormParam("description") String description,
                           @DefaultValue("-1") @FormParam("enable") int enable,
                           @DefaultValue("-1") @FormParam("parent_id") int parentId
    ) {
        int id = service.createHospitalDepartment(hospitalId, name, description, enable, parentId, null, null);
        logger.info("new hospital department id is " + id);
        return Response.ok(id).build();
    }
}

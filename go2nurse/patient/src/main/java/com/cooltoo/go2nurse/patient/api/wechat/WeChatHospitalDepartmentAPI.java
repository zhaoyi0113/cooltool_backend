package com.cooltoo.go2nurse.patient.api.wechat;

import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.entities.HospitalDepartmentEntity;
import com.cooltoo.repository.HospitalDepartmentRepository;
import com.cooltoo.services.CommonDepartmentService;
import com.cooltoo.services.CommonHospitalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by zhaoyi0113 on 20/10/2016.
 */
@Path("/wechat/hospital")
public class WeChatHospitalDepartmentAPI {

    private static final Logger logger = LoggerFactory.getLogger(WeChatHospitalDepartmentAPI.class);

    @Autowired
    private CommonDepartmentService departmentService;

    @Path("/department/{departmentId}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getDepartmentInfo(@PathParam("departmentId") int departmentId) {
        HospitalDepartmentBean departmentBean = departmentService.getById(departmentId, "");
        logger.info("get department " + departmentBean);
        return Response.ok(departmentBean).build();
    }
}

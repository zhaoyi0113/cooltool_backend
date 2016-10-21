package com.cooltoo.go2nurse.patient.api.wechat;

import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.services.CommonDepartmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by zhaoyi0113 on 20/10/2016.
 */
@Path("/wechat/hospital")
public class WeChatHospitalDepartmentAPI {

    private static final Logger logger = LoggerFactory.getLogger(WeChatHospitalDepartmentAPI.class);

    @Autowired
    private CommonDepartmentService departmentService;

    @Path("/department/{uniqueId}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getDepartmentInfo(@PathParam("uniqueId") String uniqueId) {
        List<HospitalDepartmentBean> beans = departmentService.getDepartmentByUniqueId(uniqueId, "");
        if (beans.isEmpty()) {
            logger.error("failed to find department by unique id "+uniqueId);
            return Response.ok().build();
        }
        logger.info("get department " + beans.get(0));
        return Response.ok(beans.get(0)).build();
    }
}

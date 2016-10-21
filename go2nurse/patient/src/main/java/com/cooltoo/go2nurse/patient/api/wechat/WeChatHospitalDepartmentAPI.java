package com.cooltoo.go2nurse.patient.api.wechat;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaoyi0113 on 20/10/2016.
 */
@Path("/wechat/hospital")
public class WeChatHospitalDepartmentAPI {

    private static final Logger logger = LoggerFactory.getLogger(WeChatHospitalDepartmentAPI.class);

    @Autowired
    private CommonDepartmentService departmentService;

    @Autowired private CommonHospitalService hospitalService;

    @Path("/department/{uniqueId}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getHosDepartmentName(@PathParam("uniqueId") String uniqueId) {
        if(uniqueId == null || uniqueId.length()<12){
            logger.error("Invalid department unique id "+uniqueId);
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        String depUniqueId = uniqueId.substring(6,12);
        List<HospitalDepartmentBean> beans = departmentService.getDepartmentByUniqueId(depUniqueId, "");
        if (beans.isEmpty()) {
            logger.error("failed to find department by unique id "+uniqueId);
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        logger.info("get department " + beans.get(0));
        String hosUniqueId = uniqueId.substring(0,6);
        List<HospitalBean> hospitalBeens = hospitalService.getHospitalByUniqueId(hosUniqueId);
        if(hospitalBeens.isEmpty()){
            logger.error("get hospital failed "+hosUniqueId);
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        Map<String, String> data = new HashMap<>();
        data.put("name", hospitalBeens.get(0).getName()+beans.get(0).getName());
        return Response.ok(data).build();
    }



}

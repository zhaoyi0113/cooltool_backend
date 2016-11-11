package com.cooltoo.nurse360.admin.api;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.RegisterFrom;
import com.cooltoo.nurse360.service.NurseServiceForNurse360;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/11.
 */
@Path("/admin/nurse")
public class NurseQueryAPI {

    @Autowired private NurseServiceForNurse360 nurseServiceForNurse360;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNurseByAuthorityAndName(@Context HttpServletRequest request,
                                               @QueryParam("fuzzy_name") @DefaultValue("") String fuzzyName,
                                               @QueryParam("hospital_id") @DefaultValue("") String strHospitalId,
                                               @QueryParam("department_id") @DefaultValue("") String strDepartmentId
    ) {
        Integer hospitalId = VerifyUtil.isIds(strHospitalId) ? VerifyUtil.parseIntIds(strHospitalId).get(0) : null;
        Integer departmentId = VerifyUtil.isIds(strDepartmentId) ? VerifyUtil.parseIntIds(strDepartmentId).get(0) : null;
        List<NurseBean> nurses = nurseServiceForNurse360.getNurseByCanAnswerQuestion(fuzzyName, null, null, hospitalId, departmentId, RegisterFrom.GO2NURSE);
        return Response.ok(nurses).build();
    }
}

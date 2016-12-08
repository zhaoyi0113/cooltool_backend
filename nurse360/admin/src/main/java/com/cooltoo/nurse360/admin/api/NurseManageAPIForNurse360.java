package com.cooltoo.nurse360.admin.api;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.RegisterFrom;
import com.cooltoo.go2nurse.service.NurseServiceForGo2Nurse;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by zhaolisong on 2016/12/8.
 */
@Path("/admin/nurse")
public class NurseManageAPIForNurse360 {

    @Autowired private NurseServiceForGo2Nurse nurseService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryNurse(@Context HttpServletRequest request,
                               @QueryParam("fuzzy_name") @DefaultValue("") String name
    ) {
        List<NurseBean> nurses = nurseService.getNurseByCanAnswerQuestion(name, null, null, null, null, RegisterFrom.GO2NURSE);
        return Response.ok(nurses).build();
    }
}

package com.cooltoo.go2nurse.nurse.api;

import com.cooltoo.go2nurse.service.NurseServiceForGo2Nurse;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by zhaolisong on 16/9/25.
 */
@Path("/nurse")
public class NurseAPI {

    @Autowired private NurseServiceForGo2Nurse nurseServiceForGo2Nurse;

    @Path("/register")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerNurse(@Context HttpServletRequest request,
                                  @FormParam("mobile") @DefaultValue("") String mobile,
                                  @FormParam("sms_code") @DefaultValue("") String smsCode,
                                  @FormParam("name") @DefaultValue("") String name,
                                  @FormParam("gender") @DefaultValue("") String gender,
                                  @FormParam("birthday") @DefaultValue("") String birthday,
                                  @FormParam("password") @DefaultValue("") String password
    ) {
        long nurseId = 0L;
        return Response.ok(nurseId).build();
    }
}

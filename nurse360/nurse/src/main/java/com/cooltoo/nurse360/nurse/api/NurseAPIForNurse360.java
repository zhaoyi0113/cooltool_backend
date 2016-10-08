package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.beans.NurseBean;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.nurse360.filters.Nurse360LoginAuthentication;
import com.cooltoo.nurse360.service.NurseServiceForNurse360;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by zhaolisong on 16/9/28.
 */
@Path("/nurse")
public class NurseAPIForNurse360 {

    @Autowired private NurseServiceForNurse360 nurseServiceForNurse360;

    @Path("/register")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response newUser(@Context HttpServletRequest request,
                            @FormParam("real_name") @DefaultValue("") String realName,
                            @FormParam("birthday") @DefaultValue("0") int age,
                            @FormParam("gender") @DefaultValue("2") int gender,
                            @FormParam("mobile") @DefaultValue("") String mobile,
                            @FormParam("password") @DefaultValue("") String password,
                            @FormParam("sms_code") @DefaultValue("") String smsCode,
                            @FormParam("hospital_id")  int hospitalId,
                            @FormParam("department_id") int departmentId,
                            @FormParam("job_title") String jobTitle

    ) {
        long nurseId = nurseServiceForNurse360.addNurse(realName, age, gender, mobile, password, smsCode);
        nurseServiceForNurse360.setHospitalDepartmentAndJobTitle(nurseId, hospitalId, departmentId, jobTitle);
        return Response.ok(nurseId).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getNurseInfomation(@Context HttpServletRequest request) {
        long nurseId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseBean nurse = nurseServiceForNurse360.getNurseById(nurseId);
        return Response.ok(nurse).build();
    }
}

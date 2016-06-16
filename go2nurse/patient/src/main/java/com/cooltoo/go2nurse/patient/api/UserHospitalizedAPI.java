package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.UserHospitalizedRelationBean;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.UserHospitalizedRelationService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by hp on 2016/6/16.
 */
@Path("/hospitalized_relation")
public class UserHospitalizedAPI {

    @Autowired private UserHospitalizedRelationService relationService;

    @Path("/get")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getRelation(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<UserHospitalizedRelationBean> relations = relationService.getRelation(userId, CommonStatus.ENABLED.name());
        return Response.ok(relations).build();
    }

    @Path("/add")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response addRelation(@Context HttpServletRequest request,
                                            @FormParam("hospital_id") @DefaultValue("0") int hospitalId,
                                            @FormParam("department_id") @DefaultValue("0") int departmentId
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserHospitalizedRelationBean relation = relationService.addRelation(userId, hospitalId, departmentId);
        return Response.ok(relation).build();
    }

    @Path("/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response updateRelation(@Context HttpServletRequest request,
                                            @FormParam("relation_id") @DefaultValue("0") long relationId,
                                            @FormParam("status") @DefaultValue("disabled") String status
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        UserHospitalizedRelationBean relation = relationService.updateRelation(relationId, true, userId, status);
        return Response.ok(relation).build();
    }
}

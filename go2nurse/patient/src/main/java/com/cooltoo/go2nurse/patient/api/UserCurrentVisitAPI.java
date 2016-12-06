package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.UserCurrentVisitBean;
import com.cooltoo.go2nurse.constants.DiagnosticEnumeration;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.UserCurrentVisitService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by zhaolisong on 2016/12/6.
 */
@Path("/user/visit")
public class UserCurrentVisitAPI {

    @Autowired private UserCurrentVisitService userCurrentVisitService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getUserCurrentVisit(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        if (userCurrentVisitService.existsCurrentVisit(userId)) {
            UserCurrentVisitBean currentVisit = userCurrentVisitService.getCurrentVisit(userId);
            return Response.ok(currentVisit).build();
        }
        return Response.ok().build();
    }

    @Path("/diagnostic")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response setUserCurrentVisitDiagnostic(@Context HttpServletRequest request,
                                                  @FormParam("diagnostic_point") @DefaultValue("") String diagnosticPoint/* EXTENSION_NURSING(0), HOSPITALIZED_DATE(1), PHYSICAL_EXAMINATION, OPERATION, DISCHARGED_FROM_THE_HOSPITAL, AFTER_OPERATION, RECOVERY */
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        DiagnosticEnumeration diagnostic = DiagnosticEnumeration.parseString(diagnosticPoint);
        UserCurrentVisitBean currentVisit = userCurrentVisitService.setUserCurrentVisit(userId, diagnostic);
        return Response.ok(currentVisit).build();
    }

}

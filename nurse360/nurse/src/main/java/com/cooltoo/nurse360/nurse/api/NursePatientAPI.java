package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.UserAuthority;
import com.cooltoo.exception.*;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.go2nurse.beans.UserBean;
import com.cooltoo.go2nurse.service.UserService;
import com.cooltoo.nurse360.filters.Nurse360LoginAuthentication;
import com.cooltoo.nurse360.service.NursePatientRelationServiceForNurse360;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by zhaolisong on 16/10/8.
 */
@Path("/nurse/patient")
public class NursePatientAPI {

    @Autowired private NursePatientRelationServiceForNurse360 nursePatientService;
    @Autowired private UserService userService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response grabOrder(@Context HttpServletRequest request,
                              @QueryParam("index") @DefaultValue("0") int pageIndex,
                              @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<Long> userIds = nursePatientService.getUserByNurseId(nurseId, CommonStatus.ENABLED.name());
        List<UserBean> users = userService.getUser(userIds, UserAuthority.AGREE_ALL);
        return Response.ok(users).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response grabOrder(@Context HttpServletRequest request,
                              @FormParam("user_code") @DefaultValue("0") String userUniqueId
    ) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<UserBean> users = userService.getUserByUniqueId(userUniqueId);
        if (VerifyUtil.isListEmpty(users)) {
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }
        if (users.size()>1) {
            throw new BadRequestException(ErrorCode.DATA_ERROR);
        }
        nursePatientService.addUserPatientToNurse(nurseId, 0, users.get(0).getId());
        return Response.ok(userUniqueId).build();
    }
}

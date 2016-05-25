package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.backend.beans.NurseBean;
import com.cooltoo.backend.services.NurseService;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.UserAuthority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

/**
 * Created by zhaolisong on 16/4/12.
 */
@Path("/admin/nurse")
public class NurseManageAPI {

    private static final Logger logger = LoggerFactory.getLogger(NurseManageAPI.class);

    @Autowired private NurseService nurseService;

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getNurseById(@Context HttpServletRequest request,
                                 @PathParam("id") @DefaultValue("0") long nurseId) {
        logger.info("get nurse information by nurse id={}", nurseId);
        NurseBean nurse = nurseService.getNurse(nurseId);
        logger.info("nurse is {}", nurse);
        return Response.ok(nurse).build();
    }

    @Path("/authority_type")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getAuthorityType(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} get authority type", userId);
        List<String> allType = UserAuthority.getUserAuthority();
        logger.info("user {} get authority type {}", userId, allType);
        return Response.ok(allType).build();
    }

    @Path("/count/{authority}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response countNurseByAuthority(@Context HttpServletRequest request,
                                          @PathParam("authority") String strAuthority
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} get nurse record count by authority {}", userId, strAuthority);
        long count = nurseService.countByAuthority(strAuthority);
        logger.info("user {} get nurse record count by authority {}", userId, strAuthority);
        return Response.ok(count).build();
    }

    @Path("/{authority}/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getNurseByAuthority(@Context HttpServletRequest request,
                                        @PathParam("authority") String strAuthority,
                                        @PathParam("index")  @DefaultValue("0")  int index,
                                        @PathParam("number") @DefaultValue("10") int number
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} get nurse record count by authority {} at page {} with {} record/page", userId, strAuthority, index, number);
        List<NurseBean> nurses = nurseService.getAllByAuthority(strAuthority, index, number);
        logger.info("user {} get nurse record count by authority {} at page {} with {} record/page, count={}", userId, strAuthority, index, number, nurses.size());
        return Response.ok(nurses).build();
    }

    @Path("/update/authority")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response updateAuthority(@Context HttpServletRequest request,
                                    @FormParam("nurse_ids") String nurseIds,
                                    @FormParam("authority") String authority
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} update nurse {} 's authority property to {}", userId, nurseIds, authority);
        List<NurseBean> nurses = nurseService.updateAuthority(nurseIds, authority);
        logger.info("user {} update nurse {} 's authority property to {}, count={}", userId, nurseIds, authority, nurses.size());
        return Response.ok(nurses).build();
    }

}

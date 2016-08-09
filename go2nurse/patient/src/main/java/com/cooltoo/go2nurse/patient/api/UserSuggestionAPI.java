package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.beans.SuggestionBean;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.UserSuggestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by hp on 2016/8/9.
 */
@Path("/user/suggestion")
public class UserSuggestionAPI {

    private static final Logger logger = LoggerFactory.getLogger(UserSuggestionAPI.class.getName());

    @Autowired private UserSuggestionService service;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response addSuggestion(@Context HttpServletRequest request,
                                  @FormParam("suggestion") String suggestion,
                                  @FormParam("platform") @DefaultValue("") String platform,
                                  @FormParam("version") @DefaultValue("") String version
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        SuggestionBean suggestionB = service.userAddSuggestion(userId, platform, version, suggestion);
        logger.info("add suggestion bean is {}" + suggestionB);
        return Response.ok(suggestionB).build();
    }
}

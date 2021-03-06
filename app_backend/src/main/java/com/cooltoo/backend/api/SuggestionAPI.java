package com.cooltoo.backend.api;

import com.cooltoo.beans.SuggestionBean;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.NurseSuggestionService;
import com.cooltoo.constants.ContextKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by zhaolisong on 16/4/6.
 */
@Path("/suggestion")
public class SuggestionAPI {

    private static final Logger logger = LoggerFactory.getLogger(SuggestionAPI.class.getName());

    @Autowired
    private NurseSuggestionService service;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response addSuggestion(@Context HttpServletRequest request,
                                  @FormParam("suggestion") String suggestion,
                                  @FormParam("platform") @DefaultValue("IOS") String platform,
                                  @FormParam("version") @DefaultValue("1.5") String version
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        SuggestionBean suggestionB = service.nurseAddSuggestion(userId, platform, version, suggestion);
        logger.info("add suggestion bean is {}" + suggestionB);
        return Response.ok(suggestionB).build();
    }
}

package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.beans.SuggestionBean;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.UserType;
import com.cooltoo.entities.NurseEntity;
import com.cooltoo.nurse360.filters.Nurse360LoginAuthentication;
import com.cooltoo.repository.NurseRepository;
import com.cooltoo.services.SuggestionService;
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
@Path("/nurse/suggestion")
public class NurseSuggestionAPI {

    private static final Logger logger = LoggerFactory.getLogger(NurseSuggestionAPI.class.getName());

    @Autowired private SuggestionService service;
    @Autowired private NurseRepository nurseRepository;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response addSuggestion(@Context HttpServletRequest request,
                                  @FormParam("suggestion") String suggestion,
                                  @FormParam("platform") @DefaultValue("") String platform,
                                  @FormParam("version") @DefaultValue("") String version
    ) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        NurseEntity nurse = nurseRepository.findOne(nurseId);
        SuggestionBean suggestionB = service.addSuggestion(nurseId, UserType.NURSE.name(), nurse.getName(), platform, version, suggestion);
        logger.info("add suggestion bean is {}" + suggestionB);
        return Response.ok(suggestionB).build();
    }
}

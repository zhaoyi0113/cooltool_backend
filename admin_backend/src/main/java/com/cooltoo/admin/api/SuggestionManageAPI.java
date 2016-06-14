package com.cooltoo.admin.api;

import com.cooltoo.admin.filter.AdminUserLoginAuthentication;
import com.cooltoo.backend.beans.SuggestionBean;
import com.cooltoo.backend.services.SuggestionService;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.ReadingStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by zhaolisong on 16/4/11.
 */
@Path("/suggestion")
public class SuggestionManageAPI {

    private static final Logger logger = LoggerFactory.getLogger(SuggestionManageAPI.class.getName());

    @Autowired
    private SuggestionService service;

    @Path("/status_type")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getSuggestionStatusType(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID);
        logger.info("user {} get suggestion status type", userId);
        List<String> status = ReadingStatus.getAllStatus();
        logger.info("user {} get suggestion all status {}", status);
        return Response.ok(status).build();
    }

    @Path("/count/{status}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getSuggestionCount(@Context HttpServletRequest request,
                                       @PathParam("status") String status
    ) {
        logger.info("get suggestion count at status {}", status);
        long count = service.getSuggestionCount(status);
        return Response.ok(count).build();
    }


    @Path("/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getSuggestions(@Context HttpServletRequest request,
                                   @PathParam("index")  @DefaultValue("0")  int index,
                                   @PathParam("number") @DefaultValue("10") int number) {
        List<SuggestionBean> suggestions = service.getSuggestions(index, number);
        return Response.ok(suggestions).build();
    }

    @Path("/{status}/{index}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response getSuggestions(@Context HttpServletRequest request,
                                   @PathParam("status") String status,
                                   @PathParam("index")  @DefaultValue("0")  int index,
                                   @PathParam("number") @DefaultValue("10") int number) {
        if (!"ALL".equalsIgnoreCase(status)) {
            List<SuggestionBean> suggestions = service.getSuggestions(status, index, number);
            return Response.ok(suggestions).build();
        }
        else {
            List<SuggestionBean> suggestions = service.getSuggestions(index, number);
            return Response.ok(suggestions).build();
        }
    }

    @Path("/read")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @AdminUserLoginAuthentication(requireUserLogin = true)
    public Response setSuggestionsRead(@Context HttpServletRequest request,
                                       @FormParam("ids") String suggestIds) {
        suggestIds = service.updateStatus(suggestIds, ReadingStatus.READ.name());
        return Response.ok(suggestIds).build();
    }


    public Response deleteSuggestion(@Context HttpServletRequest request,
                                     @FormParam("ids") String ids) {
        service.deleteSuggestion(ids);
        return Response.ok().build();
    }
}

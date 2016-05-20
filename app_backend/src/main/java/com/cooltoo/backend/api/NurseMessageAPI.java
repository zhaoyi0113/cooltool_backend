package com.cooltoo.backend.api;

import com.cooltoo.backend.beans.MessageBean;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.MessageService;
import com.cooltoo.constants.ContextKeys;
import org.hibernate.annotations.GeneratorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

/**
 * Created by zhaolisong on 16/5/18.
 */
@Path("/nurse/message")
public class NurseMessageAPI {

    private static final Logger logger = LoggerFactory.getLogger(NurseMessageAPI.class.getName());

    @Autowired
    private MessageService messageService;

    @Path("/count/{status}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response countMessage(@Context HttpServletRequest request,
                                 @PathParam("status") @DefaultValue("all") String status
    ) {
        long userId = (long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("user={} get message by status={}", userId, status);
        int count = messageService.countMessageByStatus(status);
        return Response.ok(count).build();
    }

    @Path("/{page}/{number}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response getMessages(@Context HttpServletRequest request,
                                @PathParam("page") @DefaultValue("0") int page,
                                @PathParam("number") @DefaultValue("10") int number
    ) {
        long userId = (long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("user={} get message at page={} size={}", userId, page, number);
        List<MessageBean> allMessage = messageService.getMessages(userId, page, number);
        return Response.ok(allMessage).build();
    }
}

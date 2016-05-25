package com.cooltoo.backend.api;

import com.cooltoo.backend.beans.NurseFriendsBean;
import com.cooltoo.backend.beans.NurseMessageBean;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.NurseFriendsService;
import com.cooltoo.backend.services.NurseMessageService;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.SuggestionStatus;
import com.cooltoo.constants.UserType;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaolisong on 16/5/18.
 */
@Path("/nurse/message")
public class NurseMessageAPI {

    private static final Logger logger = LoggerFactory.getLogger(NurseMessageAPI.class.getName());

    @Autowired private NurseMessageService nurseMessageService;
    @Autowired private NurseFriendsService nurseFriendsService;

    @Path("/unread_friend_and_message_count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response countMessage(@Context HttpServletRequest request) {
        long userId = (long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("user={} get message unread", userId);
        long messageUnreadCount = nurseMessageService.countMessageByStatus(UserType.NURSE, userId, SuggestionStatus.UNREAD.name());
        List<NurseFriendsBean> waitForAgree = nurseFriendsService.getFriendshipWaitingAgreed(userId);
        long messageFriendWaitForAgreeCount = VerifyUtil.isListEmpty(waitForAgree) ? 0 : waitForAgree.size();
        logger.info("count={} message unread", messageUnreadCount);
        logger.info("count={} friendship wait for agree", messageFriendWaitForAgreeCount);
        Map<String, Long> totalUnreadSize = new HashMap<>();
        totalUnreadSize.put("size", messageUnreadCount + messageFriendWaitForAgreeCount);
        return Response.ok(totalUnreadSize).build();
    }

    @Path("/count/{status}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response countMessage(@Context HttpServletRequest request,
                                 @PathParam("status") @DefaultValue("all") String status
    ) {
        long userId = (long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("user={} get message by status={}", userId, status);
        long count = nurseMessageService.countMessageByStatus(UserType.NURSE, userId, status);
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
        String strStatuses = "UNREAD,READ";
        long userId = (long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("userType={} user={} get message status={} at page={} size={}",UserType.NURSE, userId, page, number);
        List<NurseMessageBean> allMessage = nurseMessageService.getMessages(UserType.NURSE, userId, strStatuses, page, number);
        return Response.ok(allMessage).build();
    }

    @Path("/read/{id}")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response readMessage(@Context HttpServletRequest request,
                                @PathParam("id") long id){
        long userId = (long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        nurseMessageService.setMessageStatus(id, "read");
        return Response.ok().build();
    }

    @Path("/delete/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response deleteMessage(@Context HttpServletRequest request,
                                  @PathParam("id") long id) {
        long userId = (long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        nurseMessageService.setMessageStatus(id, "DELETED");
        return Response.ok().build();
    }
}

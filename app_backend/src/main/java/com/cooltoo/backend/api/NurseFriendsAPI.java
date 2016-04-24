package com.cooltoo.backend.api;

import com.cooltoo.backend.beans.NurseFriendsBean;
import com.cooltoo.constants.AgreeType;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.NurseFriendsService;
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
 * Created by yzzhao on 3/10/16.
 */
@Path("/nurse/friends")
@LoginAuthentication(requireNurseLogin = true)
public class NurseFriendsAPI {

    @Autowired
    private NurseFriendsService friendsService;

    private static final Logger logger = LoggerFactory.getLogger(NurseFriendsAPI.class.getName());

    @POST
    @Path("/add/{friend_id}")
    public Response addFriend(@Context HttpServletRequest request,
                              @PathParam("friend_id") long friendId) {
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        friendsService.setFriendship(userId, friendId);
        return Response.ok().build();
    }

    @POST
    @Path("/agree")
    public Response agreeFriendRequest(@Context HttpServletRequest request,
                                       @FormParam("friend_id") long friendId) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        friendsService.modifyFriendAgreed(userId, friendId, AgreeType.AGREED);
        return Response.ok().build();
    }

    @POST
    @Path("/blacklist")
    public Response blackListFriend(@Context HttpServletRequest request,
                                    @FormParam("friend_id") long friendId) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        friendsService.modifyFriendAgreed(userId, friendId, AgreeType.BLACKLIST);
        return Response.ok().build();
    }

    @POST
    @Path("/access_zone_deny")
    public Response accessZoneDenyFriend(@Context HttpServletRequest request,
                                         @FormParam("friend_id") long friendId) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        friendsService.modifyFriendAgreed(userId, friendId, AgreeType.ACCESS_ZONE_DENY);
        return Response.ok().build();
    }

    @GET
    @Path("/waiting_my_agree")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRequestNotPassList(@Context HttpServletRequest request) {
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<NurseFriendsBean> waitingToPass = friendsService.getFriendshipWaitingAgreed(userId);
        logger.info("friend's need your promise to become your friends === " + waitingToPass);
        return Response.ok(waitingToPass).build();
    }

    @GET
    @Path("/list/{page_index}/{number}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFriendList(@Context HttpServletRequest request,
                                  @PathParam("page_index") int pageIdx,
                                  @PathParam("number") int number) {
        logger.info("search friend list at page=" + pageIdx + ", number=" + number);
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<NurseFriendsBean> friendList = friendsService.getFriendship(userId, userId, pageIdx, number);
        logger.info("get friend list count " + friendList.size());
        return Response.ok(friendList).build();
    }

    @GET
    @Path("/list_by_id/{search_id}/{page_index}/{number}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFriendListByID(@Context HttpServletRequest request,
                                      @PathParam("search_id") long searchId,
                                      @PathParam("page_index") int pageIdx,
                                      @PathParam("number") int number) {
        logger.info("search friend list at page=" + pageIdx + ", number=" + number + ", search id=" + searchId);
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<NurseFriendsBean> friendList = friendsService.getFriendship(userId, searchId, pageIdx, number);
        logger.info("get friend list count " + friendList.size());
        return Response.ok(friendList).build();
    }

    @GET
    @Path("/count")
    public Response getFriendCount(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        long count = friendsService.countFriendship(userId);
        return Response.ok(count).build();
    }

    @GET
    @Path("/search/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchFriend(@Context HttpServletRequest request,
                                 @PathParam("name") String name) {
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<NurseFriendsBean> friends = friendsService.searchFriendshipByName(userId, name);
        return Response.ok(friends).build();
    }

    @POST
    @Path("/judge_friend")
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireNurseLogin = true)
    public Response judgeFriend(@Context HttpServletRequest request,
                                @FormParam("others_ids") String otherIds
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        logger.info("nurse {} judge friendship with {}", otherIds);
        List<NurseFriendsBean> judgeResult = friendsService.getFriendship(userId, otherIds);
        logger.info("result is {}", judgeResult);
        return Response.ok(judgeResult).build();
    }
}

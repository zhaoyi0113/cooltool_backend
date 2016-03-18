package com.cooltoo.backend.api;

import com.cooltoo.backend.beans.NurseFriendsBean;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.NurseFriendsService;
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
@Path("/nurse_friends")
@LoginAuthentication(requireNurseLogin = true)
public class NurseFriendsAPI {

    @Autowired
    private NurseFriendsService friendsService;

    @POST
    @Path("/add/{friend_id}")
    public Response addFriend(@Context HttpServletRequest request, @PathParam("friend_id") long friendId) {
        long userId = Long.valueOf((String) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID));
        friendsService.addFriend(userId, friendId);
        return Response.ok().build();
    }

    @GET
    @Path("/list/{page_index}/{number}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFriendList(@Context HttpServletRequest request, @PathParam("page_index") int pageIdx, @PathParam("number") int number){

        long userId = (Long) request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<NurseFriendsBean> friendList = friendsService.getFriends(userId, pageIdx, number);
        return Response.ok(friendList).build();
    }

    @DELETE
    @Path("/{friend_id}")
    public Response removeFriend(@Context HttpServletRequest request, @PathParam("friend_id") long friendId){
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        friendsService.removeFriend(userId, friendId);
        return Response.ok().build();
    }

    @GET
    @Path("/count")
    public Response getFriendCount(@Context HttpServletRequest request){
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        int count = friendsService.getFriendsCount(userId);
        return Response.ok(count).build();
    }

    @GET
    @Path("/search/{name}")
    public Response searchFriend(@Context HttpServletRequest request, @PathParam("name") String name){
        long userId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<NurseFriendsBean> friends = friendsService.searchFriends(userId, name);
        return Response.ok(friends).build();
    }
}

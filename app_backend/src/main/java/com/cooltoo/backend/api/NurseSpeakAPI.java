package com.cooltoo.backend.api;

import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.filter.LoginAuthentication;
import com.cooltoo.backend.services.NurseSpeakService;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.List;

/**
 * Created by yzzhao on 3/15/16.
 */
@Path("/nurse/speak")
@LoginAuthentication(requireNurseLogin = true)
public class NurseSpeakAPI {

    @Autowired
    private NurseSpeakService speakService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/query/{index}/{number}")
    public Response getNurseSpeakContentsList(@Context HttpServletRequest request,
                                          @PathParam("index") int index,
                                          @PathParam("number") int number){
        long userId = Long.parseLong(request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID).toString());
        List<NurseSpeakBean> nurseSpeak = speakService.getNurseSpeak(userId, index, number);
        return Response.ok(nurseSpeak).build();
    }

    @POST
    public Response addSpeak(@Context HttpServletRequest request,
                             @FormDataParam("content") String content,
                             @FormDataParam("file_name") String fileName,
                             @FormDataParam("file") InputStream fileInputStream){
        throw new BadRequestException(ErrorCode.NOT_IMPLEMENTATION);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{index}")
    public Response getNurseSpeakContent(@Context HttpServletRequest request,
                                         @PathParam("id") long id){
        throw new BadRequestException(ErrorCode.NOT_IMPLEMENTATION);
    }


}

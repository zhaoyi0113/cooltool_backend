package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.go2nurse.openapp.WeChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

/**
 * Created by yzzhao on 8/14/16.
 */
@Path("/openapp")
public class OpenAppAPI {

    private static final Logger logger = LoggerFactory.getLogger(OpenAppAPI.class);

    @Autowired
    private WeChatService weChatService;

    @Path("/wechat/entry")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response wechatEntry(@QueryParam("signature") String signature, @QueryParam("timestamp") String timestamp, @QueryParam("nonce") String nonce, @QueryParam("echostr") String echostr) {
        logger.info("wechat entry " + signature);
        boolean b = weChatService.validateEntryConnection(signature, timestamp, nonce);
        if (b) {
            return Response.ok(echostr).build();
        }
        return Response.ok().build();
    }

    @Path("/wechat/login")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response login(
            @QueryParam("code") String code,
            @QueryParam("state") String state) {
        logger.info(" login code=" + code + ", state= " + state);
        URI uri = weChatService.login(code, state);
        logger.info("redirect to " + uri);
        return Response.seeOther(uri).build();
    }

    @GET
    @Path("/wechat/jsapiticket/")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJSApiTicket(@QueryParam("url") String url) {
        return Response.ok(weChatService.getJSApiSignature(url)).build();
    }

}

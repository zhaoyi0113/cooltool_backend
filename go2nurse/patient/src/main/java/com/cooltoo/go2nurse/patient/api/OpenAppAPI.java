package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.go2nurse.openapp.WeChatService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * Created by yzzhao on 8/14/16.
 */
@Path("/openapp")
public class OpenAppAPI {

    @Autowired
    private WeChatService weChatService;

    @Path("/wechat/entry")
    @GET
    public Response wechatEntry(@QueryParam("signature") String signature, @QueryParam("timestamp") String timestamp, @QueryParam("nonce") String nonce, @QueryParam("echostr") String echostr) {
        boolean b = weChatService.validateEntryConnection(signature, timestamp, nonce);
        if (b) {
            return Response.ok(echostr).build();
        }
        return Response.ok().build();
    }
}

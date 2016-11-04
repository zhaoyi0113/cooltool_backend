package com.cooltoo.go2nurse.patient.api.wechat;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.WeChatAccountBean;
import com.cooltoo.go2nurse.filters.WeChatAuthentication;
import com.cooltoo.go2nurse.openapp.WeChatAccountService;
import com.cooltoo.go2nurse.openapp.WeChatService;
import com.cooltoo.go2nurse.service.ServiceOrderService;
import com.cooltoo.util.NetworkUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/10/18.
 */
@Path("/wechat/pay")
public class WeChatPayAPI {

    private static final Logger logger = LoggerFactory.getLogger(WeChatPayAPI.class);

    @Autowired private ServiceOrderService orderService;
    @Autowired private WeChatService weChatService;
    @Autowired private WeChatAccountService weChatAccountService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @WeChatAuthentication
    public Response payByWeChat(@Context HttpServletRequest request,
                                @FormParam("order_id") @DefaultValue("0") long orderId
    ) {
        long userId = (Long)request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        String openId = weChatService.getOpenIdByUserId(userId);
        String appId = weChatService.getAppIdByUserId(userId);

        WeChatAccountBean weChatAccount = weChatAccountService.getWeChatAccountByAppId(appId);

        String remoteIP;
        try { remoteIP = NetworkUtil.getIpAddress(request); }
        catch (IOException ex) { remoteIP = "127.0.0.1"; }
        Map<String, String> charge = orderService.payForServiceByWeChat(userId, orderId, openId, remoteIP, weChatAccount);
        logger.debug("pay success with wechat="+charge);
        return Response.ok(charge).build();
    }
}

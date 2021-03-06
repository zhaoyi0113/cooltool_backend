package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.ServiceOrderBean;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.ServiceOrderService;
import com.pingplusplus.model.Charge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by yzzhao on 7/30/16.
 */
@Path("/pingpp-test")
public class PingServiceOrderTestAPI {

    private static final Logger logger = LoggerFactory.getLogger(PingServiceOrderTestAPI.class);

    @Autowired
    private ServiceOrderService orderService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response createOrder(@Context HttpServletRequest request,
                                @FormParam("channel") @DefaultValue("wx") String channel,
                                @FormParam("patientId") long patientId,
                                @FormParam("addressId") long addressId,
                                @FormParam("serviceItemId") long serviceItemId,
                                @FormParam("startTime") String startTime) {
        logger.info("get channel " + channel);
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        int amount = 100;
        String orderNo = "xxxx";
        String ip = request.getRemoteHost();
        String subject = "test order";
        String body = "order description";
        String description = "order extra descritpion";
        String address = "order address";
        ServiceOrderBean order = orderService.addOrder(serviceItemId, userId, patientId, address, startTime, 1, "this is a test order", null);
        logger.info("service order "+order);
        Charge charge = orderService.payForServiceByPingPP(userId, order.getId(), channel, ip);
//        Charge charge = pingPPService.createCharge(orderNo, channel, amount, ip, subject, body, description);
        logger.info("create charge object "+charge);
        return Response.ok(charge).build();
    }

//    @POST
//    @Path("/payment")
//    public Response payment(@Context HttpServletRequest request) {
//        logger.info("receive web hooks");
//        try {
//            ServletInputStream inputStream = request.getInputStream();
//            Reader reader = new InputStreamReader(inputStream);
//            String body = CharStreams.toString(reader);
//            logger.info("receive body "+body);
//            if(body != null) {
//                Event event = Event.GSON.fromJson(body, Event.class);
//                Charge charge = (Charge) event.getData().getObject();
//                orderService.updateOrderCharge(event.getId(), charge.getId(), body, OrderStatus.PAID);
//            }
//        } catch (IOException e) {
//            logger.error(e.getMessage(), e);
//        }
//        return Response.ok().build();
//    }


}

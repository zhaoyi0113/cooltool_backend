package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.go2nurse.service.PingPPService;
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
    private PingPPService pingPPService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrder(@Context HttpServletRequest request, @FormParam("channel") @DefaultValue("wx") String channel){
        logger.info("get channel "+channel);
        int amount = 100;
        String orderNo="xxxx";
        String ip="127.0.0.1";
        String subject="test order";
        String body="order description";
        String description="order extra descritpion";
        Charge charge = pingPPService.createCharge(orderNo, channel, amount, ip, subject, body, description);
        return Response.ok(charge).build();
    }

    @POST
    @Path("/payment")
    public Response payment(@Context HttpServletRequest request){
        System.out.println("receive web hooks");
        return Response.ok().build();
    }
}

package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.go2nurse.service.PingPPService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by yzzhao on 7/30/16.
 */
@Path("/pingpp-test")
public class PingServiceOrderTestAPI {

    @Autowired
    private PingPPService pingPPService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response createOrder(){
        int amount = 100;
        String orderNo="xxxx";
        String channel="ws";
        String ip="192.168.1.1";
        String subject="test order";
        String body="order description";
        String description="order extra descritpion";
        com.pingplusplus.model.Charge charge = (com.pingplusplus.model.Charge) pingPPService.createCharge(amount, orderNo, channel, ip, subject, body, description);
        return Response.ok(charge).build();

    }
}

package com.cooltoo.api;

import com.cooltoo.beans.OrderBean;
import com.cooltoo.serivces.OrderService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by lg380357 on 2016/3/1.
 */
@Path("/order")
public class OrderAPI {

    private static final Logger logger = Logger.getLogger(OrderAPI.class);

    @Autowired
    private OrderService orderService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllOrder() {
        List<OrderBean> all = orderService.getAll();
        return Response.ok(all).build();
    }

    @POST
    @Path("/new")
    @Produces(MediaType.APPLICATION_JSON)
    public Response newOrder(
            @FormParam("name") String name,
            @DefaultValue("1") @FormParam("count") int count,
            @DefaultValue("99999999.99") @FormParam("cash")BigDecimal cash) {
        if (null == name || "".equals(name.trim()) || count < 0 || cash.doubleValue() < 0.0) {
            logger.info("new badge name="+name +" count="+count +" cash="+cash);
            return Response.ok().build();
        }
        long id = orderService.newOrder(name, count, cash);
        logger.info("new badge  id="+id+" name="+name +" count="+count +" cash="+cash);
        return Response.ok(id).build();
    }

    @POST
    @Path("/get")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOrder(
            @DefaultValue("-1") @FormParam("id") long id) {
        OrderBean bean = orderService.getOrder(id);
        logger.info("get order is " + bean);
        if (null == bean) {
            return Response.ok().build();
        }
        return Response.ok(bean).build();
    }

    @POST
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateOrder(
            @DefaultValue("-1") @FormParam("id") long id,
            @FormParam("name") String name,
            @DefaultValue("1") @FormParam("count") int count,
            @DefaultValue("-1.00") @FormParam("cash")BigDecimal cash) {
        OrderBean bean = orderService.updateOrder(id, name, count, cash);
        logger.info("update order is " + bean);
        if (null == bean) {
            return Response.ok().build();
        }
        return Response.ok(bean).build();
    }

    @POST
    @Path("/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteOrder(
            @DefaultValue("-1") @FormParam("id") long id) {
        OrderBean bean = orderService.deleteOrder(id);
        logger.info("delete order is " + bean);
        if (null == bean) {
            return Response.ok().build();
        }
        return Response.ok(bean).build();
    }
}

package com.cooltoo.api;

import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by yzzhao on 2/22/16.
 */
@Path("/helloworld")
@Component
public class HelloworldAPI {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response sayHello(){
        return Response.ok("hello world!").build();
    }


}



package com.cooltoo.api;

import com.cooltoo.entities.HelloEntity;
import com.cooltoo.repository.HelloRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private HelloRepository repository;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response sayHello(){
        HelloEntity entity = new HelloEntity();
        entity.setName("aaaa");
        repository.save(entity);
        return Response.ok("hello world!").build();
    }


}



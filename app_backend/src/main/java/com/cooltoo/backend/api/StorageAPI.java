package com.cooltoo.backend.api;

import com.cooltoo.services.StorageService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;

/**
 * Created by yzzhao on 3/8/16.
 */
@Path("/storage")
public class StorageAPI {

    @Autowired
    private StorageService storageService;

    @GET
    @Path("{id}")
    @Produces(MediaType.MULTIPART_FORM_DATA)
    public InputStream getResourceInputStream(@PathParam("id") long id){
        return storageService.getFileInputStream(id);
    }
}

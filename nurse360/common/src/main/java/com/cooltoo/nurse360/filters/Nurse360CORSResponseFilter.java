package com.cooltoo.nurse360.filters;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Created by yzzhao on 1/5/16.
 */
@Provider
public class Nurse360CORSResponseFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        addResponseHeaders(headers, "Access-Control-Allow-Origin", "*");
        addResponseHeaders(headers, "Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
        headers.add("Access-Control-Allow-Headers", "access_token");
        headers.add("Access-Control-Allow-Headers", "ACCESS_TOKEN");
    }

    private void addResponseHeaders(MultivaluedMap<String, Object> headers, String key, String value){
        if(!headers.containsKey(key)){
            headers.add(key, value);
        }
    }
}

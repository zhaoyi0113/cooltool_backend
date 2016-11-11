package com.cooltoo.nurse360.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Created by zhaolisong on 16/9/28.
 */
@Provider
public class Nurse360CORSResponseFilter implements ContainerResponseFilter {

    private static final Logger logger = LoggerFactory.getLogger(Nurse360CORSResponseFilter.class);

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        MultivaluedMap<String, Object> headers = responseContext.getHeaders();
        addResponseHeaders(headers, "Access-Control-Allow-Origin", "*");
        addResponseHeaders(headers, "Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE");
        headers.add("Access-Control-Allow-Headers", "access_token");
        headers.add("Access-Control-Allow-Headers", "ACCESS_TOKEN");
        logger.debug("add response headers "+headers);
    }

    private void addResponseHeaders(MultivaluedMap<String, Object> headers, String key, String value){
        if(!headers.containsKey(key)){
            headers.add(key, value);
        }
    }
}

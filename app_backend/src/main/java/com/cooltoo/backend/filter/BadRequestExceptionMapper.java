package com.cooltoo.backend.filter;

import com.cooltoo.exception.BadRequestException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by yzzhao on 1/21/16.
 */
//@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

    private static final Logger logger = Logger.getLogger(BadRequestExceptionMapper.class.getName());

    @Override
    public Response toResponse(BadRequestException exception) {
        logger.severe("get exception "+exception.getMessage());
        logger.log(Level.SEVERE, exception.getMessage(), exception);
        return Response.status(exception.getErrorCode().getCode()).header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "*").header("Access-Control-Allow-Headers", "access_token")
                .entity(exception.getMessage()).build();
    }
}

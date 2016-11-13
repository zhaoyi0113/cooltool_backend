package com.cooltoo.nurse360.filters;

import com.cooltoo.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Created by zhaolisong on 16/9/28.
 */
@Provider
public class Nurse360BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {

    private static final Logger logger = LoggerFactory.getLogger(Nurse360BadRequestExceptionMapper.class.getName());

    @Override
    public Response toResponse(BadRequestException exception) {
        logger.error("get exception "+exception.getMessage()+","+exception.getErrorCode().getCode());
        logger.error(exception.getMessage(), exception);
        logger.trace(Marker.ANY_MARKER, exception.getMessage(), exception);
        return Response.status(exception.getErrorCode().getCode())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "*")
                .header("Access-Control-Allow-Headers", "access_token")
                .entity(exception.getMessage()).build();
    }
}

package com.cooltoo.filter;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by yzzhao on 3/2/16.
 */
@Provider
public class NurseLoginAuthenticationFilter implements ContainerRequestFilter {

    private static final Logger logger = Logger.getLogger(NurseLoginAuthenticationFilter.class.getName());

    @Context
    private HttpServletRequest servletRequest;

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LoginAuthentication login = resourceInfo.getResourceClass().getAnnotation(LoginAuthentication.class);
        logger.info("login auth " + login);
        if (login == null) {
            return;
        }
        if (login.requireNurseLogin()) {
            Object nurse = servletRequest.getSession().getAttribute(ContextKeys.NURSE_LOGIN);
            if (nurse == null) {
                throw new BadRequestException(ErrorCode.NOT_LOGIN);
            }
            logger.info("nurse login as " + nurse);
        }
    }
}

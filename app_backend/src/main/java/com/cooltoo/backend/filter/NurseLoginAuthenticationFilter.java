package com.cooltoo.backend.filter;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.HeaderKeys;
import com.cooltoo.backend.entities.TokenAccessEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.backend.repository.TokenAccessRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;
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

    @Autowired
    private TokenAccessRepository tokenAccessRepository;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LoginAuthentication login = resourceInfo.getResourceClass().getAnnotation(LoginAuthentication.class);
        if (login == null) {
            login = resourceInfo.getResourceMethod().getAnnotation(LoginAuthentication.class);
            if(login == null){
                return;
            }
        }
        if (login.requireNurseLogin()) {
            MultivaluedMap<String, String> pathParameters = requestContext.getHeaders();
            List<String> tokens = pathParameters.get(HeaderKeys.ACCESS_TOKEN);
            if (tokens == null || tokens.isEmpty()) {
                throw new BadRequestException(ErrorCode.NOT_LOGIN);
            }
            String token = tokens.get(0);
            logger.info("get token " + token);
            if (token != null) {
                //read user id from token
                List<TokenAccessEntity> tokenEntities = tokenAccessRepository.findTokenAccessByToken(token);
                if (tokenEntities.isEmpty()) {
                    throw new BadRequestException(ErrorCode.NOT_LOGIN);
                }
                long userId = tokenEntities.get(0).getUserId();
                logger.info("get user id "+userId);
                requestContext.setProperty(ContextKeys.NURSE_LOGIN_USER_ID, userId);
            }
        }
    }
}

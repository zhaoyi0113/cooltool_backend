package com.cooltoo.filter;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.HeaderKeys;
import com.cooltoo.entities.TokenAccessEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.TokenAccessRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by yzzhao on 1/11/16.
 */
@Provider
public class TokenAccessAuthentication implements ContainerRequestFilter {

    private static final Logger logger = Logger.getLogger(TokenAccessAuthentication.class.getName());

    @Context
    private ResourceInfo resourceInfo;

    @Autowired
    private TokenAccessRepository tokenAccessRepository;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Method resourceMethod = resourceInfo.getResourceMethod();
        Class<?> resourceClass = resourceInfo.getResourceClass();
        TokenAccess annotation = resourceMethod.getAnnotation(TokenAccess.class);
        if (annotation == null) {
            annotation = resourceClass.getAnnotation(TokenAccess.class);
        }
        if (annotation == null) {
            return;
        }
        MultivaluedMap<String, String> pathParameters = requestContext.getHeaders();
        List<String> tokens = pathParameters.get(HeaderKeys.ACCESS_TOKEN);
        if (annotation.requireAccessToken() && (tokens == null || tokens.isEmpty())) {
            throw new BadRequestException(ErrorCode.NOT_LOGIN);
        }
        if (tokens == null || tokens.isEmpty()) {
            return;
        }
        String token = tokens.get(0);
        logger.info("get token " + token);
        requestContext.setProperty(HeaderKeys.ACCESS_TOKEN, token);
        if (token != null) {
            //read user if from token
            List<TokenAccessEntity> tokenEntities = tokenAccessRepository.findTokenAccessByToken(token);
            if (tokenEntities.isEmpty()){
                throw new BadRequestException(ErrorCode.NOT_LOGIN);
            }

            requestContext.setProperty(ContextKeys.NURSE_LOGIN, tokenEntities.get(0).getId());
        }
    }

}

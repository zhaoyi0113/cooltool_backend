package com.cooltoo.backend.filter;

import com.cooltoo.entities.NurseEntity;
import com.cooltoo.repository.NurseRepository;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.HeaderKeys;
import com.cooltoo.entities.NurseTokenAccessEntity;
import com.cooltoo.constants.UserAuthority;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.NurseTokenAccessRepository;
import com.cooltoo.util.VerifyUtil;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yzzhao on 3/2/16.
 */
@Provider
public class NurseLoginAuthenticationFilter implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(NurseLoginAuthenticationFilter.class.getName());

    @Context
    private HttpServletRequest servletRequest;
    @Context
    private ResourceInfo resourceInfo;
    @Autowired
    private NurseTokenAccessRepository tokenAccessRepository;
    @Autowired
    private NurseRepository nurseRepository;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LoginAuthentication login = resourceInfo.getResourceClass().getAnnotation(LoginAuthentication.class);
        if (login == null) {
            login = resourceInfo.getResourceMethod().getAnnotation(LoginAuthentication.class);
            if(login == null){
                return;
            }
        }
        logger.info("access "+requestContext.getUriInfo().getAbsolutePath());
        String invokePath = requestContext.getUriInfo().getAbsolutePath().getPath();
        boolean isLogoutLogin = false;
        if (invokePath.endsWith("logout") || invokePath.endsWith("login")) {
            isLogoutLogin = true;
        }
        MultivaluedMap<String, String> pathParameters = requestContext.getHeaders();
        List<String> tokens = pathParameters.get(HeaderKeys.ACCESS_TOKEN);
        if (tokens == null || tokens.isEmpty() || VerifyUtil.isStringEmpty(tokens.get(0))) {
            if(login.requireNurseLogin()) {
                throw new BadRequestException(ErrorCode.NOT_LOGIN);
            }
            else {
                return;
            }
        }
        String token = tokens.get(0);
        logger.info("get token " + token);
        if (token != null) {
            //read user id from token
            List<NurseTokenAccessEntity> tokenEntities = tokenAccessRepository.findTokenAccessByToken(token);
            if (tokenEntities.isEmpty()) {
                if (login.requireNurseLogin()) {
                    throw new BadRequestException(ErrorCode.NOT_LOGIN);
                }
                else {
                    return;
                }
            }
            long userId = tokenEntities.get(0).getUserId();
            logger.info("get user id "+userId);
            NurseEntity nurseEntity = nurseRepository.findOne(userId);
            if (null==nurseEntity) {
                throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
            }
            if (!isLogoutLogin && UserAuthority.DENY_ALL.equals(nurseEntity.getAuthority())) {
                throw new BadRequestException(ErrorCode.USER_AUTHORITY_DENY_ALL);
            }
            requestContext.setProperty(ContextKeys.NURSE_LOGIN_USER_ID, userId);
        }
    }
}

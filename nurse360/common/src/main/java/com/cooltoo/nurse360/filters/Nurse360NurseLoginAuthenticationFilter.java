package com.cooltoo.nurse360.filters;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.HeaderKeys;
import com.cooltoo.constants.UserAuthority;
import com.cooltoo.entities.NurseEntity;
import com.cooltoo.entities.NurseTokenAccessEntity;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.repository.NurseRepository;
import com.cooltoo.repository.NurseTokenAccessRepository;
import com.cooltoo.util.VerifyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.List;

/**
 * Created by zhaolisong on 16/9/28.
 */
@Provider
public class Nurse360NurseLoginAuthenticationFilter implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(Nurse360NurseLoginAuthenticationFilter.class);

    @Context private ResourceInfo resourceInfo;
    @Autowired private NurseTokenAccessRepository tokenAccessRepository;
    @Autowired private NurseRepository nurseRepository;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        Nurse360LoginAuthentication login = resourceInfo.getResourceClass().getAnnotation(Nurse360LoginAuthentication.class);
        if (null==login) {
            login = resourceInfo.getResourceMethod().getAnnotation(Nurse360LoginAuthentication.class);
            if(login == null){
                return;
            }
        }
        logger.info("access==>{}", requestContext.getUriInfo().getAbsolutePath());
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
        logger.info("get token={}", token);
        if (token != null) {
            //read nurse id from token
            List<NurseTokenAccessEntity> tokenEntities = tokenAccessRepository.findTokenAccessByToken(token);
            if (tokenEntities.isEmpty()) {
                if (login.requireNurseLogin()) {
                    throw new BadRequestException(ErrorCode.NOT_LOGIN);
                }
                else {
                    return;
                }
            }
            long nurseId = tokenEntities.get(0).getUserId();
            logger.info("get nurse id "+nurseId);
            NurseEntity nurseEntity = nurseRepository.findOne(nurseId);
            if (null==nurseEntity) {
                throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
            }
            if (!isLogoutLogin && UserAuthority.DENY_ALL.equals(nurseEntity.getAuthority())) {
                throw new BadRequestException(ErrorCode.USER_AUTHORITY_DENY_ALL);
            }
            requestContext.setProperty(ContextKeys.NURSE_LOGIN_USER_ID, nurseId);
        }
    }
}

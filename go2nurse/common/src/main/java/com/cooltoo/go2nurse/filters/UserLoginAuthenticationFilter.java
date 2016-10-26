package com.cooltoo.go2nurse.filters;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.HeaderKeys;
import com.cooltoo.constants.UserAuthority;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.entities.UserEntity;
import com.cooltoo.go2nurse.entities.UserTokenAccessEntity;
import com.cooltoo.go2nurse.repository.UserRepository;
import com.cooltoo.go2nurse.repository.UserTokenAccessRepository;
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
 * Created by hp on 6/13/16.
 */
@Provider
public class UserLoginAuthenticationFilter implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(UserLoginAuthenticationFilter.class);

    @Context private ResourceInfo resourceInfo;
    @Autowired private UserTokenAccessRepository tokenAccessRepository;
    @Autowired private UserRepository userRepository;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        LoginAuthentication login = resourceInfo.getResourceClass().getAnnotation(LoginAuthentication.class);
        if (null==login) {
            login = resourceInfo.getResourceMethod().getAnnotation(LoginAuthentication.class);
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
            if(login.requireUserLogin()) {
                throw new BadRequestException(ErrorCode.NOT_LOGIN);
            }
            else {
                return;
            }
        }
        String token = tokens.get(0);
        logger.info("get token={}", token);
        requestContext.setProperty(ContextKeys.USER_ACCESS_TOKEN, token);
        if (token != null) {
            //read user id from token
            List<UserTokenAccessEntity> tokenEntities = tokenAccessRepository.findTokenAccessByToken(token);
            if (tokenEntities.isEmpty()) {
                if (login.requireUserLogin()) {
                    throw new BadRequestException(ErrorCode.NOT_LOGIN);
                }
                else {
                    return;
                }
            }
            long userId = tokenEntities.get(0).getUserId();
            logger.info("get user id "+userId);
            UserEntity userEntity = userRepository.findOne(userId);
            if (null==userEntity) {
                logger.error("user "+userId+" doesn't exist.");
                throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
            }
            if (!isLogoutLogin && UserAuthority.DENY_ALL.equals(userEntity.getAuthority())) {
                throw new BadRequestException(ErrorCode.USER_AUTHORITY_DENY_ALL);
            }
            requestContext.setProperty(ContextKeys.USER_LOGIN_USER_ID, userId);
        }
    }
}

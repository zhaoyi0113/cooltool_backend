package com.cooltoo.admin.filter;

import com.cooltoo.admin.entities.AdminUserTokenAccessEntity;
import com.cooltoo.admin.repository.AdminUserTokenAccessRepository;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.HeaderKeys;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
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
 * Created by zhaolisong on 16/3/22.
 */
@Provider
public class AdminUserAuthenticationFilter implements ContainerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AdminUserAuthenticationFilter.class.getName());

    @Context
    private HttpServletRequest request;

    @Context
    private ResourceInfo resourceInfo;

    @Autowired
    private AdminUserTokenAccessRepository userTokenAccessRepository;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        logger.info("user login authentication filtering ....... ");
        AdminUserLoginAuthentication login = resourceInfo.getResourceClass().getAnnotation(AdminUserLoginAuthentication.class);
        if (login == null) {
            login = resourceInfo.getResourceMethod().getAnnotation(AdminUserLoginAuthentication.class);
            if(login == null){
                return;
            }
        }

        logger.info("access path : "+requestContext.getUriInfo().getAbsolutePath());
        if (login.requireUserLogin()) {
            MultivaluedMap<String, String> pathParameters = requestContext.getHeaders();
            List<String> tokens = pathParameters.get(HeaderKeys.ACCESS_TOKEN);
            if (tokens == null || tokens.isEmpty()) {
                throw new BadRequestException(ErrorCode.NOT_LOGIN);
            }
            String token = tokens.get(0);
            logger.info("the user login token : " + token);
            if (token != null) {
                //read user id from token
                List<AdminUserTokenAccessEntity> tokenEntities = userTokenAccessRepository.findAdminUserTokenAccessByToken(token);
                if (tokenEntities.isEmpty()) {
                    throw new BadRequestException(ErrorCode.NOT_LOGIN);
                }
                long userId = tokenEntities.get(0).getUserId();
                logger.info("the user id : "+userId);
                requestContext.setProperty(ContextKeys.ADMIN_USER_LOGIN_USER_ID, userId);
                requestContext.setProperty(ContextKeys.ADMIN_USER_TOKEN, token);
            }
        }
    }
}

package com.cooltoo.go2nurse.filters;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.HeaderKeys;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.beans.WeChatAccountBean;
import com.cooltoo.go2nurse.converter.UserOpenAppEntity;
import com.cooltoo.go2nurse.openapp.WeChatAccountService;
import com.cooltoo.go2nurse.repository.UserOpenAppRepository;
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
 * Created by zhaoyi0113 on 05/11/2016.
 */
@Provider
public class WeChatOpenIdAuthenticationFilter implements ContainerRequestFilter {

    @Autowired
    private UserOpenAppRepository openAppRepository;

    private static final Logger logger = LoggerFactory.getLogger(WeChatOpenIdAuthenticationFilter.class);

    @Context
    private ResourceInfo resourceInfo;

    @Autowired
    private WeChatAccountService accountService;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        WeChatAuthentication annotation = getAnnotationFromResourceClass();
        if (annotation == null) {
            return;
        }
        MultivaluedMap<String, String> pathParameters = requestContext.getHeaders();
        List<String> openids = pathParameters.get(HeaderKeys.OPEN_ID);
        if (openids == null || openids.isEmpty() || VerifyUtil.isStringEmpty(openids.get(0))) {
            throw new BadRequestException(ErrorCode.OPENID_INVALID);
        }
        logger.info("Get wechat authentication openid " + openids.get(0));
        UserOpenAppEntity appEntity = openAppRepository.findFirstByOpenid(openids.get(0));
        if (appEntity == null) {
            throw new BadRequestException(ErrorCode.OPENID_INVALID);
        }

        setHospitalDeparmentUniqueId(appEntity.getAppId(), requestContext);
        requestContext.setProperty(ContextKeys.USER_LOGIN_USER_ID, appEntity.getUserId());
        requestContext.setProperty(ContextKeys.WECHAT_OPEN_ID, appEntity.getOpenid());
    }

    private void setHospitalDeparmentUniqueId(String appid, ContainerRequestContext requestContext){
        WeChatAccountBean account = accountService.getWeChatAccountByAppId(appid);
        requestContext.setProperty(ContextKeys.DEPARTMENT_UNIQUE_ID, account.getDepartment().getUniqueId());
        requestContext.setProperty(ContextKeys.HOSPITAL_ID, account.getHospital().getId());
        requestContext.setProperty(ContextKeys.HOSPITAL_UNIQUE_ID, account.getHospital().getUniqueId());
    }

    private WeChatAuthentication getAnnotationFromResourceClass() {
        WeChatAuthentication annotation = resourceInfo.getResourceClass().getAnnotation(WeChatAuthentication.class);
        if (annotation == null) {
            return resourceInfo.getResourceMethod().getAnnotation(WeChatAuthentication.class);
        }
        return annotation;
    }
}

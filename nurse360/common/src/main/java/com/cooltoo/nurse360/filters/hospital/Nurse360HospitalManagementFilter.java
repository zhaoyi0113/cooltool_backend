package com.cooltoo.nurse360.filters.hospital;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.constants.RequestMethod;
import com.cooltoo.nurse360.beans.HospitalAdminAccessTokenBean;
import com.cooltoo.nurse360.beans.HospitalManagementUrlBean;
import com.cooltoo.nurse360.service.hospital.HospitalAdminAccessTokenService;
import com.cooltoo.nurse360.service.hospital.HospitalAdminAccessUrlService;
import com.cooltoo.nurse360.service.hospital.HospitalAdminService;
import com.cooltoo.nurse360.service.hospital.HospitalManagementUrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by zhaolisong on 2016/10/20.
 */
@Component
public class Nurse360HospitalManagementFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(Nurse360HospitalManagementFilter.class);

    @Autowired private HospitalManagementUrlService    urlService;
    @Autowired private HospitalAdminService            adminService;
    @Autowired private HospitalAdminAccessTokenService tokenService;
    @Autowired private HospitalAdminAccessUrlService   accessUrlService;

    public Nurse360HospitalManagementFilter() {
        this.setBeanName(Nurse360HospitalManagementFilter.class.getName());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest)request;
            // get http url
            String httpUrl = httpRequest.getPathInfo();
            String httpRootUrl = httpRequest.getRequestURI();
            // get http type
            RequestMethod httpType = RequestMethod.parseString(httpRequest.getMethod());
            logger.debug("get http type "+httpType);
            if("OPTIONS".equals(httpType.name())){
                logger.debug(("this is options http type."));
                return;
            }

            // check http url valid
            if (null==httpType || null==httpRootUrl || !httpUrl.startsWith("/hospital_management") || !httpRootUrl.equals("/nurse360"+httpUrl)) {
                chain.doFilter(request, response);
                return;
            }

            HospitalManagementUrlBean mngUrl = urlService.getHospitalMngUrl(httpType, httpUrl);
            if (null==mngUrl) {
                throw new ServletException(new BadRequestException(ErrorCode.AUTHENTICATION_INVALIDATE));
            }

            // need not check token
            if (!YesNoEnum.YES.equals(mngUrl.getNeedToken())) {
                chain.doFilter(request, response);
                return;
            }

            // get token
            String hospitalAdminToken = httpRequest.getHeader("ACCESS_TOKEN");
            // get admin by token
            HospitalAdminAccessTokenBean token = tokenService.getToken(hospitalAdminToken);
            // token invalid
            if (null==token) {
                throw new ServletException(new BadRequestException(ErrorCode.AUTHENTICATION_INVALIDATE));
            }

            // admin invalid
            if (!adminService.existsAdminUser(token.getAdminId(), CommonStatus.ENABLED)) {
                throw new ServletException(new BadRequestException(ErrorCode.AUTHENTICATION_INVALIDATE));
            }

            // admin can access url
            if (!accessUrlService.hasAdminMngUrl(token.getAdminId(), mngUrl.getId())) {
                throw new ServletException(new BadRequestException(ErrorCode.AUTHENTICATION_INVALIDATE));
            }

            // save token and adminId
            request.setAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID, token.getAdminId());
            request.setAttribute(ContextKeys.ADMIN_USER_TOKEN, token.getToken());

            chain.doFilter(request, response);
        }
    }
}

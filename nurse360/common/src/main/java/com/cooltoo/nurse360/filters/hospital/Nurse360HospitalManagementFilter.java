package com.cooltoo.nurse360.filters.hospital;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.nurse360.beans.HospitalAdminAccessTokenBean;
import com.cooltoo.nurse360.entities.HospitalAdminEntity;
import com.cooltoo.nurse360.repository.HospitalAdminRepository;
import com.cooltoo.nurse360.service.hospital.HospitalAdminAccessTokenService;
import com.cooltoo.nurse360.service.hospital.HospitalAdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by zhaolisong on 2016/10/20.
 */
public class Nurse360HospitalManagementFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(Nurse360HospitalManagementFilter.class);

    private HospitalAdminService adminService;
    private HospitalAdminAccessTokenService tokenService;
    private HospitalAdminRepository adminRepository;

    private void setServices(ServletRequest request) {
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
        if (adminService == null) {
            adminService = webApplicationContext.getBean(HospitalAdminService.class);
        }
        if (tokenService == null) {
            tokenService = webApplicationContext.getBean(HospitalAdminAccessTokenService.class);
        }
        if (adminRepository == null) {
            adminRepository = webApplicationContext.getBean(HospitalAdminRepository.class);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        setServices(request);
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HospitalAdminAccessTokenBean token = getHospitalAdminAccessTokenBean(httpRequest);

            if(token != null) {
                // save token and adminId
                request.setAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID, token.getAdminId());
                request.setAttribute(ContextKeys.ADMIN_USER_TOKEN, token.getToken());

                HospitalAdminEntity adminEntity = adminRepository.findById(token.getAdminId());
                HospitalUserAuthentication authentication = new HospitalUserAuthentication(adminEntity);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }

    private HospitalAdminAccessTokenBean getHospitalAdminAccessTokenBean(HttpServletRequest httpRequest) throws ServletException {
        // get token
        String hospitalAdminToken = httpRequest.getHeader("ACCESS_TOKEN");
        logger.debug("get access token "+hospitalAdminToken);
        // get admin by token
        HospitalAdminAccessTokenBean token = tokenService.getToken(hospitalAdminToken);
        logger.debug("get token bean "+token);
        // token invalid
        if (null == token) {
//            throw new ServletException(new BadRequestException(ErrorCode.NURSE360_ACCOUNT_TOKEN_NOT_FOUND));
            return null;
        }
        if (!CommonStatus.ENABLED.equals(token.getStatus())) {
//            throw new ServletException(new BadRequestException(ErrorCode.NURSE360_ACCOUNT_TOKEN_EXPIRED));
            return null;
        }

        // admin invalid
        if (!adminService.existsAdminUser(token.getAdminId(), CommonStatus.ENABLED)) {
//            throw new ServletException(new BadRequestException(ErrorCode.NURSE360_USER_NOT_FOUND));
            return null;
        }
        return token;
    }

}

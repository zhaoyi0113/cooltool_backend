package com.cooltoo.nurse360.hospital.filters;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.nurse360.beans.HospitalAdminAccessTokenBean;
import com.cooltoo.nurse360.beans.HospitalAdminAuthentication;
import com.cooltoo.nurse360.beans.HospitalAdminBean;
import com.cooltoo.nurse360.constants.AdminRole;
import com.cooltoo.nurse360.hospital.service.HospitalAdminAccessTokenService;
import com.cooltoo.nurse360.hospital.service.HospitalAdminRolesService;
import com.cooltoo.nurse360.hospital.service.HospitalAdminService;
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
import java.util.List;

/**
 * Created by zhaolisong on 2016/10/20.
 */
public class Nurse360HospitalManagementFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(Nurse360HospitalManagementFilter.class);

    private HospitalAdminService adminService;
    private HospitalAdminAccessTokenService tokenService;
    private HospitalAdminRolesService adminRolesService;

    private void setServices(ServletRequest request) {
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
        if (adminService == null) {
            adminService = webApplicationContext.getBean(HospitalAdminService.class);
        }
        if (tokenService == null) {
            tokenService = webApplicationContext.getBean(HospitalAdminAccessTokenService.class);
        }
        if (adminRolesService == null) {
            adminRolesService = webApplicationContext.getBean(HospitalAdminRolesService.class);
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

                HospitalAdminBean admin = adminService.getAdminUserWithoutInfo(token.getAdminId());
                List<AdminRole> adminRoles = adminRolesService.getAdminRoleByAdminId(token.getAdminId());
                admin.setProperty(HospitalAdminBean.ROLE, adminRoles);

                HospitalAdminAuthentication authentication = new HospitalAdminAuthentication(admin);
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
            return null;
        }
        if (!CommonStatus.ENABLED.equals(token.getStatus())) {
            return null;
        }

        // admin invalid
        if (!adminService.existsAdminUser(token.getAdminId(), CommonStatus.ENABLED)) {
            return null;
        }
        return token;
    }

}

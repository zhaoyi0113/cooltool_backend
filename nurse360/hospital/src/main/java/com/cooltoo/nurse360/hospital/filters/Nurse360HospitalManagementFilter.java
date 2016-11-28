package com.cooltoo.nurse360.hospital.filters;

import com.cooltoo.constants.AdminUserType;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.nurse360.beans.HospitalAdminAuthentication;
import com.cooltoo.nurse360.beans.HospitalAdminUserDetails;
import com.cooltoo.nurse360.hospital.service.HospitalAdminAccessTokenService;
import com.cooltoo.nurse360.hospital.util.TokenUtil;
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

    private HospitalUserDetailService userDetailService;
    private HospitalAdminAccessTokenService hospitalAdminAccessTokenService;

    private void setServices(ServletRequest request) {
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(request.getServletContext());
        if (userDetailService == null) {
            userDetailService = webApplicationContext.getBean(HospitalUserDetailService.class);
        }
        if (hospitalAdminAccessTokenService == null) {
            hospitalAdminAccessTokenService = webApplicationContext.getBean(HospitalAdminAccessTokenService.class);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        setServices(request);

        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {

            HttpServletRequest httpRequest = (HttpServletRequest) request;
            if (httpRequest.getMethod().equals("OPTIONS")) {
                response.getWriter().print("OK");
                response.getWriter().flush();
                return;
            }

            String hospitalAdminToken = httpRequest.getHeader("ACCESS_TOKEN");
            String token = TokenUtil.newInstance().getToken(hospitalAdminToken);
            AdminUserType userType = TokenUtil.newInstance().getAdminType(hospitalAdminToken);
            Long userId = hospitalAdminAccessTokenService.getUserIdByToken(userType, token);

            if(null!=userId) {
                // save token and adminId
                request.setAttribute(ContextKeys.ADMIN_USER_LOGIN_USER_ID, userId);
                request.setAttribute(ContextKeys.ADMIN_USER_TOKEN, token);
                request.setAttribute(ContextKeys.ADMIN_USER_TYPE, userType);

                HospitalAdminUserDetails userDetails = userDetailService.getUser(userType, userId);
                HospitalAdminAuthentication authentication = new HospitalAdminAuthentication(userDetails);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }
}

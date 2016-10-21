package com.cooltoo.nurse360.filters.hospital;

import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by zhaolisong on 2016/10/20.
 */
public class Nurse360HospitalManagementFilter extends GenericFilterBean {

    public Nurse360HospitalManagementFilter() {
        this.setBeanName(Nurse360HospitalManagementFilter.class.getName());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            // get token
            Object hospitalAdminToken = ((HttpServletRequest)request).getHeader("ACCESS_TOKEN");


            // TODO -- to check user authority
            boolean allowed = "hospital".equalsIgnoreCase(null==hospitalAdminToken? null : hospitalAdminToken.toString());
            //      checking ...


            if (allowed) {
                chain.doFilter(request, response);
            }
            else {
                throw new ServletException(new BadRequestException(ErrorCode.AUTHENTICATION_AUTHORITY_DENIED));
            }
        }
    }
}

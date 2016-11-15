package com.cooltoo.nurse360.hospital.filters;

import com.cooltoo.constants.HeaderKeys;
import com.cooltoo.nurse360.beans.HospitalAdminAccessTokenBean;
import com.cooltoo.nurse360.beans.HospitalAdminAuthentication;
import com.cooltoo.nurse360.beans.HospitalAdminBean;
import com.cooltoo.nurse360.constants.AdminRole;
import com.cooltoo.nurse360.hospital.service.HospitalAdminAccessTokenService;
import com.cooltoo.nurse360.hospital.service.HospitalAdminRolesService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by zhaoyi0113 on 13/11/2016.
 */
public class LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private UserDetailsService userDetailsService;
    private HospitalAdminRolesService adminRolesService;
    private HospitalAdminAccessTokenService adminAccessTokenService;

    public LoginAuthenticationFilter(String urlMapping,
                                     AuthenticationManager authManager,
                                     UserDetailsService userDetailsService,
                                     HospitalAdminRolesService adminRolesService,
                                     HospitalAdminAccessTokenService adminAccessTokenService) {
        super(new AntPathRequestMatcher(urlMapping));
        this.userDetailsService = userDetailsService;
        setAuthenticationManager(authManager);
        this.adminRolesService = adminRolesService;
        this.adminAccessTokenService = adminAccessTokenService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (request.getMethod().equals("OPTIONS")) {
            response.getWriter().print("OK");
            response.getWriter().flush();
            return null;
        }
        String name = request.getParameter("name");
        String password = request.getParameter("password");
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(name, password);
        return getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        HospitalAdminBean userDetails = (HospitalAdminBean) userDetailsService.loadUserByUsername(authResult.getName());
        HospitalAdminAccessTokenBean token = this.adminAccessTokenService.addToken(userDetails.getName(), userDetails.getPassword());
        response.addHeader(HeaderKeys.ACCESS_TOKEN, token.getToken());

        // Add the authentication to the Security context
        List<AdminRole> roles = adminRolesService.getAdminRoleByAdminId(userDetails.getId());
        userDetails.setProperty(HospitalAdminBean.ROLE, roles);

        HospitalAdminAuthentication authentication = new HospitalAdminAuthentication(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

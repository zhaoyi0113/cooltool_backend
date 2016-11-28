package com.cooltoo.nurse360.hospital.filters;

import com.cooltoo.beans.AdminUserTokenAccessBean;
import com.cooltoo.beans.NurseTokenAccessBean;
import com.cooltoo.constants.AdminUserType;
import com.cooltoo.constants.HeaderKeys;
import com.cooltoo.nurse360.beans.HospitalAdminAuthentication;
import com.cooltoo.nurse360.beans.HospitalAdminUserDetails;
import com.cooltoo.nurse360.hospital.service.HospitalAdminAccessTokenService;
import com.cooltoo.services.AdminUserTokenAccessService;
import com.cooltoo.services.NurseTokenAccessService;
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
    private HospitalAdminAccessTokenService hospitalAdminAccessTokenService;


    public LoginAuthenticationFilter(String urlMapping,
                                     AuthenticationManager authManager,
                                     UserDetailsService userDetailsService,
                                     HospitalAdminAccessTokenService hospitalAdminAccessTokenService) {
        super(new AntPathRequestMatcher(urlMapping));
        this.userDetailsService = userDetailsService;
        setAuthenticationManager(authManager);
        this.hospitalAdminAccessTokenService = hospitalAdminAccessTokenService;
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
        Authentication authentication = getAuthenticationManager().authenticate(token);

        return authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        HospitalAdminUserDetails userDetails = null;
        if (authResult instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) authResult;
            if (authenticationToken.getPrincipal() instanceof HospitalAdminUserDetails) {
                userDetails = (HospitalAdminUserDetails) authenticationToken.getPrincipal();
            }
        }
        if (null==userDetails) {
            userDetails = (HospitalAdminUserDetails) userDetailsService.loadUserByUsername(authResult.getName());
        }

        AdminUserType adminType = userDetails.getAdminType();
        String token = hospitalAdminAccessTokenService.addToken(adminType, userDetails.getUsername(), userDetails.getPassword());
        if (null!=token && null!=adminType) {
            response.addHeader(HeaderKeys.ACCESS_TOKEN, token + "_" + adminType);
        }

        // Add the authentication to the Security context
        HospitalAdminAuthentication authentication = new HospitalAdminAuthentication(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

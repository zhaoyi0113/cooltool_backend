package com.cooltoo.nurse360.hospital.filters;

import com.cooltoo.constants.HeaderKeys;
import com.cooltoo.nurse360.beans.HospitalAdminAccessTokenBean;
import com.cooltoo.nurse360.entities.HospitalAdminEntity;
import com.cooltoo.nurse360.hospital.service.HospitalAdminAccessTokenService;
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

/**
 * Created by zhaoyi0113 on 13/11/2016.
 */
public class LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private UserDetailsService userDetailsService;

    private HospitalAdminAccessTokenService adminAccessTokenService;

    public LoginAuthenticationFilter(String urlMapping,
                               UserDetailsService userDetailsService, AuthenticationManager authManager,
                                     HospitalAdminAccessTokenService adminAccessTokenService) {
        super(new AntPathRequestMatcher(urlMapping));
        this.userDetailsService = userDetailsService;
        setAuthenticationManager(authManager);
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
        HospitalAdminEntity userDetails = (HospitalAdminEntity) userDetailsService.loadUserByUsername(authResult.getName());
        HospitalUserAuthentication authentication = new HospitalUserAuthentication(userDetails);
        HospitalAdminAccessTokenBean token = this.adminAccessTokenService.addToken(authentication.getName(), (String)authentication.getCredentials());
        response.addHeader(HeaderKeys.ACCESS_TOKEN, token.getToken());
        // Add the authentication to the Security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}

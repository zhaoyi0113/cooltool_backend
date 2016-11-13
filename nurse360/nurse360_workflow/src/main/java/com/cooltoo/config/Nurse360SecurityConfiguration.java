package com.cooltoo.config;

/**
 * Created by zhaolisong on 2016/10/20.
 */

import com.cooltoo.nurse360.hospital.filters.ExceptionHandlerFilter;
import com.cooltoo.nurse360.hospital.filters.HospitalUserDetailService;
import com.cooltoo.nurse360.hospital.filters.LoginAuthenticationFilter;
import com.cooltoo.nurse360.hospital.filters.Nurse360HospitalManagementFilter;
import com.cooltoo.nurse360.hospital.service.HospitalAdminAccessTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.Header;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Configuration
@EnableWebSecurity
public class Nurse360SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(Nurse360SecurityConfiguration.class);

    private Nurse360HospitalManagementFilter filter = new Nurse360HospitalManagementFilter();

    private ExceptionHandlerFilter exceptionHandlerFilter = new ExceptionHandlerFilter();

    @Autowired
    private HospitalUserDetailService userDetailService;

    @Autowired
    private HospitalAdminAccessTokenService adminAccessTokenService;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/nurse360/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        logger.info("configure http security");
        //
        // add custom filter for user authentication to all hospital module url
        //
        http.csrf().disable().authorizeRequests()
                .antMatchers("/nurse360_hospital/login").permitAll()
                .antMatchers("/nurse360_hospital/admin/**").hasRole("ADMIN")
                .antMatchers("/nurse360_hospital/user/**").hasRole("USER")
                .anyRequest().hasRole("USER")
                .and()
                .addFilterBefore(new LoginAuthenticationFilter(
                        "/nurse360_hospital/login",
                        userDetailService, authenticationManager(),adminAccessTokenService), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlerFilter, Nurse360HospitalManagementFilter.class)

        ;


        HeaderWriter writer = getResponseHeaders();
        http.headers().addHeaderWriter(writer);
    }

    private HeaderWriter getResponseHeaders() {
        List<Header> headers = new ArrayList<>();
        headers.add(new Header("Access-Control-Allow-Origin", "*"));
        headers.add(new Header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE"));
        headers.add(new Header("Access-Control-Allow-Headers", "ACCESS_TOKEN"));
        headers.add(new Header("Access-Control-Allow-Headers", "access_token"));
        headers.add(new Header("Access-Control-Expose-Headers", "ACCESS_TOKEN"));
        return new StaticHeadersWriter(headers);
    }



    @Override
    protected UserDetailsService userDetailsService() {
        return this.userDetailService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailService);
    }

}

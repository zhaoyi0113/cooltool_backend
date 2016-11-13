package com.cooltoo.config;

/**
 * Created by zhaolisong on 2016/10/20.
 */

import com.cooltoo.nurse360.filters.hospital.ExceptionHandlerFilter;
import com.cooltoo.nurse360.filters.hospital.Nurse360HospitalManagementFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
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

    @Autowired
    private Nurse360HospitalManagementFilter filter;

    @Autowired
    private ExceptionHandlerFilter exceptionHandlerFilter;

    @Override
    public void configure(WebSecurity web) throws Exception {
        logger.info("configure wb security");
        web.ignoring().antMatchers("/nurse360/admin");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        logger.info("configure http security");
        //
        // add custom filter for user authentication to all hospital module url
        //
        http.antMatcher("/nurse360/hospital_management/**")
                .addFilterBefore(filter, BasicAuthenticationFilter.class)
                .addFilterBefore(exceptionHandlerFilter, Nurse360HospitalManagementFilter.class);

        //
        // Cross-site request forgery filter disabled
        //
        http.csrf().disable();

        HeaderWriter writer = getResponseHeaders();
        http.headers().addHeaderWriter(writer);
    }

    private HeaderWriter getResponseHeaders() {
        List<Header> headers = new ArrayList<>();
        headers.add(new Header("Access-Control-Allow-Origin", "*"));
        headers.add(new Header("Access-Control-Allow-Methods", "GET, POST, OPTIONS, PUT, DELETE"));
        headers.add(new Header("Access-Control-Allow-Headers", "ACCESS_TOKEN"));
        headers.add(new Header("Access-Control-Allow-Headers", "access_token"));
        return new StaticHeadersWriter(headers);
    }
}

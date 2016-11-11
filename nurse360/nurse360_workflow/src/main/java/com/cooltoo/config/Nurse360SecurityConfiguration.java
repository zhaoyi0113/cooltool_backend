package com.cooltoo.config;

/**
 * Created by zhaolisong on 2016/10/20.
 */

import com.cooltoo.nurse360.filters.hospital.Nurse360HospitalManagementFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 *
 */
//@Configuration
//@EnableWebSecurity
public class Nurse360SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired private Nurse360HospitalManagementFilter filter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //
        // add custom filter for user authentication to all hospital module url
        //
        http.antMatcher("/nurse360/hospital_management/**")
            .addFilterBefore(filter, BasicAuthenticationFilter.class);

        //
        // Cross-site request forgery filter disabled
        //
        http.csrf().disable();

    }
}

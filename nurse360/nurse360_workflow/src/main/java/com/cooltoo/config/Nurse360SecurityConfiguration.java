package com.cooltoo.config;

/**
 * Created by zhaolisong on 2016/10/20.
 */

import com.cooltoo.nurse360.filters.hospital.Nurse360HospitalManagementFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

/**
 *
 */
@Configuration
@EnableWebSecurity
public class Nurse360SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // add custom filter for user authentication to all hospital module url
        http.antMatcher("/nurse360/hospital_management/**")
            .addFilterBefore(new Nurse360HospitalManagementFilter(), BasicAuthenticationFilter.class);

        // Cross-site request forgery filter disabled
        http.csrf().disable();

    }
}

package com.cooltoo.config;

import com.cooltoo.api.*;
import com.cooltoo.filter.LoginAuthentication;
import com.cooltoo.filter.NurseLoginAuthenticationFilter;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

/**
 * Created by yzzhao on 2/22/16.
 */
@Configuration
public class JerseyConfiguration extends ResourceConfig {

    public JerseyConfiguration(){
        register(BadgeAPI.class);
        register(MultiPartFeature.class);
        register(OrderAPI.class );
        register(NurseAPI.class);
        register(PatientAPI.class);
        register(PatientBadgeAPI.class);
        register(HospitalAPI.class);
        register(NurseLoginAuthenticationFilter.class);
    }
}

package com.cooltoo.config;

import com.cooltoo.api.BadgeAPI;
import com.cooltoo.api.NurseAPI;
import com.cooltoo.api.OrderAPI;
<<<<<<< 747cce7cd3a899adcbb29d718ca30ec0da0d034a
import com.cooltoo.api.PatientAPI;
=======
import com.cooltoo.filter.LoginAuthentication;
import com.cooltoo.filter.NurseLoginAuthenticationFilter;
>>>>>>> add access token
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
        register(NurseLoginAuthenticationFilter.class);
    }
}

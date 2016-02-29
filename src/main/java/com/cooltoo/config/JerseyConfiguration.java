package com.cooltoo.config;

import com.cooltoo.api.BadgeAPI;
import com.cooltoo.api.OrderAPI;
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
    }
}

package com.cooltoo.config;

import com.cooltoo.api.HelloworldAPI;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

/**
 * Created by yzzhao on 2/22/16.
 */
@Configuration
public class JerseyConfiguration extends ResourceConfig {

    public JerseyConfiguration(){
        register(HelloworldAPI.class);
    }
}

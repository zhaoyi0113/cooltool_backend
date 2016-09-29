package com.cooltoo.config;


import com.cooltoo.nurse360.filters.BadRequestExceptionMapper;
import com.cooltoo.nurse360.filters.CORSResponseFilter;
import com.cooltoo.nurse360.filters.NurseLoginAuthenticationFilterForNurse360;
import com.cooltoo.nurse360.nurse.api.NurseAPIForNurse360;
import io.swagger.annotations.Api;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.ws.rs.ApplicationPath;

/**
 * Created by yzzhao on 2/22/16.
 */
@Configuration
@EnableSwagger2

@EnableAutoConfiguration
@Api(value = "home", description = "Demo API")
@ApplicationPath("/nurse360")
public class Nurse360JerseyConfiguration extends ResourceConfig {

    public Nurse360JerseyConfiguration() {
        register(MultiPartFeature.class);
        register(BadRequestExceptionMapper.class);
        register(CORSResponseFilter.class);
        register(NurseLoginAuthenticationFilterForNurse360.class);
        register(NurseAPIForNurse360.class);

        property(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, "true");
    }
}
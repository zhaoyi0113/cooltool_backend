package com.cooltoo.config;

import com.cooltoo.api.*;
import com.cooltoo.filter.BadRequestExceptionMapper;
import com.cooltoo.filter.NurseLoginAuthenticationFilter;
import io.swagger.jaxrs.config.BeanConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by yzzhao on 2/22/16.
 */
@Configuration
@EnableSwagger2
public class JerseyConfiguration extends ResourceConfig {

    public JerseyConfiguration(){
        register(BadgeAPI.class);
        register(MultiPartFeature.class);
        register(OrderAPI.class );
        register(NurseAPI.class);
        register(PatientAPI.class);
        register(PatientBadgeAPI.class);
        register(HospitalAPI.class);
        register(HospitalDepartmentAPI.class);
        register(NurseLoginAuthenticationFilter.class);
        register(StorageAPI.class);
        register(NurseLoginAPI.class);
        register(OccupationSkillAPI.class);
        register(NurseFriendsAPI.class);
        register(NurseSkillAPI.class);
        register(BadRequestExceptionMapper.class);

        configureSwagger();
    }

    private void configureSwagger() {
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.2");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost("localhost:8080");
        beanConfig.setBasePath("/");
        beanConfig.setResourcePackage("com.cooltoo.api");
        beanConfig.setPrettyPrint(true);
        beanConfig.setScan(true);
    }

}

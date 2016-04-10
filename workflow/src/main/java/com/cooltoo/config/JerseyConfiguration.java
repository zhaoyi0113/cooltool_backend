package com.cooltoo.config;

import com.cooltoo.admin.api.*;
import com.cooltoo.admin.filter.AdminUserAuthenticationFilter;
import com.cooltoo.admin.filter.CORSResponseFilter;
import com.cooltoo.backend.api.*;
import com.cooltoo.backend.filter.BadRequestExceptionMapper;
import com.cooltoo.backend.filter.NurseLoginAuthenticationFilter;
import io.swagger.annotations.Api;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
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
@ApplicationPath("/nursego")
public class JerseyConfiguration extends ResourceConfig {

    public JerseyConfiguration() {
        register(BadgeAPI.class);
        register(MultiPartFeature.class);
        register(OrderAPI.class);
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
        register(BadRequestExceptionMapper.class);
        register(NurseSpeakAPI.class);
        register(AdminUserAPI.class);
        register(AdminUserLoginAPI.class);
        register(AdminUserAuthenticationFilter.class);
        register(CORSResponseFilter.class);
        register(NurseQualificationManageAPI.class);
        register(OccupationSkillManageAPI.class);
        register(NurseOccupationSkillAPI.class);
        register(SpeakTypeAPI.class);
        register(WorkFileTypeAPI.class);
        register(RegionAPI.class);
        register(NurseHospitalAPI.class);
        register(NurseDepartmentAPI.class);
        register(NurseQualificationAPI.class);
        register(NurseQualificationManageAPI.class);
        register(SuggestionAPI.class);
        register(TagsServiceAPI.class);
        register(NurseTagsServiceAPI.class);

        property(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, "true");

        configureSwagger();
    }

    private void configureSwagger() {
        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.2");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost("localhost:8080");
        beanConfig.setBasePath("/nursego");
        beanConfig.setResourcePackage("com.cooltoo.backend.api");
        beanConfig.setPrettyPrint(true);
        beanConfig.setScan(true);
        register( io.swagger.jaxrs.listing.ApiListingResource.class );
        register( io.swagger.jaxrs.listing.SwaggerSerializers.class );
    }
}

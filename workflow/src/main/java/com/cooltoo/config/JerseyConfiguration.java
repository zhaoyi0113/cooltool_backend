package com.cooltoo.config;

import com.cooltoo.admin.api.*;
import com.cooltoo.admin.filter.AdminUserAuthenticationFilter;
import com.cooltoo.admin.filter.CORSResponseFilter;
import com.cooltoo.backend.api.*;
import com.cooltoo.backend.filter.BadRequestExceptionMapper;
import com.cooltoo.backend.filter.NurseLoginAuthenticationFilter;
import io.swagger.annotations.Api;
import io.swagger.jaxrs.config.BeanConfig;
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
        register(HospitalAPI.class);
        register(HospitalDepartmentAPI.class);
        register(NurseLoginAuthenticationFilter.class);
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
        register(NurseSocialAbilityAPI.class);
        register(SpeakTypeAPI.class);
        register(WorkFileTypeAPI.class);
        register(RegionAPI.class);
        register(NurseHospitalAPI.class);
        register(NurseDepartmentAPI.class);
        register(NurseQualificationAPI.class);
        register(NurseQualificationManageAPI.class);
        register(SuggestionAPI.class);
        register(SuggestionManageAPI.class);
        register(TagsServiceAPI.class);
        register(NurseTagsServiceAPI.class);
        register(NurseManageAPI.class);
        register(CathartProfilePhotoManageAPI.class);
        register(CathartProfilePhotoAPI.class);
        register(ActivityManageAPI.class);
        register(ActivityAPI.class);
        register(OfficialSpeakAPI.class);
        register(NurseDeviceTokensAPI.class);
        register(OfficialConfigAPI.class);
        register(UserSpeakManageAPI.class);
        register(NurseMessageAPI.class);
        register(AppVersionAPI.class);
        register(PlatformVersionAPI.class);
        register(NurseRelationshipAPI.class);
        register(SensitiveWordManageAPI.class);
        register(UserSpeakTopicManagerAPI.class);
        register(NurseSpeakTopicAPI.class);
        register(EmploymentInformationAPI.class);
        register(EmploymentInformationManageAPI.class);


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

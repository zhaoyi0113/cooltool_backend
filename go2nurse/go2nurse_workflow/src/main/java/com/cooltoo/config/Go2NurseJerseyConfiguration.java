package com.cooltoo.config;

import com.cooltoo.go2nurse.admin.api.CourseCategoryManageAPI;
import com.cooltoo.go2nurse.admin.api.CourseManageAPI;
import com.cooltoo.go2nurse.admin.api.CourseRelationManageAPI;
import com.cooltoo.go2nurse.admin.api.DiagnosticManageAPI;
import com.cooltoo.go2nurse.filters.CORSResponseFilter;
import com.cooltoo.go2nurse.patient.api.CourseAPI;
import com.cooltoo.go2nurse.patient.api.CourseRelationAPI;
import com.cooltoo.go2nurse.patient.api.DiagnosticAPI;
import com.cooltoo.go2nurse.patient.api.PatientAPI;
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
@ApplicationPath("/go2nurse")
public class Go2NurseJerseyConfiguration extends ResourceConfig {

    public Go2NurseJerseyConfiguration() {
        register(MultiPartFeature.class);
        register(PatientAPI.class);
        register(CourseAPI.class);
        register(CourseManageAPI.class);
        register(CourseCategoryManageAPI.class);
        register(CORSResponseFilter.class);
        register(CourseRelationAPI.class);
        register(CourseRelationManageAPI.class);
        register(DiagnosticAPI.class);
        register(DiagnosticManageAPI.class);

        property(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, "true");

    }

}

package com.cooltoo.config;

import com.cooltoo.go2nurse.admin.api.*;
import com.cooltoo.go2nurse.filters.BadRequestExceptionMapper;
import com.cooltoo.go2nurse.filters.CORSResponseFilter;
import com.cooltoo.go2nurse.filters.UserLoginAuthenticationFilter;
import com.cooltoo.go2nurse.patient.api.*;
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
        register(UserLoginAuthenticationFilter.class);
        register(MultiPartFeature.class);
        register(CourseAPI.class);
        register(CourseManageAPI.class);
        register(CourseCategoryManageAPI.class);
        register(CORSResponseFilter.class);
        register(CourseRelationAPI.class);
        register(CourseRelationManageAPI.class);
        register(DiagnosticAPI.class);
        register(DiagnosticManageAPI.class);
        register(UserAPI.class);
        register(UserLoginAPI.class);
        register(UserManageAPI.class);
        register(PatientAPI.class);
        register(PatientManageAPI.class);
        register(BadRequestExceptionMapper.class);
        register(UserCourseAPI.class);
        register(UserDiagnosticPointAPI.class);
        register(UserHospitalizedAPI.class);
        register(QuestionnaireServiceAPI.class);
        register(UserQuestionnaireAnswerServiceAPI.class);
        register(UserQuestionnaireServiceAPI.class);
        register(UserAddressAPI.class);


        property(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, "true");

    }

}

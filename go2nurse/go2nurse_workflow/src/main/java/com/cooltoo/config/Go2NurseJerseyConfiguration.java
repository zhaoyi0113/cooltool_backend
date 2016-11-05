package com.cooltoo.config;

import com.cooltoo.go2nurse.admin.api.*;
import com.cooltoo.go2nurse.filters.BadRequestExceptionMapper;
import com.cooltoo.go2nurse.filters.CORSResponseFilter;
import com.cooltoo.go2nurse.filters.UserLoginAuthenticationFilter;
import com.cooltoo.go2nurse.filters.WeChatOpenIdAuthenticationFilter;
import com.cooltoo.go2nurse.patient.api.*;
import com.cooltoo.go2nurse.patient.api.wechat.WeChatHospitalDepartmentAPI;
import com.cooltoo.go2nurse.patient.api.wechat.WeChatPayAPI;
import com.cooltoo.go2nurse.patient.api.wechat.WechatCourseAPI;
import com.cooltoo.go2nurse.patient.api.wechat.WeChatQuestionnaireAnswerAPI;
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
        register(UserLoginAuthenticationFilter.class, 1);
        register(WeChatOpenIdAuthenticationFilter.class, 2);
        register(MultiPartFeature.class);
        register(CourseAPI.class);
        register(CourseManageAPI.class);
        register(CourseCategoryManageAPI.class);
        register(CORSResponseFilter.class);
//        register(CourseRelationAPI.class);
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
        register(QuestionnaireManageServiceAPI.class);
        register(UserQuestionnaireAnswerServiceAPI.class);
        register(UserQuestionnaireAnswerManageServiceAPI.class);
        register(UserAddressAPI.class);
        register(UserReExaminationDateAPI.class);
        register(UserServiceOrderAPI.class);
        register(ServiceCategoryAndItemManageAPI.class);
        register(ServiceOrderManageAPI.class);
        register(DoctorManageAPI.class);
        register(UserDoctorAPI.class);
        register(AppVersionAPI.class);
        register(DoctorClinicDateManageAPI.class);
        register(UserSuggestionAPI.class);
        register(NurseAPIForPatient.class);
        register(PingServiceOrderTestAPI.class);
        register(OpenAppAPI.class);
        register(DoctorAppointmentManageAPI.class);
        register(ConsultationCategoryManageAPI.class);
        register(HospitalAPI.class);
        register(ReExaminationStrategyManageAPI.class);
        register(UserConsultationAPI.class);
        register(UserConsultationManageAPI.class);
        register(AdvertisementAPI.class);
        register(AdvertisementManageAPI.class);
        register(UserDeviceTokensAPI.class);
        register(WeChatAccountManageAPI.class);
        register(WechatCourseAPI.class);
        register(WeChatQuestionnaireAnswerAPI.class);
        register(WeChatPayAPI.class);
        register(DoctorOrderManageAPI.class);
        register(WeChatHospitalDepartmentAPI.class);
        register(CategoryCourseOrderManageAPI.class);
        property(ServerProperties.RESPONSE_SET_STATUS_OVER_SEND_ERROR, "true");

    }

}

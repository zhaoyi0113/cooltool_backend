package com.cooltoo.go2nurse.patient.api.wechat;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.QuestionnaireCategoryBean;
import com.cooltoo.go2nurse.filters.WeChatAuthentication;
import com.cooltoo.go2nurse.openapp.WeChatAccountService;
import com.cooltoo.go2nurse.openapp.WeChatService;
import com.cooltoo.go2nurse.service.QuestionnaireService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by hp on 2016/6/28.
 */
@Path("/wechat/questionnaire")
public class WeChatQuestionnaireAnswerAPI {


    @Autowired
    private QuestionnaireService questionnaireService;
    @Autowired
    private WeChatService weChatService;
    @Autowired
    private WeChatAccountService weChatAccountService;

    @Path("/hospital")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @WeChatAuthentication
    public Response getQuestionnaireOfHospital(@Context HttpServletRequest request) {
        int hosId = Integer.parseInt(""+request.getAttribute(ContextKeys.HOSPITAL_ID));
        List<QuestionnaireCategoryBean> categories = questionnaireService.getCategoryWithQuestionnaireByHospitalId(hosId);
        return Response.ok(categories).build();
    }

}

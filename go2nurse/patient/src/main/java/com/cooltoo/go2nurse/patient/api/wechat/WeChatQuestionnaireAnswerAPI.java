package com.cooltoo.go2nurse.patient.api.wechat;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.QuestionnaireCategoryBean;
import com.cooltoo.go2nurse.beans.WeChatAccountBean;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2016/6/28.
 */
@Path("/wechat/questionnaire")
public class WeChatQuestionnaireAnswerAPI {


    @Autowired private QuestionnaireService questionnaireService;
    @Autowired private WeChatService weChatService;
    @Autowired private WeChatAccountService weChatAccountService;

    @Path("/hospital")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getQuestionnaireOfHospital(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        WeChatAccountBean weChatAccount = getWeChatAccount(userId);
        if (null==weChatAccount) {
            return Response.ok(new ArrayList<>()).build();
        }
        List<QuestionnaireCategoryBean> categories = questionnaireService.getCategoryWithQuestionnaireByHospitalId(weChatAccount.getHospitalId());
        return Response.ok(categories).build();
    }

    private WeChatAccountBean getWeChatAccount(long userId) {
        String appId = weChatService.getAppIdByUserId(userId);
        WeChatAccountBean weChatAccount = weChatAccountService.getWeChatAccountByAppId(appId);
        return weChatAccount;
    }
}

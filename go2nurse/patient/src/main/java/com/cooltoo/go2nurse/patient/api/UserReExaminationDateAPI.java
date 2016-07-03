package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.UserReExaminationDateBean;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.UserReExaminationDateService;
import com.cooltoo.util.NumberUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.web.PageableDefault;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/7/3.
 */
@Path("/user/re_examination_date")
public class UserReExaminationDateAPI {

    @Autowired private UserReExaminationDateService userReExamDateService;

    @Path("/get/hospitalized_group")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getUserHospitalizedGroupReExaminationDate(@Context HttpServletRequest request,
                                                              @QueryParam("hospitalized_group_id") @DefaultValue("-1") long groupId) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<UserReExaminationDateBean> reExamDates = userReExamDateService.getUserHospitalizedGroupReExamination(
                userId, groupId
        );
        return Response.ok(reExamDates).build();
    }

    @Path("/get")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getUserReExaminationDate(@Context HttpServletRequest request) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        Map<Long, List<UserReExaminationDateBean>> reExamDates = userReExamDateService.getUserReExamination(userId);
        return Response.ok(reExamDates).build();
    }

    @Path("/add")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response addUserReExaminationDate(@Context HttpServletRequest request,
                                             @FormParam("hospitalized_group_id") @DefaultValue("-1") long groupId,
                                             @FormParam("re_examination_date") @DefaultValue("1900-01-01 00:00:00") String reExamDate
    ){
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        long dateTime = NumberUtil.getTime(reExamDate, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        if (dateTime<0) {
            return Response.ok().build();
        }
        Date date = new Date(dateTime);
        UserReExaminationDateBean bean = userReExamDateService.addReExamination(userId, groupId, date);
        return Response.ok(bean).build();
    }

    @Path("/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response addUserReExaminationDate(@Context HttpServletRequest request,
                                             @FormParam("re_examination_date_id") @DefaultValue("0") long reExamDateId,
                                             @FormParam("re_examination_date") @DefaultValue("1900-01-01 00:00:00") String reExamDate,
                                             @FormParam("ignore") @DefaultValue("disabled") String strIgnore,
                                             @FormParam("status") @DefaultValue("enabled") String strStatus
    ){
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);

        Date date = null;
        long dateTime = NumberUtil.getTime(reExamDate, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        if (dateTime>0) {
            date = new Date(dateTime);
        }
        CommonStatus ignore = CommonStatus.parseString(strIgnore);
        CommonStatus status = CommonStatus.parseString(strStatus);
        UserReExaminationDateBean bean = userReExamDateService.updateReExamination(reExamDateId, date, ignore, status);
        return Response.ok(bean).build();
    }

}

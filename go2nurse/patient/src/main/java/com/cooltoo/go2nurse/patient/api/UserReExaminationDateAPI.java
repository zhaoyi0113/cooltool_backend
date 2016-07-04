package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ContextKeys;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.beans.UserReExaminationDateBean;
import com.cooltoo.go2nurse.filters.LoginAuthentication;
import com.cooltoo.go2nurse.service.UserReExaminationDateService;
import com.cooltoo.util.NumberUtil;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Path("/get/group")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response getUserGroupReExaminationDate(@Context HttpServletRequest request,
                                                              @QueryParam("group_id") @DefaultValue("-1") long groupId) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        List<UserReExaminationDateBean> reExamDates = userReExamDateService.getUserGroupReExamination(
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
                                             @FormParam("group_id") @DefaultValue("-1") long groupId,
                                             @FormParam("hospitalized_group_id") @DefaultValue("-1") long hospitalizedGroupId,
                                             @FormParam("re_examination_date") @DefaultValue("") String reExamDate
    ){
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        long dateTime = NumberUtil.getTime(reExamDate, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        Date date;
        if (dateTime<0) {
            date = null;
        }
        else {
            date = new Date(dateTime);
        }
        UserReExaminationDateBean bean = userReExamDateService.addReExamination(userId, groupId, hospitalizedGroupId, date, YesNoEnum.NO);
        return Response.ok(bean).build();
    }


    @Path("/add/by_start_date")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response addUserReExaminationDateByStartDate(@Context HttpServletRequest request,
                                                        @FormParam("start_date") @DefaultValue("") String reExamStartDate,
                                                        @FormParam("has_operation") @DefaultValue("0") int iHasOperation
    ) {
        long userId = (Long) request.getAttribute(ContextKeys.USER_LOGIN_USER_ID);
        YesNoEnum hasOperation = YesNoEnum.parseInt(iHasOperation);
        long dateTime = NumberUtil.getTime(reExamStartDate, NumberUtil.DATE_YYYY_MM_DD_HH_MM_SS);
        Date date;
        if (dateTime<0) {
            date = null;
        }
        else {
            date = new Date(dateTime);
        }

        List<UserReExaminationDateBean> reExamDates = userReExamDateService.addReExaminationByStartDate(userId, 0, date, hasOperation);
        return Response.ok(reExamDates).build();
    }

    @Path("/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @LoginAuthentication(requireUserLogin = true)
    public Response addUserReExaminationDate(@Context HttpServletRequest request,
                                             @FormParam("re_examination_date_id") @DefaultValue("0") long reExamDateId,
                                             @FormParam("re_examination_date") @DefaultValue("") String reExamDate,
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

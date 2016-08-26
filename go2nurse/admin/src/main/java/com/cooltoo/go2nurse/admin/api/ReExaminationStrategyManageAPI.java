package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.go2nurse.beans.ReExaminationStrategyBean;
import com.cooltoo.go2nurse.service.ReExaminationStrategyService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by hp on 2016/8/26.
 */
@Path("/admin/department/re_examination_strategy")
public class ReExaminationStrategyManageAPI {

    @Autowired private ReExaminationStrategyService strategyService;

    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countByStatus(@Context HttpServletRequest request,
                                  @QueryParam("statues") @DefaultValue("") String statuses
    ) {
        List<CommonStatus> statusList = VerifyUtil.parseCommonStatus(statuses);
        long count = strategyService.countByStatus(statusList);
        return Response.ok(count).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countByStatus(@Context HttpServletRequest request,
                                  @QueryParam("statues") @DefaultValue("") String statuses,
                                  @QueryParam("index") @DefaultValue("0") int pageIndex,
                                  @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<CommonStatus> statusList = VerifyUtil.parseCommonStatus(statuses);
        List<ReExaminationStrategyBean> beans = strategyService.getByStatus(statusList, pageIndex, sizePerPage);
        return Response.ok(beans).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReExamination(@Context HttpServletRequest request,
                                     @FormParam("department_id") @DefaultValue("0") int departmentId,
                                     @FormParam("is_recycled") @DefaultValue("false") boolean isRecycled,
                                     @FormParam("strategy_day") @DefaultValue("") String strategyDay
    ) {
        strategyService.addReExaminationStrategyForDepartment(departmentId, isRecycled, strategyDay);
        return Response.ok().build();
    }

    @Path("/edit")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response editReExamination(@Context HttpServletRequest request,
                                      @FormParam("re_examination_id") @DefaultValue("0") int reExaminationId,
                                      @FormParam("is_recycled") @DefaultValue("") String strIsRecycled,
                                      @FormParam("strategy_day") @DefaultValue("") String strategyDay,
                                      @FormParam("status") @DefaultValue("") String status
    ) {
        Boolean isRecycled = VerifyUtil.isStringEmpty(strIsRecycled) ? null : Boolean.valueOf(strIsRecycled);
        strategyService.updateReExaminationStrategyForDepartment(reExaminationId, isRecycled, strategyDay, status);
        return Response.ok().build();
    }
}

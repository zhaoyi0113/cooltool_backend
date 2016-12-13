package com.cooltoo.nurse360.admin.api;

import com.cooltoo.go2nurse.beans.NurseWalletBean;
import com.cooltoo.go2nurse.constants.WalletInOutType;
import com.cooltoo.go2nurse.constants.WalletProcess;
import com.cooltoo.go2nurse.service.NurseWalletService;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by zhaolisong on 13/12/2016.
 */
@Path("/admin/nurse/wallet")
public class NurseWalletManageAPI {

    @Autowired private NurseWalletService nurseWalletService;

    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countFlowRecord(@Context HttpServletRequest request,
                                    @QueryParam("nurse_id") @DefaultValue("") String nurseId,
                                    @QueryParam("reason") @DefaultValue("")     String reason, /**/
                                    @QueryParam("process") @DefaultValue("")    String process,/**/
                                    @QueryParam("summary") @DefaultValue("")    String summary
    ) {
        Long lNurseId = VerifyUtil.isIds(nurseId) ? VerifyUtil.parseLongIds(nurseId).get(0) : null;
        WalletInOutType eReason = WalletInOutType.parseString(reason);
        WalletProcess eProcess = WalletProcess.parseString(process);
        long recordCount = nurseWalletService.countNurseWalletFlowRecord(
                lNurseId,
                eReason,
                eProcess,
                summary);
        return Response.ok(recordCount).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getFlowRecord(@Context HttpServletRequest request,
                                  @QueryParam("nurse_id") @DefaultValue("") String nurseId,
                                  @QueryParam("reason") @DefaultValue("")     String reason, /**/
                                  @QueryParam("process") @DefaultValue("")    String process,/**/
                                  @QueryParam("summary") @DefaultValue("")    String summary,
                                  @QueryParam("index") @DefaultValue("0") int pageIndex,
                                  @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        Long lNurseId = VerifyUtil.isIds(nurseId) ? VerifyUtil.parseLongIds(nurseId).get(0) : null;
        WalletInOutType eReason = WalletInOutType.parseString(reason);
        WalletProcess eProcess = WalletProcess.parseString(process);
        List<NurseWalletBean> flowRecords = nurseWalletService.getNurseWalletFlowRecord(
                lNurseId,
                eReason,
                eProcess,
                summary,
                pageIndex, sizePerPage);
        return Response.ok(flowRecords).build();
    }

    @Path("/withdraw/completed/{flow_record_id}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response completedWithdraw(@Context HttpServletRequest request,
                                      @PathParam("flow_record_id") @DefaultValue("0") long flowRecordId
    ) {
        NurseWalletBean record = nurseWalletService.updateWalletInOutStatus(flowRecordId, WalletProcess.COMPLETED);
        return Response.ok(record).build();
    }

    @Path("/withdraw/refused/{flow_record_id}")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response refusedWithdraw(@Context HttpServletRequest request,
                                    @PathParam("flow_record_id") @DefaultValue("0") long flowRecordId
    ) {
        NurseWalletBean record = nurseWalletService.updateWalletInOutStatus(flowRecordId, WalletProcess.REFUSED);
        return Response.ok(record).build();
    }


}

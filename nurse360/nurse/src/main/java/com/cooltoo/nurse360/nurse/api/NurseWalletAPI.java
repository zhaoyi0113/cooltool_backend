package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.exception.*;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.go2nurse.beans.NurseWalletBean;
import com.cooltoo.go2nurse.constants.WalletInOutType;
import com.cooltoo.go2nurse.constants.WalletProcess;
import com.cooltoo.go2nurse.service.NurseWalletService;
import com.cooltoo.nurse360.filters.Nurse360LoginAuthentication;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by zhaolisong on 12/12/2016.
 */
@Path("/nurse/wallet")
public class NurseWalletAPI {

    @Autowired private NurseWalletService nurseWalletService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response getNurseWalletRecord(@Context HttpServletRequest request,
                                         @QueryParam("index") @DefaultValue("0") int pageIndex,
                                         @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        List<NurseWalletBean> wallerRecord = nurseWalletService.getNurseWalletRecord(nurseId, pageIndex, sizePerPage);
        return Response.ok(wallerRecord).build();
    }

    @Path("/withdraw")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response deleteWalletRecord(@Context HttpServletRequest request,
                                       @FormParam("withdraw_cash") @DefaultValue("0") String withdrawCash
    ) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        Long lWithdrawCase = VerifyUtil.isIds(withdrawCash) ? VerifyUtil.parseLongIds(withdrawCash).get(0) : 0L;
        String amount = VerifyUtil.parsePrice(lWithdrawCase.intValue());

        long canWithdraw = nurseWalletService.getNurseWalletBalance(nurseId);
        if (canWithdraw<lWithdrawCase) {
            throw new BadRequestException(ErrorCode.NURSE360_AMOUNT_EXCEEDS_BALANCE);
        }
        NurseWalletBean bean = nurseWalletService.recordWalletInOut(nurseId, -lWithdrawCase, "提现 "+amount, WalletProcess.PROCESSING, WalletInOutType.WITHDRAW, 0L);
        return Response.ok(bean).build();
    }


    @Path("/{wallet_record_id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Nurse360LoginAuthentication(requireNurseLogin = true)
    public Response deleteWalletRecord(@Context HttpServletRequest request,
                                       @PathParam("wallet_record_id") @DefaultValue("0") long walletRecordId
    ) {
        long nurseId = (Long)request.getAttribute(ContextKeys.NURSE_LOGIN_USER_ID);
        Long recordId = nurseWalletService.deleteWalletInOut(nurseId, walletRecordId);
        return Response.ok(recordId).build();
    }
}

package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.constants.ContextKeys;
import com.cooltoo.go2nurse.beans.NurseWalletBean;
import com.cooltoo.go2nurse.service.NurseWalletService;
import com.cooltoo.nurse360.filters.Nurse360LoginAuthentication;
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
public class NurseWalletRecordAPI {

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

package com.cooltoo.go2nurse.admin.api;

import com.cooltoo.go2nurse.beans.WeChatAccountBean;
import com.cooltoo.go2nurse.openapp.WeChatAccountService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by zhaolisong on 16/10/8.
 */
@Path("/admin/we_chat_account")
public class WeChatAccountManageAPI {

    @Autowired
    private WeChatAccountService weChatAccountService;

    @Path("/{account_id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWeChatAccountByAccountId(@Context HttpServletRequest request,
                                                @PathParam("account_id") @DefaultValue("0") int accountId
    ) {
        WeChatAccountBean account = weChatAccountService.getWeChatAccountById(accountId);
        return Response.ok(account).build();
    }

    @Path("/count")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response countWeChatAccountByStatus(@Context HttpServletRequest request,
                                               @QueryParam("status") @DefaultValue("all") String status /* all, enabled, disabled, deleted */
    ) {
        long count = weChatAccountService.countWeChatAccount(status);
        return Response.ok(count).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWeChatAccountByStatus(@Context HttpServletRequest request,
                                             @QueryParam("status") @DefaultValue("all") String status, /* all, enabled, disabled, deleted */
                                             @QueryParam("index") @DefaultValue("0") int pageIndex,
                                             @QueryParam("number") @DefaultValue("10") int sizePerPage
    ) {
        List<WeChatAccountBean> account = weChatAccountService.getWeChatAccount(status, pageIndex, sizePerPage);
        return Response.ok(account).build();
    }

    @Path("/{account_id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteWeChatAccountByAccountId(@Context HttpServletRequest request,
                                                   @PathParam("account_id") @DefaultValue("0") int accountId
    ) {
        WeChatAccountBean account = weChatAccountService.deleteWeChatAccountById(accountId);
        return Response.ok(account).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response addClinicDate(@Context HttpServletRequest request,
                                  @FormParam("app_id") @DefaultValue("") String appId,
                                  @FormParam("app_secret") @DefaultValue("") String appSecret,
                                  @FormParam("mch_id") @DefaultValue("") String mchId,
                                  @FormParam("name") @DefaultValue("") String name,
                                  @FormParam("hospital_id") @DefaultValue("0") int hospitalId,
                                  @FormParam("department_id") @DefaultValue("0") int departmentId

    ) {
        WeChatAccountBean bean = weChatAccountService.addWeChatAccount(appId, appSecret, mchId, name, hospitalId, departmentId);
        return Response.ok(bean).build();
    }

    @Path("/edit")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response editClinicDate(@Context HttpServletRequest request,
                                   @FormParam("account_id") @DefaultValue("0") int accountId,
                                   @FormParam("app_secret") @DefaultValue("") String appSecret,
                                   @FormParam("mch_id") @DefaultValue("") String mchId,
                                   @FormParam("name") @DefaultValue("") String name,
                                   @FormParam("hospital_id") @DefaultValue("0") int hospitalId,
                                   @FormParam("department_id") @DefaultValue("0") int departmentId,
                                   @FormParam("status") @DefaultValue("") String status /* enabled, disabled, deleted */
    ) {
        WeChatAccountBean bean = weChatAccountService.updateWeChatAccount(accountId, appSecret, mchId, name, status, hospitalId, departmentId);
        return Response.ok().build();
    }
}

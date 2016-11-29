package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.DeviceType;
import com.cooltoo.go2nurse.beans.UserDeviceTokensBean;
import com.cooltoo.go2nurse.service.UserDeviceTokensService;
import com.cooltoo.go2nurse.service.notification.MessageBean;
import com.cooltoo.go2nurse.service.notification.MessageType;
import com.cooltoo.go2nurse.service.notification.Notifier;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/29.
 */
@Path("/notifier")
public class NotiferAPI {

    @Autowired private Notifier notifier;
    @Autowired private UserDeviceTokensService deviceTokensService;

    @Path("/test")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public void notifier(@FormParam("device_type") String deviceType,
                         @FormParam("token") String token,
                         @FormParam("alert") String alert,
                         @FormParam("descr") String description
    ) {
        MessageBean message = new MessageBean();
        message.setAlertBody(alert);
        message.setDescription(description);
        message.setType("TEST_go2nurse");
        message.setStatus("TEST Status_go2nurse");
        message.setRelativeId(0);
        notifier.notifyDevice(token, DeviceType.parseString(deviceType), message);
    }


    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response notifyMessage(@DefaultValue("0") @FormParam("user_id")    String strUserIds,
                                  @DefaultValue("")  @FormParam("alert")       String alert,
                                  @DefaultValue("")  @FormParam("description") String description,
                                  @DefaultValue("")  @FormParam("message_type")String messageType,
                                  @DefaultValue("0") @FormParam("relative_id")   long relativeId,
                                  @DefaultValue("")  @FormParam("status")      String status

    ) {
        List<Long> userIds = VerifyUtil.isIds(strUserIds) ? VerifyUtil.parseLongIds(strUserIds) : null;
        MessageType msgType = MessageType.parseString(messageType);
        MessageBean msg = new MessageBean();
        msg.setAlertBody(alert);
        msg.setType(null==msgType ? null : msgType.name());
        msg.setStatus(status);
        msg.setDescription(description);
        msg.setRelativeId(relativeId);

        if (null!=msgType && 0!=relativeId && !userIds.isEmpty()) {
            List<UserDeviceTokensBean> tokens = deviceTokensService.getUserDeviceTokens(userIds);
            notifier.notifyUserPatient(tokens, msg);
        }
        return Response.ok().build();
    }
}

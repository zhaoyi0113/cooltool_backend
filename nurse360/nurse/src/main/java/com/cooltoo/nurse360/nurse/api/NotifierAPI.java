package com.cooltoo.nurse360.nurse.api;

import com.cooltoo.constants.DeviceType;
import com.cooltoo.go2nurse.beans.Nurse360DeviceTokensBean;
import com.cooltoo.go2nurse.service.Nurse360DeviceTokensService;
import com.cooltoo.go2nurse.service.notification.MessageBean;
import com.cooltoo.go2nurse.service.notification.MessageType;
import com.cooltoo.go2nurse.service.notification.Notifier;
import com.cooltoo.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhaolisong on 2016/11/29.
 */
@Path("/notifier")
public class NotifierAPI {

    @Autowired private Notifier notifier;
    @Autowired private Nurse360DeviceTokensService deviceTokensService;

    @Path("/test")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public void notifier(@FormParam("device_type") String deviceType,
                         @FormParam("token") String token,
                         @FormParam("alert") String alert,
                         @FormParam("descr") String description,
                         @FormParam("type")  String type,
                         @FormParam("status")String status,
                         @FormParam("relativeId") long relativeId
    ) {
        MessageBean message = new MessageBean();
        message.setAlertBody(alert);
        message.setDescription(description);
        message.setType(type);
        message.setStatus(status);
        message.setRelativeId(relativeId);

        Nurse360DeviceTokensBean deviceToken = new Nurse360DeviceTokensBean();
        deviceToken.setDeviceType(DeviceType.parseString(deviceType));
        deviceToken.setDeviceToken(token);
        deviceToken.setUserId(0L);
        notifier.notifyNurse360Nurse(Arrays.asList(new Nurse360DeviceTokensBean[]{deviceToken}), message);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response notifyMessage(@DefaultValue("0") @FormParam("nurse_id")    String strNurseIds,
                                  @DefaultValue("")  @FormParam("alert")       String alert,
                                  @DefaultValue("")  @FormParam("description") String description,
                                  @DefaultValue("")  @FormParam("message_type")String messageType,
                                  @DefaultValue("0") @FormParam("relative_id")   long relativeId,
                                  @DefaultValue("")  @FormParam("status")      String status

    ) {
        List<Long> nurseIds = VerifyUtil.isIds(strNurseIds) ? VerifyUtil.parseLongIds(strNurseIds) : null;
        MessageType msgType = MessageType.parseString(messageType);
        MessageBean msg = new MessageBean();
        msg.setAlertBody(alert);
        msg.setType(null==msgType ? null : msgType.name());
        msg.setStatus(status);
        msg.setDescription(description);
        msg.setRelativeId(relativeId);

        if (null!=msgType && 0!=relativeId && !nurseIds.isEmpty()) {
            List<Nurse360DeviceTokensBean> tokens = deviceTokensService.getUserDeviceToknes(nurseIds);
            notifier.notifyNurse360Nurse(tokens, msg);
        }
        return Response.ok().build();
    }
}

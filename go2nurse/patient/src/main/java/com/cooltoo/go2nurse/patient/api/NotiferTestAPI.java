package com.cooltoo.go2nurse.patient.api;

import com.cooltoo.constants.DeviceType;
import com.cooltoo.go2nurse.service.notification.MessageBean;
import com.cooltoo.go2nurse.service.notification.Notifier;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by zhaolisong on 2016/11/29.
 */
@Path("/user/notifier/test")
public class NotiferTestAPI {

    @Autowired private Notifier notifier;

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
        message.setType("TEST");
        message.setStatus("TEST Status");
        message.setRelativeId(0);
        notifier.notifyDevice(token, DeviceType.parseString(deviceType), message);
    }
}

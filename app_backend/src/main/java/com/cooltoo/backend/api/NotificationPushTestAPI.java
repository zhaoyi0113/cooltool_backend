package com.cooltoo.backend.api;

import com.cooltoo.backend.services.notification.NotificationCenter;
import com.cooltoo.backend.services.notification.NotificationCode;
import com.cooltoo.backend.services.notification.NotificationType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

/**
 * Created by zhaoyi0113 on 9/13/16.
 */
@Path("/push/notification/test")
public class NotificationPushTestAPI {

    @Autowired
    private NotificationCenter notificationCenter;

    @POST
    public Response pushNotification(@FormParam("device_id") String deviceId, @FormParam("text") String text,
                                     @FormParam("notification_code") int code){
        notificationCenter.publishToDevice(deviceId, text, null, code+"", NotificationType.ALERT);
        return Response.ok().build();
    }
}

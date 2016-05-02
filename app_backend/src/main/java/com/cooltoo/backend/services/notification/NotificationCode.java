package com.cooltoo.backend.services.notification;

/**
 * Created by yzzhao on 5/1/16.
 */
final  public class NotificationCode {

    private static final int BASE_CODE = 1000;

    public static final String REQUEST_ADD_FRIEND_CODE = String.valueOf(BASE_CODE + 1);

    public static final String APPROVE_ADD_FRIEND_CODE = String.valueOf(BASE_CODE + 2);

    public static final String OFFICIAL_SPEAK_CODE = String.valueOf(BASE_CODE + 3);

    public static final String PUBLISH_ACTIVITY = String.valueOf(BASE_CODE + 4);

    public static final String SPEAK_ID_FIELD = "speak_id";

}

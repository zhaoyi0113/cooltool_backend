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

    public static final String PUBLISH_SMUG_SPEAK_CODE = String.valueOf(BASE_CODE+5);

    public static final String PUBLISH_COMPLAIN_SPEAK_CODE = String.valueOf(BASE_CODE+6);

    public static final String PUBLISH_QUESTION_SPEAK_CODE = String.valueOf(BASE_CODE+7);

    public static final String PUBLISH_MESSAGE_CODE = String.valueOf(BASE_CODE+8);

    public static final String PUBLISH_MESSAGE_COUNT_CODE = String.valueOf(BASE_CODE+9);

    public static final String SPEAK_ID_FIELD = "speak_id";

}

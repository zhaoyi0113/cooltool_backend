package com.cooltoo.constants;

/**
 * Created by hp on 2016/6/23.
 */
public enum CCVideoStatus {
    OTHER, // 其他
    OK, // 成功
    NETWORK_ERROR, // 网络错误
    INVALID_REQUEST, // 用户输入参数错误
    SPACE_NOT_ENOUGH, // 用户剩余空间不足
    SERVICE_EXPIRED, // 用户服务终止
    PROCESS_FAIL, // 服务器处理失败
    TOO_MANY_REQUEST, // 访问过于频繁
    PERMISSION_DENY // 用户服务无权限
    ;

    public static CCVideoStatus parseString(String type) {
        CCVideoStatus status = null;
        if (OTHER.name().equalsIgnoreCase(type)) {
            status = OTHER;
        }
        else if (OK.name().equalsIgnoreCase(type)) {
            status = OK;
        }
        else if (NETWORK_ERROR.name().equalsIgnoreCase(type)) {
            status = NETWORK_ERROR;
        }
        else if (INVALID_REQUEST.name().equalsIgnoreCase(type)) {
            status = INVALID_REQUEST;
        }
        else if (SPACE_NOT_ENOUGH.name().equalsIgnoreCase(type)) {
            status = SPACE_NOT_ENOUGH;
        }
        else if (SERVICE_EXPIRED.name().equalsIgnoreCase(type)) {
            status = SERVICE_EXPIRED;
        }
        else if (PROCESS_FAIL.name().equalsIgnoreCase(type)) {
            status = PROCESS_FAIL;
        }
        else if (TOO_MANY_REQUEST.name().equalsIgnoreCase(type)) {
            status = TOO_MANY_REQUEST;
        }
        else if (PERMISSION_DENY.name().equalsIgnoreCase(type)) {
            status = PERMISSION_DENY;
        }
        return status;
    }

    public static CCVideoStatus parseInt(int type) {
        CCVideoStatus status = null;
        if (OTHER.ordinal() == type) {
            status = OTHER;
        }
        else if (OK.ordinal() == type) {
            status = OK;
        }
        else if (NETWORK_ERROR.ordinal() == type) {
            status = NETWORK_ERROR;
        }
        else if (INVALID_REQUEST.ordinal() == type) {
            status = INVALID_REQUEST;
        }
        else if (SPACE_NOT_ENOUGH.ordinal() == type) {
            status = SPACE_NOT_ENOUGH;
        }
        else if (SERVICE_EXPIRED.ordinal() == type) {
            status = SERVICE_EXPIRED;
        }
        else if (PROCESS_FAIL.ordinal() == type) {
            status = PROCESS_FAIL;
        }
        else if (TOO_MANY_REQUEST.ordinal() == type) {
            status = TOO_MANY_REQUEST;
        }
        else if (PERMISSION_DENY.ordinal() == type) {
            status = PERMISSION_DENY;
        }
        return status;
    }
}

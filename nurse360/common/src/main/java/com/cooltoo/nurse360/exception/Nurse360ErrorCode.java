package com.cooltoo.nurse360.exception;

import javax.ws.rs.core.Response;

/**
 * Created by zhaolisong on 2016/11/11.
 */
public enum Nurse360ErrorCode {

    //
    //
    //  Error Code start from 3000
    //
    //
/*


    * 200 OK, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.1">HTTP/1.1 documentation</a>}.
    OK(200, "OK"),
     * 201 Created, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.2">HTTP/1.1 documentation</a>}.
    CREATED(201, "Created"),
     * 202 Accepted, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.3">HTTP/1.1 documentation</a>}.
    ACCEPTED(202, "Accepted"),
     * 204 No Content, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.5">HTTP/1.1 documentation</a>}.
    NO_CONTENT(204, "No Content"),
     * 205 Reset Content, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.6">HTTP/1.1 documentation</a>}.
    RESET_CONTENT(205, "Reset Content"),
     * 206 Reset Content, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.2.7">HTTP/1.1 documentation</a>}.
    PARTIAL_CONTENT(206, "Partial Content"),
     * 301 Moved Permanently, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.2">HTTP/1.1 documentation</a>}.
    MOVED_PERMANENTLY(301, "Moved Permanently"),
     * 302 Found, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.3">HTTP/1.1 documentation</a>}.
    FOUND(302, "Found"),
     * 303 See Other, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.4">HTTP/1.1 documentation</a>}.
    SEE_OTHER(303, "See Other"),
     * 304 Not Modified, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5">HTTP/1.1 documentation</a>}.
    NOT_MODIFIED(304, "Not Modified"),
     * 305 Use Proxy, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.6">HTTP/1.1 documentation</a>}.
    USE_PROXY(305, "Use Proxy"),
     * 307 Temporary Redirect, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.8">HTTP/1.1 documentation</a>}.
    TEMPORARY_REDIRECT(307, "Temporary Redirect"),
     * 400 Bad Request, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.1">HTTP/1.1 documentation</a>}.
    BAD_REQUEST(400, "Bad Request"),
     * 401 Unauthorized, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.2">HTTP/1.1 documentation</a>}.
    UNAUTHORIZED(401, "Unauthorized"),
     * 402 Payment Required, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.3">HTTP/1.1 documentation</a>}.
    PAYMENT_REQUIRED(402, "Payment Required"),
     * 403 Forbidden, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.4">HTTP/1.1 documentation</a>}.
    FORBIDDEN(403, "Forbidden"),
     * 404 Not Found, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.5">HTTP/1.1 documentation</a>}.
    NOT_FOUND(404, "Not Found"),
     * 405 Method Not Allowed, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.6">HTTP/1.1 documentation</a>}.
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
     * 406 Not Acceptable, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.7">HTTP/1.1 documentation</a>}.
    NOT_ACCEPTABLE(406, "Not Acceptable"),
     * 407 Proxy Authentication Required, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.8">HTTP/1.1 documentation</a>}.
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
     * 408 Request Timeout, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.9">HTTP/1.1 documentation</a>}.
    REQUEST_TIMEOUT(408, "Request Timeout"),
     * 409 Conflict, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.10">HTTP/1.1 documentation</a>}.
    CONFLICT(409, "Conflict"),
     * 410 Gone, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.11">HTTP/1.1 documentation</a>}.
    GONE(410, "Gone"),
     * 411 Length Required, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.12">HTTP/1.1 documentation</a>}.
    LENGTH_REQUIRED(411, "Length Required"),
     * 412 Precondition Failed, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.13">HTTP/1.1 documentation</a>}.
    PRECONDITION_FAILED(412, "Precondition Failed"),
     * 413 Request Entity Too Large, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.14">HTTP/1.1 documentation</a>}.
    REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
     * 414 Request-URI Too Long, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.15">HTTP/1.1 documentation</a>}.
    REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),
     * 415 Unsupported Media Type, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.16">HTTP/1.1 documentation</a>}.
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
     * 416 Requested Range Not Satisfiable, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.17">HTTP/1.1 documentation</a>}.
    REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
     * 417 Expectation Failed, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.4.18">HTTP/1.1 documentation</a>}.
    EXPECTATION_FAILED(417, "Expectation Failed"),
     * 500 Internal Server Error, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.1">HTTP/1.1 documentation</a>}.
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
     * 501 Not Implemented, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.2">HTTP/1.1 documentation</a>}.
    NOT_IMPLEMENTED(501, "Not Implemented"),
     * 502 Bad Gateway, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.3">HTTP/1.1 documentation</a>}.
    BAD_GATEWAY(502, "Bad Gateway"),
     * 503 Service Unavailable, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.4">HTTP/1.1 documentation</a>}.
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
     * 504 Gateway Timeout, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.5">HTTP/1.1 documentation</a>}.
    GATEWAY_TIMEOUT(504, "Gateway Timeout"),
     * 505 HTTP Version Not Supported, see {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.5.6">HTTP/1.1 documentation</a>}.
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported");
*/

    // * 201 ~ 210, system error
    UNKNOWN(3001, Response.Status.BAD_REQUEST, "系统未知错误", "System unknown error."),
    SYSTEM_ERROR(3002, Response.Status.INTERNAL_SERVER_ERROR, "系统错误", "System error."),
    DATABASE_ERROR(3003, Response.Status.INTERNAL_SERVER_ERROR, "数据库错误", "DataBase error."),
    DATA_NOT_CONSISTENT(3004, Response.Status.INTERNAL_SERVER_ERROR, "数据不一致错误", "Data not consistent error."),
    NOT_PERMITTED(3005, Response.Status.FORBIDDEN, "不允许访问", "Request not permitted"),
    UNAUTHORIZED(3006, Response.Status.NOT_ACCEPTABLE, "未授权的访问", "Request Unauthorized"),
    ILLEGAL_REQUEST(3007, Response.Status.BAD_REQUEST, "非法请求", "Illegal request"),
    FILTER_REQUEST_BODY_ERROR(3008, Response.Status.BAD_REQUEST, "请求内容处理异常", "deal with request object error."),
    ACCOUNT_TOKEN_EXPIRED(3009, Response.Status.NOT_ACCEPTABLE, "account token已过期", "account token has been expired!"),

    // * 210 ~ 299, business related generic error
    RESOURCE_NOT_FOUND(210, Response.Status.NOT_FOUND, "资源不存在", "Resource not found"),
    AUTHENTICATION_EXISTED(211, Response.Status.CONFLICT, "账号已存在", "Account existed"),
    AUTHENTICATION_NOT_EXISTED(212, Response.Status.NOT_FOUND, "账号不存在", "Account don't exist"),
    AUTHENTICATION_INVALIDATE(213, Response.Status.NOT_ACCEPTABLE, "账号未激活", "Account has not activated"),
    AUTHENTICATION_FORBIDDEN(214, Response.Status.FORBIDDEN, "账号已禁用或已失效,请与管理员联系",
            "Account has been disabled or deleted, Please contact the administrator"),
    EMAIL_FORMAT_ERROR(215, Response.Status.NOT_ACCEPTABLE, "邮箱格式不正确", "Email format error"),
    LOGIN_AUTHENTICATION_NOT_EXISTED(216, Response.Status.NOT_FOUND, "账号不存在,或用户名、密码不正确",
            "Account don't existed, or account name or password not correct."),
    INVALID_PASSWORD(217, Response.Status.FORBIDDEN, "密码不正确", "Invalid password."),
    PASSWORD_EXPIRED(218, Response.Status.FORBIDDEN, "密码已失效", "Password expired."),
    UNSUPPORTED_ENCODING(219, Response.Status.BAD_REQUEST, "不支持的编码格式", "Unsupported encoding."),
    DATA_ERROR(220, Response.Status.BAD_REQUEST, "数据错误", "data error!"),
    USER_NOT_EXISTED(221, Response.Status.BAD_REQUEST, "用户不存在", "user doesn't exist"),
    NOT_LOGIN(222, Response.Status.BAD_REQUEST, "未登陆", "Not Login"),
    BADGE_NOT_EXIST(223, Response.Status.BAD_REQUEST, "徽章不存在", "Badge not exist"),
    ORDER_NOT_EXIST(224, Response.Status.BAD_REQUEST, "订单不存在", "Order not exist"),
    RECORD_NOT_EXIST(226, Response.Status.BAD_REQUEST, "记录不存在", "Record not exist"),
    HOSPITAL_NOT_EXIST(227, Response.Status.BAD_REQUEST, "医院不存在", "Hospital not exist"),
    HOSPITAL_DEPARTMENT_NOT_EXIST(228, Response.Status.BAD_REQUEST, "科室不存在", "Hospital department not exist"),
    NO_SUCH_OCCUPATION(229, Response.Status.BAD_REQUEST, "职业不存在", "No such occupation"),
    SKILL_NOT_EXIST(230, Response.Status.BAD_REQUEST, "技能不存在", "Skill not exist"),
    SPEAK_CONTENT_IS_EMPTY(231, Response.Status.BAD_REQUEST, "发言为空", "Speak content is empty"),
    SPEAK_CONTENT_NOT_EXIST(232, Response.Status.BAD_REQUEST, "发言不存在", "Speak content not exist"),
    SPEAK_COMMENT_NOT_EXIST(233, Response.Status.BAD_REQUEST, "评论为空", "Speak comment is empty"),
    NURSE_ALREADY_EXISTED(234, Response.Status.BAD_REQUEST, "已注册", "Already registered"),
    SPEAK_THUMBS_UP_EXIST(235, Response.Status.BAD_REQUEST, "点赞已存在", "Thumbs up is exist already"),
    SPEAK_THUMBS_UP_NOT_EXIST(236, Response.Status.BAD_REQUEST, "点赞不存在", "Thumbs up not exist"),
    SPEAK_THUMBS_UP_CAN_NOT_FOR_SELF(237, Response.Status.BAD_REQUEST, "不可以给自己点赞", "Can not thumbs_up for self"),
    SMS_VERIFY_FAILED(238, Response.Status.BAD_REQUEST, "短信验证码失败", "Verify SMS Code Failed."),
    FILE_DELETE_FAILED(239, Response.Status.BAD_REQUEST, "删除文件失败", "Delete file operation failed"),
    AUTHENTICATION_NAME_INVALID(240, Response.Status.BAD_REQUEST, "账户名无效", "Account name is invalid"),
    AUTHENTICATION_PASSWORD_INVALID(241, Response.Status.BAD_REQUEST, "账户密码为空", "Account password is empty"),
    AUTHENTICATION_AUTHORITY_DENIED(242, Response.Status.BAD_REQUEST, "账户操作没有权限", "Account operation authority denied"),
    AUTHENTICATION_DELETE_ADMIN_DENIED(243, Response.Status.BAD_REQUEST, "不可以删除管理员账户", "Administrator account have not to be deleted"),
    NURSE_QUALIFICATION_NAME_EXIST(244, Response.Status.BAD_REQUEST, "资质名已存在", "Qualification name exist"),
    NURSE_QUALIFICATION_IDENTIFICATION_EXIST(245, Response.Status.BAD_REQUEST, "身份证存在", "Identification exist"),
    SKILL_EXIST(246, Response.Status.BAD_REQUEST, "技能已存在", "Skill exist"),
    SKILL_NAME_IS_NULL(247, Response.Status.BAD_REQUEST, "技能名称为空字符串", "Skill name is empty"),
    SKILL_TYPE_INVALID(248, Response.Status.BAD_REQUEST, "无效的技能类型", "Skill type is invalid"),
    NOMINATION_CAN_NOT_FOR_SELF(249, Response.Status.BAD_REQUEST, "不可以给自己提名", "Can not nominate for self"),
    NURSE_DONT_HAVE_SKILL(250, Response.Status.BAD_REQUEST, "护士没有此技能", "Nurse do not have this skill"),
    WORK_FILE_UPLOAD_FAILED(251, Response.Status.BAD_REQUEST, "工作证上传失败", "Upload work file Nurse do not have this skill"),
    RECORD_ALREADY_EXIST(252, Response.Status.BAD_REQUEST, "记录已存在", "Record already exist"),
    PLATFORM_VERSION_NOT_FOUND(253, Response.Status.BAD_REQUEST, "版本信息不存在", "Platform version not found"),
    PLATFORM_VERSION_EXISTED(254, Response.Status.BAD_REQUEST, "版本已存在", "Platform version already exists."),
    CONTAINS_SENSITIVE_WORD(255, Response.Status.BAD_REQUEST, "包含敏感词", "Contains sensitive word"),
    USER_AUTHORITY_DENY_ALL(256, Response.Status.BAD_REQUEST, "用户权限被禁", "User authority denied all"),
    PATIENT_HAS_APPOINT_DOCTOR_TODAY(257, Response.Status.BAD_REQUEST, "患者已预约过", "the patient has appointment with doctor today"),
    CLINIC_DATE_NOT_ALLOWED(258, Response.Status.BAD_REQUEST, "预约日期无效", "cannot appoint at this clinic date"),
    SERVICE_ORDER_BEEN_FETCHED(259, Response.Status.BAD_REQUEST, "订单已被抢", "service order has been fetched"),
    PAY_FAILED(260, Response.Status.BAD_REQUEST, "支付失败", "Payment failed"),
    OPENID_INVALID(261, Response.Status.BAD_GATEWAY, "Openid 不存在", "Openid Invalid");

    private final int code;
    private final String cnMsg;
    private final String enMsg;
    private final Response.Status status;

    Nurse360ErrorCode(int code, Response.Status status, String cnMsg, String enMsg) {
        this.code = code;
        this.status = status;
        this.cnMsg = cnMsg;
        this.enMsg = enMsg;
    }

    public int getCode() {
        return code;
    }

    public Response.Status getStatus() {
        return status;
    }

    public String getCnMsg() {
        return cnMsg;
    }

    public String getEnMsg() {
        return enMsg;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append("Nurse360ErrorCode[");
        msg.append("code=").append(code);
        msg.append(", status=").append(status);
        msg.append(", cnMsg=").append(cnMsg);
        msg.append(", enMsg=").append(enMsg);
        return msg.toString();
    }
}

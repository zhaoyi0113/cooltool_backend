package com.cooltoo.exception;

import javax.ws.rs.core.Response;

public enum ErrorCode {

    //========================================================================
    //
    //              Error Code start from 201
    //
    //========================================================================
    // * 201 ~ 210, system error
    UNKNOWN(201, Response.Status.BAD_REQUEST, "系统未知错误", "System unknown error."),
    SYSTEM_ERROR(202, Response.Status.INTERNAL_SERVER_ERROR, "系统错误", "System error."),
    DATABASE_ERROR(203, Response.Status.INTERNAL_SERVER_ERROR, "数据库错误", "DataBase error."),
    DATA_NOT_CONSISTENT(204, Response.Status.INTERNAL_SERVER_ERROR, "数据不一致错误", "Data not consistent error."),
    NOT_PERMITTED(205, Response.Status.FORBIDDEN, "不允许访问", "Request not permitted"),
    UNAUTHORIZED(206, Response.Status.NOT_ACCEPTABLE, "未授权的访问", "Request Unauthorized"),
    ILLEGAL_REQUEST(207, Response.Status.BAD_REQUEST, "非法请求", "Illegal request"),
    FILTER_REQUEST_BODY_ERROR(208, Response.Status.BAD_REQUEST, "请求内容处理异常", "deal with request object error."),
    ACCOUNT_TOKEN_EXPIRED(209, Response.Status.NOT_ACCEPTABLE, "account token已过期", "account token has been expired!"),

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
    OPENID_INVALID(261, Response.Status.BAD_GATEWAY, "Openid 不存在", "Openid Invalid"),
    USER_FORBIDDEN_BY_VENDOR(262, Response.Status.BAD_REQUEST, "服务商屏蔽了用户", "User is forbidden by vendor"),
    NEED_VISIT_PATIENT_RECORD(263, Response.Status.BAD_REQUEST, "服务员未设置出诊记录", "Server not set record of visiting patient"),
    NURSE_DO_NOT_FORBID_USER(264, Response.Status.BAD_REQUEST, "护士没有屏蔽用户", "Nurse do not forbid user"),
    VENDOR_DO_NOT_FORBID_USER(265, Response.Status.BAD_REQUEST, "服务商没有屏蔽用户", "Vendor do not forbid user"),
    USER_FORBIDDEN_BY_NURSE(266, Response.Status.BAD_REQUEST, "护士屏蔽了用户", "User is forbidden by nurse"),
    NURSE_AUTH_DENIED_BY_HEAD_NURSE(267, Response.Status.BAD_REQUEST, "护士长禁用了护士的咨询权限", "Nurse's consultation authorization is forbidden by head nurse"),
    NURSE_AUTH_DENIED_BY_ADMIN(268, Response.Status.BAD_REQUEST, "管理员禁用了护士的咨询权限", "Nurse's consultation authorization is forbidden by admin"),





    //========================================================================
    //
    //              Nurse360 Error Code start from 3000
    //
    //========================================================================

    // * 3001 ~ 3030, system error
    NURSE360_UNKNOWN(3001, Response.Status.BAD_REQUEST, "系统未知错误", "System unknown error."),
    NURSE360_ACCOUNT_TOKEN_NOT_FOUND(3002, Response.Status.NOT_FOUND, "account token 不存在", "account token not found!"),
    NURSE360_ACCOUNT_TOKEN_EXPIRED(3003, Response.Status.NOT_ACCEPTABLE, "account token已过期", "account token has been expired!"),
    NURSE360_NOT_ACCEPTABLE(3004, Response.Status.NOT_ACCEPTABLE, "资源不存在", "Resource not found"),
    NURSE360_NOT_PERMITTED(3005, Response.Status.FORBIDDEN, "请求不被允许", "Request not permitted"),
    NURSE360_UNAUTHORIZED(3006, Response.Status.NOT_ACCEPTABLE, "未授权的访问", "Request Unauthorized"),
    NURSE360_NOT_LOGIN(3007, Response.Status.NOT_ACCEPTABLE, "未登录", "Not login"),


    // * 3031 ~ 3299, business related generic error
    NURSE360_USER_NOT_FOUND(3051, Response.Status.NOT_FOUND, "用户不存在", "User not found"),
    NURSE360_RECORD_NOT_FOUND(3052, Response.Status.NOT_FOUND, "数据记录不存在", "Record not found"),
    NURSE360_RECORD_EXISTS_ALREADY(3053, Response.Status.EXPECTATION_FAILED, "数据记录已存在", "Record exist already"),
    NURSE360_PARAMETER_IS_EMPTY(3054, Response.Status.EXPECTATION_FAILED, "参数为空", "Parameter is empty or null"),
    NURSE360_PARAMETER_NOT_EXPECTED(3055, Response.Status.EXPECTATION_FAILED, "参数为不匹配", "Parameter is not match"),
    NURSE360_UNIQUE_ID_INVALID(3056, Response.Status.EXPECTATION_FAILED, "UniqueId 无效", "UniqueId is not match"),
    NURSE360_WRONG_PASSWORD(3057, Response.Status.EXPECTATION_FAILED, "密码错误", "Password wrong"),
    NURSE360_SERVICE_ORDER_BEEN_FETCHED(3058, Response.Status.BAD_REQUEST, "订单已被抢", "Service order has been fetched"),
    NURSE360_SERVICE_ORDER_NOT_YOURS(3059, Response.Status.BAD_REQUEST, "订单不属于你", "Service order not belong to you"),
    NURSE360_FILE_OPERATION_FAILED(3060, Response.Status.BAD_REQUEST, "文件操作失败", "File operation failed"),
    NURSE360_RESULT_NOT_EXPECTED(3061, Response.Status.BAD_REQUEST, "结果错误", "Result not expected"),
    NURSE360_CASEBOOK_OR_CASE_NOT_BELONG_TO_YOU(3062, Response.Status.BAD_REQUEST, "该病例不属于你", "This casebook or case do not belong to you"),
    NURSE360_AMOUNT_EXCEEDS_BALANCE(3063, Response.Status.BAD_REQUEST, "超出余额范围", "Amount exceeds balance"),
    NURSE360_RECORD_STATUS_NOT_EXPECTED(3064, Response.Status.BAD_REQUEST, "数据记录状态不符合操作要求", "Record status is not expected"),
    ;

    private final int code;

    private final Response.Status status;

    private final String cnMsg;

    private final String enMsg;

    ErrorCode(int code, Response.Status status, String cnMsg, String enMsg) {
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
        return "ErrorCode{" +
                "code=" + code +
                ", status=" + status +
                ", cnMsg='" + cnMsg + '\'' +
                ", enMsg='" + enMsg + '\'' +
                '}';
    }
}

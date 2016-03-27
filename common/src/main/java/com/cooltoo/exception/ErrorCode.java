package com.cooltoo.exception;

import javax.ws.rs.core.Response;

public enum ErrorCode {

    // * 0 ~ 10000, system error
    UNKNOWN(0, Response.Status.BAD_REQUEST, "系统未知错误", "System unknown error."),
    SYSTEM_ERROR(1, Response.Status.INTERNAL_SERVER_ERROR, "系统错误", "System error."),
    DATABASE_ERROR(2, Response.Status.INTERNAL_SERVER_ERROR, "数据库错误", "DataBase error."),
    DATA_NOT_CONSISTENT(3, Response.Status.INTERNAL_SERVER_ERROR, "数据不一致错误", "Data not consistent error."),
    NOT_PERMITTED(4, Response.Status.FORBIDDEN, "不允许访问", "Request not permitted"),
    UNAUTHORIZED(5, Response.Status.NOT_ACCEPTABLE, "未授权的访问", "Request Unauthorized"),
    ILLEGAL_REQUEST(6, Response.Status.BAD_REQUEST, "非法请求", "Illegal request"),
    FILTER_REQUEST_BODY_ERROR(7, Response.Status.BAD_REQUEST, "请求内容处理异常", "deal with request object error."),
    ACCOUNT_TOKEN_EXPIRED(10014, Response.Status.NOT_ACCEPTABLE, "account token已过期", "account token has been expired!"),

    // * 10000 ~ 20000, business related generic error
    RESOURCE_NOT_FOUND(10001, Response.Status.NOT_FOUND, "资源不存在", "Resource not found"),
    AUTHENTICATION_EXISTED(10002, Response.Status.CONFLICT, "账号已存在", "Account existed"),
    AUTHENTICATION_NOT_EXISTED(10003, Response.Status.NOT_FOUND, "账号不存在", "Account don't exist"),
    AUTHENTICATION_INVALIDATE(10004, Response.Status.NOT_ACCEPTABLE, "账号未激活", "Account has not activated"),
    AUTHENTICATION_FORBIDDEN(10005, Response.Status.FORBIDDEN, "账号已禁用或已失效,请与管理员联系",
            "Account has been disabled or deleted, Please contact the administrator"),
    EMAIL_FORMAT_ERROR(10006, Response.Status.NOT_ACCEPTABLE, "邮箱格式不正确", "Email format error"),
    LOGIN_AUTHENTICATION_NOT_EXISTED(10007, Response.Status.NOT_FOUND, "账号不存在,或用户名、密码不正确",
            "Account don't existed, or account name or password not correct."),
    INVALID_PASSWORD(10008, Response.Status.FORBIDDEN, "密码不正确", "Invalid password."),
    PASSWORD_EXPIRED(10009, Response.Status.FORBIDDEN, "密码已失效", "Password expired."),
    UNSUPPORTED_ENCODING(10010, Response.Status.BAD_REQUEST, "不支持的编码格式", "Unsupported encoding."),
    REGISTER_NEW_MEMBER_FAILED(10011, Response.Status.BAD_REQUEST, "注册失败", "Register failed"),
    DUPLICATE_ACCOUNT_ID(10012, Response.Status.CONFLICT, "账号已存在", "account has existed"),
    INVALID_ACCESS_TOKEN(10013, Response.Status.BAD_REQUEST, "无效的token", "invalid access token"),
    ACCESS_TOKEN_DELETED(10014, Response.Status.BAD_REQUEST, "token已被删除", "access token deleted"),
    SYNC_TACTICS_ERROR(10015, Response.Status.BAD_REQUEST, "同步策略出错", "sync tactics error"),
    PASSBOOK_NOT_FOUND(10016, Response.Status.NOT_FOUND, "没找到密码本", "passbook not found"),
    NO_RESOURCE(10017, Response.Status.BAD_REQUEST, "没有资源", "no resource"),
    FOLDER_EXISTED(10018, Response.Status.BAD_REQUEST, "目录已存在", "folder already existed."),
    FOLDER_NOT_EXISTED(10019, Response.Status.BAD_REQUEST, "目录不存在", "folder doesn't exist"),
    DELETE_FOLDER_ERROR(10020, Response.Status.BAD_REQUEST, "删除目录失败", "delete folder failed"),
    FILE_NOT_EXISTED(10021, Response.Status.BAD_REQUEST, "文件不存在", "file doesn't exist"),
    FILE_DOES_NOT_SUPPORT_PREVIEW(10022, Response.Status.BAD_REQUEST, "该文件不支持预览", "The file does not support Preview"),
    IMAGES_SIZE_ERR(10024, Response.Status.BAD_REQUEST, "必须指定图片的长或者宽", "images's size is error!"),
    COULD_NOT_UNBIND_USED_DEVICE(10023, Response.Status.BAD_REQUEST, "不能解绑正在使用的设备", "could not unbind used device"),
    ENCRYPTION_ERROR(10025, Response.Status.BAD_REQUEST, "加密失败", "encrypt call failed"),
    DECRYPTION_ERROR(10026, Response.Status.BAD_REQUEST, "解密失败", "decrypt call failed"),
    DATA_ERROR(10027, Response.Status.BAD_REQUEST, "数据错误", "data error!"),
    WECHAT_CODE_ERROR(10028, Response.Status.BAD_REQUEST, "没有微信Code", "can't find wechat code"),
    USER_NOT_EXISTED(10029, Response.Status.BAD_REQUEST, "用户不存在", "user doesn't exist"),
    NOT_LOGIN(10030, Response.Status.BAD_REQUEST, "未登陆", "Not Login"),
    COURSE_TAG_ALREADY_EXISTED(10031, Response.Status.BAD_REQUEST, "课程标签已存在", "course tag alread exist."),
    COURSE_NOT_FOUND(10032, Response.Status.BAD_REQUEST, "没有该课程", "Course not found"),
    DUPLICATE_COURSE_NAME(10033, Response.Status.BAD_REQUEST, "重复的课程名称", "Duplicate couse name"),
    COURSE_TAG_NOT_EXIST(10034, Response.Status.BAD_REQUEST, "课程标签不存在", "Course Tag not exist"),
    ROLE_NOT_FOUND(10035, Response.Status.BAD_REQUEST, "角色不存在", "Role not exited."),
    INVALID_USER(10036, Response.Status.BAD_REQUEST, "用户错误", "Invalid Users Access"),
    BUILD_QRCONNECT_URI_ERROR(10037, Response.Status.BAD_REQUEST, "创建QR Connect URI失败", "Failed to build QR Connect URI"),
    COMMON_NOT_FOUND(10038, Response.Status.BAD_REQUEST, "未发现", "Not Found"),
    PAY_FAILED(10039, Response.Status.BAD_REQUEST, "支付失败", "Pay Failed"),
    COURSE_ALREADY_EXISTED(10040, Response.Status.BAD_REQUEST, "课程已存在", "Course Already Existed."),
    BADGE_NOT_EXIST(10041, Response.Status.BAD_REQUEST, "徽章不存在", "Badge not exist"),
    ORDER_NOT_EXIST(10042, Response.Status.BAD_REQUEST,  "订单不存在", "Order not exist"),
    NURSE_NOT_EXIST(10043, Response.Status.BAD_REQUEST,  "护士不存在", "Nurse not exist"),
    RECORD_NOT_EXIST(10044, Response.Status.BAD_REQUEST,  "记录不存在", "Record not exist"),
    HOSPITAL_NOT_EXIST(10045, Response.Status.BAD_REQUEST,  "医院不存在", "Hospital not exist"),
    HOSPITAL_DEPARTMENT_NOT_EXIST(10046, Response.Status.BAD_REQUEST,  "科室不存在", "Hospital department not exist"),
    NO_SUCH_OCCUPATION(10047, Response.Status.BAD_REQUEST, "职业不存在" , "No such occupation"),
    SKILL_NOT_EXIST(10048, Response.Status.BAD_REQUEST, "技能不存在", "Skill not exist"),
    NOT_IMPLEMENTATION(10049, Response.Status.BAD_REQUEST, "方法未实现", "Not Implementation"),
    SPEAK_CONTENT_IS_EMPTY(10050, Response.Status.BAD_REQUEST, "发言为空", "Speak content is empty"),
    SPEAK_CONTENT_NOT_EXIST(10051, Response.Status.BAD_REQUEST, "发言不存在", "Speak content not exist"),
    SPEAK_COMMENT_NOT_EXIST(10052, Response.Status.BAD_REQUEST, "评论为空", "Speak comment is empty"),
    NURSE_ALREADY_EXISTED(10053, Response.Status.BAD_REQUEST, "已注册", "Already registered"),
    SPEAK_THUMBS_UP_EXIST(10054, Response.Status.BAD_REQUEST, "点赞已存在", "Thumbs up is exist already"),
    SPEAK_THUMBS_UP_NOT_EXIST(10055, Response.Status.BAD_REQUEST, "点赞不存在", "Thumbs up not exist"),
    SPEAK_THUMBS_UP_CAN_NOT_FOR_SELF(10056, Response.Status.BAD_REQUEST, "不可以给自己点赞", "Can not thumbs_up for self"),
    SMS_VERIFY_FAILED(10057, Response.Status.BAD_REQUEST, "短信验证码失败", "Verify SMS Code Failed."),
    FILE_DELETE_FAILED(10058, Response.Status.BAD_REQUEST, "删除文件失败", "Delete file operation failed"),
    AUTHENTICATION_NAME_INVALID(10059, Response.Status.BAD_REQUEST, "账户名无效", "Account name is invalid"),
    AUTHENTICATION_PASSWORD_INVALID(10060, Response.Status.BAD_REQUEST, "账户密码为空", "Account password is empty"),
    AUTHENTICATION_AUTHORITY_DENIED(10061, Response.Status.BAD_REQUEST, "账户操作没有权限", "Account operation authority denied"),
    AUTHENTICATION_DELETE_ADMIN_DENIED(10062, Response.Status.BAD_REQUEST, "不可以删除管理员账户", "Administrator account have not to be deleted"),
    NURSE_QUALIFICATION_NAME_EXIST(10063, Response.Status.BAD_REQUEST, "资质名已存在", "Qualification name exist"),
    NURSE_QUALIFICATION_NAME_NOT_EXIST(10064, Response.Status.BAD_REQUEST, "资质名不存在", "Qualification name not exist"),
    NURSE_QUALIFICATION_IDENTIFICATION_EXIST(10065, Response.Status.BAD_REQUEST, "身份证存在", "Identification exist"),
    SKILL_EXIST(10066, Response.Status.BAD_REQUEST, "技能已存在", "Skill exist"),
    SKILL_NAME_IS_NULL(10067, Response.Status.BAD_REQUEST, "技能名称为空字符串", "Skill name is empty"),
    SKILL_TYPE_INVALID(10068, Response.Status.BAD_REQUEST, "无效的技能类型", "Skill type is invalid"),
    FILE_UPLOADING_IS_EMPTY(10069, Response.Status.BAD_REQUEST, "上传文件无效", "File uploading is invalid"),
    NOMINATION_CAN_NOT_FOR_SELF(10070, Response.Status.BAD_REQUEST, "不可以给自己提名", "Can not nominate for self"),
    NURSE_DONT_HAVE_EXIST(10066, Response.Status.BAD_REQUEST, "护士没有此技能", "Nurse do not have this skill");

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

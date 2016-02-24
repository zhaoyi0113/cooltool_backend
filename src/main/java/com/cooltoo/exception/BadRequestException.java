package com.cooltoo.exception;

import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yzzhao on 12/17/15.
 */
public class BadRequestException extends RuntimeException{
    private final Response.Status status;
    private final ErrorCode errorCode;
    private final Map<String, String> errorMessages;

    public BadRequestException(Response.Status status) {
        this(status, ErrorCode.UNKNOWN, new HashMap<String, String>());
    }

    public BadRequestException(Response.Status status, ErrorCode errorCode) {
        this(status, errorCode, new HashMap<String, String>());
    }

    public BadRequestException(ErrorCode errorCode) {
        this(errorCode.getStatus(), errorCode, new HashMap<String, String>());
    }

    protected BadRequestException(Response.Status status, ErrorCode errorCode, Map<String, String> errorMessages) {
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessages = errorMessages;
    }

    public Response.Status getStatus() {
        return status;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Map<String, String> getErrorMessages() {
        return errorMessages;
    }

}

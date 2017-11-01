package com.tao.hammer.api;

import java.util.Collections;

/**
 * @author tyq
 * @version 1.0, 2017/11/1
 */
public class ApiResult<T> {
    /**
     * http相应成功
     */
    private static final ApiResult SUCCESS = success(Collections.emptyMap());

    private boolean success;

    /**
     * 错误码
     */
    private String messageCode;

    private String message;

    private T data;

    public ApiResult() {
    }

    public ApiResult(boolean success, String messageCode, String message, T data) {
        this.success = success;
        this.messageCode = messageCode;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResult<T> success() {
        return SUCCESS;
    }

    public static <T> ApiResult success(T data) {
        return new ApiResult<T>(true, null, null, data);
    }
}

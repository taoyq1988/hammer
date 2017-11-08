package com.tao.hammer.api;

import com.tao.hammer.Pagination;

import java.util.Collections;
import java.util.List;

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

    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<T>(true, null, null, data);
    }

    /**
     * 根据参数组装给表格使用的成功处理结果
     *
     * @param data 业务数据体
     * @param summary 合计
     * @param total 总条数
     */
    public static <T, S> ApiResult<List<T>> success(List<T> data, S summary, long total) {
        return new ApiTableResult<T, S>(true, null, null, data, summary, total);
    }

    /**
     * 根据参数组装给表格使用的成功处理结果
     */
    public static <T, S> ApiResult<List<T>> success(Pagination<T> pagination) {
        return new ApiTableResult<T, S>(true, null, null, pagination.getRecords(), null, pagination.getTotal());
    }

    public static <T> ApiResult<T> failure(String code, String message) {
        return new ApiResult<T>(false, code, message, null);
    }


    /**
     * getter and setter
     */

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(String messageCode) {
        this.messageCode = messageCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

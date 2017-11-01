package com.tao.hammer.api;

import lombok.Data;

import java.util.List;

/**
 * 用于表格数据
 * @author tyq
 * @version 1.0, 2017/11/1
 */
@Data
public class ApiTableResult<T, S> extends ApiResult<List<T>> {

    /**
     * 合计
     */
    private S sum;

    private long total;

    public ApiTableResult() {
    }

    public ApiTableResult(boolean success, String messageCode, String message, List<T> data, S sum, long total) {
        super(success, messageCode, message, data);
        this.sum = sum;
        this.total = total;
    }
}

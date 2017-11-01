package com.tao.hammer;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 根据数据库分页信息
 * @author tyq
 * @version 1.0, 2017/11/1
 */
@Data
public class Pagination<T> implements Serializable {

    private List<T> records;

    private long total;
}

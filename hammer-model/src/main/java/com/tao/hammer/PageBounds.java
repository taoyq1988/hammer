package com.tao.hammer;

/**
 * 分页条件
 * @author tyq
 * @version 1.0, 2017/11/1
 */
public class PageBounds {
    private static final int DEFAULT_PAGE_SIZE = 20;

    /**
     * 单页数量
     */
    private int pageSize;

    /**
     * 单页索引
     */
    private int pageIndex;

    /**
     * 是否需要总页数
     */
    private boolean count;

    public PageBounds() {
        this(DEFAULT_PAGE_SIZE, 0, false);
    }

    /**
     * 查询结果中没有包含数据总数
     *
     * @param pageSize 每页条数
     * @param pageIndex 索引页（0-based）
     */
    public PageBounds(int pageSize, int pageIndex) {
        this(pageSize, pageIndex, true);
    }

    public PageBounds(int pageSize, int pageIndex, boolean count) {
        this.pageSize = pageSize;
        this.pageIndex = pageIndex;
        this.count = count;
    }
}

package com.youyi.user_management_back.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageRequest{

    /**
     * 页面大小
     */
    protected int pageSize = 10;
    /**
     * 当前是第几页
     */
    protected int pageNum = 1;
}

package com.youyi.user_management_back.model.vo;


import lombok.Data;

import java.io.Serializable;

@Data
public class FriendUserVO implements Serializable {

    /**
     * 好友表id
     */
    private long applyId;

    /**
     * 用户id
     */
    private long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 申请原因
     */
    private String reason;

    /**
     * 申请状态,0 申请中，1 已同意，2 已拒绝
     */
    private Integer status;


    private static final long serialVersionUID = 1L;
}

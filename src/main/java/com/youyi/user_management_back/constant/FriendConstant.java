package com.youyi.user_management_back.constant;

public interface FriendConstant {

    /* 表明是自己向对方发起的好友请求
     */
    String APPLY_REASON = "请求添加对方为好友";

    /*申请好友状态信息
     * */

    /**
     * 申请中
     */
    int APPLYING_STATUS = 0;

    /**
     * 已接受
     */
    int ACCEPTED_STATUS = 1;

    /**
     * 已拒绝
     */
    int REJECTED_STATUS = 2;

}

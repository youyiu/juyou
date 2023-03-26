package com.youyi.user_management_back.model.request;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

@Data
public class FriendAddRequest implements Serializable {

    /**
     * 好友id
     */
    private long friendId;

    /**
     * 申请原因
     */
    private String reason;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

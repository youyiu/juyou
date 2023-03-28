package com.youyi.user_management_back.model.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class FriendHandleRequest {

    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private long id;


    /**
     * 申请状态,0 申请中，1 已同意，2 已拒绝
     */
    private Integer status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

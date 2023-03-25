package com.youyi.user_management_back.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 好友
 * @TableName friend
 */
@TableName(value ="friend")
@Data
public class Friend implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 好友id
     */
    private Long friendId;

    /**
     * 用户对好友的备注
     */
    private String friendAlias;

    /**
     * 好友对用户的备注
     */
    private String userAlias;

    /**
     * 申请原因
     */
    private String reason;

    /**
     * 申请状态,0 申请中，1 已同意，2 已拒绝
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
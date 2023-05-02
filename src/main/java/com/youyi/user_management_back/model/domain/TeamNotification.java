package com.youyi.user_management_back.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName team_notification
 */
@TableName(value ="team_notification")
@Data
public class TeamNotification implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 接受消息的用户id
     */
    private long acceptUserId;

    /**
     * 进行操作的用户id
     */
    private long operateUserId;

    /**
     * 队伍Id
     */
    private long teamId;

    /**
     * 0 成员退出队伍
1 队长退出队伍
2 队长解散队伍

     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建时间
     */
    private Date updateTime;

    /**
     * 
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
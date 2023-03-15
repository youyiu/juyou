package com.youyi.user_management_back.model.request;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户退出队伍请求体
 */
@Data
public class TeamQuitRequest implements Serializable {

    private Long teamId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

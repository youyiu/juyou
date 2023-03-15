package com.youyi.user_management_back.model.request;

import lombok.Data;

import java.io.Serializable;

/*用户注册请求体
* */
@Data
public class UserRegisterRequest implements Serializable {
    //序列化
    public static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;
    private String userPassword;
    private String checkPassword;
}

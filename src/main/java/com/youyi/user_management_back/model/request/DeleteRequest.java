package com.youyi.user_management_back.model.request;

import lombok.Data;

import java.io.Serializable;
@Data
public class DeleteRequest implements Serializable {

    public static final long serialVersionUID = 1L;

    private long id;
}

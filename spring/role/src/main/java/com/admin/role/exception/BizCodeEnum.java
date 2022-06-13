package com.admin.role.exception;

import com.admin.common.exception.IBizCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public enum BizCodeEnum implements IBizCode {
    THE_SAME_ROLE_NAME_EXISTS(300011, "操作失败：存在相同的角色名");

    private int code;
    private String msg;
}

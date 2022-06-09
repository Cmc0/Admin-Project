package com.admin.user.exception;

import com.admin.common.exception.IBizCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public enum BizCodeEnum implements IBizCode {
    ACCOUNT_NUMBER_AND_PASSWORD_NOT_VALID(300041, "账号或密码错误"), //
    ;

    private int code;
    private String msg;
}

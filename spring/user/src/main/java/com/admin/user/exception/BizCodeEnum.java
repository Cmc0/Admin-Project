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
    LOSS_OF_ACCOUNT_INTEGRITY(300091, "账户完整性缺失，请联系管理员"), //
    NO_PASSWORD_SET(300201, "未设置密码，请点击【忘记密码】，进行密码设置"), //
    ACCOUNT_IS_DISABLED(300051, "账户被冻结，请联系管理员"), //
    ;

    private int code;
    private String msg;
}

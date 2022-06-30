package com.admin.user.exception;

import com.admin.common.exception.IBizCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public enum BizCodeEnum implements IBizCode {
    EMAIL_HAS_BEEN_REGISTERED(300011, "该邮箱已被注册"), //
    ACCOUNT_NUMBER_OR_PASSWORD_NOT_VALID(300021, "账号或密码错误"), //
    PASSWORD_NOT_VALID(300031, "密码错误"), //
    NO_PASSWORD_SET(300041, "未设置密码，请点击【忘记密码】，进行密码设置"), //
    ACCOUNT_IS_DISABLED(300051, "账户被冻结，请联系管理员"), //
    PASSWORD_RESTRICTIONS(300061, "密码限制：必须包含大小写字母和数字，可以使用特殊字符，长度8-20"), //
    ;

    private int code;
    private String msg;
}

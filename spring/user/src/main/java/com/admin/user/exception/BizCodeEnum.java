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
    ACCOUNT_NUMBER_AND_PASSWORD_NOT_VALID(300041, "账号或密码错误"), //
    NO_PASSWORD_SET(300201, "未设置密码，请点击【忘记密码】，进行密码设置"), //
    ACCOUNT_IS_DISABLED(300051, "账户被冻结，请联系管理员"), //
    PASSWORD_RESTRICTIONS(300101, "密码限制：必须包含大小写字母和数字，可以使用特殊字符，长度8-20"), //
    PLEASE_GET_THE_VERIFICATION_CODE_FIRST(300021, "操作失败：请先获取验证码"), //
    CODE_IS_INCORRECT(300031, "验证码有误，请重新输入"), //
    ;

    private int code;
    private String msg;
}

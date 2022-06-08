package com.cmc.common.model.constant;

/**
 * 正则表达式的常量类
 */
public interface BaseRegexConstant {

    String NON_NEGATIVE_INTEGER = "^\\d+$"; // 非负整数：>= 0

    String NON_ZERO_INTEGER = "^-?[1-9]\\d*$"; // 非零整数

    String EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"; // 邮箱

    String CODE_6_REGEXP = "^[A-Za-z0-9]{6}$"; // 6位数的验证码：英文和数字

    String CODE_NUMBER_6_REGEXP = "^[0-9]{6}$"; // 6位数的验证码：数字

    String PASSWORD_REGEXP = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,20}$"; // 密码限制：必须包含大小写字母和数字，可以使用特殊字符，长度8-20

    String NICK_NAME_REGEXP = "^[\\u4E00-\\u9FA5A-Za-z0-9_]{2,20}$"; // 用户昵称限制：只能包含中文，数字，字母，下划线，长度2-20

    String PHONE = "^(13[0-9]|14[5|7]|15[0|1|2|3|4|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$"; // 手机号码

}

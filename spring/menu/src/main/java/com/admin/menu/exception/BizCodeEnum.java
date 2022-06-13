package com.admin.menu.exception;

import com.admin.common.exception.IBizCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public enum BizCodeEnum implements IBizCode {
    MENU_URI_IS_EXIST(300011, "操作失败：path 重复"), //
    ;

    private int code;
    private String msg;
}

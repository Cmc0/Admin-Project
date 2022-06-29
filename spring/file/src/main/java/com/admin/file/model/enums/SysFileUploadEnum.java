package com.admin.file.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件上传：枚举类
 */
@AllArgsConstructor
@Getter
public enum SysFileUploadEnum {
    AVATAR(1) // 头像
    ;

    @EnumValue
    @JsonValue
    private int code;

    public static SysFileUploadEnum getByCode(Byte code) {
        if (code == null) {
            return null;
        }
        for (SysFileUploadEnum item : SysFileUploadEnum.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }

}

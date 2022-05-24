package com.cmc.projectutil.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
public enum BaseBizCodeEnum implements IBizCode {
    API_RESULT_OK(200, "操作成功 (￣▽￣)／"), //
    API_RESULT_SYS_ERROR(100010, "系统异常，请重试 (￣▽￣)~*"), //
    API_RESULT_BUSINESS_ERROR(100021, "处理超时，请重试 (oﾟ▽ﾟ)o  "), //
    PARAMETER_CHECK_ERROR(100031, "参数校验出现问题 ヽ(°▽、°)ﾉ"), //
    ;

    private int code;
    private String msg;
}

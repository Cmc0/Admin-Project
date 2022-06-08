package com.cmc.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * 规则：
 * 错误代码格式：300010 300021
 * 解释：前面5位：错误代码，最后一位：0，系统异常，1，业务异常
 * 注意：10001开头的留给 BaseBizCodeEnum类进行配置，建议用 20001开头配置一些公用异常，实际业务从 30001开头，开始使用
 * 备注：可以每个业务的错误代码配置类，使用相同的 错误代码，比如每个业务的错误代码配置类都从 30001开始，为什么呢，因为
 * ApiResultVO 任何的 error方法都会打印服务名，所有就不用关心是哪个服务报出异常，直接根据打印的服务名，找到对应的错误信息即可，以上。
 */
@AllArgsConstructor
@ToString
@Getter
public enum BaseBizCodeEnum implements IBizCode {
    API_RESULT_OK(200, "操作成功 (￣▽￣)／"), //
    API_RESULT_SYS_ERROR(100010, "系统异常，请重试 (￣▽￣)~*"), //
    API_RESULT_BUSINESS_ERROR(100021, "处理超时，请重试 (oﾟ▽ﾟ)o  "), //
    PARAMETER_CHECK_ERROR(100031, "参数校验出现问题 ヽ(°▽、°)ﾉ"), //
    ILLEGAL_REQUEST(100091, "非法请求 罒ω罒"), //
    NOT_LOGGED_IN_YET(100111, "尚未登录，请先登录 (〃'▽'〃)"), // 返回这个 code，会触发前端，登出功能（清除所有缓存，并重定向到 /login页面）
    LOGIN_EXPIRED(100111, "登录过期，请重新登录 (・ω<)"), //
    FORCED_OFFLINE(100111, "账号在其他地方登录，您被迫下线 (〒︿〒)"), //
    ;

    private int code;
    private String msg;
}

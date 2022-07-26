package com.admin.common.exception;

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
    API_RESULT_OK(200, "操作成功"), //
    API_RESULT_SEND_OK(200, "发送成功"), //
    API_RESULT_SYS_ERROR(100010, "系统异常，请联系管理员"), //
    PARAMETER_CHECK_ERROR(100011, "参数校验出现问题"), //
    ILLEGAL_REQUEST(100021, "非法请求"), //
    PLEASE_DELETE_THE_CHILD_NODE_FIRST(100031, "请先删除子节点"), //
    INSUFFICIENT_PERMISSIONS(100041, "权限不足"), //
    NO_DATA_AFFECTED(100051, "操作失败：未有数据受到影响"), //
    NOT_LOGGED_IN_YET(100111, "尚未登录，请先登录"), // 返回这个 code，会触发前端，登出功能（清除所有缓存，并重定向到 /login页面）
    LOGIN_EXPIRED(100111, "登录过期，请重新登录"), //
    FORCED_OFFLINE(100111, "账号在其他地方登录，您被迫下线"), //
    PARENT_ID_CANNOT_BE_EQUAL_TO_ID(100121, "操作失败：parentId 不能等于 id"), //
    THE_ADMIN_ACCOUNT_DOES_NOT_SUPPORT_THIS_OPERATION(100131, "操作失败：admin 账号不支持此操作"), //
    EMAIL_DOES_NOT_EXIST_PLEASE_RE_ENTER(100141, "操作失败：邮箱不存在，请重新输入"), //
    PLEASE_GET_THE_VERIFICATION_CODE_FIRST(100151, "操作失败：请先获取验证码"), //
    CODE_IS_INCORRECT(100161, "验证码有误，请重新输入"), //
    EMAIL_ADDRESS_NOT_SET(100171, "操作失败：未设置邮箱地址"), //
    EMAIL_HAS_BEEN_REGISTERED(100181, "该邮箱已被注册"), //
    OPERATION_TIMED_OUT_PLEASE_TRY_AGAIN(100191, "操作超时，请重新进行操作"), // 返回这个 code，前端会在步骤表单，往前返回步骤
    EMAIL_FORMAT_IS_INCORRECT(100201, "操作失败：邮箱格式不正确，请重新输入"), //
    ;

    private int code;
    private String msg;
}

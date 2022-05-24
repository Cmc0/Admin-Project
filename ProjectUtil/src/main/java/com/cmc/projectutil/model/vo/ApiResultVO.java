package com.cmc.projectutil.model.vo;

import com.cmc.projectutil.configuration.BaseConfiguration;
import com.cmc.projectutil.exception.BaseBizCodeEnum;
import com.cmc.projectutil.exception.BaseException;
import com.cmc.projectutil.exception.IBizCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "统一响应实体类")
public class ApiResultVO<T> {

    @ApiModelProperty(value = "响应代码，成功返回：200")
    private Integer code;

    @ApiModelProperty(value = "响应描述")
    private String msg;

    @ApiModelProperty(value = "服务器是否收到请求，只会返回 true")
    private Boolean success = true;

    @ApiModelProperty(value = "数据")
    private T data;

    @ApiModelProperty(value = "服务名")
    private String service = BaseConfiguration.applicationName;

    private ApiResultVO(Integer code, String msg, T data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }

    private void setSuccess(boolean success) {
        // 不允许修改 success的值
    }

    private void setService(String service) {
        // 不允许修改 service的值
    }

    private ApiResultVO<T> end() {
        throw new BaseException(this);
    }

    /**
     * 系统异常
     */
    public static <T> void sysError() {
        error(BaseBizCodeEnum.API_RESULT_SYS_ERROR);
    }

    /**
     * 异常方法
     */
    public static <T> ApiResultVO<T> error(IBizCode iBizCode) {
        return new ApiResultVO<T>(iBizCode.getCode(), iBizCode.getMsg(), null).end();
    }

    public static <T> ApiResultVO<T> error(IBizCode iBizCode, T data) {
        return new ApiResultVO<>(iBizCode.getCode(), iBizCode.getMsg(), data).end();
    }

    public static <T> ApiResultVO<T> error(String msg) {
        return new ApiResultVO<T>(BaseBizCodeEnum.API_RESULT_SYS_ERROR.getCode(), msg, null).end();
    }

    /**
     * 操作成功
     */
    public static <T> ApiResultVO<T> ok(String msg, T data) {
        return new ApiResultVO<>(BaseBizCodeEnum.API_RESULT_OK.getCode(), msg, data);
    }

    public static <T> ApiResultVO<T> ok(T data) {
        return new ApiResultVO<>(BaseBizCodeEnum.API_RESULT_OK.getCode(), BaseBizCodeEnum.API_RESULT_OK.getMsg(), data);
    }

    public static <T> ApiResultVO<T> ok(String msg) {
        return new ApiResultVO<>(BaseBizCodeEnum.API_RESULT_OK.getCode(), msg, null);
    }

}

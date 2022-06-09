package com.admin.common.exception;

import cn.hutool.json.JSONUtil;
import com.admin.common.model.vo.ApiResultVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseException extends RuntimeException {

    private ApiResultVO<?> apiResultVO;

    public <T> BaseException(ApiResultVO<T> apiResult) {
        super(JSONUtil.toJsonStr(apiResult)); // 把信息封装成json格式
        setApiResultVO(apiResult);
    }

}

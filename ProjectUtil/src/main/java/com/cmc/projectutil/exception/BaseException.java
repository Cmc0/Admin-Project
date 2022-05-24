package com.cmc.projectutil.exception;

import cn.hutool.json.JSONUtil;
import com.cmc.projectutil.model.vo.ApiResultVO;

public class BaseException extends RuntimeException {

    public <T> BaseException(ApiResultVO<T> apiResult) {
        // 把信息封装成json格式
        super(JSONUtil.toJsonStr(apiResult));
    }

}

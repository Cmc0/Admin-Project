package com.admin.common.exception;

import cn.hutool.core.map.MapUtil;
import com.admin.common.configuration.BaseConfiguration;
import com.admin.common.model.vo.ApiResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@RestControllerAdvice
@Slf4j
public class ExceptionHandlerAdvice {

    /**
     * 参数校验异常
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ApiResultVO<?> handleValidException(MethodArgumentNotValidException e) {

        e.printStackTrace();

        if (BaseConfiguration.prodFlag) {
            try {
                ApiResultVO.error(BaseBizCodeEnum.PARAMETER_CHECK_ERROR);  // 这里肯定会抛出 BaseException异常
            } catch (BaseException baseException) {
                return getBaseExceptionApiResult(baseException);
            }
        }

        // 返回详细的参数校验错误信息
        HashMap<String, String> map = MapUtil.newHashMap();
        BindingResult bindingResult = e.getBindingResult();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            String message = fieldError.getDefaultMessage();
            String field = fieldError.getField();
            map.put(field, message);
        }

        try {
            ApiResultVO.error(BaseBizCodeEnum.PARAMETER_CHECK_ERROR, map); // 这里肯定会抛出 BaseException异常
        } catch (BaseException baseException) {
            return getBaseExceptionApiResult(baseException);
        }

        return null; // 这里不会执行，只是为了通过语法检查
    }

    /**
     * 自定义异常
     */
    @ExceptionHandler(value = BaseException.class)
    public ApiResultVO<?> handleBaseException(BaseException e) {

        e.printStackTrace();

        return getBaseExceptionApiResult(e);
    }

    /**
     * 缺省异常处理，直接提示系统异常
     */
    @ExceptionHandler(value = Throwable.class)
    public ApiResultVO<?> handleThrowable(Throwable e) {

        e.printStackTrace();

        try {
            ApiResultVO.sysError(); // 这里肯定会抛出 BaseException异常
        } catch (BaseException baseException) {
            return getBaseExceptionApiResult(baseException);
        }

        return null; // 这里不会执行，只是为了通过语法检查
    }

    private ApiResultVO<?> getBaseExceptionApiResult(BaseException e) {
        return e.getApiResultVO();
    }

}

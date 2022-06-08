package com.cmc.common.util;

import cn.hutool.core.convert.Convert;
import com.cmc.common.model.constant.BaseConstant;
import com.cmc.common.model.enums.RequestCategoryEnum;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class RequestUtil {

    /**
     * 获取当前上下文的 request对象
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes =
            (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        return requestAttributes.getRequest();
    }

    /**
     * 获取请求类别
     */
    public static RequestCategoryEnum getRequestCategoryEnum() {
        return getRequestCategoryEnum(getRequest());
    }

    /**
     * 获取请求类别
     */
    public static RequestCategoryEnum getRequestCategoryEnum(HttpServletRequest request) {
        if (request == null) {
            return RequestCategoryEnum.H5;
        }
        return RequestCategoryEnum.getByCode(Convert.toByte(request.getHeader(BaseConstant.REQUEST_HEADER_CATEGORY)));
    }

}

package com.admin.common.util;

import cn.hutool.core.convert.Convert;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.enums.SysRequestCategoryEnum;
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
    public static SysRequestCategoryEnum getSysRequestCategoryEnum() {
        return getSysRequestCategoryEnum(getRequest());
    }

    /**
     * 获取请求类别
     */
    public static SysRequestCategoryEnum getSysRequestCategoryEnum(HttpServletRequest request) {
        if (request == null) {
            return SysRequestCategoryEnum.H5;
        }
        return SysRequestCategoryEnum
            .getByCode(Convert.toByte(request.getHeader(BaseConstant.REQUEST_HEADER_CATEGORY)));
    }

}

package com.cmc.common.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONUtil;
import com.cmc.common.model.constant.BaseConstant;
import com.cmc.common.model.entity.ParamDO;
import com.cmc.common.model.entity.UserIdDO;
import com.cmc.common.service.UserIdService;
import com.cmc.common.util.MyJwtUtil;
import com.cmc.common.util.ParamUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.List;

/**
 * 所有请求的拦截器
 */
@Component
@Slf4j
public class MyHandlerInterceptor implements HandlerInterceptor {

    @Resource
    UserIdService userIdService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        if (BaseConstant.PARAM_CHANGE_VALUE_URI_SET.contains(request.getRequestURI())) {
            // 用户互斥配置修改时，进行额外处理
            handleUserMutuallyExclusive(request);
        }

        return true;
    }

    /**
     * 用户互斥配置修改时，进行额外处理
     */
    @SneakyThrows
    private void handleUserMutuallyExclusive(HttpServletRequest request) {

        // 获取请求body
        String body = IoUtil.read(request.getInputStream(), Charset.defaultCharset());

        ParamDO paramDO = BeanUtil.toBean(JSONUtil.parseObj(body), ParamDO.class);

        if (!BaseConstant.USER_MUTUALLY_EXCLUSIVE_ID.equals(paramDO.getId())) {
            return;
        }

        if ("1".equals(paramDO.getValue())) {
            // 如果设置为都不互斥，则结束方法
            return;
        }

        String paramValue = ParamUtil.getValueById(BaseConstant.USER_MUTUALLY_EXCLUSIVE_ID);

        if (paramValue == null || paramValue.equals(paramDO.getValue())) {
            return; // 如果value没有发生改变，则不处理
        }

        // 下线有互斥关系的账号
        String value = paramDO.getValue();
        if ("2".equals(value)) {
            // 2 相同端的互斥（H5/移动端/桌面程序）
            List<UserIdDO> userIdDbList = userIdService.lambdaQuery().select(UserIdDO::getId).list();
            for (UserIdDO item : userIdDbList) {
                MyJwtUtil.removeJwtHashByRequestCategoryOrJwtHash(item.getId(), null, null, false);
            }
        } else if ("3".equals(value)) {
            // 3 所有端都互斥
            List<UserIdDO> userIdDbList = userIdService.lambdaQuery().select(UserIdDO::getId).list();
            for (UserIdDO item : userIdDbList) {
                MyJwtUtil.removeJwtHashByRequestCategoryOrJwtHash(item.getId(), null, null, true);
            }
        }

    }

}

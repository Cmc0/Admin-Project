package com.admin.common.interceptor;

import cn.hutool.core.io.IoUtil;
import cn.hutool.json.JSONUtil;
import com.admin.common.mapper.BaseUserIdMapper;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.entity.BaseParamDO;
import com.admin.common.model.entity.BaseUserIdDO;
import com.admin.common.util.MyJwtUtil;
import com.admin.common.util.ParamUtil;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 所有请求的拦截器
 */
@Component
@Slf4j
public class MyHandlerInterceptor implements HandlerInterceptor {

    @Resource
    BaseUserIdMapper baseUserIdMapper;

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
        String bodyStr = IoUtil.readUtf8(request.getInputStream());

        BaseParamDO baseParamDO = JSONUtil.toBean(bodyStr, BaseParamDO.class);

        if (!BaseConstant.USER_MUTUALLY_EXCLUSIVE_ID.equals(baseParamDO.getId())) {
            return;
        }

        if ("1".equals(baseParamDO.getValue())) {
            // 如果设置为都不互斥，则结束方法
            return;
        }

        String paramValue = ParamUtil.getValueById(BaseConstant.USER_MUTUALLY_EXCLUSIVE_ID);

        if (paramValue == null || paramValue.equals(baseParamDO.getValue())) {
            return; // 如果value没有发生改变，则不处理
        }

        // 下线有互斥关系的账号
        String value = baseParamDO.getValue();
        if ("2".equals(value)) {
            // 2 相同端的互斥（H5/移动端/桌面程序）
            List<BaseUserIdDO> userIdDbList = ChainWrappers.lambdaQueryChain(baseUserIdMapper).select(BaseUserIdDO::getId).list();
            for (BaseUserIdDO item : userIdDbList) {
                MyJwtUtil.removeJwtHashByRequestCategoryOrJwtHash(item.getId(), null, null, false);
            }
        } else if ("3".equals(value)) {
            // 3 所有端都互斥
            List<BaseUserIdDO> userIdDbList = ChainWrappers.lambdaQueryChain(baseUserIdMapper).select(BaseUserIdDO::getId).list();
            for (BaseUserIdDO item : userIdDbList) {
                MyJwtUtil.removeJwtHashByRequestCategoryOrJwtHash(item.getId(), null, null, true);
            }
        }

    }

}

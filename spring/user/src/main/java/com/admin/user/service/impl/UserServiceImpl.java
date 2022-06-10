package com.admin.user.service.impl;

import com.admin.common.model.constant.BaseConstant;
import com.admin.common.util.MyJwtUtil;
import com.admin.common.util.UserUtil;
import com.admin.user.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    HttpServletRequest httpServletRequest;

    /**
     * 退出登录
     */
    @Override
    public String logout() {

        Long currentUserId = UserUtil.getCurrentUserId();

        // 清除 redis中的 jwt
        MyJwtUtil.removeJwtHashByRequestCategoryOrJwtHash(currentUserId, null,
            MyJwtUtil.generateRedisJwtHash(httpServletRequest.getHeader(BaseConstant.JWT_HEADER_KEY)), null);

        return "登出成功";
    }

}

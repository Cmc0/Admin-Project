package com.admin.user.service.impl;

import com.admin.user.model.dto.UserLoginPasswordDTO;
import com.admin.user.service.UserLoginService;
import org.springframework.stereotype.Service;

@Service
public class UserLoginServiceImpl implements UserLoginService {

    /**
     * 账号密码登录
     */
    @Override
    public String password(UserLoginPasswordDTO dto) {

        return "jwt";
    }
}

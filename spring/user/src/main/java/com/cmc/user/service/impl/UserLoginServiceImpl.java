package com.cmc.user.service.impl;

import com.cmc.user.model.dto.UserLoginPasswordDTO;
import com.cmc.user.service.UserLoginService;
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

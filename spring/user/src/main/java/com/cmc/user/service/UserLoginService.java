package com.cmc.user.service;

import com.cmc.user.model.dto.UserLoginPasswordDTO;

public interface UserLoginService {

    String password(UserLoginPasswordDTO dto);
}

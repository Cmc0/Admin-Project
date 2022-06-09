package com.admin.user.service;

import com.admin.user.model.dto.UserLoginPasswordDTO;

public interface UserLoginService {

    String password(UserLoginPasswordDTO dto);

}

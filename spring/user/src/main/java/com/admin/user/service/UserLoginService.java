package com.admin.user.service;

import com.admin.common.model.entity.BaseUserLoginDO;
import com.admin.user.model.dto.UserLoginPasswordDTO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserLoginService extends IService<BaseUserLoginDO> {

    String password(UserLoginPasswordDTO dto);

}

package com.admin.user.service;

import com.admin.common.model.entity.SysUserDO;
import com.admin.user.model.dto.UserLoginPasswordDTO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserLoginService extends IService<SysUserDO> {

    String password(UserLoginPasswordDTO dto);

}

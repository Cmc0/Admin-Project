package com.admin.user.service;

import com.admin.common.model.dto.EmailNotBlankDTO;
import com.admin.common.model.entity.SysUserDO;
import com.admin.user.model.dto.UserRegisterByEmailDTO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserRegisterService extends IService<SysUserDO> {

    String userRegisterByEmail(UserRegisterByEmailDTO dto);

    String userRegisterByEmailSendCode(EmailNotBlankDTO dto);
}

package com.admin.user.service;

import com.admin.common.model.dto.EmailNotBlankDTO;
import com.admin.common.model.entity.SysUserDO;
import com.admin.user.model.dto.UserRegByEmailDTO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserRegService extends IService<SysUserDO> {

    String userRegByEmail(UserRegByEmailDTO dto);

    String userRegByEmailSendCode(EmailNotBlankDTO dto);
}

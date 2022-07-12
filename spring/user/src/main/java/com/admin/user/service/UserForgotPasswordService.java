package com.admin.user.service;

import com.admin.common.model.dto.EmailNotBlankDTO;
import com.admin.common.model.entity.SysUserDO;
import com.admin.user.model.dto.UserForgotPasswordDTO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserForgotPasswordService extends IService<SysUserDO> {

    String userForgotPassword(UserForgotPasswordDTO dto);

    String userForgotPasswordSendEmailCode(EmailNotBlankDTO dto);

}

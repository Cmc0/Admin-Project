package com.admin.user.service;

import com.admin.common.model.dto.EmailNotBlankDTO;
import com.admin.common.model.entity.SysUserDO;
import com.admin.user.model.dto.UserSelfForgotPasswordDTO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserForgotPasswordService extends IService<SysUserDO> {

    String userForgotPassword(UserSelfForgotPasswordDTO dto);

    String userForgotPasswordSendEmailCode(EmailNotBlankDTO dto);

}

package com.admin.user.service;

import com.admin.common.model.dto.MyCodeToKeyDTO;
import com.admin.common.model.entity.SysUserDO;
import com.admin.user.model.dto.UserSelfDeleteDTO;
import com.admin.user.model.dto.UserSelfUpdateBaseInfoDTO;
import com.admin.user.model.dto.UserSelfUpdateEmailDTO;
import com.admin.user.model.dto.UserSelfUpdatePasswordDTO;
import com.admin.user.model.vo.UserSelfBaseInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserSelfService extends IService<SysUserDO> {

    String selfLogout();

    UserSelfBaseInfoVO selfBaseInfo();

    String selfUpdateBaseInfo(UserSelfUpdateBaseInfoDTO dto);

    String selfUpdatePassword(UserSelfUpdatePasswordDTO dto);

    String selfUpdatePasswordSendEmailCode();

    String selfUpdateEmail(UserSelfUpdateEmailDTO dto);

    String selfUpdateEmailSendEmailCode();

    String selfUpdateEmailSendEmailCodeCodeToKey(MyCodeToKeyDTO dto);

    String selfRefreshJwtSecretSuf();

    String selfDelete(UserSelfDeleteDTO dto);

    String selfDeleteSendEmailCode();

}

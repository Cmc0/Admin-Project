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

    String userSelfLogout();

    UserSelfBaseInfoVO userSelfBaseInfo();

    String userSelfUpdateBaseInfo(UserSelfUpdateBaseInfoDTO dto);

    String userSelfUpdatePassword(UserSelfUpdatePasswordDTO dto);

    String userSelfUpdatePasswordSendEmailCode();

    String userSelfUpdateEmail(UserSelfUpdateEmailDTO dto);

    String userSelfUpdateEmailSendEmailCode();

    String userSelfUpdateEmailSendEmailCodeCodeToKey(MyCodeToKeyDTO dto);

    String userSelfRefreshJwtSecretSuf();

    String userSelfDelete(UserSelfDeleteDTO dto);

    String userSelfDeleteSendEmailCode();

}

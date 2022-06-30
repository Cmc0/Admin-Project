package com.admin.user.service;

import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.entity.SysUserDO;
import com.admin.user.model.dto.*;
import com.admin.user.model.vo.SysUserInfoByIdVO;
import com.admin.user.model.vo.SysUserPageVO;
import com.admin.user.model.vo.SysUserSelfBaseInfoVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysUserService extends IService<SysUserDO> {

    String selfLogout();

    SysUserSelfBaseInfoVO selfBaseInfo();

    String selfUpdateBaseInfo(SysUserSelfUpdateBaseInfoDTO dto);

    String selfUpdatePassword(SysUserSelfUpdatePasswordDTO dto);

    String selfUpdatePasswordSendEmailCode();

    String selfUpdateEmail(SysUserSelfUpdateEmailDTO dto);

    String selfUpdateEmailSendEmailCode();

    String selfRefreshJwtSecretSuf();

    String selfDelete(SysUserSelfDeleteDTO dto);

    String selfDeleteSendEmailCode();

    Page<SysUserPageVO> myPage(SysUserPageDTO dto);

    String insertOrUpdate(SysUserInsertOrUpdateDTO dto);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    SysUserInfoByIdVO infoById(NotNullId notNullId);

    String refreshJwtSecretSuf(NotEmptyIdSet notEmptyIdSet);

    String resetAvatar(NotEmptyIdSet notEmptyIdSet);

    String updatePassword(SysUserUpdatePasswordDTO dto);

}

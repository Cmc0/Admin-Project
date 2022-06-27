package com.admin.user.service;

import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.entity.SysUserDO;
import com.admin.user.model.dto.SysUserInsertOrUpdateDTO;
import com.admin.user.model.dto.SysUserPageDTO;
import com.admin.user.model.dto.SysUserUpdatePasswordDTO;
import com.admin.user.model.vo.SysUserBaseInfoVO;
import com.admin.user.model.vo.SysUserInfoByIdVO;
import com.admin.user.model.vo.SysUserPageVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysUserService extends IService<SysUserDO> {

    String logout();

    SysUserBaseInfoVO baseInfo();

    Page<SysUserPageVO> myPage(SysUserPageDTO dto);

    String insertOrUpdate(SysUserInsertOrUpdateDTO dto);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    SysUserInfoByIdVO infoById(NotNullId notNullId);

    String refreshJwtSecretSuf(NotEmptyIdSet notEmptyIdSet);

    String resetAvatar(NotEmptyIdSet notEmptyIdSet);

    String updatePassword(SysUserUpdatePasswordDTO dto);
}

package com.admin.user.service;

import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.entity.SysUserDO;
import com.admin.common.model.vo.SelectListVO;
import com.admin.user.model.dto.SysUserInsertOrUpdateDTO;
import com.admin.user.model.dto.SysUserPageDTO;
import com.admin.user.model.dto.SysUserSelectListDTO;
import com.admin.user.model.dto.SysUserUpdatePasswordDTO;
import com.admin.user.model.vo.SysUserInfoByIdVO;
import com.admin.user.model.vo.SysUserPageVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysUserService extends IService<SysUserDO> {

    Page<SysUserPageVO> myPage(SysUserPageDTO dto);

    List<SelectListVO> selectList(SysUserSelectListDTO dto);

    String insertOrUpdate(SysUserInsertOrUpdateDTO dto);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    SysUserInfoByIdVO infoById(NotNullId notNullId);

    String refreshJwtSecretSuf(NotEmptyIdSet notEmptyIdSet);

    String resetAvatar(NotEmptyIdSet notEmptyIdSet);

    String updatePassword(SysUserUpdatePasswordDTO dto);

}

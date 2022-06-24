package com.admin.user.service;

import com.admin.common.model.entity.SysUserDO;
import com.admin.user.model.dto.SysUserPageDTO;
import com.admin.user.model.vo.SysUserPageVO;
import com.admin.user.model.vo.UserBaseInfoVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysUserService extends IService<SysUserDO> {

    String logout();

    UserBaseInfoVO baseInfo();

    Page<SysUserPageVO> myPage(SysUserPageDTO dto);
}

package com.admin.user.service;

import com.admin.common.model.entity.SysUserDO;
import com.admin.user.model.vo.UserBaseInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<SysUserDO> {

    String logout();

    UserBaseInfoVO baseInfo();
}

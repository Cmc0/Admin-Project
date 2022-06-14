package com.admin.user.service;

import com.admin.common.model.entity.BaseUserInfoDO;
import com.admin.user.model.vo.UserBaseInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<BaseUserInfoDO> {

    String logout();

    UserBaseInfoVO baseInfo();
}

package com.admin.user.mapper;

import com.admin.common.model.entity.BaseUserInfoDO;
import com.admin.user.model.vo.UserCenterBaseInfoVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface UserInfoMapper extends BaseMapper<BaseUserInfoDO> {

    // 用户基本信息
    UserCenterBaseInfoVO baseInfo(@Param("userId") Long userId);

}

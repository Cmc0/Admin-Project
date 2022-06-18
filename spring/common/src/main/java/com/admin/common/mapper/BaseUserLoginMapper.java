package com.admin.common.mapper;

import com.admin.common.model.entity.BaseUserLoginDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

public interface BaseUserLoginMapper extends BaseMapper<BaseUserLoginDO> {

    // 通过 email获取 BaseUserLoginDO
    BaseUserLoginDO getByEmail(@Param("email") String email);

}





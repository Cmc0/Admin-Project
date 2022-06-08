package com.cmc.common.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BaseUserMapper {

    // 通过 userId 获取：用户权限
    List<String> getAuthsByUserId(@Param("userId") String userId);

}

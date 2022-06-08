package com.cmc.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cmc.common.model.entity.MenuDO;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

public interface MenuMapper extends BaseMapper<MenuDO> {

    // 通过 menuIdSet，获取 userIdSet
    Set<Long> getUserIdSetByMenuIdSet(@Param("menuIdSet") Set<Long> menuIdSet);

    // 判断默认角色是否包含了 菜单 idSet
    boolean checkDefaultRoleHasMenu(@Param("menuIdSet") Set<Long> menuIdSet);

}





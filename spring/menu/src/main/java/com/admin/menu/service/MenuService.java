package com.admin.menu.service;

import com.admin.common.model.dto.AddOrderNoDTO;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.entity.SysMenuDO;
import com.admin.menu.model.dto.MenuInsertOrUpdateDTO;
import com.admin.menu.model.dto.MenuPageDTO;
import com.admin.menu.model.vo.MenuInfoByIdVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface MenuService extends IService<SysMenuDO> {

    String insertOrUpdate(MenuInsertOrUpdateDTO dto);

    Page<SysMenuDO> myPage(MenuPageDTO dto);

    List<SysMenuDO> tree(MenuPageDTO dto);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    List<SysMenuDO> menuListForUser();

    MenuInfoByIdVO infoById(NotNullId notNullId);

    String addOrderNo(AddOrderNoDTO dto);
}

package com.admin.menu.service;

import com.admin.common.model.dto.AddOrderNoDTO;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.entity.SysMenuDO;
import com.admin.menu.model.dto.SysMenuInsertOrUpdateDTO;
import com.admin.menu.model.dto.SysMenuPageDTO;
import com.admin.menu.model.vo.SysMenuInfoByIdVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysMenuService extends IService<SysMenuDO> {

    String insertOrUpdate(SysMenuInsertOrUpdateDTO dto);

    Page<SysMenuDO> myPage(SysMenuPageDTO dto);

    List<SysMenuDO> tree(SysMenuPageDTO dto);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    List<SysMenuDO> userSelfMenuList();

    SysMenuInfoByIdVO infoById(NotNullId notNullId);

    String addOrderNo(AddOrderNoDTO dto);
}

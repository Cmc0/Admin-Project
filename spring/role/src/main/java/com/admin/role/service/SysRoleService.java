package com.admin.role.service;

import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.entity.SysRoleDO;
import com.admin.role.model.dto.SysRoleInsertOrUpdateDTO;
import com.admin.role.model.dto.SysRolePageDTO;
import com.admin.role.model.vo.SysRolePageVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysRoleService extends IService<SysRoleDO> {

    String insertOrUpdate(SysRoleInsertOrUpdateDTO dto);

    Page<SysRoleDO> myPage(SysRolePageDTO dto);

    SysRolePageVO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

}

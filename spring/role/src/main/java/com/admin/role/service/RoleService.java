package com.admin.role.service;

import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.entity.SysRoleDO;
import com.admin.role.model.dto.RoleInsertOrUpdateDTO;
import com.admin.role.model.dto.RolePageDTO;
import com.admin.role.model.vo.RolePageVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface RoleService extends IService<SysRoleDO> {

    String insertOrUpdate(RoleInsertOrUpdateDTO dto);

    Page<SysRoleDO> myPage(RolePageDTO dto);

    RolePageVO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

}

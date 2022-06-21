package com.admin.dept.service;

import com.admin.common.model.dto.AddOrderNoDTO;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.dept.model.dto.SysDeptInsertOrUpdateDTO;
import com.admin.dept.model.dto.SysDeptPageDTO;
import com.admin.dept.model.entity.SysDeptDO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysDeptService extends IService<SysDeptDO> {

    String insertOrUpdate(SysDeptInsertOrUpdateDTO dto);

    Page<SysDeptDO> myPage(SysDeptPageDTO dto);

    List<SysDeptDO> tree(SysDeptPageDTO dto);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    SysDeptDO infoById(NotNullId notNullId);

    String addOrderNo(AddOrderNoDTO dto);
}

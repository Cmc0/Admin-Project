package com.admin.param.service;

import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.entity.SysParamDO;
import com.admin.param.model.dto.SysParamInsertOrUpdateDTO;
import com.admin.param.model.dto.SysParamPageDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysParamService extends IService<SysParamDO> {

    String insertOrUpdate(SysParamInsertOrUpdateDTO dto);

    Page<SysParamDO> myPage(SysParamPageDTO dto);

    SysParamDO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

}

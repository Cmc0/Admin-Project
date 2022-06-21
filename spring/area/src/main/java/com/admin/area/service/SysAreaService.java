package com.admin.area.service;

import com.admin.area.model.dto.SysAreaInsertOrUpdateDTO;
import com.admin.area.model.dto.SysAreaPageDTO;
import com.admin.area.model.entity.SysAreaDO;
import com.admin.area.model.vo.SysAreaInfoByIdVO;
import com.admin.common.model.dto.AddOrderNoDTO;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysAreaService extends IService<SysAreaDO> {

    String insertOrUpdate(SysAreaInsertOrUpdateDTO dto);

    Page<SysAreaDO> myPage(SysAreaPageDTO dto);

    List<SysAreaDO> tree(SysAreaPageDTO dto);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    SysAreaInfoByIdVO infoById(NotNullId notNullId);

    String addOrderNo(AddOrderNoDTO dto);
}

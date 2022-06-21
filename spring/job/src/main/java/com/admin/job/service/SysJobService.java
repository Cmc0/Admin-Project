package com.admin.job.service;

import com.admin.common.model.dto.AddOrderNoDTO;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.job.model.dto.SysJobInsertOrUpdateDTO;
import com.admin.job.model.dto.SysJobPageDTO;
import com.admin.job.model.entity.SysJobDO;
import com.admin.job.model.vo.SysJobInfoByIdVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysJobService extends IService<SysJobDO> {

    String insertOrUpdate(SysJobInsertOrUpdateDTO dto);

    Page<SysJobDO> myPage(SysJobPageDTO dto);

    List<SysJobDO> tree(SysJobPageDTO dto);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    SysJobInfoByIdVO infoById(NotNullId notNullId);

    String addOrderNo(AddOrderNoDTO dto);
}

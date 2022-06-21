package com.admin.dict.service;

import com.admin.common.model.dto.AddOrderNoDTO;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.entity.SysDictDO;
import com.admin.dict.model.dto.SysDictInsertOrUpdateDTO;
import com.admin.dict.model.dto.SysDictPageDTO;
import com.admin.dict.model.vo.SysDictTreeVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysDictService extends IService<SysDictDO> {

    String insertOrUpdate(SysDictInsertOrUpdateDTO dto);

    Page<SysDictDO> myPage(SysDictPageDTO dto);

    List<SysDictTreeVO> tree(SysDictPageDTO dto);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    SysDictDO infoById(NotNullId notNullId);

    String addOrderNo(AddOrderNoDTO dto);
}

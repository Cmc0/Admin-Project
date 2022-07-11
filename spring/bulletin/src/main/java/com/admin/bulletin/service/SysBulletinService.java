package com.admin.bulletin.service;

import com.admin.bulletin.model.dto.SysBulletinInsertOrUpdateDTO;
import com.admin.bulletin.model.dto.SysBulletinPageDTO;
import com.admin.bulletin.model.dto.SysBulletinUserSelfPageDTO;
import com.admin.bulletin.model.entity.SysBulletinDO;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

public interface SysBulletinService extends IService<SysBulletinDO> {

    String insertOrUpdate(SysBulletinInsertOrUpdateDTO dto);

    String publish(NotNullId notNullId);

    String revoke(NotNullId notNullId);

    Page<SysBulletinDO> myPage(SysBulletinPageDTO dto);

    SysBulletinDO infoById(NotNullId notNullId);

    String deleteByIdSet(NotEmptyIdSet notEmptyIdSet);

    Page<SysBulletinDO> userSelfPage(SysBulletinUserSelfPageDTO dto);

    Long userSelfCount();

}

package com.admin.bulletin.service.impl;

import com.admin.bulletin.mapper.SysBulletinMapper;
import com.admin.bulletin.model.dto.SysBulletinInsertOrUpdateDTO;
import com.admin.bulletin.model.dto.SysBulletinPageDTO;
import com.admin.bulletin.model.dto.SysBulletinUserSelfPageDTO;
import com.admin.bulletin.model.entity.SysBulletinDO;
import com.admin.bulletin.model.vo.SysBulletinPageVO;
import com.admin.bulletin.model.vo.SysBulletinUserSelfPageVO;
import com.admin.bulletin.service.SysBulletinService;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class SysBulletinServiceImpl extends ServiceImpl<SysBulletinMapper, SysBulletinDO>
    implements SysBulletinService {

    /**
     * 新增/修改
     */
    @Override
    public String insertOrUpdate(SysBulletinInsertOrUpdateDTO dto) {
        return null;
    }

    /**
     * 发布 公告
     */
    @Override
    public String publish(NotNullId notNullId) {
        return null;
    }

    /**
     * 撤回 公告
     */
    @Override
    public String revoke(NotNullId notNullId) {
        return null;
    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysBulletinPageVO> myPage(SysBulletinPageDTO dto) {
        return null;
    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysBulletinDO infoById(NotNullId notNullId) {
        return null;
    }

    /**
     * 批量删除
     */
    @Override
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {
        return null;
    }

    /**
     * 分页排序查询：当前用户可以查看的公告
     */
    @Override
    public Page<SysBulletinUserSelfPageVO> userSelfPage(SysBulletinUserSelfPageDTO dto) {
        return null;
    }

    /**
     * 获取：当前用户可以查看的公告，总数
     */
    @Override
    public Long userSelfCount() {
        return null;
    }
}

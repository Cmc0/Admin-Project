package com.admin.role.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.mapper.SysRoleMapper;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.entity.*;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.MyEntityUtil;
import com.admin.role.exception.BizCodeEnum;
import com.admin.role.model.dto.SysRoleInsertOrUpdateDTO;
import com.admin.role.model.dto.SysRolePageDTO;
import com.admin.role.model.vo.SysRolePageVO;
import com.admin.role.service.SysRoleRefMenuService;
import com.admin.role.service.SysRoleRefUserService;
import com.admin.role.service.SysRoleService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRoleDO> implements SysRoleService {

    @Resource
    SysRoleRefMenuService sysRoleRefMenuService;
    @Resource
    SysRoleRefUserService sysRoleRefUserService;

    /**
     * 新增/修改
     */
    @Override
    @Transactional
    public String insertOrUpdate(SysRoleInsertOrUpdateDTO dto) {

        // 角色名，不能重复
        Long count = lambdaQuery().eq(SysRoleDO::getName, dto.getName())
            .ne(dto.getId() != null, BaseEntityTwo::getId, dto.getId()).count();
        if (count != 0) {
            ApiResultVO.error(BizCodeEnum.THE_SAME_ROLE_NAME_EXISTS);
        }

        // 如果是默认角色，则取消之前的默认角色
        if (dto.isDefaultFlag()) {
            lambdaUpdate().set(SysRoleDO::getDefaultFlag, false).eq(SysRoleDO::getDefaultFlag, true)
                .ne(dto.getId() != null, BaseEntityTwo::getId, dto.getId()).update();
        }

        SysRoleDO sysRoleDO = new SysRoleDO();
        sysRoleDO.setName(dto.getName());
        sysRoleDO.setDefaultFlag(dto.isDefaultFlag());
        sysRoleDO.setEnableFlag(dto.isEnableFlag());
        sysRoleDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));
        sysRoleDO.setId(dto.getId());

        if (dto.getId() == null) {
            // 新增
            baseMapper.insert(sysRoleDO);
        } else {
            // 修改
            baseMapper.updateById(sysRoleDO);
            // 先删除子表数据
            deleteByIdSetSub(Collections.singleton(dto.getId()));
        }

        // 再插入子表数据
        if (CollUtil.isNotEmpty(dto.getMenuIdSet())) {
            List<SysRoleRefMenuDO> insertList = new ArrayList<>();
            for (Long menuId : dto.getMenuIdSet()) {
                SysRoleRefMenuDO sysRoleRefMenuDO = new SysRoleRefMenuDO();
                sysRoleRefMenuDO.setRoleId(sysRoleDO.getId());
                sysRoleRefMenuDO.setMenuId(menuId);
                insertList.add(sysRoleRefMenuDO);
            }
            sysRoleRefMenuService.saveBatch(insertList);
        }

        if (CollUtil.isNotEmpty(dto.getUserIdSet())) {
            List<SysRoleRefUserDO> insertList = new ArrayList<>();
            for (Long userId : dto.getUserIdSet()) {
                SysRoleRefUserDO sysRoleRefUserDO = new SysRoleRefUserDO();
                sysRoleRefUserDO.setRoleId(sysRoleDO.getId());
                sysRoleRefUserDO.setUserId(userId);
                insertList.add(sysRoleRefUserDO);
            }
            sysRoleRefUserService.saveBatch(insertList);
        }

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysRoleDO> myPage(SysRolePageDTO dto) {

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysRoleDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntityThree::getRemark, dto.getRemark())
            .eq(dto.getId() != null, BaseEntityTwo::getId, dto.getId())
            .eq(dto.getEnableFlag() != null, BaseEntityThree::getEnableFlag, dto.getEnableFlag())
            .eq(dto.getDefaultFlag() != null, SysRoleDO::getDefaultFlag, dto.getDefaultFlag()).page(dto.getPage());

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysRolePageVO infoById(NotNullId notNullId) {

        SysRolePageVO sysRolePageVO = BeanUtil.copyProperties(getById(notNullId.getId()), SysRolePageVO.class);

        if (sysRolePageVO == null) {
            return null;
        }

        // 完善子表的数据
        List<SysRoleRefMenuDO> menuList =
            sysRoleRefMenuService.lambdaQuery().eq(SysRoleRefMenuDO::getRoleId, sysRolePageVO.getId())
                .select(SysRoleRefMenuDO::getMenuId).list();

        List<SysRoleRefUserDO> userList =
            sysRoleRefUserService.lambdaQuery().eq(SysRoleRefUserDO::getRoleId, sysRolePageVO.getId())
                .select(SysRoleRefUserDO::getUserId).list();

        sysRolePageVO.setMenuIdSet(menuList.stream().map(SysRoleRefMenuDO::getMenuId).collect(Collectors.toSet()));
        sysRolePageVO.setUserIdSet(userList.stream().map(SysRoleRefUserDO::getUserId).collect(Collectors.toSet()));

        return sysRolePageVO;
    }

    /**
     * 批量删除
     */
    @Override
    @Transactional
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        deleteByIdSetSub(notEmptyIdSet.getIdSet()); // 删除子表数据

        baseMapper.deleteBatchIds(notEmptyIdSet.getIdSet());

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 删除子表数据
     */
    private void deleteByIdSetSub(Set<Long> idSet) {

        // 删除 角色菜单关联表
        sysRoleRefMenuService.removeByIds(idSet);
        // 删除 角色用户关联表
        sysRoleRefUserService.removeByIds(idSet);

    }

}





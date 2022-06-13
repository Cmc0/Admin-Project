package com.admin.role.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.mapper.BaseRoleMapper;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.entity.*;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.MyEntityUtil;
import com.admin.role.exception.BizCodeEnum;
import com.admin.role.model.dto.RoleInsertOrUpdateDTO;
import com.admin.role.model.dto.RolePageDTO;
import com.admin.role.model.vo.RolePageVO;
import com.admin.role.service.RoleRefMenuService;
import com.admin.role.service.RoleRefUserService;
import com.admin.role.service.RoleService;
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
public class RoleServiceImpl extends ServiceImpl<BaseRoleMapper, BaseRoleDO> implements RoleService {

    @Resource
    RoleRefMenuService roleRefMenuService;
    @Resource
    RoleRefUserService roleRefUserService;

    /**
     * 新增/修改
     */
    @Override
    @Transactional
    public String insertOrUpdate(RoleInsertOrUpdateDTO dto) {

        // 角色名，不能重复
        Long count = lambdaQuery().eq(BaseRoleDO::getName, dto.getName())
            .ne(dto.getId() != null, BaseEntityTwo::getId, dto.getId()).count();
        if (count != 0) {
            ApiResultVO.error(BizCodeEnum.THE_SAME_ROLE_NAME_EXISTS);
        }

        // 如果是默认角色，则取消之前的默认角色
        if (dto.isDefaultFlag()) {
            lambdaUpdate().set(BaseRoleDO::getDefaultFlag, false).eq(BaseRoleDO::getDefaultFlag, true)
                .ne(dto.getId() != null, BaseEntityTwo::getId, dto.getId()).update();
        }

        BaseRoleDO baseRoleDO = new BaseRoleDO();
        baseRoleDO.setName(dto.getName());
        baseRoleDO.setDefaultFlag(dto.isDefaultFlag());
        baseRoleDO.setEnableFlag(dto.isEnableFlag());
        baseRoleDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));
        baseRoleDO.setId(dto.getId());

        if (dto.getId() == null) {
            // 新增
            baseMapper.insert(baseRoleDO);
        } else {
            // 修改
            baseMapper.updateById(baseRoleDO);
            // 先删除子表数据
            deleteByIdSetSub(Collections.singleton(dto.getId()));
        }

        // 再插入子表数据
        if (CollUtil.isNotEmpty(dto.getMenuIdSet())) {
            List<BaseRoleRefMenuDO> insertList = new ArrayList<>();
            for (Long menuId : dto.getMenuIdSet()) {
                BaseRoleRefMenuDO baseRoleRefMenuDO = new BaseRoleRefMenuDO();
                baseRoleRefMenuDO.setRoleId(baseRoleDO.getId());
                baseRoleRefMenuDO.setMenuId(menuId);
                insertList.add(baseRoleRefMenuDO);
            }
            roleRefMenuService.saveBatch(insertList);
        }

        if (CollUtil.isNotEmpty(dto.getUserIdSet())) {
            List<BaseRoleRefUserDO> insertList = new ArrayList<>();
            for (Long userId : dto.getUserIdSet()) {
                BaseRoleRefUserDO baseRoleRefUserDO = new BaseRoleRefUserDO();
                baseRoleRefUserDO.setRoleId(baseRoleDO.getId());
                baseRoleRefUserDO.setUserId(userId);
                insertList.add(baseRoleRefUserDO);
            }
            roleRefUserService.saveBatch(insertList);
        }

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<BaseRoleDO> myPage(RolePageDTO dto) {

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), BaseRoleDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getRemark()), BaseEntityThree::getRemark, dto.getRemark())
            .eq(dto.getId() != null, BaseEntityTwo::getId, dto.getId())
            .eq(dto.getEnableFlag() != null, BaseEntityThree::getEnableFlag, dto.getEnableFlag())
            .eq(dto.getDefaultFlag() != null, BaseRoleDO::getDefaultFlag, dto.getDefaultFlag()).page(dto.getPage());

    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public RolePageVO infoById(NotNullId notNullId) {

        RolePageVO rolePageVO = BeanUtil.copyProperties(getById(notNullId.getId()), RolePageVO.class);

        if (rolePageVO == null) {
            return null;
        }

        // 完善子表的数据
        List<BaseRoleRefMenuDO> menuList =
            roleRefMenuService.lambdaQuery().eq(BaseRoleRefMenuDO::getRoleId, rolePageVO.getId())
                .select(BaseRoleRefMenuDO::getMenuId).list();

        List<BaseRoleRefUserDO> userList =
            roleRefUserService.lambdaQuery().eq(BaseRoleRefUserDO::getRoleId, rolePageVO.getId())
                .select(BaseRoleRefUserDO::getUserId).list();

        rolePageVO.setMenuIdSet(menuList.stream().map(BaseRoleRefMenuDO::getMenuId).collect(Collectors.toSet()));
        rolePageVO.setUserIdSet(userList.stream().map(BaseRoleRefUserDO::getUserId).collect(Collectors.toSet()));

        return rolePageVO;
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
        roleRefMenuService.removeByIds(idSet);
        // 删除 角色用户关联表
        roleRefUserService.removeByIds(idSet);

    }

}





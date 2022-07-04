package com.admin.menu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.mapper.SysMenuMapper;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.dto.AddOrderNoDTO;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.dto.NotNullId;
import com.admin.common.model.entity.*;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.MyEntityUtil;
import com.admin.common.util.MyTreeUtil;
import com.admin.common.util.UserUtil;
import com.admin.menu.exception.BizCodeEnum;
import com.admin.menu.model.dto.SysMenuInsertOrUpdateDTO;
import com.admin.menu.model.dto.SysMenuPageDTO;
import com.admin.menu.model.vo.SysMenuInfoByIdVO;
import com.admin.menu.service.SysMenuService;
import com.admin.role.service.SysRoleRefMenuService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenuDO> implements SysMenuService {

    @Resource
    SysRoleRefMenuService sysRoleRefMenuService;

    /**
     * 新增/修改
     */
    @Override
    @Transactional
    public String insertOrUpdate(SysMenuInsertOrUpdateDTO dto) {

        if (dto.getId() != null && dto.getId().equals(dto.getParentId())) {
            ApiResultVO.error(BaseBizCodeEnum.PARENT_ID_CANNOT_BE_EQUAL_TO_ID);
        }

        if (dto.isAuthFlag() && StrUtil.isBlank(dto.getAuths())) {
            ApiResultVO.error("操作失败：权限菜单的权限不能为空");
        }

        // path不能重复
        if (StrUtil.isNotBlank(dto.getPath())) {
            Long count = lambdaQuery().eq(SysMenuDO::getPath, dto.getPath())
                .ne(dto.getId() != null, BaseEntityTwo::getId, dto.getId()).count();
            if (count != 0) {
                ApiResultVO.error(BizCodeEnum.MENU_URI_IS_EXIST);
            }
        }

        // 如果是起始页面，则取消之前的起始页面
        if (dto.isFirstFlag()) {
            lambdaUpdate().set(SysMenuDO::getFirstFlag, false).eq(SysMenuDO::getFirstFlag, true)
                .ne(dto.getId() != null, BaseEntityTwo::getId, dto.getId()).update();
        }

        // 判断：path是否以 http开头
        dto.setLinkFlag(StrUtil.startWith(dto.getPath(), "http", true));

        if (dto.getId() != null) {
            deleteByIdSetSub(Collections.singleton(dto.getId())); // 先删除 子表数据
        }

        SysMenuDO sysMenuDO = getEntityByDTO(dto);
        saveOrUpdate(sysMenuDO);
        UserUtil.updateMenuIdAndAuthsListForRedis(); // 更新：redis中的缓存

        insertOrUpdateSub(sysMenuDO.getId(), dto); // 新增 子表数据

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 新增/修改：新增 子表数据
     */
    private void insertOrUpdateSub(Long id, SysMenuInsertOrUpdateDTO dto) {

        // 新增：菜单角色 关联表数据
        if (CollUtil.isNotEmpty(dto.getRoleIdSet())) {
            List<SysRoleRefMenuDO> insertList = new ArrayList<>();
            for (Long item : dto.getRoleIdSet()) {
                SysRoleRefMenuDO sysRoleRefMenuDO = new SysRoleRefMenuDO();
                sysRoleRefMenuDO.setRoleId(item);
                sysRoleRefMenuDO.setMenuId(id);
                insertList.add(sysRoleRefMenuDO);
            }
            sysRoleRefMenuService.saveBatch(insertList);
        }

        UserUtil.updateRoleRefMenuForRedis(); // 更新：redis中的缓存

    }

    /**
     * 通过 dto，获取 实体类
     */
    private SysMenuDO getEntityByDTO(SysMenuInsertOrUpdateDTO dto) {

        SysMenuDO sysMenuDO = new SysMenuDO();

        sysMenuDO.setName(dto.getName());
        sysMenuDO.setPath(MyEntityUtil.getNotNullStr(dto.getPath()));
        sysMenuDO.setIcon(MyEntityUtil.getNotNullStr(dto.getIcon()));
        sysMenuDO.setParentId(MyEntityUtil.getNotNullParentId(dto.getParentId()));
        sysMenuDO.setId(dto.getId());
        sysMenuDO.setOrderNo(dto.getOrderNo());
        sysMenuDO.setEnableFlag(dto.isEnableFlag());
        sysMenuDO.setLinkFlag(dto.isLinkFlag());
        sysMenuDO.setRouter(MyEntityUtil.getNotNullStr(dto.getRouter()));
        sysMenuDO.setRedirect(MyEntityUtil.getNotNullStr(dto.getRedirect()));
        sysMenuDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));
        sysMenuDO.setFirstFlag(dto.isFirstFlag());
        sysMenuDO.setAuthFlag(dto.isAuthFlag());
        if (dto.isAuthFlag()) {
            sysMenuDO.setAuths(dto.getAuths()); // 只有权限菜单，才可以设置 auths
            sysMenuDO.setShowFlag(false);
        } else {
            sysMenuDO.setAuths("");
            sysMenuDO.setShowFlag(dto.isShowFlag());
        }

        return sysMenuDO;
    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysMenuDO> myPage(SysMenuPageDTO dto) {

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), SysMenuDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getPath()), SysMenuDO::getPath, dto.getPath())
            .like(StrUtil.isNotBlank(dto.getAuths()), SysMenuDO::getAuths, dto.getAuths())
            .like(StrUtil.isNotBlank(dto.getRedirect()), SysMenuDO::getRedirect, dto.getRedirect())
            .eq(StrUtil.isNotBlank(dto.getRouter()), SysMenuDO::getRouter, dto.getRouter())
            .eq(dto.getParentId() != null, SysMenuDO::getParentId, dto.getParentId())
            .eq(dto.getEnableFlag() != null, BaseEntityThree::getEnableFlag, dto.getEnableFlag())
            .eq(dto.getLinkFlag() != null, SysMenuDO::getLinkFlag, dto.getLinkFlag())
            .eq(dto.getFirstFlag() != null, SysMenuDO::getFirstFlag, dto.getFirstFlag())
            .eq(dto.getAuthFlag() != null, SysMenuDO::getAuthFlag, dto.getAuthFlag())
            .eq(dto.getShowFlag() != null, SysMenuDO::getShowFlag, dto.getShowFlag())
            .orderByDesc(BaseEntityFour::getOrderNo).page(dto.getPage());
    }

    /**
     * 查询：树结构
     */
    @Override
    public List<SysMenuDO> tree(SysMenuPageDTO dto) {

        List<SysMenuDO> resList = new ArrayList<>();

        // 根据条件进行筛选，得到符合条件的数据，然后再逆向生成整棵树，并返回这个树结构
        dto.setPageSize(-1); // 不分页
        List<SysMenuDO> sysMenuDOList = myPage(dto).getRecords();

        if (sysMenuDOList.size() == 0) {
            return resList;
        }

        List<SysMenuDO> allList = list();

        if (allList.size() == 0) {
            return resList;
        }

        return MyTreeUtil.getFullTreeByDeepNode(sysMenuDOList, allList);
    }

    /**
     * 批量删除
     */
    @Override
    @Transactional
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        // 如果存在下级，则无法删除
        Long count = lambdaQuery().in(SysMenuDO::getParentId, notEmptyIdSet.getIdSet()).count();
        if (count != 0) {
            ApiResultVO.error(BaseBizCodeEnum.PLEASE_DELETE_THE_CHILD_NODE_FIRST);
        }

        // 删除子表数据
        deleteByIdSetSub(notEmptyIdSet.getIdSet());

        removeByIds(notEmptyIdSet.getIdSet());

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 删除子表数据
     */
    private void deleteByIdSetSub(Set<Long> idSet) {

        // 删除 角色菜单关联表
        sysRoleRefMenuService.lambdaUpdate().in(SysRoleRefMenuDO::getMenuId, idSet).remove();

    }

    /**
     * 获取当前用户绑定的菜单
     */
    @Override
    public List<SysMenuDO> menuListForUser() {
        Long userId = UserUtil.getCurrentUserId();

        if (BaseConstant.ADMIN_ID.equals(userId)) {
            // 如果是 admin账号，则查询所有【不是被禁用了的】菜单
            /** 这里和{@link UserUtil#getMenuListByUserId}需要进行同步修改 */
            return lambdaQuery()
                .select(BaseEntityTwo::getId, BaseEntityFour::getParentId, SysMenuDO::getPath, SysMenuDO::getIcon,
                    SysMenuDO::getRouter, SysMenuDO::getName, SysMenuDO::getFirstFlag, SysMenuDO::getLinkFlag,
                    SysMenuDO::getShowFlag, SysMenuDO::getAuths, SysMenuDO::getAuthFlag)
                .eq(BaseEntityThree::getEnableFlag, true).orderByDesc(SysMenuDO::getOrderNo).list();
        }

        // 获取当前用户绑定的菜单
        return UserUtil.getMenuListByUserId(userId, 1).stream()
            .sorted(Comparator.comparing(BaseEntityFour::getOrderNo, Comparator.reverseOrder()))
            .collect(Collectors.toList());
    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public SysMenuInfoByIdVO infoById(NotNullId notNullId) {

        SysMenuInfoByIdVO sysMenuInfoByIdVO =
            BeanUtil.copyProperties(getById(notNullId.getId()), SysMenuInfoByIdVO.class);

        if (sysMenuInfoByIdVO == null) {
            return null;
        }

        // 设置 角色 idSet
        List<SysRoleRefMenuDO> sysRoleRefMenuDOList =
            sysRoleRefMenuService.lambdaQuery().eq(SysRoleRefMenuDO::getMenuId, notNullId.getId())
                .select(SysRoleRefMenuDO::getRoleId).list();
        sysMenuInfoByIdVO
            .setRoleIdSet(sysRoleRefMenuDOList.stream().map(SysRoleRefMenuDO::getRoleId).collect(Collectors.toSet()));

        MyEntityUtil.handleParentId(sysMenuInfoByIdVO);

        return sysMenuInfoByIdVO;
    }

    /**
     * 通过主键 idSet，加减排序号
     */
    @Override
    @Transactional
    public String addOrderNo(AddOrderNoDTO dto) {

        if (dto.getNumber() == 0) {
            return BaseBizCodeEnum.API_RESULT_OK.getMsg();
        }

        List<SysMenuDO> listByIds = listByIds(dto.getIdSet());

        for (SysMenuDO item : listByIds) {
            item.setOrderNo(item.getOrderNo() + dto.getNumber());
        }

        updateBatchById(listByIds);

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }
}





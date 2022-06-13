package com.admin.menu.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.mapper.BaseMenuMapper;
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
import com.admin.menu.model.dto.MenuInsertOrUpdateDTO;
import com.admin.menu.model.dto.MenuPageDTO;
import com.admin.menu.model.vo.MenuInfoByIdVO;
import com.admin.menu.service.MenuService;
import com.admin.role.service.RoleRefMenuService;
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
public class MenuServiceImpl extends ServiceImpl<BaseMenuMapper, BaseMenuDO> implements MenuService {

    @Resource
    RoleRefMenuService roleRefMenuService;

    /**
     * 新增/修改
     */
    @Override
    @Transactional
    public String insertOrUpdate(MenuInsertOrUpdateDTO dto) {

        if (dto.getId() != null && dto.getId().equals(dto.getParentId())) {
            ApiResultVO.error(BaseBizCodeEnum.PARENT_ID_CANNOT_BE_EQUAL_TO_ID);
        }

        if (dto.isAuthFlag() && StrUtil.isBlank(dto.getAuths())) {
            ApiResultVO.error("操作失败：权限菜单的权限不能为空");
        }

        // path不能重复
        if (StrUtil.isNotBlank(dto.getPath())) {
            Long count = lambdaQuery().eq(BaseMenuDO::getPath, dto.getPath())
                .ne(dto.getId() != null, BaseEntityTwo::getId, dto.getId()).count();
            if (count != 0) {
                ApiResultVO.error(BizCodeEnum.MENU_URI_IS_EXIST);
            }
        }

        // 如果是起始页面，则取消之前的起始页面
        if (dto.isFirstFlag()) {
            lambdaUpdate().set(BaseMenuDO::getFirstFlag, false).eq(BaseMenuDO::getFirstFlag, true)
                .ne(dto.getId() != null, BaseEntityTwo::getId, dto.getId()).update();
        }

        // 如果是外链，则清空 一些属性
        if (dto.isLinkFlag()) {
            dto.setRouter(null);
            dto.setRedirect(null);
        }

        if (dto.getId() != null) {
            deleteByIdSetSub(Collections.singleton(dto.getId())); // 先删除 子表数据
        }

        BaseMenuDO baseMenuDO = getEntityByDTO(dto);
        saveOrUpdate(baseMenuDO);

        insertOrUpdateSub(baseMenuDO.getId(), dto); // 新增 子表数据

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 新增/修改：新增 子表数据
     */
    private void insertOrUpdateSub(Long id, MenuInsertOrUpdateDTO dto) {

        // 新增：菜单角色 关联表数据
        if (CollUtil.isNotEmpty(dto.getRoleIdSet())) {
            List<BaseRoleRefMenuDO> insertList = new ArrayList<>();
            for (Long item : dto.getRoleIdSet()) {
                BaseRoleRefMenuDO baseRoleRefMenuDO = new BaseRoleRefMenuDO();
                baseRoleRefMenuDO.setRoleId(item);
                baseRoleRefMenuDO.setMenuId(id);
                insertList.add(baseRoleRefMenuDO);
            }
            roleRefMenuService.saveBatch(insertList);
        }

    }

    /**
     * 通过 dto，获取 实体类
     */
    private BaseMenuDO getEntityByDTO(MenuInsertOrUpdateDTO dto) {

        BaseMenuDO baseMenuDO = new BaseMenuDO();

        baseMenuDO.setName(dto.getName());
        baseMenuDO.setPath(MyEntityUtil.getNotNullStr(dto.getPath()));
        baseMenuDO.setIcon(MyEntityUtil.getNotNullStr(dto.getIcon()));
        baseMenuDO.setParentId(MyEntityUtil.getNullParentId(dto.getParentId()));
        baseMenuDO.setId(dto.getId());
        baseMenuDO.setOrderNo(dto.getOrderNo());
        baseMenuDO.setEnableFlag(dto.isEnableFlag());
        baseMenuDO.setLinkFlag(dto.isLinkFlag());
        baseMenuDO.setRouter(MyEntityUtil.getNotNullStr(dto.getRouter()));
        baseMenuDO.setRedirect(MyEntityUtil.getNotNullStr(dto.getRedirect()));
        baseMenuDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));
        baseMenuDO.setFirstFlag(dto.isFirstFlag());
        if (dto.getId() == null) {
            baseMenuDO.setAuthFlag(dto.isAuthFlag()); // 当新增时，才允许设置 authFlag的值
        }
        if (dto.isAuthFlag()) {
            baseMenuDO.setAuths(dto.getAuths()); // 只有权限菜单，才可以设置 auths
            baseMenuDO.setShowFlag(false);
        } else {
            baseMenuDO.setAuths("");
            baseMenuDO.setShowFlag(dto.isShowFlag());
        }

        return baseMenuDO;
    }

    /**
     * 分页排序查询
     */
    @Override
    public Page<BaseMenuDO> myPage(MenuPageDTO dto) {
        return doMyPage(dto);
    }

    /**
     * 执行：分页排序查询
     */
    private Page<BaseMenuDO> doMyPage(MenuPageDTO dto) {

        return lambdaQuery().like(StrUtil.isNotBlank(dto.getName()), BaseMenuDO::getName, dto.getName())
            .like(StrUtil.isNotBlank(dto.getPath()), BaseMenuDO::getPath, dto.getPath())
            .like(StrUtil.isNotBlank(dto.getAuths()), BaseMenuDO::getAuths, dto.getAuths())
            .like(StrUtil.isNotBlank(dto.getRedirect()), BaseMenuDO::getRedirect, dto.getRedirect())
            .eq(dto.getId() != null, BaseEntityTwo::getId, dto.getId())
            .eq(StrUtil.isNotBlank(dto.getRouter()), BaseMenuDO::getRouter, dto.getRouter())
            .eq(dto.getParentId() != null, BaseMenuDO::getParentId, dto.getParentId())
            .eq(dto.getEnableFlag() != null, BaseEntityThree::getEnableFlag, dto.getEnableFlag())
            .eq(dto.getLinkFlag() != null, BaseMenuDO::getLinkFlag, dto.getLinkFlag())
            .eq(dto.getFirstFlag() != null, BaseMenuDO::getFirstFlag, dto.getFirstFlag())
            .eq(dto.getAuthFlag() != null, BaseMenuDO::getAuthFlag, dto.getAuthFlag())
            .eq(dto.getShowFlag() != null, BaseMenuDO::getShowFlag, dto.getShowFlag()).page(dto.getPage());

    }

    /**
     * 查询：树结构
     */
    @Override
    public List<BaseMenuDO> tree(MenuPageDTO dto) {

        List<BaseMenuDO> resList = new ArrayList<>(); // 本接口返回值

        // 根据条件进行筛选，得到符合条件的数据，然后再逆向生成整棵树，并返回这个树结构
        dto.setPageSize(-1); // 不分页
        List<BaseMenuDO> dbList = doMyPage(dto).getRecords();

        if (dbList.size() == 0) {
            return resList;
        }

        // 查询出所有的菜单
        List<BaseMenuDO> allList = list();

        if (allList.size() == 0) {
            return resList;
        }

        return MyTreeUtil.getFullTreeByDeepNode(dbList, allList);
    }

    /**
     * 批量删除
     */
    @Override
    @Transactional
    public String deleteByIdSet(NotEmptyIdSet notEmptyIdSet) {

        // 如果存在下级，则无法删除
        Long selectCount = lambdaQuery().in(BaseMenuDO::getParentId, notEmptyIdSet.getIdSet()).count();
        if (selectCount != 0) {
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
        roleRefMenuService.lambdaUpdate().in(BaseRoleRefMenuDO::getMenuId, idSet).remove();

    }

    /**
     * 获取当前用户绑定的菜单
     */
    @Override
    public List<BaseMenuDO> userMenuList() {
        Long userId = UserUtil.getCurrentUserId();

        if (BaseConstant.ADMIN_ID.equals(userId)) {
            // 如果是 admin账号，则查询所有【不是被禁用了的】菜单
            /** 这里和{@link UserUtil#getMenuListByUserId}需要进行同步修改 */
            return lambdaQuery()
                .select(BaseEntityTwo::getId, BaseEntityFour::getParentId, BaseMenuDO::getPath, BaseMenuDO::getIcon,
                    BaseMenuDO::getRouter, BaseMenuDO::getName, BaseMenuDO::getFirstFlag, BaseMenuDO::getLinkFlag,
                    BaseMenuDO::getShowFlag, BaseMenuDO::getAuthFlag).eq(BaseEntityThree::getEnableFlag, 1)
                .orderByDesc(BaseMenuDO::getOrderNo).orderByDesc(BaseEntityTwo::getUpdateTime).list();
        }

        // 获取当前用户绑定的菜单
        return UserUtil.getMenuListByUserId(userId, 1);
    }

    /**
     * 通过主键id，查看详情
     */
    @Override
    public MenuInfoByIdVO infoById(NotNullId notNullId) {

        MenuInfoByIdVO menuInfoByIdVO = BeanUtil.copyProperties(getById(notNullId.getId()), MenuInfoByIdVO.class);

        if (menuInfoByIdVO == null) {
            return null;
        }

        // 设置 角色 idSet
        List<BaseRoleRefMenuDO> baseRoleRefMenuDOList =
            roleRefMenuService.lambdaQuery().eq(BaseRoleRefMenuDO::getMenuId, notNullId.getId())
                .select(BaseRoleRefMenuDO::getRoleId).list();
        menuInfoByIdVO
            .setRoleIdSet(baseRoleRefMenuDOList.stream().map(BaseRoleRefMenuDO::getRoleId).collect(Collectors.toSet()));

        return menuInfoByIdVO;
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

        List<BaseMenuDO> listByIds = listByIds(dto.getIdSet());

        for (BaseMenuDO item : listByIds) {
            item.setOrderNo(item.getOrderNo() + dto.getNumber());
        }

        updateBatchById(listByIds);

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }
}





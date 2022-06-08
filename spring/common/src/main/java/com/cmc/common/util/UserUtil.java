package com.cmc.common.util;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.cmc.common.exception.BaseBizCodeEnum;
import com.cmc.common.mapper.*;
import com.cmc.common.model.constant.BaseConstant;
import com.cmc.common.model.entity.*;
import com.cmc.common.model.vo.ApiResultVO;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserUtil {

    private static MenuMapper menuMapper;

    @Resource
    private void setMenuMapper(MenuMapper value) {
        menuMapper = value;
    }

    private static RoleRefMenuMapper roleRefMenuMapper;

    @Resource
    private void setRoleRefMenuMapper(RoleRefMenuMapper value) {
        roleRefMenuMapper = value;
    }

    private static RoleRefUserMapper roleRefUserMapper;

    @Resource
    private void setRoleRefUserMapper(RoleRefUserMapper value) {
        roleRefUserMapper = value;
    }

    private static RoleMapper roleMapper;

    @Resource
    private void setRoleMapper(RoleMapper value) {
        roleMapper = value;
    }

    private static BaseUserSecurityMapper baseUserSecurityMapper;

    @Resource
    private void setBaseUserSecurityMapper(BaseUserSecurityMapper value) {
        baseUserSecurityMapper = value;
    }

    /**
     * 获取当前 userId
     * 这里只会返回实际的 userId，如果为 null，则会抛出异常
     */
    public static Long getCurrentUserId() {
        Long userId = getCurrentUserIdNoCheck();

        if (userId == null) {
            ApiResultVO.error(BaseBizCodeEnum.NOT_LOGGED_IN_YET);
        }

        return userId;
    }

    /**
     * 这里只会返回实际的 userId 或者 -1，备注：-1表示没有 用户id，则是大多数情况下，表示的是 系统
     */
    public static Long getCurrentUserIdSafe() {
        Long userId = getCurrentUserIdNoCheck();
        if (userId == null) {
            userId = -1L;
        }

        return userId;
    }

    /**
     * 获取当前 userId，注意：这里获取 userId之后需要做 非空判断
     * 这里只会返回实际的 userId或者 null
     */
    private static Long getCurrentUserIdNoCheck() {
        Long userId = null;
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            userId = (Long)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }

        return userId;
    }

    /**
     * 通过 userId，获取数据库中用户安全以及状态相关
     */
    public static BaseUserSecurityDO getUserStatusInfoByUserId(Long userId) {

        if (!BaseConstant.ADMIN_ID.equals(userId)) {
            return ChainWrappers.lambdaQueryChain(baseUserSecurityMapper)
                .eq(BaseUserSecurityDO::getUserId, userId).eq(BaseUserSecurityDO::getEnableFlag, true)
                .eq(BaseUserSecurityDO::getDelFlag, false).select(BaseUserSecurityDO::getJwtSecretSuf).one();
        }

        return null;
    }

    /**
     * 通过 menuIdSet，获取 userIdSet
     */
    public static Set<Long> getUserIdSetByMenuIdSet(Set<Long> menuIdSet) {

        Set<Long> resSet = new HashSet<>(); // 本方法返回值

        if (CollUtil.isEmpty(menuIdSet)) {
            return resSet;
        }

        // 获取所有菜单：条件，没有被禁用的
        List<MenuDO> allMenuDbList = ChainWrappers.lambdaQueryChain(menuMapper).eq(BaseEntityThree::getEnableFlag, true)
            .select(BaseEntityTwo::getId, BaseEntityFour::getParentId).list();

        if (allMenuDbList.size() == 0) {
            return resSet;
        }

        List<MenuDO> menuList = new ArrayList<>();

        for (MenuDO item : allMenuDbList) {
            if (menuIdSet.contains(item.getId())) {
                menuList.add(item); // 添加 menuId 对应数据库的对象
            }
        }

        if (menuList.size() == 0) {
            return resSet;
        }

        /**
         * 注意：要和{@link #getMenuListByUserId}同步修改
         */
        // 根据底级节点 list，逆向生成整棵树 list
        menuList = MyTreeUtil.getFullTreeList(menuList, allMenuDbList);

        // 再添加 menuIdSet 的所有子级菜单
        for (Long item : menuIdSet) {
            getMenuListByUserIdNext(menuList, allMenuDbList, item);
        }

        // 得到完整的 menuIdSet
        menuIdSet = menuList.stream().map(BaseEntityTwo::getId).collect(Collectors.toSet());

        // 判断默认角色是否包含了菜单 idSet，如果是，则直接返回 未被注销的，所有用户 idSet
        boolean defaultRoleHasMenuFlag = menuMapper.checkDefaultRoleHasMenu(menuIdSet);
        if (defaultRoleHasMenuFlag) {
            List<BaseUserSecurityDO> baseUserSecurityDOList =
                ChainWrappers.lambdaQueryChain(baseUserSecurityMapper).select(BaseUserSecurityDO::getUserId)
                    .eq(BaseUserSecurityDO::getDelFlag, false).list();
            return baseUserSecurityDOList.stream().map(BaseUserSecurityDO::getUserId).collect(Collectors.toSet());
        }

        // 通过 menuIdSet，获取 userIdSet
        resSet = menuMapper.getUserIdSetByMenuIdSet(menuIdSet);

        resSet.removeAll(Collections.singleton(null));
        return resSet;
    }

    /**
     * 通过用户 id，获取 菜单集合
     * type：1 完整的菜单信息 2 给 security获取权限时使用
     */
    public static List<MenuDO> getMenuListByUserId(Long userId, int type) {

        List<MenuDO> resList = new ArrayList<>(); // 本方法返回值

        // 获取用户绑定的 角色
        List<RoleRefUserDO> baseRoleRefUserList =
            ChainWrappers.lambdaQueryChain(roleRefUserMapper).eq(RoleRefUserDO::getUserId, userId)
                .select(RoleRefUserDO::getRoleId).list();

        Set<Long> roleIdSet = baseRoleRefUserList.stream().map(RoleRefUserDO::getRoleId).collect(Collectors.toSet());

        // 查询是否有 默认角色，条件：没被禁用的
        RoleDO roleOne = ChainWrappers.lambdaQueryChain(roleMapper).eq(RoleDO::getDefaultFlag, true)
            .eq(BaseEntityThree::getEnableFlag, true).select(BaseEntityTwo::getId).one();
        if (roleOne != null) {
            roleIdSet.add(roleOne.getId()); // 添加到 roleIdSet里面
        }

        if (roleIdSet.size() == 0) {
            return resList; // 结束方法
        }

        // 获取 角色绑定的菜单
        List<RoleRefMenuDO> baseRoleRefMenuList =
            ChainWrappers.lambdaQueryChain(roleRefMenuMapper).in(RoleRefMenuDO::getRoleId, roleIdSet)
                .select(RoleRefMenuDO::getMenuId).list();
        if (baseRoleRefMenuList.size() == 0) {
            return resList; // 结束方法
        }

        // 获取所有菜单，条件：没有被 禁用
        /** 这里和{@link com.cmc6.menu.service.MenuService#getUserMenuInfo}需要进行同步修改 */
        List<MenuDO> allMenuDbList;
        if (type == 2) { // 2 给 security获取权限时使用
            allMenuDbList = ChainWrappers.lambdaQueryChain(menuMapper)
                .select(BaseEntityTwo::getId, BaseEntityFour::getParentId, MenuDO::getAuths)
                .eq(BaseEntityThree::getEnableFlag, true).list();
        } else { // 默认是 1
            allMenuDbList = ChainWrappers.lambdaQueryChain(menuMapper)
                .select(BaseEntityTwo::getId, BaseEntityFour::getParentId, MenuDO::getPath, MenuDO::getIcon,
                    MenuDO::getRouter, MenuDO::getName, MenuDO::getFirstFlag, MenuDO::getLinkFlag, MenuDO::getShowFlag,
                    MenuDO::getAuths, MenuDO::getAuthFlag).eq(BaseEntityThree::getEnableFlag, true)
                .orderByDesc(BaseEntityFour::getOrderNo).orderByDesc(BaseEntityTwo::getUpdateTime).list();
        }
        if (allMenuDbList.size() == 0) {
            return resList; // 结束方法
        }

        Set<Long> menuIdSet = baseRoleRefMenuList.stream().map(RoleRefMenuDO::getMenuId).collect(Collectors.toSet());

        // 开始进行匹配，组装返回值
        for (MenuDO item : allMenuDbList) {
            if (menuIdSet.contains(item.getId())) {
                resList.add(item); // 先添加 menuIdSet里面的 菜单
            }
        }

        /**
         * 注意：要和{@link #getUserIdSetByMenuIdSet}同步修改
         */
        // 根据底级节点 list，逆向生成整棵树 list
        resList = MyTreeUtil.getFullTreeList(resList, allMenuDbList);

        for (Long item : menuIdSet) { // 再添加 menuIdSet的所有子级菜单
            getMenuListByUserIdNext(resList, allMenuDbList, item);
        }

        return resList;
    }

    /**
     * 通过用户 id，获取 菜单集合，后续操作
     */
    private static void getMenuListByUserIdNext(List<MenuDO> resList, List<MenuDO> allBaseMenuList, Long parentId) {

        for (MenuDO item : allBaseMenuList) {
            if (item.getParentId().equals(parentId)) {
                long count = resList.stream().filter(it -> it.getId().equals(item.getId())).count();
                if (count == 0) { // 不能重复添加到 返回值里
                    resList.add(item);
                }
                getMenuListByUserIdNext(resList, allBaseMenuList, item.getId()); // 继续匹配下一级
            }
        }

    }

}

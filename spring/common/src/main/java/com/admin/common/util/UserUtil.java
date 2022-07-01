package com.admin.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.common.configuration.JsonRedisTemplate;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.mapper.*;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.entity.*;
import com.admin.common.model.vo.ApiResultVO;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserUtil {

    private static SysMenuMapper sysMenuMapper;

    @Resource
    private void setMenuMapper(SysMenuMapper value) {
        sysMenuMapper = value;
    }

    private static SysRoleRefMenuMapper sysRoleRefMenuMapper;

    @Resource
    private void setRoleRefMenuMapper(SysRoleRefMenuMapper value) {
        sysRoleRefMenuMapper = value;
    }

    private static SysRoleRefUserMapper sysRoleRefUserMapper;

    @Resource
    private void setRoleRefUserMapper(SysRoleRefUserMapper value) {
        sysRoleRefUserMapper = value;
    }

    private static SysRoleMapper sysRoleMapper;

    @Resource
    private void setRoleMapper(SysRoleMapper value) {
        sysRoleMapper = value;
    }

    private static SysUserMapper sysUserMapper;

    @Resource
    private void setSysUserMapper(SysUserMapper value) {
        sysUserMapper = value;
    }

    private static JsonRedisTemplate<Object> jsonRedisTemplate;

    @Resource
    private void setJsonRedisTemplate(JsonRedisTemplate<Object> value) {
        jsonRedisTemplate = value;
    }

    /**
     * 获取当前 userId
     * 这里只会返回实际的 userId，如果为 null，则会抛出异常
     */
    public static Long getCurrentUserId() {

        Long userId = getCurrentUserIdWillNull();

        if (userId == null) {
            ApiResultVO.error(BaseBizCodeEnum.NOT_LOGGED_IN_YET);
        }

        return userId;
    }

    /**
     * 获取当前 userId，如果是 admin账号，则会报错，只会返回 用户id，不会返回 null
     * 因为 admin不支持一些操作，例如：修改密码，修改邮箱等
     */
    public static Long getCurrentUserIdNotAdmin() {

        Long currentUserId = getCurrentUserId();

        if (BaseConstant.ADMIN_ID.equals(currentUserId)) {
            ApiResultVO.error(BaseBizCodeEnum.THE_ADMIN_ACCOUNT_DOES_NOT_SUPPORT_THIS_OPERATION);
        }

        return currentUserId;
    }

    /**
     * 这里只会返回实际的 userId 或者 -1，备注：-1表示没有 用户id，则是大多数情况下，表示的是 系统
     */
    public static Long getCurrentUserIdDefault() {

        Long userId = getCurrentUserIdWillNull();

        if (userId == null) {
            userId = BaseConstant.SYS_ID;
        }

        return userId;
    }

    /**
     * 获取当前 userId，注意：这里获取 userId之后需要做 非空判断
     * 这里只会返回实际的 userId或者 null
     */
    private static Long getCurrentUserIdWillNull() {

        Long userId = null;

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            userId = (Long)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        }

        return userId;
    }

    /**
     * 通过 userId，获取数据库中用户 jwt私钥后缀
     */
    public static SysUserDO getUserJwtSecretSufByUserId(Long userId) {

        if (!BaseConstant.ADMIN_ID.equals(userId)) {
            return ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getId, userId)
                .eq(SysUserDO::getEnableFlag, true).eq(SysUserDO::getDelFlag, false).select(SysUserDO::getJwtSecretSuf)
                .one();
        }

        return null;
    }

    /**
     * 通过 menuIdSet，获取 userIdSet
     */
    public static Set<Long> getUserIdSetByMenuIdSet(Set<Long> menuIdSet) {

        Set<Long> userIdSet = new HashSet<>();

        if (CollUtil.isEmpty(menuIdSet)) {
            return userIdSet;
        }

        // 获取所有菜单：条件，没有被禁用的
        List<SysMenuDO> sysMenuDOList =
            ChainWrappers.lambdaQueryChain(sysMenuMapper).eq(BaseEntityThree::getEnableFlag, true)
                .select(BaseEntityTwo::getId, BaseEntityFour::getParentId).list();

        if (sysMenuDOList.size() == 0) {
            return userIdSet;
        }

        List<SysMenuDO> menuList = new ArrayList<>();

        for (SysMenuDO item : sysMenuDOList) {
            if (menuIdSet.contains(item.getId())) {
                menuList.add(item); // 添加 menuId 对应数据库的对象
            }
        }

        if (menuList.size() == 0) {
            return userIdSet;
        }

        /**
         * 注意：要和{@link #getMenuListByUserId}同步修改
         */
        // 根据底级节点 list，逆向生成整棵树 list
        menuList = MyTreeUtil.getFullTreeList(menuList, sysMenuDOList);

        // 再添加 menuIdSet 的所有子级菜单
        for (Long item : menuIdSet) {
            getMenuListByUserIdNext(menuList, sysMenuDOList, item);
        }

        // 得到完整的 menuIdSet
        menuIdSet = menuList.stream().map(BaseEntityTwo::getId).collect(Collectors.toSet());

        // 判断：默认角色是否包含了菜单 idSet，如果是，则直接返回 未被注销的，所有用户 idSet
        boolean defaultRoleHasMenuFlag = sysMenuMapper.checkDefaultRoleHasMenu(menuIdSet);
        if (defaultRoleHasMenuFlag) {
            List<SysUserDO> sysUserDOList =
                ChainWrappers.lambdaQueryChain(sysUserMapper).select(SysUserDO::getId).eq(SysUserDO::getDelFlag, false)
                    .list();
            return sysUserDOList.stream().map(SysUserDO::getId).collect(Collectors.toSet());
        }

        // 通过 menuIdSet，获取 userIdSet
        userIdSet = sysMenuMapper.getUserIdSetByMenuIdSet(menuIdSet);

        userIdSet.removeAll(Collections.singleton(null));
        return userIdSet;
    }

    /**
     * 更新 redis缓存：角色关联用户
     */
    public static Map<Long, Set<Long>> updateRoleRefUserForRedis() {

        List<SysRoleRefUserDO> sysRoleRefUserDOList = ChainWrappers.lambdaQueryChain(sysRoleRefUserMapper)
            .select(SysRoleRefUserDO::getRoleId, SysRoleRefUserDO::getUserId).list();

        // 用户对应的，角色 idSet
        Map<Long, Set<Long>> userRefRoleIdSetMap = sysRoleRefUserDOList.stream().collect(Collectors
            .groupingBy(SysRoleRefUserDO::getUserId,
                Collectors.mapping(SysRoleRefUserDO::getRoleId, Collectors.toSet())));

        // 删除缓存
        jsonRedisTemplate.delete(BaseConstant.PRE_REDIS_ROLE_REF_USER_CACHE);
        // 设置缓存
        jsonRedisTemplate.boundHashOps(BaseConstant.PRE_REDIS_ROLE_REF_USER_CACHE).putAll(userRefRoleIdSetMap);

        return userRefRoleIdSetMap;
    }

    /**
     * 更新 redis缓存：默认角色 id
     */
    public static long updateDefaultRoleIdForRedis() {

        long defaultRoleId = -1L; // -1 表示没有 默认角色

        SysRoleDO sysRoleDO = ChainWrappers.lambdaQueryChain(sysRoleMapper).eq(SysRoleDO::getDefaultFlag, true)
            .eq(BaseEntityThree::getEnableFlag, true).select(BaseEntityTwo::getId).one();

        if (sysRoleDO != null) {
            defaultRoleId = sysRoleDO.getId();
        }

        // 删除缓存
        jsonRedisTemplate.delete(BaseConstant.PRE_REDIS_DEFAULT_ROLE_ID_CACHE);
        // 设置缓存
        jsonRedisTemplate.opsForValue().set(BaseConstant.PRE_REDIS_DEFAULT_ROLE_ID_CACHE, defaultRoleId);

        return defaultRoleId;
    }

    /**
     * 更新 redis缓存：角色关联菜单
     */
    public static Map<Long, Set<Long>> updateRoleRefMenuForRedis() {

        List<SysRoleRefMenuDO> sysRoleRefMenuDOList = ChainWrappers.lambdaQueryChain(sysRoleRefMenuMapper)
            .select(SysRoleRefMenuDO::getRoleId, SysRoleRefMenuDO::getMenuId).list();

        // 角色对应的，菜单 idSet
        Map<Long, Set<Long>> roleRefMenuIdSetMap = sysRoleRefMenuDOList.stream().collect(Collectors
            .groupingBy(SysRoleRefMenuDO::getRoleId,
                Collectors.mapping(SysRoleRefMenuDO::getMenuId, Collectors.toSet())));

        // 删除缓存
        jsonRedisTemplate.delete(BaseConstant.PRE_REDIS_ROLE_REF_MENU_CACHE);
        // 设置缓存
        jsonRedisTemplate.boundHashOps(BaseConstant.PRE_REDIS_ROLE_REF_MENU_CACHE).putAll(roleRefMenuIdSetMap);

        return roleRefMenuIdSetMap;
    }

    /**
     * 更新 redis缓存：菜单id - 权限 的集合
     */
    public static List<SysMenuDO> updateMenuIdAndAuthsListForRedis() {

        List<SysMenuDO> sysMenuDOList = ChainWrappers.lambdaQueryChain(sysMenuMapper)
            .select(BaseEntityTwo::getId, BaseEntityFour::getParentId, SysMenuDO::getAuths)
            .eq(BaseEntityThree::getEnableFlag, true).list();

        // 删除缓存
        jsonRedisTemplate.delete(BaseConstant.PRE_REDIS_MENU_ID_AND_AUTHS_LIST_CACHE);
        // 设置缓存
        jsonRedisTemplate.boundListOps(BaseConstant.PRE_REDIS_MENU_ID_AND_AUTHS_LIST_CACHE).rightPushAll(sysMenuDOList);

        return sysMenuDOList;
    }

    /**
     * 通过用户 id，获取 菜单集合
     * type：1 完整的菜单信息 2 给 security获取权限时使用
     */
    public static List<SysMenuDO> getMenuListByUserId(Long userId, int type) {

        List<SysMenuDO> resList = new ArrayList<>();

        // 获取用户绑定的 角色
        List<SysRoleRefUserDO> sysRoleRefUserDOList =
            ChainWrappers.lambdaQueryChain(sysRoleRefUserMapper).eq(SysRoleRefUserDO::getUserId, userId)
                .select(SysRoleRefUserDO::getRoleId).list();

        Set<Long> roleIdSet =
            sysRoleRefUserDOList.stream().map(SysRoleRefUserDO::getRoleId).collect(Collectors.toSet());

        // 查询是否有 默认角色，条件：没被禁用的
        SysRoleDO sysRoleDO = ChainWrappers.lambdaQueryChain(sysRoleMapper).eq(SysRoleDO::getDefaultFlag, true)
            .eq(BaseEntityThree::getEnableFlag, true).select(BaseEntityTwo::getId).one();
        if (sysRoleDO != null) {
            roleIdSet.add(sysRoleDO.getId()); // 添加到 roleIdSet里面
        }

        if (roleIdSet.size() == 0) {
            return resList; // 结束方法
        }

        // 获取：角色绑定的菜单
        List<SysRoleRefMenuDO> sysRoleRefMenuDOList =
            ChainWrappers.lambdaQueryChain(sysRoleRefMenuMapper).in(SysRoleRefMenuDO::getRoleId, roleIdSet)
                .select(SysRoleRefMenuDO::getMenuId).list();
        if (sysRoleRefMenuDOList.size() == 0) {
            return resList; // 结束方法
        }

        // 获取所有菜单，条件：没有被 禁用
        /** 这里和{@link com.admin.menu.service.SysMenuService#menuListForUser}需要进行同步修改 */
        List<SysMenuDO> sysMenuDOList;
        if (type == 2) { // 2 给 security获取权限时使用
            sysMenuDOList = ChainWrappers.lambdaQueryChain(sysMenuMapper)
                .select(BaseEntityTwo::getId, BaseEntityFour::getParentId, SysMenuDO::getAuths)
                .eq(BaseEntityThree::getEnableFlag, true).list();
        } else { // 默认是 1
            sysMenuDOList = ChainWrappers.lambdaQueryChain(sysMenuMapper)
                .select(BaseEntityTwo::getId, BaseEntityFour::getParentId, SysMenuDO::getPath, SysMenuDO::getIcon,
                    SysMenuDO::getRouter, SysMenuDO::getName, SysMenuDO::getFirstFlag, SysMenuDO::getLinkFlag,
                    SysMenuDO::getShowFlag, SysMenuDO::getAuths, SysMenuDO::getAuthFlag)
                .eq(BaseEntityThree::getEnableFlag, true).orderByDesc(BaseEntityFour::getOrderNo).list();
        }
        if (sysMenuDOList.size() == 0) {
            return resList; // 结束方法
        }

        Set<Long> menuIdSet =
            sysRoleRefMenuDOList.stream().map(SysRoleRefMenuDO::getMenuId).collect(Collectors.toSet());

        // 开始进行匹配，组装返回值
        for (SysMenuDO item : sysMenuDOList) {
            if (menuIdSet.contains(item.getId())) {
                resList.add(item); // 先添加 menuIdSet里面的 菜单
            }
        }

        /**
         * 注意：要和{@link #getUserIdSetByMenuIdSet}同步修改
         */
        // 根据底级节点 list，逆向生成整棵树 list
        resList = MyTreeUtil.getFullTreeList(resList, sysMenuDOList);

        for (Long item : menuIdSet) { // 再添加 menuIdSet的所有子级菜单
            getMenuListByUserIdNext(resList, sysMenuDOList, item);
        }

        return resList;
    }

    /**
     * 通过用户 id，获取 菜单集合，后续操作
     */
    private static void getMenuListByUserIdNext(List<SysMenuDO> resList, List<SysMenuDO> allBaseMenuList,
        Long parentId) {

        for (SysMenuDO item : allBaseMenuList) {
            if (item.getParentId().equals(parentId)) {
                long count = resList.stream().filter(it -> it.getId().equals(item.getId())).count();
                if (count == 0) { // 不能重复添加到 返回值里
                    resList.add(item);
                }
                getMenuListByUserIdNext(resList, allBaseMenuList, item.getId()); // 继续匹配下一级
            }
        }

    }

    /**
     * 获取默认的用户名
     * 备注：不使用邮箱的原因，因为邮箱不符合 用户昵称的规则：只能包含中文，数字，字母，下划线，长度2-20
     */
    public static String getDefaultNickname() {
        return "用户昵称" + RandomUtil.randomStringUpper(6);
    }

    /**
     * 获取：当前用户的 邮箱地址，只会返回 正确的邮箱地址
     */
    public static String getCurrentUserEmail() {

        Long currentUserIdNotAdmin = getCurrentUserIdNotAdmin();

        SysUserDO sysUserDO =
            ChainWrappers.lambdaQueryChain(sysUserMapper).eq(BaseEntityTwo::getId, currentUserIdNotAdmin)
                .select(SysUserDO::getEmail).one();

        if (StrUtil.isBlank(sysUserDO.getEmail())) {
            ApiResultVO.error(BaseBizCodeEnum.EMAIL_ADDRESS_NOT_SET);
        }

        return sysUserDO.getEmail();
    }

}

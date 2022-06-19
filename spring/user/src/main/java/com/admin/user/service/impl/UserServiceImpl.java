package com.admin.user.service.impl;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.common.configuration.BaseConfiguration;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.entity.BaseEntityTwo;
import com.admin.common.model.entity.SysUserDO;
import com.admin.common.util.MyJwtUtil;
import com.admin.common.util.UserUtil;
import com.admin.user.mapper.UserMapper;
import com.admin.user.model.vo.UserBaseInfoVO;
import com.admin.user.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, SysUserDO> implements UserService {

    @Resource
    HttpServletRequest httpServletRequest;

    /**
     * 退出登录
     */
    @Override
    public String logout() {

        Long currentUserId = UserUtil.getCurrentUserId();

        // 清除 redis中的 jwt
        MyJwtUtil.removeJwtHashByRequestCategoryOrJwtHash(currentUserId, null,
            MyJwtUtil.generateRedisJwtHash(httpServletRequest.getHeader(BaseConstant.JWT_HEADER_KEY)), null);

        return "登出成功";
    }

    /**
     * 用户基本信息
     */
    @Override
    public UserBaseInfoVO baseInfo() {

        Long userId = UserUtil.getCurrentUserId();

        UserBaseInfoVO userBaseInfoVO = new UserBaseInfoVO();

        if (BaseConstant.ADMIN_ID.equals(userId)) {
            userBaseInfoVO.setAvatarUrl("");
            userBaseInfoVO.setNickname(BaseConfiguration.adminProperties.getAdminNickname());
            userBaseInfoVO.setBio("");
            userBaseInfoVO.setEmail("");
            userBaseInfoVO.setPasswordFlag(true);
            return userBaseInfoVO;
        }

        SysUserDO sysUserDO = lambdaQuery().eq(BaseEntityTwo::getId, userId)
            .select(SysUserDO::getAvatarUrl, SysUserDO::getNickname, SysUserDO::getEmail, SysUserDO::getBio,
                SysUserDO::getEmail, SysUserDO::getPassword).one();

        if (sysUserDO != null) {
            userBaseInfoVO.setAvatarUrl(sysUserDO.getAvatarUrl());
            userBaseInfoVO.setNickname(sysUserDO.getNickname());
            userBaseInfoVO.setBio(sysUserDO.getBio());
            userBaseInfoVO.setEmail(DesensitizedUtil.email(sysUserDO.getEmail())); // 脱敏
            userBaseInfoVO.setPasswordFlag(StrUtil.isNotBlank(sysUserDO.getPassword()));
        }

        return userBaseInfoVO;
    }

}

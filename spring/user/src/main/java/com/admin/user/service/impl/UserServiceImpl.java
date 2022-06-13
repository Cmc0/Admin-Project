package com.admin.user.service.impl;

import cn.hutool.core.util.DesensitizedUtil;
import com.admin.common.configuration.BaseConfiguration;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.entity.BaseUserInfoDO;
import com.admin.common.util.MyJwtUtil;
import com.admin.common.util.UserUtil;
import com.admin.user.mapper.UserInfoMapper;
import com.admin.user.model.vo.UserCenterBaseInfoVO;
import com.admin.user.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Service
public class UserServiceImpl extends ServiceImpl<UserInfoMapper, BaseUserInfoDO> implements UserService {

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
    public UserCenterBaseInfoVO baseInfo() {

        Long userId = UserUtil.getCurrentUserId();

        if (BaseConstant.ADMIN_ID.equals(userId)) {
            UserCenterBaseInfoVO userCenterBaseInfoVO = new UserCenterBaseInfoVO();
            userCenterBaseInfoVO.setAvatarUrl("");
            userCenterBaseInfoVO.setNickname(BaseConfiguration.adminProperties.getAdminNickname());
            userCenterBaseInfoVO.setPersonalStatement("");
            userCenterBaseInfoVO.setEmail("");
            userCenterBaseInfoVO.setPhone("");
            userCenterBaseInfoVO.setPasswordFlag(true);
            return userCenterBaseInfoVO;
        }

        UserCenterBaseInfoVO userCenterBaseInfoVO = baseMapper.baseInfo(userId);

        if (userCenterBaseInfoVO != null) {
            userCenterBaseInfoVO.setEmail(DesensitizedUtil.email(userCenterBaseInfoVO.getEmail())); // 脱敏
            userCenterBaseInfoVO.setPhone(DesensitizedUtil.mobilePhone(userCenterBaseInfoVO.getPhone())); // 脱敏
        }

        return userCenterBaseInfoVO;
    }

}

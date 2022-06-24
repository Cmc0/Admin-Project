package com.admin.user.service.impl;

import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.common.configuration.BaseConfiguration;
import com.admin.common.configuration.JsonRedisTemplate;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.entity.BaseEntityTwo;
import com.admin.common.model.entity.SysUserDO;
import com.admin.common.util.MyJwtUtil;
import com.admin.common.util.RequestUtil;
import com.admin.common.util.UserUtil;
import com.admin.user.mapper.SysUserProMapper;
import com.admin.user.model.dto.SysUserPageDTO;
import com.admin.user.model.vo.SysUserPageVO;
import com.admin.user.model.vo.UserBaseInfoVO;
import com.admin.user.service.SysUserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserProMapper, SysUserDO> implements SysUserService {

    @Resource
    HttpServletRequest httpServletRequest;
    @Resource
    JsonRedisTemplate<String> jsonRedisTemplate;

    /**
     * 退出登录
     */
    @Override
    public String logout() {

        // 清除 redis中的 jwtHash
        String jwtHash = MyJwtUtil.generateRedisJwtHash(httpServletRequest.getHeader(BaseConstant.JWT_HEADER_KEY),
            UserUtil.getCurrentUserId(), RequestUtil.getSysRequestCategoryEnum(httpServletRequest));

        jsonRedisTemplate.delete(jwtHash);

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

    /**
     * 分页排序查询
     */
    @Override
    public Page<SysUserPageVO> myPage(SysUserPageDTO dto) {

        Page<SysUserPageVO> page = baseMapper.myPage(dto.getPage(), dto);

        for (SysUserPageVO item : page.getRecords()) {
            item.setEmail(DesensitizedUtil.email(item.getEmail())); // 脱敏
        }

        // 增加 admin账号
        if (dto.isAddAdminFlag() && dto.getPageSize() == -1) {
            SysUserPageVO sysUserPageVO = new SysUserPageVO();
            sysUserPageVO.setId(BaseConstant.ADMIN_ID);
            sysUserPageVO.setNickname(BaseConfiguration.adminProperties.getAdminNickname());
            page.getRecords().add(sysUserPageVO);
            page.setTotal(page.getTotal() + 1); // total + 1
        }

        return page;
    }

}

package com.admin.user.service.impl;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.common.configuration.BaseConfiguration;
import com.admin.common.mapper.SysUserMapper;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.constant.BaseRegexConstant;
import com.admin.common.model.entity.BaseEntityThree;
import com.admin.common.model.entity.BaseEntityTwo;
import com.admin.common.model.entity.SysUserDO;
import com.admin.common.model.enums.RequestCategoryEnum;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.MyJwtUtil;
import com.admin.common.util.MyRsaUtil;
import com.admin.common.util.PasswordConvertUtil;
import com.admin.common.util.RequestUtil;
import com.admin.user.exception.BizCodeEnum;
import com.admin.user.model.dto.UserLoginByPasswordDTO;
import com.admin.user.service.UserLoginService;
import com.admin.websocket.service.WebSocketService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Service
public class UserLoginServiceImpl extends ServiceImpl<SysUserMapper, SysUserDO> implements UserLoginService {

    @Resource
    RedissonClient redissonClient;
    @Resource
    HttpServletRequest httpServletRequest;
    @Resource
    WebSocketService webSocketService;

    /**
     * 账号密码登录
     */
    @Override
    public String userLoginByPassword(UserLoginByPasswordDTO dto) {

        // 密码，非对称，解密
        dto.setPassword(MyRsaUtil.rsaDecrypt(dto.getPassword()));

        if (BaseConfiguration.adminProperties.getAdminAccount().equals(dto.getAccount())
            && BaseConfiguration.adminProperties.isAdminEnable()) {
            return passwordForAdmin(dto); // 如果是 admin账户，并且配置文件中允许 admin登录
        }

        boolean isEmail = ReUtil.isMatch(BaseRegexConstant.EMAIL, dto.getAccount());
        if (isEmail) {
            return passwordByAccount(dto);
        }

        ApiResultVO.error(BizCodeEnum.ACCOUNT_NUMBER_AND_PASSWORD_NOT_VALID);
        return null; // 这里不会执行，只是为了语法检测
    }

    /**
     * admin 账号密码登录
     */
    private String passwordForAdmin(UserLoginByPasswordDTO dto) {

        if (!BaseConfiguration.adminProperties.getAdminPassword().equals(dto.getPassword())) {
            ApiResultVO.error(BizCodeEnum.ACCOUNT_NUMBER_AND_PASSWORD_NOT_VALID);
        }

        // admin jwt 一天过期
        return getLoginResult(BaseConstant.ADMIN_ID, false, null);
    }

    /**
     * 账号密码登录
     */
    private String passwordByAccount(UserLoginByPasswordDTO dto) {

        SysUserDO sysUserDO = lambdaQuery().eq(SysUserDO::getEmail, dto.getAccount())
            .select(SysUserDO::getPassword, BaseEntityThree::getDelFlag, BaseEntityThree::getEnableFlag,
                SysUserDO::getJwtSecretSuf, BaseEntityTwo::getId).one();

        // 账户是否存在
        if (sysUserDO == null) {
            ApiResultVO.error(BizCodeEnum.ACCOUNT_NUMBER_AND_PASSWORD_NOT_VALID);
        }

        if (StrUtil.isBlank(sysUserDO.getPassword())) {
            ApiResultVO.error(BizCodeEnum.NO_PASSWORD_SET); // 未设置密码，请点击忘记密码，进行密码设置
        }

        // 校验密码，成功之后，再判断是否被冻结，免得透露用户被封号的信息
        if (!PasswordConvertUtil.match(sysUserDO.getPassword(), dto.getPassword())) {
            ApiResultVO.error(BizCodeEnum.ACCOUNT_NUMBER_AND_PASSWORD_NOT_VALID);
        }

        if (sysUserDO.getDelFlag()) {
            ApiResultVO.error(BizCodeEnum.ACCOUNT_NUMBER_AND_PASSWORD_NOT_VALID);
        }

        if (!sysUserDO.getEnableFlag()) {
            ApiResultVO.error(BizCodeEnum.ACCOUNT_IS_DISABLED);
        }

        return getLoginResult(sysUserDO.getId(), dto.isRememberMe(), sysUserDO.getJwtSecretSuf());
    }

    // 登录成功之后，返回给前端的数据
    private String getLoginResult(Long userId, boolean rememberMe, String jwtSecretSuf) {

        RequestCategoryEnum requestCategoryEnum = RequestUtil.getRequestCategoryEnum(httpServletRequest);

        return MyJwtUtil.generateJwt(userId, rememberMe, jwtSecretSuf, requestCategoryEnum); // 颁发，并返回 jwt

    }

}

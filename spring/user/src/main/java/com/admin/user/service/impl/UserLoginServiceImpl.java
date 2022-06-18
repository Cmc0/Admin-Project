package com.admin.user.service.impl;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.common.configuration.BaseConfiguration;
import com.admin.common.mapper.BaseUserLoginMapper;
import com.admin.common.mapper.BaseUserSecurityMapper;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.constant.BaseRegexConstant;
import com.admin.common.model.entity.BaseEntityThree;
import com.admin.common.model.entity.BaseEntityTwo;
import com.admin.common.model.entity.BaseUserLoginDO;
import com.admin.common.model.entity.BaseUserSecurityDO;
import com.admin.common.model.enums.RequestCategoryEnum;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.*;
import com.admin.user.exception.BizCodeEnum;
import com.admin.user.model.dto.UserLoginPasswordDTO;
import com.admin.user.service.UserLoginService;
import com.admin.websocket.model.entity.WebSocketDO;
import com.admin.websocket.service.WebSocketService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserLoginServiceImpl extends ServiceImpl<BaseUserLoginMapper, BaseUserLoginDO>
    implements UserLoginService {

    @Resource
    RedissonClient redissonClient;
    @Resource
    HttpServletRequest httpServletRequest;
    @Resource
    WebSocketService webSocketService;
    @Resource
    BaseUserSecurityMapper baseUserSecurityMapper;

    /**
     * 账号密码登录
     */
    @Override
    public String password(UserLoginPasswordDTO dto) {

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
    private String passwordForAdmin(UserLoginPasswordDTO dto) {

        if (!BaseConfiguration.adminProperties.getAdminPassword().equals(dto.getPassword())) {
            ApiResultVO.error(BizCodeEnum.ACCOUNT_NUMBER_AND_PASSWORD_NOT_VALID);
        }

        // admin jwt 一天过期
        return getLoginResult(BaseConstant.ADMIN_ID, false, null);
    }

    /**
     * 账号密码登录
     */
    private String passwordByAccount(UserLoginPasswordDTO dto) {

        BaseUserLoginDO baseUserLoginDO = baseMapper.getByEmail(dto.getAccount());

        // 账户是否存在
        if (baseUserLoginDO == null) {
            ApiResultVO.error(BizCodeEnum.ACCOUNT_NUMBER_AND_PASSWORD_NOT_VALID);
        }

        if (StrUtil.isBlank(baseUserLoginDO.getPassword())) {
            ApiResultVO.error(BizCodeEnum.NO_PASSWORD_SET); // 未设置密码，请点击忘记密码，进行密码设置
        }

        // 校验密码
        if (!PasswordConverterUtil.match(baseUserLoginDO.getPassword(), dto.getPassword())) {
            ApiResultVO.error(BizCodeEnum.ACCOUNT_NUMBER_AND_PASSWORD_NOT_VALID);
        }

        // 校验成功之后，再判断是否被冻结，免得透露用户被封号的信息
        BaseUserSecurityDO baseUserSecurityDO = ChainWrappers.lambdaQueryChain(baseUserSecurityMapper)
            .eq(BaseUserSecurityDO::getUserId, baseUserLoginDO.getUserId())
            .select(BaseUserSecurityDO::getDelFlag, BaseUserSecurityDO::getEnableFlag,
                BaseUserSecurityDO::getJwtSecretSuf).one();

        if (baseUserSecurityDO == null) {
            ApiResultVO.error(BizCodeEnum.LOSS_OF_ACCOUNT_INTEGRITY);
        }

        // 如果被注销了
        if (baseUserSecurityDO.getDelFlag()) {
            ApiResultVO.error(BizCodeEnum.ACCOUNT_NUMBER_AND_PASSWORD_NOT_VALID);
        }

        if (!baseUserSecurityDO.getEnableFlag()) {
            ApiResultVO.error(BizCodeEnum.ACCOUNT_IS_DISABLED);
        }

        return getLoginResult(baseUserLoginDO.getUserId(), dto.isRememberMe(), baseUserSecurityDO.getJwtSecretSuf());
    }

    // 登录成功之后，返回给前端的数据
    private String getLoginResult(Long userId, boolean rememberMe, String jwtSecretSuf) {

        RequestCategoryEnum requestCategoryEnum = RequestUtil.getRequestCategoryEnum(httpServletRequest);

        // 获取互斥配置：1 所有都不互斥（默认） 2 相同端的互斥，H5/移动端/桌面程序 3 所有端都互斥
        String exclusionSetting = "1";
        String paramValue = ParamUtil.getValueById(BaseConstant.USER_MUTUALLY_EXCLUSIVE_ID);
        if (paramValue != null) {
            exclusionSetting = paramValue;
        }

        if ("1".equals(exclusionSetting)) {
            return MyJwtUtil.generateJwt(userId, rememberMe, jwtSecretSuf); // 颁发，并返回 jwt
        }

        RLock lock = redissonClient.getLock(BaseConstant.PRE_REDISSON + BaseConstant.PRE_LOCK_LOGIN + userId);
        lock.lock();

        try {

            if ("2".equals(exclusionSetting)) {

                List<WebSocketDO> webSocketDOList = webSocketService.lambdaQuery().eq(WebSocketDO::getUserId, userId)
                    .eq(BaseEntityThree::getEnableFlag, true).eq(WebSocketDO::getCategory, requestCategoryEnum)
                    .select(BaseEntityTwo::getId).list();

                // 下线该类型的用户
                Set<Long> webSocketIdSet =
                    webSocketDOList.stream().map(BaseEntityTwo::getId).collect(Collectors.toSet());
                webSocketService.offlineAndNoticeBySocketIdSetAndUserId(webSocketIdSet, userId, requestCategoryEnum);

            } else if ("3".equals(exclusionSetting)) {
                // 下线其他
                webSocketService.offlineAndNoticeByUserIdSet(Collections.singleton(userId));
            }

            /**
             * 备注：如果这里修改了返回值，那么 {@link com.cmc.request.aop.RequestAop#around} 也要同步进行修改
             */
            return MyJwtUtil.generateJwt(userId, rememberMe, jwtSecretSuf);

        } finally {
            lock.unlock();
        }

    }

}

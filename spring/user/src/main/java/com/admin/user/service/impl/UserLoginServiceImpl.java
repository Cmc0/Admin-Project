package com.admin.user.service.impl;

import cn.hutool.core.util.ReUtil;
import com.admin.common.configuration.BaseConfiguration;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.constant.BaseRegexConstant;
import com.admin.common.model.entity.BaseEntityThree;
import com.admin.common.model.entity.BaseEntityTwo;
import com.admin.common.model.enums.RequestCategoryEnum;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.MyJwtUtil;
import com.admin.common.util.MyRsaUtil;
import com.admin.common.util.ParamUtil;
import com.admin.common.util.RequestUtil;
import com.admin.user.exception.BizCodeEnum;
import com.admin.user.model.dto.UserLoginPasswordDTO;
import com.admin.user.service.UserLoginService;
import com.admin.websocket.model.entity.WebSocketDO;
import com.admin.websocket.service.WebSocketService;
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
public class UserLoginServiceImpl implements UserLoginService {

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
    public String password(UserLoginPasswordDTO dto) {

        // 密码，非对称，解密
        dto.setPassword(MyRsaUtil.rsaDecrypt(dto.getPassword()));

        if (BaseConfiguration.adminProperties.getAdminAccount().equals(dto.getAccount())
            && BaseConfiguration.adminProperties.isAdminEnable()) {
            return passwordForAdmin(dto); // 如果是 admin账户，并且配置文件中允许 admin登录
        }

        boolean isEmail = ReUtil.isMatch(BaseRegexConstant.EMAIL, dto.getAccount());
        if (isEmail) {
            return loginByAccount(dto);
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
    private String loginByAccount(UserLoginPasswordDTO dto) {

        return "jwt";
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

                List<WebSocketDO> socketDbList = webSocketService.lambdaQuery().eq(WebSocketDO::getUserId, userId)
                    .eq(BaseEntityThree::getEnableFlag, true).eq(WebSocketDO::getCategory, requestCategoryEnum)
                    .select(BaseEntityTwo::getId).list();

                if (socketDbList.size() != 0) {
                    // 下线该类型的用户
                    Set<Long> socketIdSet = socketDbList.stream().map(BaseEntityTwo::getId).collect(Collectors.toSet());
                    webSocketService.offlineAndNoticeBySocketIdSetAndUserId(socketIdSet, userId, requestCategoryEnum);
                }

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

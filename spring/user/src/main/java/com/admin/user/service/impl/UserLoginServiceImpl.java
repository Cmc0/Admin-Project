package com.admin.user.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.common.configuration.BaseConfiguration;
import com.admin.common.configuration.JsonRedisTemplate;
import com.admin.common.mapper.SysUserMapper;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.constant.BaseRegexConstant;
import com.admin.common.model.entity.BaseEntityThree;
import com.admin.common.model.entity.BaseEntityTwo;
import com.admin.common.model.entity.SysUserDO;
import com.admin.common.model.enums.SysRequestCategoryEnum;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.MyJwtUtil;
import com.admin.common.util.MyRsaUtil;
import com.admin.common.util.PasswordConvertUtil;
import com.admin.common.util.RequestUtil;
import com.admin.user.exception.BizCodeEnum;
import com.admin.user.model.dto.UserLoginByPasswordDTO;
import com.admin.user.service.UserLoginService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Service
public class UserLoginServiceImpl extends ServiceImpl<SysUserMapper, SysUserDO> implements UserLoginService {

    @Resource
    HttpServletRequest httpServletRequest;
    @Resource
    JsonRedisTemplate<String> jsonRedisTemplate;

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

        ApiResultVO.error(BizCodeEnum.ACCOUNT_NUMBER_OR_PASSWORD_NOT_VALID);
        return null; // 这里不会执行，只是为了通过语法检查
    }

    /**
     * admin 账号密码登录
     */
    private String passwordForAdmin(UserLoginByPasswordDTO dto) {

        if (!BaseConfiguration.adminProperties.getAdminPassword().equals(dto.getPassword())) {
            loginErrorHandler(BaseConstant.ADMIN_ID);
            ApiResultVO.error(BizCodeEnum.ACCOUNT_NUMBER_OR_PASSWORD_NOT_VALID);
        }

        // admin jwt 一天过期
        return getLoginResult(BaseConstant.ADMIN_ID, false, null);
    }

    /**
     * 账号密码登录
     */
    private String passwordByAccount(UserLoginByPasswordDTO dto) {

        SysUserDO sysUserDO = lambdaQuery().eq(SysUserDO::getEmail, dto.getAccount()).eq(SysUserDO::getDelFlag, false)
            .select(SysUserDO::getPassword, BaseEntityThree::getEnableFlag, SysUserDO::getJwtSecretSuf,
                BaseEntityTwo::getId).one();

        // 账户是否存在
        if (sysUserDO == null) {
            ApiResultVO.error(BizCodeEnum.ACCOUNT_NUMBER_OR_PASSWORD_NOT_VALID);
        }

        // 判断：密码错误次数过多，已被冻结
        Boolean hasKey = jsonRedisTemplate.hasKey(BaseRedisConstant.PRE_REDIS_LOGIN_BLACKLIST + sysUserDO.getId());
        if (BooleanUtil.isTrue(hasKey)) {
            ApiResultVO.error(BizCodeEnum.TOO_MANY_LOGIN_FAILURES);
        }

        if (StrUtil.isBlank(sysUserDO.getPassword())) {
            ApiResultVO.error(BizCodeEnum.NO_PASSWORD_SET); // 未设置密码，请点击忘记密码，进行密码设置
        }

        // 校验密码，成功之后，再判断是否被冻结，免得透露用户被封号的信息
        if (!PasswordConvertUtil.match(sysUserDO.getPassword(), dto.getPassword())) {
            loginErrorHandler(sysUserDO.getId());
            ApiResultVO.error(BizCodeEnum.ACCOUNT_NUMBER_OR_PASSWORD_NOT_VALID);
        }

        if (!sysUserDO.getEnableFlag()) {
            ApiResultVO.error(BizCodeEnum.ACCOUNT_IS_DISABLED);
        }

        return getLoginResult(sysUserDO.getId(), dto.isRememberMe(), sysUserDO.getJwtSecretSuf());
    }

    // 登录成功之后，返回给前端的数据
    private String getLoginResult(Long userId, boolean rememberMe, String jwtSecretSuf) {

        SysRequestCategoryEnum sysRequestCategoryEnum = RequestUtil.getSysRequestCategoryEnum(httpServletRequest);

        return MyJwtUtil.generateJwt(userId, rememberMe, jwtSecretSuf, sysRequestCategoryEnum); // 颁发，并返回 jwt

    }

    /**
     * 账号密码错误次数过多时，直接锁定账号，可以进行【忘记密码】操作，解除锁定
     */
    private void loginErrorHandler(Long userId) {

        ValueOperations<String, String> ops = jsonRedisTemplate.opsForValue();

        String redisKey = BaseRedisConstant.PRE_REDIS_LOGIN_ERROR_COUNT + userId;

        Long redisTotal = ops.increment(redisKey); // 次数 加 1

        if (redisTotal != null) {
            if (redisTotal == 1) {
                jsonRedisTemplate
                    .expire(redisKey, BaseConstant.DAY_30_EXPIRE_TIME, TimeUnit.MILLISECONDS); // 等于 1表示，是第一次访问，则设置过期时间
            }
            if (redisTotal > 10) {
                // 超过十次密码错误，则封禁账号，下次再错误，则才会提示
                ops.set(BaseRedisConstant.PRE_REDIS_LOGIN_BLACKLIST + userId, "登录失败次数过多，被锁定的账号");
                // 清空错误次数
                jsonRedisTemplate.delete(redisKey);
            }
        }

    }

}

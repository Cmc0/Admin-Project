package com.admin.user.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.common.configuration.JsonRedisTemplate;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.constant.BaseRegexConstant;
import com.admin.common.model.dto.EmailNotBlankDTO;
import com.admin.common.model.entity.BaseEntityTwo;
import com.admin.common.model.entity.SysUserDO;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.*;
import com.admin.user.exception.BizCodeEnum;
import com.admin.user.mapper.SysUserProMapper;
import com.admin.user.model.dto.UserSelfForgotPasswordDTO;
import com.admin.user.service.UserForgotPasswordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class UserForgotPasswordServiceImpl extends ServiceImpl<SysUserProMapper, SysUserDO>
    implements UserForgotPasswordService {

    @Resource
    JsonRedisTemplate<String> jsonRedisTemplate;
    @Resource
    RedissonClient redissonClient;

    /**
     * 忘记密码，重置密码
     */
    @Override
    public String userForgotPassword(UserSelfForgotPasswordDTO dto) {

        boolean isEmail = ReUtil.isMatch(BaseRegexConstant.EMAIL, dto.getAccount());
        if (!isEmail) {
            ApiResultVO.error(BaseBizCodeEnum.EMAIL_FORMAT_IS_INCORRECT);
        }

        SysUserDO sysUserDO = lambdaQuery().eq(SysUserDO::getEmail, dto.getAccount()).eq(SysUserDO::getDelFlag, false)
            .select(SysUserDO::getEmail, BaseEntityTwo::getId).one();

        if (sysUserDO == null || StrUtil.isBlank(sysUserDO.getEmail())) {
            ApiResultVO.error(BaseBizCodeEnum.EMAIL_DOES_NOT_EXIST_PLEASE_RE_ENTER);
        }

        // 非对称：解密 ↓
        String paramValue = SysParamUtil.getValueById(BaseConstant.RSA_PRIVATE_KEY_ID); // 获取非对称 私钥
        dto.setNewPassword(MyRsaUtil.rsaDecrypt(dto.getNewPassword(), paramValue)); // 非对称：解密
        dto.setNewOrigPassword(MyRsaUtil.rsaDecrypt(dto.getNewOrigPassword(), paramValue)); // 非对称：解密
        // 非对称：解密 ↑

        if (!ReUtil.isMatch(BaseRegexConstant.PASSWORD_REGEXP, dto.getNewOrigPassword())) {
            ApiResultVO.error(BizCodeEnum.PASSWORD_RESTRICTIONS); // 不合法直接抛出异常
        }

        String redisKey = BaseConstant.PRE_LOCK_SELF_FORGOT_PASSWORD_EMAIL_CODE + sysUserDO.getEmail();

        RLock lock = redissonClient.getLock(BaseConstant.PRE_REDISSON + redisKey);
        lock.lock();

        try {

            CodeUtil.checkCode(dto.getCode(), jsonRedisTemplate.opsForValue().get(redisKey));

            sysUserDO.setPassword(PasswordConvertUtil.convert(dto.getNewPassword(), true));
            sysUserDO.setJwtSecretSuf(IdUtil.simpleUUID());

            updateById(sysUserDO); // 操作数据库

            MyJwtUtil.updateUserIdJwtSecretSufForRedis(sysUserDO.getId(), sysUserDO.getJwtSecretSuf());

            // 清除该账号【登录失败次数过多，被锁定】
            jsonRedisTemplate.delete(BaseConstant.PRE_REDIS_LOGIN_BLACKLIST + sysUserDO.getId());

            jsonRedisTemplate.delete(redisKey);

            return BaseBizCodeEnum.API_RESULT_OK.getMsg();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 忘记密码，发送，邮箱验证码
     */
    @Override
    public String userForgotPasswordSendEmailCode(EmailNotBlankDTO dto) {
        return MyEmailUtil.userSelfForgotPasswordSend(dto.getEmail());
    }

}

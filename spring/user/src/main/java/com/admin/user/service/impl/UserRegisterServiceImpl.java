package com.admin.user.service.impl;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.common.configuration.JsonRedisTemplate;
import com.admin.common.mapper.SysUserMapper;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.constant.BaseRegexConstant;
import com.admin.common.model.dto.EmailNotBlankDTO;
import com.admin.common.model.entity.SysUserDO;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.*;
import com.admin.user.exception.BizCodeEnum;
import com.admin.user.model.dto.UserRegisterByEmailDTO;
import com.admin.user.service.UserRegisterService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Service
public class UserRegisterServiceImpl extends ServiceImpl<SysUserMapper, SysUserDO> implements UserRegisterService {

    @Resource
    JsonRedisTemplate<String> jsonRedisTemplate;
    @Resource
    RedissonClient redissonClient;

    /**
     * 邮箱-注册
     */
    @Override
    public String userRegisterByEmail(UserRegisterByEmailDTO dto) {

        // 前端会在 SHA256加密 之后，再进行一次非对称加密，并且会携带一分钟之后的时间戳
        // 后端会把这个非对称加密，存入 redis（一分钟自动过期），目的：一个非对称加密，只能用一次
        // 先检查 redis中是否存在，存在则说明已经用过了，不存在，则解密之后，校验是否过期，过期了也不能使用

        // 非对称：解密 ↓
        String paramValue = SysParamUtil.getValueById(BaseConstant.RSA_PRIVATE_KEY_ID); // 获取非对称 私钥
        dto.setOrigPassword(MyRsaUtil.rsaDecrypt(dto.getOrigPassword(), paramValue)); // 非对称：解密
        dto.setPassword(MyRsaUtil.rsaDecrypt(dto.getPassword(), paramValue)); // 非对称：解密
        // 非对称：解密 ↑

        if (!ReUtil.isMatch(BaseRegexConstant.PASSWORD_REGEXP, dto.getOrigPassword())) {
            ApiResultVO.error(BizCodeEnum.PASSWORD_RESTRICTIONS); // 不合法直接抛出异常
        }

        String redisKey = BaseConstant.PRE_LOCK_EMAIL_CODE + dto.getEmail();
        RLock lock = redissonClient.getLock(BaseConstant.PRE_REDISSON + redisKey);
        lock.lock();

        try {

            // 从 redis中根据 redisKey，拿到验证码（随机值）
            ValueOperations<String, String> ops = jsonRedisTemplate.opsForValue();
            String redisCode = ops.get(redisKey);

            // 如果不存在验证码，则提示：操作失败：请先获取验证码
            if (StrUtil.isBlank(redisCode)) {
                ApiResultVO.error(BizCodeEnum.PLEASE_GET_THE_VERIFICATION_CODE_FIRST);
            }
            // 如果验证码不匹配，则提示：验证码有误，请重试
            if (!dto.getCode().equalsIgnoreCase(redisCode)) {
                ApiResultVO.error(BizCodeEnum.CODE_IS_INCORRECT);
            }

            jsonRedisTemplate.delete(redisKey); // 验证通过，删除 redis中的验证码

            SysUserDO sysUserDO = new SysUserDO();
            sysUserDO.setUuid(IdUtil.simpleUUID());
            sysUserDO.setJwtSecretSuf(IdUtil.simpleUUID());
            sysUserDO.setNickname(UserUtil.getDefaultNickname());
            sysUserDO.setBio("");
            sysUserDO.setAvatarUrl("");
            sysUserDO.setPassword(PasswordConvertUtil.convert(dto.getPassword(), true));
            sysUserDO.setEmail(dto.getEmail());

            save(sysUserDO); // 保存

        } finally {
            lock.unlock();
        }

        return "注册成功";
    }

    /**
     * 邮箱-注册-发送验证码
     */
    @Override
    public String userRegisterByEmailSendCode(EmailNotBlankDTO dto) {

        RLock lock =
            redissonClient.getLock(BaseConstant.PRE_REDISSON + BaseConstant.PRE_LOCK_EMAIL_CODE + dto.getEmail());
        lock.lock();

        try {

            // 判断邮箱是否存在
            boolean exist =
                lambdaQuery().eq(SysUserDO::getEmail, dto.getEmail()).eq(SysUserDO::getDelFlag, false).exists();
            if (exist) {
                ApiResultVO.error(BizCodeEnum.EMAIL_HAS_BEEN_REGISTERED);
            }

            StrBuilder strBuilder = new StrBuilder("尊敬的用户您好，您本次注册的验证码是（10分钟内有效）：");
            String subject = "邮箱注册";

            // 生成随机码，注意：这里是写死的，只生成6位数，如果需要改，则 controller层 code的正则表达式校验也需要改
            String code = RandomUtil.randomNumbers(6).toUpperCase();
            strBuilder.append(code);

            MyMailUtil.send(dto.getEmail(), subject, strBuilder.toString(), false);

            // 保存到 redis中，设置10分钟过期
            jsonRedisTemplate.opsForValue()
                .set(BaseConstant.PRE_LOCK_EMAIL_CODE + dto.getEmail(), code, BaseConstant.MINUTE_10_EXPIRE_TIME,
                    TimeUnit.MILLISECONDS);

        } finally {
            lock.unlock();
        }

        return "发送成功";
    }

}

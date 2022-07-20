package com.admin.user.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import com.admin.common.configuration.JsonRedisTemplate;
import com.admin.common.mapper.SysUserMapper;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.constant.BaseRedisConstant;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

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
    @Transactional
    public String userRegisterByEmail(UserRegisterByEmailDTO dto) {

        // 前端会在 SHA256加密 之后，再进行一次非对称加密，并且会携带 30s之后的时间戳
        // 后端会把这个非对称加密，存入 redis（30s自动过期），目的：一个非对称加密，只能用一次
        // 先检查 redis中是否存在，存在则说明已经用过了，不存在，则解密之后，校验是否过期，过期了也不能使用

        // 非对称：解密 ↓
        String paramValue = SysParamUtil.getValueById(BaseConstant.RSA_PRIVATE_KEY_ID); // 获取非对称 私钥
        dto.setOrigPassword(MyRsaUtil.rsaDecrypt(dto.getOrigPassword(), paramValue)); // 非对称：解密
        dto.setPassword(MyRsaUtil.rsaDecrypt(dto.getPassword(), paramValue)); // 非对称：解密
        // 非对称：解密 ↑

        if (!ReUtil.isMatch(BaseRegexConstant.PASSWORD_REGEXP, dto.getOrigPassword())) {
            ApiResultVO.error(BizCodeEnum.PASSWORD_RESTRICTIONS); // 不合法直接抛出异常
        }

        String redisKey = BaseRedisConstant.PRE_LOCK_EMAIL_CODE + dto.getEmail();
        RLock lock = redissonClient.getLock(BaseRedisConstant.PRE_REDISSON + redisKey);
        lock.lock();

        try {

            CodeUtil.checkCode(dto.getCode(), jsonRedisTemplate.opsForValue().get(redisKey));

            SysUserDO sysUserDO = new SysUserDO();
            sysUserDO.setUuid(IdUtil.simpleUUID());
            sysUserDO.setJwtSecretSuf(IdUtil.simpleUUID());
            sysUserDO.setNickname(UserUtil.getDefaultNickname());
            sysUserDO.setBio("");
            sysUserDO.setAvatarUrl("");
            sysUserDO.setPassword(PasswordConvertUtil.convert(dto.getPassword(), true));
            sysUserDO.setEmail(dto.getEmail());

            save(sysUserDO); // 保存

            MyJwtUtil.setUserIdJwtSecretSufForRedis(sysUserDO.getId(), sysUserDO.getJwtSecretSuf()); // 设置：redis中的缓存

            jsonRedisTemplate.delete(redisKey);

            return "注册成功";
        } finally {
            lock.unlock();
        }
    }

    /**
     * 邮箱-注册-发送验证码
     */
    @Override
    public String userRegisterByEmailSendCode(EmailNotBlankDTO dto) {
        return MyEmailUtil.userRegisterSend(dto.getEmail());
    }

}

package com.admin.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.common.configuration.BaseConfiguration;
import com.admin.common.configuration.JsonRedisTemplate;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.constant.BaseRegexConstant;
import com.admin.common.model.dto.MyCodeToKeyDTO;
import com.admin.common.model.dto.NotEmptyIdSet;
import com.admin.common.model.entity.BaseEntityTwo;
import com.admin.common.model.entity.SysUserDO;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.*;
import com.admin.user.exception.BizCodeEnum;
import com.admin.user.mapper.SysUserProMapper;
import com.admin.user.model.dto.UserSelfDeleteDTO;
import com.admin.user.model.dto.UserSelfUpdateBaseInfoDTO;
import com.admin.user.model.dto.UserSelfUpdateEmailDTO;
import com.admin.user.model.dto.UserSelfUpdatePasswordDTO;
import com.admin.user.model.vo.UserSelfBaseInfoVO;
import com.admin.user.service.SysUserService;
import com.admin.user.service.UserSelfService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Service
public class UserSelfServiceImpl extends ServiceImpl<SysUserProMapper, SysUserDO> implements UserSelfService {

    @Resource
    HttpServletRequest httpServletRequest;
    @Resource
    JsonRedisTemplate<String> jsonRedisTemplate;
    @Resource
    RedissonClient redissonClient;
    @Resource
    SysUserService sysUserService;

    /**
     * 当前用户：退出登录
     */
    @Override
    public String userSelfLogout() {

        // 清除 redis中的 jwtHash
        String jwtHash = MyJwtUtil.generateRedisJwtHash(httpServletRequest.getHeader(BaseConstant.JWT_HEADER_KEY),
            UserUtil.getCurrentUserId(), RequestUtil.getSysRequestCategoryEnum(httpServletRequest));

        jsonRedisTemplate.delete(jwtHash);

        return "登出成功";
    }

    /**
     * 获取：当前用户，基本信息
     */
    @Override
    public UserSelfBaseInfoVO userSelfBaseInfo() {

        Long userId = UserUtil.getCurrentUserId();

        UserSelfBaseInfoVO sysUserSelfBaseInfoVO = new UserSelfBaseInfoVO();

        if (BaseConstant.ADMIN_ID.equals(userId)) {
            sysUserSelfBaseInfoVO.setAvatarUrl("");
            sysUserSelfBaseInfoVO.setNickname(BaseConfiguration.adminProperties.getAdminNickname());
            sysUserSelfBaseInfoVO.setBio("");
            sysUserSelfBaseInfoVO.setEmail("");
            sysUserSelfBaseInfoVO.setPasswordFlag(true);
            return sysUserSelfBaseInfoVO;
        }

        SysUserDO sysUserDO = lambdaQuery().eq(BaseEntityTwo::getId, userId)
            .select(SysUserDO::getAvatarUrl, SysUserDO::getNickname, SysUserDO::getEmail, SysUserDO::getBio,
                SysUserDO::getEmail, SysUserDO::getPassword).one();

        if (sysUserDO != null) {
            sysUserSelfBaseInfoVO.setAvatarUrl(sysUserDO.getAvatarUrl());
            sysUserSelfBaseInfoVO.setNickname(sysUserDO.getNickname());
            sysUserSelfBaseInfoVO.setBio(sysUserDO.getBio());
            sysUserSelfBaseInfoVO.setEmail(DesensitizedUtil.email(sysUserDO.getEmail())); // 脱敏
            sysUserSelfBaseInfoVO.setPasswordFlag(StrUtil.isNotBlank(sysUserDO.getPassword()));
        }

        return sysUserSelfBaseInfoVO;
    }

    /**
     * 当前用户：基本信息：修改
     */
    @Override
    @Transactional
    public String userSelfUpdateBaseInfo(UserSelfUpdateBaseInfoDTO dto) {

        Long currentUserIdNotAdmin = UserUtil.getCurrentUserIdNotAdmin();

        SysUserDO sysUserDO = new SysUserDO();
        sysUserDO.setId(currentUserIdNotAdmin);
        sysUserDO.setNickname(dto.getNickname());
        sysUserDO.setBio(MyEntityUtil.getNotNullStr(dto.getBio()));
        sysUserDO.setAvatarUrl(MyEntityUtil.getNotNullStr(dto.getAvatarUrl()));

        updateById(sysUserDO);

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 当前用户：修改密码
     */
    @Override
    @Transactional
    public String userSelfUpdatePassword(UserSelfUpdatePasswordDTO dto) {

        Long currentUserIdNotAdmin = UserUtil.getCurrentUserIdNotAdmin();

        SysUserDO sysUserDO = lambdaQuery().eq(BaseEntityTwo::getId, currentUserIdNotAdmin)
            .select(SysUserDO::getEmail, BaseEntityTwo::getId).one();

        if (StrUtil.isBlank(sysUserDO.getEmail())) {
            ApiResultVO.error(BaseBizCodeEnum.EMAIL_ADDRESS_NOT_SET);
        }

        // 非对称：解密 ↓
        String paramValue = SysParamUtil.getValueById(BaseConstant.RSA_PRIVATE_KEY_ID); // 获取非对称 私钥
        dto.setNewPassword(MyRsaUtil.rsaDecrypt(dto.getNewPassword(), paramValue)); // 非对称：解密
        dto.setNewOrigPassword(MyRsaUtil.rsaDecrypt(dto.getNewOrigPassword(), paramValue)); // 非对称：解密
        // 非对称：解密 ↑

        if (!ReUtil.isMatch(BaseRegexConstant.PASSWORD_REGEXP, dto.getNewOrigPassword())) {
            ApiResultVO.error(BizCodeEnum.PASSWORD_RESTRICTIONS); // 不合法直接抛出异常
        }

        String redisKey = BaseConstant.PRE_LOCK_SELF_UPDATE_PASSWORD_EMAIL_CODE + sysUserDO.getEmail();

        RLock lock = redissonClient.getLock(BaseConstant.PRE_REDISSON + redisKey);
        lock.lock();

        try {

            CodeUtil.checkCode(dto.getCode(), jsonRedisTemplate.opsForValue().get(redisKey));

            sysUserDO.setPassword(PasswordConvertUtil.convert(dto.getNewPassword(), true));
            sysUserDO.setJwtSecretSuf(IdUtil.simpleUUID());

            updateById(sysUserDO); // 操作数据库

            MyJwtUtil.updateUserIdJwtSecretSufForRedis(sysUserDO.getId(), sysUserDO.getJwtSecretSuf()); // 更新：redis中的缓存

            jsonRedisTemplate.delete(redisKey);

            return BaseBizCodeEnum.API_RESULT_OK.getMsg();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 当前用户：修改密码，发送，邮箱验证码
     */
    @Override
    public String userSelfUpdatePasswordSendEmailCode() {
        return MyEmailUtil.selfUpdatePasswordSend();
    }

    /**
     * 当前用户：修改邮箱
     */
    @Override
    @Transactional
    public String userSelfUpdateEmail(UserSelfUpdateEmailDTO dto) {

        Long currentUserIdNotAdmin = UserUtil.getCurrentUserIdNotAdmin();

        String keyRedisKey = BaseConstant.PRE_LOCK_SELF_UPDATE_EMAIL_EMAIL_CODE_CODE_TO_KEY + dto.getKey();

        String codeRedisKey = BaseConstant.PRE_LOCK_EMAIL_CODE + dto.getEmail();

        RLock keyLock = redissonClient.getLock(BaseConstant.PRE_REDISSON + keyRedisKey);
        RLock codeLock = redissonClient.getLock(BaseConstant.PRE_REDISSON + codeRedisKey);

        // 连锁
        RLock multiLock = redissonClient.getMultiLock(keyLock, codeLock);
        multiLock.lock();

        try {

            // 先检查 key
            Boolean hasKey = jsonRedisTemplate.hasKey(keyRedisKey);
            if (hasKey == null || !hasKey) {
                ApiResultVO.error(BaseBizCodeEnum.OPERATION_TIMED_OUT_PLEASE_TRY_AGAIN);
            }

            CodeUtil.checkCode(dto.getCode(), jsonRedisTemplate.opsForValue().get(codeRedisKey));

            SysUserDO sysUserDO = new SysUserDO();
            sysUserDO.setId(currentUserIdNotAdmin);
            sysUserDO.setJwtSecretSuf(IdUtil.simpleUUID());
            sysUserDO.setEmail(dto.getEmail());

            updateById(sysUserDO);

            MyJwtUtil.updateUserIdJwtSecretSufForRedis(sysUserDO.getId(), sysUserDO.getJwtSecretSuf()); // 更新：redis中的缓存

            jsonRedisTemplate.delete(keyRedisKey);
            jsonRedisTemplate.delete(codeRedisKey);

            return BaseBizCodeEnum.API_RESULT_OK.getMsg();
        } finally {
            multiLock.unlock();
        }
    }

    /**
     * 当前用户：修改邮箱，发送，邮箱验证码
     */
    @Override
    public String userSelfUpdateEmailSendEmailCode() {
        return MyEmailUtil.selfUpdateEmailSend();
    }

    /**
     * 当前用户：修改邮箱，发送，邮箱验证码，验证码兑换 key
     */
    @Override
    public String userSelfUpdateEmailSendEmailCodeCodeToKey(MyCodeToKeyDTO dto) {

        String currentUserEmail = UserUtil.getCurrentUserEmail();

        String redisKey = BaseConstant.PRE_LOCK_SELF_UPDATE_EMAIL_EMAIL_CODE + currentUserEmail;

        RLock lock = redissonClient.getLock(BaseConstant.PRE_REDISSON + redisKey);
        lock.lock();

        try {

            CodeUtil.checkCode(dto.getCode(), jsonRedisTemplate.opsForValue().get(redisKey));

            String uuid = IdUtil.simpleUUID();

            // 十分钟过期
            jsonRedisTemplate.opsForValue()
                .set(BaseConstant.PRE_LOCK_SELF_UPDATE_EMAIL_EMAIL_CODE_CODE_TO_KEY + uuid, "当前用户：修改邮箱，邮箱验证码兑换 key",
                    BaseConstant.MINUTE_10_EXPIRE_TIME, TimeUnit.MILLISECONDS);

            jsonRedisTemplate.delete(redisKey);

            return uuid;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 当前用户：刷新jwt私钥后缀
     */
    @Override
    @Transactional
    public String userSelfRefreshJwtSecretSuf() {

        SysUserDO sysUserDO = new SysUserDO();
        sysUserDO.setId(UserUtil.getCurrentUserIdNotAdmin());
        sysUserDO.setJwtSecretSuf(IdUtil.simpleUUID());

        updateById(sysUserDO);

        MyJwtUtil.updateUserIdJwtSecretSufForRedis(sysUserDO.getId(), sysUserDO.getJwtSecretSuf()); // 更新：redis中的缓存

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 当前用户：注销
     */
    @Override
    @Transactional
    public String userSelfDelete(UserSelfDeleteDTO dto) {

        String currentUserEmail = UserUtil.getCurrentUserEmail();

        String redisKey = BaseConstant.PRE_LOCK_SELF_DELETE_EMAIL_CODE + currentUserEmail;

        RLock lock = redissonClient.getLock(BaseConstant.PRE_REDISSON + redisKey);
        lock.lock();

        try {

            CodeUtil.checkCode(dto.getCode(), jsonRedisTemplate.opsForValue().get(redisKey));

            sysUserService.deleteByIdSet(new NotEmptyIdSet(CollUtil.newHashSet(UserUtil.getCurrentUserIdNotAdmin())));

            jsonRedisTemplate.delete(redisKey);

            return BaseBizCodeEnum.API_RESULT_OK.getMsg();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 当前用户：注销，发送，邮箱验证码
     */
    @Override
    public String userSelfDeleteSendEmailCode() {
        return MyEmailUtil.selfDeleteSend();
    }

}

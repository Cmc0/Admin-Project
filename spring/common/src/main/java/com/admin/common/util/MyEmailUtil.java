package com.admin.common.util;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailException;
import cn.hutool.extra.mail.MailUtil;
import com.admin.common.configuration.BaseConfiguration;
import com.admin.common.configuration.JsonRedisTemplate;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.mapper.SysUserMapper;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.entity.SysUserDO;
import com.admin.common.model.vo.ApiResultVO;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class MyEmailUtil {

    private static JsonRedisTemplate<String> jsonRedisTemplate;

    @Resource
    private void setJsonRedisTemplate(JsonRedisTemplate<String> val) {
        jsonRedisTemplate = val;
    }

    private static SysUserMapper sysUserMapper;

    @Resource
    private void setSysUserMapper(SysUserMapper val) {
        sysUserMapper = val;
    }

    private static RedissonClient redissonClient;

    @Resource
    private void setRedissonClient(RedissonClient val) {
        redissonClient = val;
    }

    // 用户注册：发送邮箱，模板
    private final static String USER_REGISTER_SEND_SUBJECT = "邮箱注册";
    private final static String USER_REGISTER_SEND_TEMP = "尊敬的用户您好，您本次注册的验证码是（10分钟内有效）：{}";
    // 当前用户：修改密码，发送邮箱，模板
    private final static String SELF_UPDATE_PASSWORD_SEND_SUBJECT = "修改密码";
    private final static String SELF_UPDATE_PASSWORD_SEND_TEMP = "尊敬的用户您好，您正在进行修改密码操作，您本次修改密码的验证码是（10分钟内有效）：{}";
    // 当前用户：修改邮箱，发送邮箱，模板
    private final static String SELF_UPDATE_EMAIL_SEND_SUBJECT = "修改邮箱";
    private final static String SELF_UPDATE_EMAIL_SEND_TEMP = "尊敬的用户您好，您本次修改邮箱的验证码是（10分钟内有效）：{}";
    // 当前用户：注销，发送邮箱，模板
    private final static String SELF_DELETE_SEND_SUBJECT = "账号注销";
    private final static String SELF_DELETE_SEND_TEMP = "尊敬的用户您好，您账号注销的验证码是（10分钟内有效）：{}";

    /**
     * 当前用户：注销，发送邮箱
     */
    public static String selfDeleteSend() {

        String currentUserEmail = UserUtil.getCurrentUserEmail();

        String code = RandomUtil.randomStringUpper(6);
        String content = StrUtil.format(SELF_DELETE_SEND_TEMP, code);

        // 保存到 redis中，设置10分钟过期
        jsonRedisTemplate.opsForValue().set(BaseConstant.PRE_LOCK_SELF_DELETE_EMAIL_CODE + currentUserEmail, code,
            BaseConstant.MINUTE_10_EXPIRE_TIME, TimeUnit.MILLISECONDS);

        MyEmailUtil.send(currentUserEmail, SELF_DELETE_SEND_SUBJECT, content, false);

        return BaseBizCodeEnum.API_RESULT_SEND_OK.getMsg();

    }

    /**
     * 当前用户：修改邮箱，发送邮箱
     */
    public static String selfUpdateEmailSend() {

        String currentUserEmail = UserUtil.getCurrentUserEmail();

        String code = RandomUtil.randomStringUpper(6);
        String content = StrUtil.format(SELF_UPDATE_EMAIL_SEND_TEMP, code);

        // 保存到 redis中，设置10分钟过期
        jsonRedisTemplate.opsForValue().set(BaseConstant.PRE_LOCK_SELF_UPDATE_EMAIL_EMAIL_CODE + currentUserEmail, code,
            BaseConstant.MINUTE_10_EXPIRE_TIME, TimeUnit.MILLISECONDS);

        MyEmailUtil.send(currentUserEmail, SELF_UPDATE_EMAIL_SEND_SUBJECT, content, false);

        return BaseBizCodeEnum.API_RESULT_SEND_OK.getMsg();
    }

    /**
     * 当前用户：修改密码，发送邮箱
     */
    public static String selfUpdatePasswordSend() {

        String currentUserEmail = UserUtil.getCurrentUserEmail();

        String code = RandomUtil.randomStringUpper(6);
        String content = StrUtil.format(SELF_UPDATE_PASSWORD_SEND_TEMP, code);

        // 保存到 redis中，设置10分钟过期
        jsonRedisTemplate.opsForValue()
            .set(BaseConstant.PRE_LOCK_SELF_UPDATE_PASSWORD_EMAIL_CODE + currentUserEmail, code,
                BaseConstant.MINUTE_10_EXPIRE_TIME, TimeUnit.MILLISECONDS);

        MyEmailUtil.send(currentUserEmail, SELF_UPDATE_PASSWORD_SEND_SUBJECT, content, false);

        return BaseBizCodeEnum.API_RESULT_SEND_OK.getMsg();
    }

    /**
     * 用户注册：发送邮箱
     */
    public static String userRegisterSend(String email) {

        RLock lock = redissonClient.getLock(BaseConstant.PRE_REDISSON + BaseConstant.PRE_LOCK_EMAIL_CODE + email);
        lock.lock();

        try {
            // 判断邮箱是否存在
            boolean exist = ChainWrappers.lambdaQueryChain(sysUserMapper).eq(SysUserDO::getEmail, email)
                .eq(SysUserDO::getDelFlag, false).exists();
            if (exist) {
                ApiResultVO.error(BaseBizCodeEnum.EMAIL_HAS_BEEN_REGISTERED);
            }

            String code = RandomUtil.randomStringUpper(6);
            String content = StrUtil.format(USER_REGISTER_SEND_TEMP, code);

            // 保存到 redis中，设置10分钟过期
            jsonRedisTemplate.opsForValue()
                .set(BaseConstant.PRE_LOCK_EMAIL_CODE + email, code, BaseConstant.MINUTE_10_EXPIRE_TIME,
                    TimeUnit.MILLISECONDS);

            MyEmailUtil.send(email, USER_REGISTER_SEND_SUBJECT, content, false);

            return BaseBizCodeEnum.API_RESULT_SEND_OK.getMsg();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 发送邮件
     */
    private static String send(String to, String subject, String content, boolean isHtml) {
        try {

            // 消息内容，加上统一的前缀
            content = "【" + BaseConfiguration.adminProperties.getPlatformName() + "】" + content;

            return MailUtil.send(to, subject, content, isHtml);
        } catch (MailException e) {
            e.printStackTrace();
            if (e.getMessage() != null && e.getMessage().contains("Invalid Addresses")) {
                ApiResultVO.error(BaseBizCodeEnum.EMAIL_DOES_NOT_EXIST_PLEASE_RE_ENTER);
            }
            return null; // 这里不会执行，只是为了通过语法检查
        }
    }

}

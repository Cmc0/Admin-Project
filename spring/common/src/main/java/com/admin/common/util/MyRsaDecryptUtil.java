package com.admin.common.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.CryptoException;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.vo.ApiResultVO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class MyRsaDecryptUtil {

    private static RedisTemplate<String, String> redisTemplate;

    @Resource
    private void setRedisTemplate(RedisTemplate<String, String> value) {
        redisTemplate = value;
    }

    /**
     * 非对称：解密
     */
    public static String rsaDecrypt(String str) {

        String paramValue = ParamUtil.getValueById(BaseConstant.RSA_PRIVATE_KEY_ID); // 获取非对称加密，私钥

        return rsaDecrypt(str, paramValue); // 返回解密之后的 字符串
    }

    /**
     * 非对称：解密
     */
    public static String rsaDecrypt(String str, String privateKey) {

        if (StrUtil.isBlank(str)) {
            ApiResultVO.error(BaseBizCodeEnum.PARAMETER_CHECK_ERROR);
        }

        if (StrUtil.isBlank(privateKey)) {
            ApiResultVO.error(BaseBizCodeEnum.PARAMETER_CHECK_ERROR);
        }

        RSA rsa = new RSA(privateKey, null);

        String decryptStr = null;
        try {
            decryptStr = rsa.decryptStr(str, KeyType.PrivateKey); // 非对称解密之后的字符串
        } catch (CryptoException e) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
        }

        // 获取 时间戳
        String[] split = decryptStr.split(";");
        if (split.length != 2) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
        }

        // 校验 时间戳是否过期
        DateTime checkTime = DateUtil.date(Long.parseLong(split[1]));
        int compare = DateUtil.compare(checkTime, new Date());
        if (compare < 0) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
        }

        String redisKey = BaseConstant.PRE_REDIS_RSA_ENCRYPT + str;

        // 校验 是否存在
        Boolean hasKey = redisTemplate.hasKey(redisKey);
        if (hasKey != null && hasKey) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
        }

        // 这个 key存入 redis，不能再次使用了
        redisTemplate.opsForValue().set(redisKey, "不能使用该非对称加密字符串", 1, TimeUnit.MINUTES);

        return split[0]; // 返回解密之后的 字符串
    }

}

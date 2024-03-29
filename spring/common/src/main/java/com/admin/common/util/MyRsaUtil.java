package com.admin.common.util;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.CryptoException;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import com.admin.common.configuration.JsonRedisTemplate;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.constant.BaseRedisConstant;
import com.admin.common.model.vo.ApiResultVO;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class MyRsaUtil {

    private static JsonRedisTemplate<String> jsonRedisTemplate;

    @Resource
    private void setJsonRedisTemplate(JsonRedisTemplate<String> value) {
        jsonRedisTemplate = value;
    }

    /**
     * 非对称：解密
     */
    public static String rsaDecrypt(String str) {

        String paramValue = SysParamUtil.getValueById(BaseConstant.RSA_PRIVATE_KEY_ID); // 获取非对称加密，私钥

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
            ApiResultVO.sysError();
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

        DateTime now = new DateTime();

        int compare = DateUtil.compare(checkTime, now);
        if (compare < 0) {
            ApiResultVO.error("操作失败：您的时间【" + checkTime.toString() + "】小于【" + now.toString() + "】，请调整时间后再试");
        }

        String redisKey = BaseRedisConstant.PRE_REDIS_RSA_ENCRYPT + str;

        // 校验 是否存在
        Boolean hasKey = jsonRedisTemplate.hasKey(redisKey);
        if (hasKey != null && hasKey) {
            ApiResultVO.error(BaseBizCodeEnum.ILLEGAL_REQUEST);
        }

        // 这个 key存入 redis，不能再次使用了
        jsonRedisTemplate.opsForValue()
            .set(redisKey, "不能使用该非对称加密字符串", BaseConstant.SECOND_30_EXPIRE_TIME, TimeUnit.MILLISECONDS);

        return split[0]; // 返回解密之后的 字符串
    }

}

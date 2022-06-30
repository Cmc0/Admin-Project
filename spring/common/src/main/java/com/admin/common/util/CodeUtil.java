package com.admin.common.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.model.vo.ApiResultVO;

import java.util.Date;

/**
 * 编码/编号 工具类
 */
public class CodeUtil {

    /**
     * 获取：编号类型简称 + yyyyMMddHHmmss + 6位随机数，例如：CP20211231141949151232
     */
    public static String getCode(String preStr) {
        String format = DateUtil.format(new Date(), DatePattern.PURE_DATETIME_PATTERN); // yyyyMMddHHmmss
        return preStr + format + RandomUtil.randomNumbers(6);
    }

    public static void checkCode(String sourceCode, String targetCode) {

        // 如果不存在验证码，则提示：操作失败：请先获取验证码
        if (StrUtil.isBlank(targetCode)) {
            ApiResultVO.error(BaseBizCodeEnum.PLEASE_GET_THE_VERIFICATION_CODE_FIRST);
        }

        // 如果验证码不匹配，则提示：验证码有误，请重试
        if (!targetCode.equalsIgnoreCase(sourceCode)) {
            ApiResultVO.error(BaseBizCodeEnum.CODE_IS_INCORRECT);
        }

    }

}

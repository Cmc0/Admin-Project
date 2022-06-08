package com.cmc.common.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;

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

}

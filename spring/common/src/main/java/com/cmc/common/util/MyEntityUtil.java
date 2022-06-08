package com.cmc.common.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.cmc.common.model.vo.ApiResultVO;

import java.math.BigDecimal;
import java.util.Map;

public class MyEntityUtil {

    /**
     * 获取不为 null对象的 字符串
     */
    public static String getNotNullStr(String str) {
        return StrUtil.isBlank(str) ? "" : str;
    }

    /**
     * 获取不为 null对象的 ParentId字符串
     */
    public static Long getNullParentId(Long aLong) {
        return aLong == null ? 0L : aLong;
    }

    /**
     * 获取不为 null对象的 BigDecimal
     */
    public static BigDecimal getNotNullBigDecimal(BigDecimal bigDecimal) {
        return bigDecimal == null ? new BigDecimal("0.00") : bigDecimal;
    }

    /**
     * 获取不为 null对象的 Integer
     */
    public static Integer getNotNullInt(Integer integer) {
        return integer == null ? -1 : integer;
    }

    /**
     * 获取不为 null对象的 Long
     */
    public static Long getNotNullLong(Long aLong) {
        return aLong == null ? -1L : aLong;
    }

    /**
     * 检查并且获取 code值
     */
    public static String checkAndGetCode(Long id, String code, String preStr) {
        boolean codeBlankFlag = StrUtil.isBlank(code);
        if (id != null && codeBlankFlag) {
            ApiResultVO.error("参数校验错误：code不能为空");
        }

        return codeBlankFlag ? CodeUtil.getCode(preStr) : code;
    }

    /**
     * number为 -1的，设置为 null
     */
    public static <T> T removeDefault(T t) {
        if (t == null) {
            return null;
        }

        Map<String, Object> map = BeanUtil.beanToMap(t);

        for (Map.Entry<String, Object> item : map.entrySet()) {
            if (item.getValue() == null) {
                continue;
            }
            if ((item.getValue() instanceof Integer) && ((int)item.getValue() == -1)) {
                item.setValue(null);
                continue;
            }
            if ((item.getValue() instanceof Byte) && ((byte)item.getValue() == -1)) {
                item.setValue(null);
                continue;
            }
            if ((item.getValue() instanceof Long) && ((long)item.getValue() == -1)) {
                item.setValue(null);
            }
        }

        return BeanUtil.toBean(map, (Class<T>)t.getClass());
    }

}

package com.admin.common.util;

import cn.hutool.core.convert.Convert;
import com.admin.common.configuration.JsonRedisTemplate;
import com.admin.common.mapper.SysParamMapper;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.entity.BaseEntityThree;
import com.admin.common.model.entity.BaseEntityTwo;
import com.admin.common.model.entity.SysParamDO;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统参数 工具类
 */
@Component
public class SysParamUtil {

    private static SysParamMapper sysParamMapper;

    @Resource
    private void setParamMapper(SysParamMapper value) {
        sysParamMapper = value;
    }

    private static JsonRedisTemplate<String> jsonRedisTemplate;

    @Resource
    private void setJsonRedisTemplate(JsonRedisTemplate<String> value) {
        jsonRedisTemplate = value;
    }

    /**
     * 通过主键 id，获取 value，没有 value则返回 null
     */
    public static String getValueById(Long id) {

        String idStr = Convert.toStr(id);
        if (idStr == null) {
            return null;
        }

        BoundHashOperations<String, String, String> ops =
            jsonRedisTemplate.boundHashOps(BaseConstant.PRE_REDIS_PARAM_CACHE);

        Long size = ops.size();
        if (size == null || size == 0) {
            // 更新 redis中【系统参数】的缓存，并返回 值，备注：值可能会为 null
            return updateRedisCache().get(idStr);
        }

        return ops.get(idStr);
    }

    /**
     * 更新 redis中【系统参数】的缓存，并返回 值
     */
    public static Map<String, String> updateRedisCache() {

        List<SysParamDO> paramRedisList =
            ChainWrappers.lambdaQueryChain(sysParamMapper).select(BaseEntityTwo::getId, SysParamDO::getValue)
                .eq(BaseEntityThree::getEnableFlag, true).list();

        // 转换为 map，目的：提供速度
        // 注意：Collectors.toMap()方法，key不能重复，不然会报错
        // 可以用第三个参数，解决这个报错：(v1, v2) -> v2 不覆盖（留前值）(v1, v2) -> v1 覆盖（取后值）
        Map<String, String> map =
            paramRedisList.stream().collect(Collectors.toMap(it -> it.getId().toString(), SysParamDO::getValue));

        jsonRedisTemplate.delete(BaseConstant.PRE_REDIS_PARAM_CACHE);
        jsonRedisTemplate.boundHashOps(BaseConstant.PRE_REDIS_PARAM_CACHE).putAll(map);

        return map;
    }

}

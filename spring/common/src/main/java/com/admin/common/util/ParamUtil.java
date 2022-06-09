package com.admin.common.util;

import cn.hutool.core.convert.Convert;
import com.admin.common.configuration.JsonRedisTemplate;
import com.admin.common.mapper.BaseParamMapper;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.entity.BaseEntityTwo;
import com.admin.common.model.entity.BaseParamDO;
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
public class ParamUtil {

    private static BaseParamMapper baseParamMapper;

    @Resource
    private void setParamMapper(BaseParamMapper value) {
        baseParamMapper = value;
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
            return updateRedisCache(idStr);
        }

        return ops.get(idStr);
    }

    /**
     * 更新 redis中【系统参数】的缓存，并返回 值，备注：值可能会为 null
     */
    public static String updateRedisCache(String idStr) {

        List<BaseParamDO> paramRedisList =
            ChainWrappers.lambdaQueryChain(baseParamMapper).select(BaseEntityTwo::getId, BaseParamDO::getValue).list();

        // 转换为 map，目的：提供速度
        // 注意：Collectors.toMap()方法，key不能重复，不然会报错
        // 可以用第三个参数，解决这个报错：(v1, v2) -> v2 不覆盖（留前值）(v1, v2) -> v1 覆盖（取后值）
        Map<String, String> map =
            paramRedisList.stream().collect(Collectors.toMap(it -> Convert.toStr(it.getId()), BaseParamDO::getValue));

        jsonRedisTemplate.boundHashOps(BaseConstant.PRE_REDIS_PARAM_CACHE).putAll(map);

        return map.get(idStr);

    }

    /**
     * 移除 redis中【系统参数】的缓存
     */
    public static void deleteRedisCache() {

        jsonRedisTemplate.delete(BaseConstant.PRE_REDIS_PARAM_CACHE);
    }

}

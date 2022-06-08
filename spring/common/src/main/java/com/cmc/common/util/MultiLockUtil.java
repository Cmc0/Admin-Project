package com.cmc.common.util;

import cn.hutool.extra.spring.SpringUtil;
import com.cmc.common.model.constant.BaseConstant;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.Set;

/**
 * 连锁工具类
 */
public class MultiLockUtil {

    /**
     * 获取连锁
     */
    public static RLock getMultiLock(String preName, Set<Long> nameSet) {

        RLock[] lockArr = new RLock[nameSet.size()];
        RedissonClient redissonClient = SpringUtil.getBean(RedissonClient.class);

        int i = 0;
        for (Long item : nameSet) {
            lockArr[i] = redissonClient.getLock(BaseConstant.PRE_REDISSON + preName + item); // 设置锁名
            i++;
        }

        return redissonClient.getMultiLock(lockArr);
    }

}

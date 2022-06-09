package com.admin.common.util;

import com.admin.common.model.constant.BaseConstant;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 连锁工具类
 */
@Component
public class MultiLockUtil {

    private static RedissonClient redissonClient;

    @Resource
    private void setRedissonClient(RedissonClient value) {
        redissonClient = value;
    }

    /**
     * 获取连锁
     */
    public static RLock getMultiLock(String preName, Set<Long> nameSet) {

        RLock[] lockArr = new RLock[nameSet.size()];

        int i = 0;
        for (Long item : nameSet) {
            lockArr[i] = redissonClient.getLock(BaseConstant.PRE_REDISSON + preName + item); // 设置锁名
            i++;
        }

        return redissonClient.getMultiLock(lockArr);
    }

}

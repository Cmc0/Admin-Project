package com.admin.common.util;

import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.constant.BaseRedisConstant;
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
    public static RLock getMultiLock(String preName, Set<String> nameSet, RLock... locks) {

        RLock[] lockArr;
        if (locks == null) {
            lockArr = new RLock[nameSet.size()];
        } else {
            lockArr = new RLock[nameSet.size() + locks.length];
        }

        int i = 0;
        for (String item : nameSet) {
            lockArr[i] = redissonClient.getLock(BaseRedisConstant.PRE_REDISSON + preName + item); // 设置锁名
            i++;
        }

        if (locks != null) {
            for (RLock item : locks) {
                lockArr[i] = item;
                i++;
            }
        }

        return redissonClient.getMultiLock(lockArr);
    }

    /**
     * 获取连锁
     */
    public static RLock getMultiLockForLong(String preName, Set<Long> nameSet, RLock... locks) {

        RLock[] lockArr;
        if (locks == null) {
            lockArr = new RLock[nameSet.size()];
        } else {
            lockArr = new RLock[nameSet.size() + locks.length];
        }

        int i = 0;
        for (Long item : nameSet) {
            lockArr[i] = redissonClient.getLock(BaseRedisConstant.PRE_REDISSON + preName + item); // 设置锁名
            i++;
        }

        if (locks != null) {
            for (RLock item : locks) {
                lockArr[i] = item;
                i++;
            }
        }

        return redissonClient.getMultiLock(lockArr);
    }

}

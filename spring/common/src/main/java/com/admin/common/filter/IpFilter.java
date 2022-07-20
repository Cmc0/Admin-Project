package com.admin.common.filter;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.admin.common.configuration.BaseConfiguration;
import com.admin.common.configuration.JsonRedisTemplate;
import com.admin.common.model.constant.BaseConstant;
import com.admin.common.model.constant.BaseRedisConstant;
import com.admin.common.util.ResponseUtil;
import com.admin.common.util.SysParamUtil;
import lombok.SneakyThrows;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * ip 拦截器
 */
@Order(value = Integer.MIN_VALUE)
@Component
@WebFilter(urlPatterns = "/*")
public class IpFilter implements Filter {

    @Resource
    JsonRedisTemplate<String> jsonRedisTemplate;

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {

        if (!BaseConfiguration.prodFlag) {
            chain.doFilter(request, response);
            return;
        }

        String timeStr = ipCheckHandler(ServletUtil.getClientIP((HttpServletRequest)request));

        if (timeStr == null) {
            chain.doFilter(request, response);
        } else {
            ResponseUtil.out((HttpServletResponse)response, "操作次数过多，请在 " + timeStr + "后，再进行操作");
        }

    }

    /**
     * ip 请求速率处理
     * 返回 null，则表示不在黑名单，不为 null，则会返回剩余移除黑名单的时间（字符串）
     */
    private String ipCheckHandler(String ip) {

        // 判断是否在 黑名单里
        ValueOperations<String, String> ops = jsonRedisTemplate.opsForValue();

        String blackListIpKey = BaseRedisConstant.PRE_REDIS_IP_BLACKLIST + ip;

        Long expire = jsonRedisTemplate.getExpire(blackListIpKey, TimeUnit.MILLISECONDS); // 获取 key过期时间，-1 过期 -2 不存在

        if (expire != null && expire > -1) {
            // 如果在 黑名单里，则返回剩余时间（字符串）
            return DateUtil.formatBetween(expire, BetweenFormatter.Level.SECOND);
        }

        return setRedisTotal(ip, ops, blackListIpKey);

    }

    /**
     * 给 redis中 ip设置 请求次数
     */
    private String setRedisTotal(String ip, ValueOperations<String, String> ops, String blackListIpKey) {

        // ip 请求速率：多少秒钟，一个 ip可以请求多少次，用冒号隔开的
        String ipTotalCheckValue = SysParamUtil.getValueById(BaseConstant.IP_REQUESTS_PER_SECOND_ID);

        if (ipTotalCheckValue == null) {
            return null;
        }

        List<String> splitTrimList = StrUtil.splitTrim(ipTotalCheckValue, ":");
        if (splitTrimList.size() != 2) {
            return null;
        }

        Integer timeInt = Convert.toInt(splitTrimList.get(0)); // 多少秒钟
        if (timeInt == null || timeInt <= 0) {
            return null;
        }
        Integer total = Convert.toInt(splitTrimList.get(1)); // 可以请求多少次
        if (total == null || total <= 0) {
            return null;
        }

        String ipKey = BaseRedisConstant.PRE_REDIS_IP_TOTAL_CHECK + ip;

        Long redisTotal = ops.increment(ipKey); // 次数 加 1

        if (redisTotal != null) {
            if (redisTotal == 1) {
                jsonRedisTemplate.expire(ipKey, timeInt, TimeUnit.SECONDS); // 等于 1表示，是第一次访问，则设置过期时间
                return null;
            }
            if (redisTotal > total) {
                ops.set(blackListIpKey, "黑名单 ip", BaseConstant.DAY_1_EXPIRE_TIME, TimeUnit.MILLISECONDS);
                return "1天";
            }
        }

        return null;
    }
}

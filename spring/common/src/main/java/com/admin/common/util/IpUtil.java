package com.admin.common.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import lombok.SneakyThrows;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.http.HttpServletRequest;

public class IpUtil {

    private static Searcher searcher;

    static {
        try {
            ClassPathResource cpr = new ClassPathResource("ip2region/ip2region.xdb");
            searcher = Searcher.newWithBuffer(IoUtil.readBytes(cpr.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过 httpServletRequest获取 ip的区域
     */
    public static String getRegion() {

        HttpServletRequest request = RequestUtil.getRequest();
        if (request == null) {
            return "";
        }

        return getRegion(ServletUtil.getClientIP(request));
    }

    /**
     * 通过 ip获取 ip的区域
     * 返回格式：0|0|0|内网IP|内网IP
     */
    @SneakyThrows
    public static String getRegion(String ip) {
        if (StrUtil.isBlank(ip)) {
            return "";
        }
        return searcher.search(ip);
    }
}

package com.admin.common.util;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import lombok.SneakyThrows;
import org.lionsoul.ip2region.DbConfig;
import org.lionsoul.ip2region.DbSearcher;
import org.springframework.core.io.ClassPathResource;

import javax.servlet.http.HttpServletRequest;

public class IpUtil {

    private static DbSearcher searcher;

    static {
        try {
            // 配置
            DbConfig dbConfig = new DbConfig();
            // 获取 resource下面的文件，备注：需要更新时，直接访问下面的地址，下载文件之后直接覆盖即可
            // https://gitee.com/lionsoul/ip2region/blob/master/data/ip2region.db
            ClassPathResource cpr = new ClassPathResource("/ip2region/ip2region.db");
            // 数据文件
            byte[] dbBinStr = IoUtil.readBytes(cpr.getInputStream());

            searcher = new DbSearcher(dbConfig, dbBinStr); // 这里只支持 MEMORY_ALGORITYM 进行静态赋值，因为只有它才是线程安全的，另外两个都不是线程安全的
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
        return searcher.memorySearch(ip).getRegion(); // 作者说这个算法是线程安全的，并且速度是最快的
    }
}

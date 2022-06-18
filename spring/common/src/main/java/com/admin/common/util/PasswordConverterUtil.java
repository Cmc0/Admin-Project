package com.admin.common.util;

import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.admin.common.model.vo.ApiResultVO;

/**
 * 密码加密类
 */
public class PasswordConverterUtil {

    // 盐和加密后密码的分割符
    private static final String REGEX = "/";

    /**
     * 密码加密
     * checkPasswordBlank：一般情况下就传 true
     */
    public static String converter(String password, boolean checkPasswordBlank) {

        if (StrUtil.isBlank(password)) {
            if (checkPasswordBlank) {
                ApiResultVO.error("密码不能为空");
            } else {
                return "";
            }
        }

        String salt = IdUtil.simpleUUID(); // 取盐

        String saltPro = shaEncode2(salt); // 盐处理一下

        String p = cycle6(saltPro + password); // 循环

        StrBuilder strBuilder = new StrBuilder(salt); // 把盐放到最前面
        strBuilder.append(REGEX).append(p);

        return strBuilder.toString();
    }

    /**
     * 循环加密：6次
     */
    private static String cycle6(String p) {
        for (int i = 0; i < 6; i++) {
            p = shaEncode(p);
        }
        return p;
    }

    /**
     * 密码匹配
     *
     * @param source 用户数据库的密码
     * @param target 前端传过来的密码
     * @return true 一致 false 不一致
     */
    public static boolean match(String source, String target) {

        if (StrUtil.isBlank(source)) {
            ApiResultVO.error("原密码不能为空");
        }
        if (StrUtil.isBlank(target)) {
            ApiResultVO.error("需要比对的密码不能为空");
        }

        String[] split = source.split(REGEX);

        split[0] = shaEncode2(split[0]); // 盐处理一下

        return cycle6(split[0] + target).equals(split[1]);
    }

    /**
     * SHA256和SHA512摘要算法混合
     */
    private static String shaEncode(String password) {
        Digester digester = new Digester(DigestAlgorithm.SHA256);
        password = digester.digestHex(password);
        digester = new Digester(DigestAlgorithm.SHA512);
        return digester.digestHex(password);
    }

    /**
     * SHA256和SHA512摘要算法混合：2
     */
    private static String shaEncode2(String password) {
        Digester digester = new Digester(DigestAlgorithm.SHA512);
        password = digester.digestHex(password);
        digester = new Digester(DigestAlgorithm.SHA256);
        return digester.digestHex(password);
    }
}

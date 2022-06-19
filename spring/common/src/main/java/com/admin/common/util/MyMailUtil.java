package com.admin.common.util;

import cn.hutool.extra.mail.MailException;
import cn.hutool.extra.mail.MailUtil;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.model.vo.ApiResultVO;

public class MyMailUtil {

    /**
     * 发送邮件
     */
    public static void send(String to, String subject, String content, boolean isHtml) {
        try {
            MailUtil.send(to, subject, content, isHtml);
        } catch (MailException e) {
            e.printStackTrace();
            if (e.getMessage() != null && e.getMessage().contains("Invalid Addresses")) {
                ApiResultVO.error(BaseBizCodeEnum.EMAIL_DOES_NOT_EXIST_PLEASE_RE_ENTER);
            }
        }
    }

}

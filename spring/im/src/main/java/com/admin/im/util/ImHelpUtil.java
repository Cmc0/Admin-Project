package com.admin.im.util;

import com.admin.im.model.enums.ImToTypeEnum;

public class ImHelpUtil {

    public static String getSessionId(ImToTypeEnum imToTypeEnum, Long createId, String toId) {
        return "s_" + imToTypeEnum.getCode() + "_" + createId + "_" + toId;
    }

    public static String getFriendId(Long createId, Long uId) {
        return "f_" + createId + "_" + uId;
    }

    public static String getFriendRequestId(Long createId, Long toId) {
        return "fr_" + createId + "_" + toId;
    }

    public static String getGroupJoinId(Long createId, String gId) {
        return "gj_" + createId + "_" + gId;
    }

    public static String getGroupRequestId(Long createId, String gId) {
        return "gr_" + createId + "_" + gId;
    }

}

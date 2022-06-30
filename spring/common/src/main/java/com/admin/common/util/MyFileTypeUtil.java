package com.admin.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.IoUtil;

import java.io.InputStream;
import java.util.Set;

public class MyFileTypeUtil {

    // 头像的文件类型
    public final static Set<String> AVATAR_FILE_TYPE_SET = CollUtil.newHashSet("jpeg", "png", "jpg");

    private final static String IMAGE_TYPE = "image/";
    private final static String AUDIO_TYPE = "audio/";
    private final static String VIDEO_TYPE = "video/";
    private final static String APPLICATION_TYPE = "application/";
    private final static String TXT_TYPE = "text/";

    /**
     * 获取文件类型（不含点），可能返回 null或者空字符串，只要 fileName不为 null，则不会返回 null
     */
    public static String getType(InputStream inputStream, String fileName) {

        String type = FileTypeUtil.getType(inputStream, fileName);

        IoUtil.close(inputStream); // 这里直接关闭流，因为这个 流已经不完整了

        return type;
    }

    /**
     * 通过：文件类型（不含点），获取 contentType
     */
    public static String getContentType(String type, String defaultValue) {

        if (type == null) {
            return defaultValue;
        }

        type = type.toLowerCase();

        if ("jpg".equals(type) || "jpeg".equals(type) || "gif".equals(type) || "png".equals(type) || "bmp".equals(type)
            || "pcx".equals(type) || "tga".equals(type) || "psd".equals(type) || "tiff".equals(type)) {
            return IMAGE_TYPE + type;
        }
        if ("mp3".equals(type) || "ogg".equals(type) || "wav".equals(type) || "real".equals(type) || "ape".equals(type)
            || "module".equals(type) || "midi".equals(type) || "vqf".equals(type) || "cd".equals(type)) {
            return AUDIO_TYPE + type;
        }
        if ("mp4".equals(type) || "avi".equals(type) || "mpeg-1".equals(type) || "rm".equals(type) || "asf".equals(type)
            || "wmv".equals(type) || "qlv".equals(type) || "mpeg-2".equals(type) || "mpeg4".equals(type) || "mov"
            .equals(type) || "3gp".equals(type)) {
            return VIDEO_TYPE + type;
        }
        if ("doc".equals(type) || "docx".equals(type) || "ppt".equals(type) || "pptx".equals(type) || "xls".equals(type)
            || "xlsx".equals(type) || "zip".equals(type) || "jar".equals(type)) {
            return APPLICATION_TYPE + type;
        }
        if ("txt".equals(type)) {
            return TXT_TYPE + type;
        }

        return defaultValue;
    }

}

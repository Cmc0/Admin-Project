package com.admin.common.util;

import cn.hutool.core.io.FileTypeUtil;
import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public class MyFileTypeUtil {

    private final static String IMAGE_TYPE = "image/";
    private final static String AUDIO_TYPE = "audio/";
    private final static String VIDEO_TYPE = "video/";
    private final static String APPLICATION_TYPE = "application/";
    private final static String TXT_TYPE = "text/";

    /**
     * 获取文件类型
     */
    @SneakyThrows
    public static String getType(MultipartFile multipartFile) {
        return getType(multipartFile.getInputStream());
    }

    /**
     * 获取文件类型
     */
    public static String getType(InputStream inputStream) {

        String type = FileTypeUtil.getType(inputStream);

        if ("JPG".equalsIgnoreCase(type) || "JPEG".equalsIgnoreCase(type) || "GIF".equalsIgnoreCase(type) || "PNG"
            .equalsIgnoreCase(type) || "BMP".equalsIgnoreCase(type) || "PCX".equalsIgnoreCase(type) || "TGA"
            .equalsIgnoreCase(type) || "PSD".equalsIgnoreCase(type) || "TIFF".equalsIgnoreCase(type)) {
            return IMAGE_TYPE + type;
        }
        if ("mp3".equalsIgnoreCase(type) || "OGG".equalsIgnoreCase(type) || "WAV".equalsIgnoreCase(type) || "REAL"
            .equalsIgnoreCase(type) || "APE".equalsIgnoreCase(type) || "MODULE".equalsIgnoreCase(type) || "MIDI"
            .equalsIgnoreCase(type) || "VQF".equalsIgnoreCase(type) || "CD".equalsIgnoreCase(type)) {
            return AUDIO_TYPE + type;
        }
        if ("mp4".equalsIgnoreCase(type) || "avi".equalsIgnoreCase(type) || "MPEG-1".equalsIgnoreCase(type) || "RM"
            .equalsIgnoreCase(type) || "ASF".equalsIgnoreCase(type) || "WMV".equalsIgnoreCase(type) || "qlv"
            .equalsIgnoreCase(type) || "MPEG-2".equalsIgnoreCase(type) || "MPEG4".equalsIgnoreCase(type) || "mov"
            .equalsIgnoreCase(type) || "3gp".equalsIgnoreCase(type)) {
            return VIDEO_TYPE + type;
        }
        if ("doc".equalsIgnoreCase(type) || "docx".equalsIgnoreCase(type) || "ppt".equalsIgnoreCase(type) || "pptx"
            .equalsIgnoreCase(type) || "xls".equalsIgnoreCase(type) || "xlsx".equalsIgnoreCase(type) || "zip"
            .equalsIgnoreCase(type) || "jar".equalsIgnoreCase(type)) {
            return APPLICATION_TYPE + type;
        }
        if ("txt".equalsIgnoreCase(type)) {
            return TXT_TYPE + type;
        }

        return null;
    }

}

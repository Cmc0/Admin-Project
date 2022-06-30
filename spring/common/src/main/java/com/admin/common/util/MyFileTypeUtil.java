package com.admin.common.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.ConfigurableMimeFileTypeMap;
import org.springframework.util.MimeTypeUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyFileTypeUtil {

    private final static Map<String, String> FILE_TYPE_MAP = MapUtil.newHashMap();

    static {
        Resource resource = new ClassPathResource("mime.types", ConfigurableMimeFileTypeMap.class);

        Set<String> lineSet = CollUtil.newHashSet();

        try {
            IoUtil.readLines(resource.getInputStream(), CharsetUtil.CHARSET_ISO_8859_1, lineSet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        lineSet.stream().filter(it -> !it.startsWith("#")).forEach(it -> {
            List<String> splitTrimList = StrUtil.splitTrim(it, "\t");
            if (splitTrimList.size() == 2) {
                String contentType = splitTrimList.get(0);
                for (String item : StrUtil.splitTrim(splitTrimList.get(1), StrUtil.SPACE)) {
                    FILE_TYPE_MAP.put(item, contentType);
                }
            }
        });
    }

    /**
     * 获取文件类型（不含点）：读取文件头部字节，获取文件类型，如果没有匹配上，则返回 null
     */
    public static String getType(InputStream inputStream, String fileName) {

        String typeName = FileTypeUtil.getType(inputStream);

        IoUtil.close(inputStream); // 这里直接关闭流，因为这个 流已经不完整了

        // 备注：这一段代码是拷贝 hutool的 ↓
        if ("xls".equals(typeName)) {
            // xls、doc、msi的头一样，使用扩展名辅助判断
            final String extName = FileUtil.extName(fileName);
            if ("doc".equalsIgnoreCase(extName)) {
                typeName = "doc";
            } else if ("msi".equalsIgnoreCase(extName)) {
                typeName = "msi";
            }
        } else if ("zip".equals(typeName)) {
            // zip可能为docx、xlsx、pptx、jar、war、ofd等格式，扩展名辅助判断
            final String extName = FileUtil.extName(fileName);
            if ("docx".equalsIgnoreCase(extName)) {
                typeName = "docx";
            } else if ("xlsx".equalsIgnoreCase(extName)) {
                typeName = "xlsx";
            } else if ("pptx".equalsIgnoreCase(extName)) {
                typeName = "pptx";
            } else if ("jar".equalsIgnoreCase(extName)) {
                typeName = "jar";
            } else if ("war".equalsIgnoreCase(extName)) {
                typeName = "war";
            } else if ("ofd".equalsIgnoreCase(extName)) {
                typeName = "ofd";
            }
        } else if ("jar".equals(typeName)) {
            // wps编辑过的.xlsx文件与.jar的开头相同,通过扩展名判断
            final String extName = FileUtil.extName(fileName);
            if ("xlsx".equalsIgnoreCase(extName)) {
                typeName = "xlsx";
            } else if ("docx".equalsIgnoreCase(extName)) {
                // issue#I47JGH
                typeName = "docx";
            }
        }
        // 备注：这一段代码是拷贝 hutool的 ↑

        return typeName;
    }

    /**
     * 通过：文件类型，获取 contentType
     */
    public static String getContentType(String fileType) {

        String contentType = FILE_TYPE_MAP.get(fileType);

        if (contentType == null) {
            return MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE;
        }

        return contentType;
    }

}

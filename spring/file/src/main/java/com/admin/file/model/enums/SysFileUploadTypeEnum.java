package com.admin.file.model.enums;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.setting.SettingUtil;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.MyFileTypeUtil;
import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Set;

/**
 * 文件上传：枚举类
 */
@AllArgsConstructor
@Getter
public enum SysFileUploadTypeEnum {
    AVATAR(1, "public", "avatar", MyFileTypeUtil.AVATAR_FILE_TYPE_SET) // 头像
    ;

    @EnumValue
    @JsonValue
    private int code;
    private String bucketName; // 桶名
    private String folderName; // 文件夹名
    private Set<String> acceptFileTypeSet; // 支持上传的文件类型（字母必须全小写），为 null则表示支持所有文件，为 空集合则表示不支持所有文件

    public static SysFileUploadTypeEnum getByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (SysFileUploadTypeEnum item : SysFileUploadTypeEnum.values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }

    /**
     * 检查：文件类型，并返回文件类型，返回 null，则表示不支持此文件类型
     */
    public String checkFileType(InputStream inputStream) {

        String fileType = MyFileTypeUtil.getType(inputStream);

        if (fileType == null) {
            return null;
        }

        Set<String> acceptFileTypeSet = getAcceptFileTypeSet();

        if (acceptFileTypeSet == null) {
            return fileType;
        }

        if (acceptFileTypeSet.size() == 0) {
            return null;
        }

        if (acceptFileTypeSet.contains(fileType)) {
            return fileType;
        }

        return null;
    }

}

package com.admin.file.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * {@link com.admin.file.model.entity.SysFileDO}
 */
@Data
public class SysFileUploadDTO {

    @ApiModelProperty(value = "文件")
    private MultipartFile file;

    /**
     * {@link com.admin.file.model.enums.SysFileUploadTypeEnum}
     */
    @ApiModelProperty(value = "文件上传的类型")
    private Integer type;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "额外信息（json格式）")
    private String extraJson;

}
